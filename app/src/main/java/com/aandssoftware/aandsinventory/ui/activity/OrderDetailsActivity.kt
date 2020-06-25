package com.aandssoftware.aandsinventory.ui.activity

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import com.aandssoftware.aandsinventory.R
import com.aandssoftware.aandsinventory.common.Utils
import com.aandssoftware.aandsinventory.firebase.FirebaseUtil
import com.aandssoftware.aandsinventory.listing.OrderDetailsListAdapter
import com.aandssoftware.aandsinventory.models.*
import com.aandssoftware.aandsinventory.notification.NotificationUtil
import com.aandssoftware.aandsinventory.utilities.AppConstants
import com.aandssoftware.aandsinventory.utilities.AppConstants.Companion.EMPTY_STRING
import com.aandssoftware.aandsinventory.utilities.AppConstants.Companion.ORDER_ID
import com.aandssoftware.aandsinventory.utilities.CrashlaticsUtil
import com.aandssoftware.aandsinventory.utilities.SharedPrefsUtils
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_order.*
import kotlinx.android.synthetic.main.custom_action_bar_layout.*
import kotlinx.android.synthetic.main.fab_button_layout.*
import java.util.*
import kotlin.collections.HashMap
import kotlin.math.roundToInt


class OrderDetailsActivity : ListingActivity() {
    private var orderModel: OrderModel? = null
    private var isOrderUpdated = false
    private var menuItemAdd: MenuItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()
    }

    private fun init() {
        getOrderFromIntent()
        navBarBack.setOnClickListener {
            onBackPressed()
        }
        btnConfirm.setOnClickListener {
            orderModel?.let {
                //we want updated order model thats why
                //newly added or updated inventry should save
                var model = (operations as OrderDetailsListAdapter).orderModel
                validateAndConfirmOrder(model)
            }
        }
        fabText?.text = getString(R.string.add_button)
        fab.setOnClickListener {
            (operations as  OrderDetailsListAdapter).actionAdd()
        }
    }

    private fun validateAndConfirmOrder(orderModel: OrderModel?) {
        if (orderModel?.orderItems?.isNotEmpty() == true) {
            var currentDate = System.currentTimeMillis()
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = currentDate
            calendar.add(Calendar.DATE, 45)
            val afterDueDate = calendar.timeInMillis

            orderModel.invoiceNumber = orderModel.orderId.toString()
            orderModel.invoiceDate = currentDate
            orderModel.orderDateUpdated = currentDate
            orderModel.dueDate = afterDueDate

            var totalGstAmount: Double = 0.0
            var totalCgstAmount: Double = 0.0
            var totalsgstAmount: Double = 0.0
            var billAmount: Double = 0.0
            var totalItemPurchasePrice: Double = 0.0
            var finalBillAmount: Double = 0.0
            var discount: Double = 0.0

            var list: List<InventoryItem> = ArrayList<InventoryItem>(orderModel.orderItems.values)
            list.forEachIndexed { index, inventoryItem ->
                totalsgstAmount += inventoryItem.sgstAmount
                totalCgstAmount += inventoryItem.gstAmount
                var itemQuantity = Utils.isEmptyIntFromString(inventoryItem.itemQuantity, 1)
                var singleItemPurchasePrice = itemQuantity * Utils.isEmptyIntFromString(inventoryItem.finalBillAmount.toString(), 0)
                totalItemPurchasePrice += singleItemPurchasePrice
                billAmount += singleItemPurchasePrice
            }
            totalGstAmount = totalCgstAmount + totalsgstAmount
            finalBillAmount = billAmount + totalGstAmount

            var finalAmountAfterRoundRoud = finalBillAmount.roundToInt()
            var roundOff = finalBillAmount - finalAmountAfterRoundRoud
            orderModel.totalTaxableAmount = totalItemPurchasePrice
            orderModel.gstOrderTotalAmount = totalCgstAmount
            orderModel.sgstOrderTotalAmount = totalsgstAmount
            orderModel.gstTotalAmount = totalGstAmount
            orderModel.finalBillAmount = finalAmountAfterRoundRoud.toDouble()
            orderModel.taxableAmountBeforeDiscount = totalItemPurchasePrice
            orderModel.cessAmount = 0// ?
            orderModel.roundOff = roundOff// ?
            orderModel.totalFigure = 0.0// ?
            orderModel.paymentReceived = 0// ?
            orderModel.totalCreditApplied = 0// ?
            orderModel.totalDebitApplied = 0// ?
            orderModel.balanceDue = 0// ?
            orderModel.orderStatus = OrderStatus.CONFIRM.name
            orderModel.orderStatusName = Utils.capitalize(OrderStatus.CONFIRM.toString())

            showProgressBar()
            FirebaseUtil.getInstance().getCustomerDao().updateOrder(orderModel, CallBackListener {
                if (it) {
                    showSnackBarMessage(getString(R.string.order_confirm_successfully))
                    checkAndDisableOrder(menuItemAdd)
                    isOrderUpdated = true
                    showNotificationForOrderConfirm(orderModel)
                } else {
                    showSnackBarMessage(getString(R.string.unable_to_update_order))
                }
                dismissProgressBar()
            })
        } else {
            showSnackBarMessage(getString(R.string.add_at_least_one_item))
        }
    }

    private fun showNotificationForOrderConfirm(mItem: OrderModel) {
        var appVersion = SharedPrefsUtils.getAppVersionPreference(this, SharedPrefsUtils.APP_VERSION)
        appVersion?.let {
            var customerId = appVersion.adminCustomerId ?: AppConstants.EMPTY_STRING
            FirebaseUtil.getInstance().getCustomerDao().getCustomerFromID(customerId,
                    object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            var model = FirebaseUtil.getInstance().getClassData(dataSnapshot, CustomerModel::class.java)
                            model?.let {
                                CrashlaticsUtil.logInfo(CrashlaticsUtil.TAG_INFO, Gson().toJson(model))
                                model.notificationToken?.let { token ->
                                    var map = HashMap<String, String>()
                                    map.put(NotificationUtil.TITLE,NotificationUtil.ORDER_CONFIRM_TITLE.plus(mItem.customerModel?.customerName))
                                    map.put(NotificationUtil.BODY, Utils.getItemNames(mItem))
                                    map.put(NotificationUtil.ORDER_ID, mItem.id ?: EMPTY_STRING)
                                    map.put(NotificationUtil.CUSTOMER_ID, mItem.customerId
                                            ?: EMPTY_STRING)
                                    map.put(NotificationUtil.FLOW_ID, NotificationUtil.NOTIFICATION_FLOW)
                                    map.put(NotificationUtil.NOTIFICATION_TYPE, NotificationUtil.NotificationType.ORDER_CONFIRM_INDICATE_TO_ADMIN.toString())
                                    NotificationUtil.sendNotification(token, map, CallBackListener {
                                        reloadActivity()
                                    })
                                }
                            }
                        }

                        override fun onCancelled(databaseError: DatabaseError) {
                            reloadActivity()
                        }
                    })
        } ?: reloadActivity()

    }

    private fun reloadActivity() {
        finish()
        startActivity(intent);
    }

    override fun onBackPressed() {
        val intent = Intent()
        intent.putExtra(AppConstants.UPDATED, isOrderUpdated)
        intent.putExtra(AppConstants.ORDER_ID, orderModel?.id)
        intent.putExtras(getIntent())
        setResult(AppConstants.ORDER_DETAIL_RELOAD_LIST_RESULT_CODE, intent)
        finish()
        super.onBackPressed()
    }

    private fun getOrderFromIntent() {
        if (intent != null && intent.hasExtra(ORDER_ID)) {
            val orderID = intent.getStringExtra(ORDER_ID)
            if (orderID != null && orderID.isNotEmpty()) {
                FirebaseUtil.getInstance().getCustomerDao().getOrderFromID(orderID, object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {}
                    override fun onDataChange(p0: DataSnapshot) {
                        var model = p0.getValue(OrderModel::class.java)
                        model?.let {
                            orderModel = model
                            setValues(model)
                        }
                    }
                })
            }
        }
    }

    public fun setValues(orderModel: OrderModel?) {
        orderModel?.customerModel?.let { customerDetails ->
            tvCustomerName.text = Utils.isEmpty(customerDetails.customerName, "-")
            tvContactNameAndNumber.text = customerDetails.contactPerson?.plus(" ").plus(customerDetails
                    .contactPersonNumber)
            tvCustomerGstNumber.text = customerDetails.customerGstNumber
            customerDetails.imagePath?.let {
                if (it.contains(AppConstants.HTTP, ignoreCase = true)) {
                    Glide.with(this)
                            .load(customerDetails.imagePath)
                            .placeholder(android.R.drawable.ic_menu_gallery)
                            .crossFade()
                            .into(imgCustomerItemLogo)
                }
            }
            if (orderModel.orderItems.isNotEmpty()) {
                llOrderDetails.visibility = View.VISIBLE
                btnConfirm.visibility = View.VISIBLE
                tvInvoiceNumber.text = getString(R.string.invoice_number).plus(" ").plus(orderModel.invoiceNumber?:"")
                tvItemCount.text = orderModel.orderItems.size.toString()
                tvFinalAmount.text = Utils.currencyLocale(Utils.getOrderFinalPrice(orderModel))
                tvGstAmount.text = Utils.currencyLocale(Utils.getOrderGstAmount(orderModel))
                tvTaxableAmount.text =Utils.currencyLocale( Utils.getTaxableOrderAmount(orderModel))
            } else {
                llOrderDetails.visibility = View.GONE
                btnConfirm.visibility = View.GONE
            }

        }
        checkAndDisableOrder(menuItemAdd)
    }

    public fun checkAndDisableOrder(itemAdd: MenuItem?) {
        menuItemAdd = itemAdd
        orderModel?.orderStatus?.let {
            if (OrderStatus.valueOf(it) != OrderStatus.CREATED && !Utils.isAdminUser(this)) {
                btnConfirm.visibility = View.GONE
                if (null != itemAdd) {
                    itemAdd.isVisible = false
                }
            }
        }
        itemAdd?.let {
            if(it.isVisible){
               fabLayout.visibility = View.VISIBLE
            }else{
                fabLayout.visibility = View.GONE
            }
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == AppConstants.LISTING_REQUEST_CODE) {
            isOrderUpdated = true
            (operations as OrderDetailsListAdapter).getResult()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}

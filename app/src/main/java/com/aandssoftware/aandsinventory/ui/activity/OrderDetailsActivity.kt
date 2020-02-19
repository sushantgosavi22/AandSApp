package com.aandssoftware.aandsinventory.ui.activity

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import com.aandssoftware.aandsinventory.R
import com.aandssoftware.aandsinventory.common.Utils
import com.aandssoftware.aandsinventory.firebase.FirebaseUtil
import com.aandssoftware.aandsinventory.listing.OrderDetailsListAdapter
import com.aandssoftware.aandsinventory.models.InventoryItem
import com.aandssoftware.aandsinventory.models.OrderModel
import com.aandssoftware.aandsinventory.models.OrderStatus
import com.aandssoftware.aandsinventory.models.callBackListener
import com.aandssoftware.aandsinventory.utilities.AppConstants
import com.aandssoftware.aandsinventory.utilities.AppConstants.Companion.ORDER_ID
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_order.*
import kotlinx.android.synthetic.main.custom_action_bar_layout.*
import java.util.*


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
                validateAndConfirmOrder(orderModel)
            }
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

            var totalGstAmount = 0
            var totalCgstAmount = 0
            var totalsgstAmount = 0
            var billAmount = 0
            var totalItemPurchasePrice = 0
            var finalBillAmount = 0
            var discount = 0

            var list: List<InventoryItem> = ArrayList<InventoryItem>(orderModel.orderItems.values)
            list.forEachIndexed { index, inventoryItem ->
                totalsgstAmount += inventoryItem.sgstAmount
                totalCgstAmount += inventoryItem.gstAmount
                totalItemPurchasePrice += Utils.isEmptyIntFromString(inventoryItem.itemPurchasePrice, 0)
                billAmount += inventoryItem.finalBillAmount
            }
            totalGstAmount = totalCgstAmount + totalsgstAmount
            finalBillAmount = billAmount + totalGstAmount
            orderModel.totalTaxableAmount = totalItemPurchasePrice// ?
            orderModel.gstOrderTotalAmount = totalCgstAmount// ?
            orderModel.sgstOrderTotalAmount = totalsgstAmount// ?
            orderModel.gstTotalAmount = totalGstAmount// ?
            orderModel.finalBillAmount = finalBillAmount// ?
            orderModel.taxableAmountBeforeDiscount = totalItemPurchasePrice// ?
            orderModel.cessAmount = 0// ?
            orderModel.roundOff = 0// ?
            orderModel.totalFigure = 0// ?
            orderModel.paymentReceived = 0// ?
            orderModel.totalCreditApplied = 0// ?
            orderModel.totalDebitApplied = 0// ?
            orderModel.balanceDue = 0// ?
            orderModel.orderStatus = OrderStatus.CONFIRM.name
            orderModel.orderStatusName = Utils.capitalize(OrderStatus.CONFIRM.toString())

            showProgressBar()
            FirebaseUtil.getInstance().getCustomerDao().updateOrder(orderModel, callBackListener {
                if (it) {
                    showSnackBarMessage(getString(R.string.order_confirm_successfully))
                    checkAndDisableOrder(menuItemAdd)
                    isOrderUpdated = true
                } else {
                    showSnackBarMessage(getString(R.string.unable_to_update_order))
                }
                dismissProgressBar()
            })
        } else {
            showSnackBarMessage(getString(R.string.add_at_least_one_item))
        }
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

    private fun setValues(orderModel: OrderModel?) {
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
        }
        checkAndDisableOrder(menuItemAdd)
    }

    public fun checkAndDisableOrder(itemAdd: MenuItem?) {
        menuItemAdd = itemAdd
        orderModel?.orderStatus?.let {
            if (OrderStatus.valueOf(it) != OrderStatus.CREATED) {
                btnConfirm.visibility = View.GONE
                if (null != itemAdd) {
                    itemAdd.isVisible = false
                }
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

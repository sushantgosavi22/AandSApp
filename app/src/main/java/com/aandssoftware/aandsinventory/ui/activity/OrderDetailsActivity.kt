package com.aandssoftware.aandsinventory.ui.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatImageView
import com.aandssoftware.aandsinventory.R
import com.aandssoftware.aandsinventory.common.DateUtils
import com.aandssoftware.aandsinventory.common.Utils
import com.aandssoftware.aandsinventory.firebase.FirebaseUtil
import com.aandssoftware.aandsinventory.listing.OrderDetailsListAdapter
import com.aandssoftware.aandsinventory.models.*
import com.aandssoftware.aandsinventory.notification.NotificationUtil
import com.aandssoftware.aandsinventory.ui.component.CustomEditText
import com.aandssoftware.aandsinventory.ui.component.signature.CaptureSignatureView
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
import kotlinx.android.synthetic.main.signature_view_layout.view.*
import org.apache.poi.util.IOUtils
import java.util.*
import kotlin.collections.HashMap
import kotlin.math.roundToInt


class OrderDetailsActivity : ListingActivity() , UpdateOrderDialogFragment.OnOrderUpdateListener {
    private var orderModel: OrderModel? = null
    private var isOrderUpdated = false
    private var menuItemAdd: MenuItem? = null
    private var menuItemSave: MenuItem? = null

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

            var invoiceNum  : String = orderModel.orderId.toString().padStart(4,'0')
            orderModel.invoiceNumber = "AS/"+invoiceNum+"/"+ Calendar.getInstance().get(Calendar.YEAR);
            orderModel.invoiceDate = currentDate
            orderModel.orderDateUpdated = currentDate
            orderModel.dueDate = afterDueDate

            var totalGstAmount: Double = 0.0
            var totalCgstAmount: Double = 0.0
            var totalsgstAmount: Double = 0.0
            var billAmount: Double = 0.0
            var totalTaxableAmount: Double = 0.0
            var discount: Double = 0.0

            var list: List<InventoryItem> = ArrayList<InventoryItem>(orderModel.orderItems.values)
            list.forEachIndexed { index, inventoryItem ->
                totalsgstAmount += inventoryItem.sgstAmount
                totalCgstAmount += inventoryItem.gstAmount
                //var itemQuantity = Utils.isEmptyIntFromString(inventoryItem.itemQuantity, 1)
                billAmount += Utils.isEmptyIntFromString(inventoryItem.finalBillAmount.toString(), 0)
                totalTaxableAmount +=  Utils.isEmptyIntFromString(inventoryItem.taxableAmount.toString(), 0)
                discount += inventoryItem.discountOnInventory
            }
            totalGstAmount = totalCgstAmount + totalsgstAmount


            var finalAmountAfterRoundRoud = billAmount.roundToInt()
            var roundOff = Utils.round((billAmount - finalAmountAfterRoundRoud),2)
            orderModel.totalTaxableAmount = totalTaxableAmount
            orderModel.gstOrderTotalAmount = totalCgstAmount
            orderModel.sgstOrderTotalAmount = totalsgstAmount
            orderModel.gstTotalAmount = totalGstAmount
            orderModel.finalBillAmount = finalAmountAfterRoundRoud.toDouble()
            orderModel.taxableAmountBeforeDiscount = totalTaxableAmount
            orderModel.discount =0.0// ?
            orderModel.taxableAmountAfterDiscount = orderModel.discount + totalTaxableAmount
            orderModel.cessAmount = 0// ?
            orderModel.roundOff = roundOff// ?
            orderModel.totalFigure =finalAmountAfterRoundRoud.toDouble()// ?
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
                    checkAndDisableOrder(menuItemAdd,menuItemSave)
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
            showProgressBar()
            var customerId = appVersion.adminCustomerId ?: AppConstants.EMPTY_STRING
            FirebaseUtil.getInstance().getCustomerDao().getCustomerFromID(customerId,
                    object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            dismissProgressBar()
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
                                    showProgressBar()
                                    NotificationUtil.sendNotification(token, map, CallBackListener {
                                        if(cbConfirmOrderWithMail.isChecked){
                                            sendOrderMail(mItem)
                                        }
                                        dismissProgressBar()
                                        reloadActivity()
                                    })
                                }
                            }

                        }

                        override fun onCancelled(databaseError: DatabaseError) {
                            dismissProgressBar()
                            reloadActivity()
                        }
                    })
        } ?: reloadActivity()

    }

    private fun sendOrderMail(mItem: OrderModel) {
        var title = AppConstants.ORDER_CONFIRM_MAIL_SUBJECT.plus(mItem.customerModel?.customerName?:"")
        Utils.sendMailToAdmin(this,title,getConfirmOrderBodyMessage(mItem))
    }

    private fun getConfirmOrderBodyMessage(mItem: OrderModel) : String{
        var mailBody = StringBuilder()
        var body  = AppConstants.ORDER_CONFIRM_MAIL_BODY_UPPER
                .replace("#Date#",DateUtils.getDateFormatted(System.currentTimeMillis()))
                .replace("#PERSON#",mItem.customerModel?.contactPerson?:"")
                .replace("#CompanyName#",mItem.customerModel?.customerName?:"")
                .replace("#Address#",mItem.customerModel?.address?:"")
        mailBody.append(body).append("\n\n\n")
        var mailItemsBody = StringBuilder()
        var row  = StringBuilder()
        mItem?.orderItems?.values?.forEachIndexed { index, inventoryItem ->
            if(index==0){
                row.append( AppConstants.ORDER_CONFIRM_MAIL_BODY_COLUMN)
            }
            var  rowItem =  AppConstants.ORDER_CONFIRM_MAIL_BODY_MIDDLE
                    .replace("#I#",(index+1).toString())
                    .replace("#NAME#",inventoryItem.inventoryItemName.toString())
                    .replace("#HSN#",inventoryItem.hsnCode?:"")
                    .replace("#QTY#",inventoryItem.itemQuantity?:"")
                    .replace("#UNIT#",inventoryItem.itemQuantityUnit?:"")
                    .replace("#DESCRIPTION#",inventoryItem.description.toString()?:"")

            row.append(rowItem)
        }
        mailItemsBody.append(row.toString())
        mailBody.append(mailItemsBody.toString())
        mailBody.append("\n \n")
        mailBody.append(AppConstants.ORDER_CONFIRM_MAIL_BODY_LOWER)
        return mailBody.toString()
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
            var isAdminUser = Utils.isAdminUser(this)
            var date = EMPTY_STRING.plus(DateUtils.getDateFormatted(orderModel.orderDateCreated))
            tvCustomerName.text = if(isAdminUser) {Utils.isEmpty(customerDetails.customerName, "-")}else{ Utils.isEmpty(orderModel.invoiceNumber, date)}
            tvContactNameAndNumber.text = customerDetails.contactPerson?.plus(" ").plus(customerDetails
                    .contactPersonNumber)
            tvCustomerGstNumber.text = customerDetails.customerGstNumber

            tvOrderStatus.text = Utils.isEmpty(orderModel.orderStatusName)
            tvOrderStatus.setBackgroundDrawable(
                    Utils.getStatusBackgroud(this, Utils.isEmpty(orderModel.orderStatus)))
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
                tvInvoiceNumber.text = if(isAdminUser){Utils.isEmpty(orderModel.invoiceNumber,date)}else{ Utils.isEmpty(date)}
                tvItemCount.text = orderModel.orderItems.size.toString()
                tvFinalAmount.text = Utils.currencyLocale(Utils.getOrderFinalPrice(orderModel))
                tvGstAmount.text = Utils.currencyLocale(Utils.getOrderGstAmount(orderModel))
                tvTaxableAmount.text =Utils.currencyLocale( Utils.getTaxableOrderAmount(orderModel))
            } else {
                llOrderDetails.visibility = View.GONE
                btnConfirm.visibility = View.GONE
                tvInvoiceNumber.visibility = View.GONE
            }


            orderModel.orderStatus?.let {
                var status = OrderStatus.valueOf(it)
                if(status == OrderStatus.CREATED && orderModel.orderItems.isNotEmpty() ){
                    cbConfirmOrderWithMail.visibility = View.VISIBLE
                }
                if( (status== OrderStatus.CREATED ||status== OrderStatus.CONFIRM||status== OrderStatus.PENDING )&& orderModel.orderItems.isNotEmpty() ){
                    ivWhatsApp.visibility = View.VISIBLE
                    ivMail.visibility = View.VISIBLE
                }
            }

        }
        checkAndDisableOrder(menuItemAdd,menuItemSave)
    }

    public fun checkAndDisableOrder(itemAdd: MenuItem?,itemSave: MenuItem?) {
        menuItemAdd = itemAdd
        menuItemSave = itemSave
        orderModel?.orderStatus?.let {
            if (!Utils.isAdminUser(this)) {
                itemSave?.isVisible = false
                if(OrderStatus.valueOf(it) == OrderStatus.CONFIRM ||
                        OrderStatus.valueOf(it) == OrderStatus.PENDING ||
                        OrderStatus.valueOf(it) == OrderStatus.DELIVERED){
                    btnConfirm.visibility = View.VISIBLE
                    btnConfirm.text = getString(R.string.confirm_delivery)
                    btnConfirm.setOnClickListener {
                        signatureDialog(this,orderModel,false)
                    }
                    if (null != itemAdd) {
                        itemAdd.isVisible = false
                    }
                }else if(OrderStatus.valueOf(it) != OrderStatus.CREATED){
                    btnConfirm.visibility = View.GONE
                    if (null != itemAdd) {
                        itemAdd.isVisible = false
                    }
                    if(null!=itemSave && OrderStatus.valueOf(it) == OrderStatus.PAYMENT){
                        itemSave.isVisible = true
                    }
                }
            }else if(Utils.isAdminUser(this) ){
                if(itemSave!=null){
                    itemSave.isVisible = true
                }
                if(OrderStatus.valueOf(it) == OrderStatus.PAYMENT ){
                    if (orderModel?.signatureNameDistributor?.isNotEmpty() == true){
                        btnConfirm.visibility = View.GONE
                    }else{
                        btnConfirm.visibility = View.VISIBLE
                        btnConfirm.text = getString(R.string.sign)
                        btnConfirm.setOnClickListener {
                            signatureDialog(this,orderModel,true)
                        }
                        if (null != itemAdd) {
                            itemAdd.isVisible = false
                        }
                    }
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

    public fun signatureDialog( activity : Context, orderModel: OrderModel?,isAdminSign : Boolean) {
        val alertDialogBuilderUserInput = AlertDialog.Builder(activity)
        var view: View = LayoutInflater.from(activity).inflate(R.layout.signature_dialog, null)
        var imgClear = view.findViewById<ImageView>(R.id.imgClear)
        var dateEditText = view.findViewById<CustomEditText>(R.id.tvSignDate)
        dateEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                val input = s.toString()
                view.tvDateOnSign.text = input
            }
        })
        var signName = view.findViewById<CustomEditText>(R.id.tvSignPersonName)
        signName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                val input = s.toString()
                view.tvNameOnSign.text = input
            }
        })
        if(isAdminSign){
            view.findViewById<AppCompatImageView>(R.id.ivStamp)?.visibility = View.VISIBLE
        }
        var signView = view.findViewById<CaptureSignatureView>(R.id.svSignatureView)
        imgClear.setOnClickListener {
            signView.ClearCanvas()
        }
        dateEditText.setText(DateUtils.getFormatedDatePickerFormat(System.currentTimeMillis()))
        alertDialogBuilderUserInput
                .setView(view)
                .setCancelable(false)
                .setPositiveButton(activity.getString(R.string.confirm_delivery)) {dialogBox, _ ->

                    if(signName.getText().isNotEmpty()){
                        orderModel?.id?.let {
                            showProgressBar()
                            var map: HashMap<String, Any> = HashMap()
                            map.put(OrderModel.ORDER_STATUS, OrderStatus.PAYMENT.name.toUpperCase())
                            map.put(OrderModel.ORDER_STATUS_NAME, OrderStatus.PAYMENT.name.capitalize())
                            if(isAdminSign){
                                map.put(OrderModel.DISTRIBUTOR_SIGNATURE, signView.signatureStringFormat)
                                map.put(OrderModel.DISTRIBUTOR_SIGNATURE_NAME, signName.getText())
                                map.put(OrderModel.DISTRIBUTOR_SIGNATURE_DATE, dateEditText.getText())
                            }else{
                                map.put(OrderModel.SIGNATURE, signView.signatureStringFormat)
                                map.put(OrderModel.SIGNATURE_NAME, signName.getText())
                                map.put(OrderModel.SIGNATURE_DATE, dateEditText.getText())
                                var stringBase64 = Utils.getBase64String(IOUtils.toByteArray( this.resources.openRawResource(R.drawable.aands_stamp)))
                                stringBase64?.let {
                                    map.put(OrderModel.DISTRIBUTOR_SIGNATURE, stringBase64)
                                }
                            }
                            FirebaseUtil.getInstance().getCustomerDao().updateOrderStatus(it,map, CallBackListener {
                                if(it){
                                     reloadActivity()
                                }
                                dismissProgressBar()
                            })
                        }
                    }else{
                        showSnackBarMessage(getString(R.string.enter_name))
                    }

                }
                .setNegativeButton(activity.getString(R.string.cancel))
                { dialogBox, _ -> dialogBox.cancel() }

        val alertDialog = alertDialogBuilderUserInput.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    override fun onOrderUpdate(orderModel: OrderModel) {
        if(FirebaseUtil.getInstance().isInternetConnected(this@OrderDetailsActivity)){
            FirebaseUtil.getInstance().getCustomerDao().updateOrder(orderModel, CallBackListener {
                if(it){
                    init()
                }
            })
        }else{
          showSnackBarMessage(getString(R.string.no_internet_connection))
        }
    }

    fun showUpdateDailog() {
        orderModel?.let {
            UpdateOrderDialogFragment(it,this).show(supportFragmentManager,"");
        }
    }

    fun onWhatsAppClick(view: View) {
        orderModel?.let {orderModel ->
            var appVersion = SharedPrefsUtils.getAppVersionPreference(this@OrderDetailsActivity, SharedPrefsUtils.APP_VERSION)
            appVersion?.let {
                var customerId = appVersion.adminCustomerId ?: AppConstants.EMPTY_STRING
                showProgressBar()
                FirebaseUtil.getInstance().getCustomerDao().getCustomerFromID(customerId,
                        object : ValueEventListener {
                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                dismissProgressBar()
                                var model = FirebaseUtil.getInstance().getClassData(dataSnapshot, CustomerModel::class.java)
                                model?.contactPersonNumber?.let {number->
                                    Utils.sendWhatsAppMessage(this@OrderDetailsActivity, number,getConfirmOrderBodyMessage(orderModel))
                                }
                            }
                            override fun onCancelled(databaseError: DatabaseError) {
                                dismissProgressBar()
                            }
                        })
            }
        }
    }

    fun onMailClick(view: View) {
        orderModel?.let {
            sendOrderMail(it)
        }
    }

}

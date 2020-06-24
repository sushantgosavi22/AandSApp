package com.aandssoftware.aandsinventory.listing

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatTextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import com.aandssoftware.aandsinventory.R
import com.aandssoftware.aandsinventory.common.DateUtils
import com.aandssoftware.aandsinventory.common.Navigator
import com.aandssoftware.aandsinventory.common.Utils
import com.aandssoftware.aandsinventory.firebase.FirebaseUtil
import com.aandssoftware.aandsinventory.models.OrderModel
import com.aandssoftware.aandsinventory.models.OrderStatus
import com.aandssoftware.aandsinventory.models.CallBackListener
import com.aandssoftware.aandsinventory.models.CustomerModel
import com.aandssoftware.aandsinventory.notification.NotificationUtil
import com.aandssoftware.aandsinventory.ui.activity.ListingActivity
import com.aandssoftware.aandsinventory.ui.activity.OrderListActivity
import com.aandssoftware.aandsinventory.ui.adapters.BaseAdapter.BaseViewHolder
import com.aandssoftware.aandsinventory.utilities.AppConstants
import com.aandssoftware.aandsinventory.utilities.AppConstants.Companion.EMPTY_STRING
import com.aandssoftware.aandsinventory.utilities.CrashlaticsUtil
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.gson.Gson
import kotlinx.android.synthetic.main.order_item.view.*
import java.io.Serializable

class OrderListAdapter(private val activity: ListingActivity) : ListingOperations {

    internal var lastNodeKey: Double = 0.0
    internal var isAdmin: Boolean = Utils.isAdminUser(activity)

    companion object {
        const val ORDER_RECORD_FETCH_AT_TIME = 51
    }

    inner class OrderViewHolder(itemView: View) : BaseViewHolder(itemView) {

        var tvOrderDate: AppCompatTextView = itemView.tvOrderDate
        var tvOrderStatus: AppCompatTextView = itemView.tvOrderStatus
        var tvFinalAmount: AppCompatTextView = itemView.tvFinalAmount
        var tvItemCount: AppCompatTextView = itemView.tvItemCount
        var tvInvoiceNumber: AppCompatTextView = itemView.tvInvoiceNumber
        var tvCustomerName: AppCompatTextView = itemView.tvCustomerName
        var tvContactNameAndNumber: AppCompatTextView = itemView.tvContactNameAndNumber
        var cardView: CardView = itemView.cardView

        init {
            itemView.imgDelete.setOnClickListener {
                var pos: Int = itemView.getTag(R.string.tag) as Int
                deleteOrder(itemView.tag as OrderModel, activity, pos)
            }
            cardView.setOnClickListener {
                var pos: Int = itemView.getTag(R.string.tag) as Int
                showOrderInventoryActivity(activity, (itemView.tag as OrderModel).id, pos)
            }

            cardView.setOnLongClickListener {
                if (isAdmin) {
                    var pos: Int = itemView.getTag(R.string.tag) as Int
                    var order = itemView.tag as OrderModel
                    showOrderStatusDialog(order, pos)
                }
                false
            }
        }
    }

    private fun showOrderInventoryActivity(activity: Activity, orderId: String?, pos: Int) {
        var intent = Intent().putExtra(AppConstants.POSITION_IN_LIST, pos)
        Navigator.openOrderDetailsScreen(activity, orderId!!, intent)
    }

    override fun getActivityLayoutId(): Int {
        return R.layout.activity_listing
    }


    override fun getBaseViewHolder(viewGroup: ViewGroup, i: Int): BaseViewHolder {
        return OrderViewHolder(LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.order_item, viewGroup, false))
    }

    override fun onBindSearchViewHolder(baseHolder: BaseViewHolder, position: Int, item: Serializable) {
        val holder = baseHolder as OrderViewHolder
        val mItem = item as OrderModel
        holder.tvCustomerName.text = mItem.customerModel?.customerName
        holder.tvContactNameAndNumber.text = mItem.customerModel?.contactPerson + " " + mItem.customerModel?.contactPersonNumber
        holder.tvFinalAmount.text = Utils.getOrderFinalPrice(mItem).toString()
        holder.tvItemCount.text = mItem.orderItems.size.toString()
        holder.tvInvoiceNumber.text = Utils.isEmpty(mItem.invoiceNumber, "-")
        holder.tvOrderDate.text = DateUtils.getDateFormatted(mItem.orderDateCreated)
        holder.tvOrderStatus.text = Utils.isEmpty(mItem.orderStatusName)
        holder.tvOrderStatus.setBackgroundDrawable(
                getStatusBackgroud(baseHolder.itemView.context, Utils.isEmpty(mItem.orderStatus)))

    }


    override fun getTitle(): String {
        return activity.getString(R.string.order)
    }

    private fun getStatusBackgroud(context: Context, statusCode: String): Drawable? {
        var drawable = ContextCompat.getDrawable(context, R.drawable.chip_background_blue)
        when (OrderStatus.valueOf(statusCode)) {
            OrderStatus.CREATED -> drawable = ContextCompat.getDrawable(context, R.drawable.chip_background_blue)
            OrderStatus.CONFIRM -> drawable = ContextCompat.getDrawable(context, R.drawable.chip_background_green)
            OrderStatus.PENDING -> drawable = ContextCompat.getDrawable(context, R.drawable.chip_background_orange)
            OrderStatus.DELIVERED -> drawable = ContextCompat.getDrawable(context, R.drawable.chip_background_purpule)
            OrderStatus.PAYMENT -> drawable = ContextCompat.getDrawable(context, R.drawable.chip_background_red)
            OrderStatus.FINISH -> drawable = ContextCompat.getDrawable(context, R.drawable.chip_background_white)
        }
        return drawable
    }

    override fun getResult() {
        activity.showProgressBar()
        FirebaseUtil.getInstance().getCustomerDao().getOrders(lastNodeKey, ORDER_RECORD_FETCH_AT_TIME, object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val list = FirebaseUtil.getInstance()
                        .getListData(dataSnapshot, OrderModel::class.java)
                if (list.isNotEmpty()) {
                    list.reverse()
                    var shouldLoadMore: Boolean
                    val lastItem = list[list.size - 1]
                    lastItem.let {
                        lastNodeKey = lastItem.orderDateUpdated.toDouble()
                        shouldLoadMore = list.size >= ORDER_RECORD_FETCH_AT_TIME
                        if (shouldLoadMore) {
                            list.remove(lastItem)
                        }
                    }
                    activity.reloadNewData(list)
                    activity.isLoading = shouldLoadMore.not()
                }
                activity.dismissProgressBar()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                activity.dismissProgressBar()
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        activity.menuInflater.inflate(R.menu.inventory_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                activity.finish()
                return true
            }
            R.id.actionAdd -> {
                addOrderSelectCompany()
                return true
            }
        }
        return true
    }

    override fun onBackPressed() {
        activity.finish()
    }

    fun addOrderSelectCompany() {
        (activity as OrderListActivity).showCustomerListingActivity()
    }

    fun showInventoryListingActivity(custId: String, orderId: String) {
        (activity as OrderListActivity).showInventoryListingActivity(custId, orderId)
    }


    fun deleteOrder(orderModel: OrderModel, context: Context, pos: Int) {
        val alertDialogBuilderUserInput = AlertDialog.Builder(context)
        alertDialogBuilderUserInput
                .setTitle(context.getString(R.string.remove_order_item_title))
                .setMessage(context.getString(R.string.remove_order_item_message))
                .setCancelable(false)
                .setPositiveButton(context.getString(R.string.yes)
                ) { _, _ ->
                    FirebaseUtil.getInstance().getCustomerDao()
                            .removeOrder(orderModel, DatabaseReference.CompletionListener { databaseError, databaseReference ->
                                if (null == databaseError) {
                                    activity.removeAt(pos)
                                }
                            })
                }
                .setNegativeButton(
                        context.getString(R.string.no)
                ) { dialogBox, id -> dialogBox.cancel() }

        val alertDialog = alertDialogBuilderUserInput.create()
        alertDialog.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            AppConstants.LISTING_REQUEST_CODE // open order List activity
            -> updateOrder(data)
        }
    }

    private fun updateOrder(data: Intent?) {
        data?.let {
            var isOrderUpdated = data.getBooleanExtra(AppConstants.UPDATED, false)
            if (isOrderUpdated) {
                var position = data.getIntExtra(AppConstants.POSITION_IN_LIST, AppConstants.INVALID_ID)
                if (position != AppConstants.INVALID_ID) {
                    var orderId = data.getStringExtra(AppConstants.ORDER_ID)
                    orderId?.let {
                        activity.showProgressBar()
                        FirebaseUtil.getInstance().getCustomerDao().getOrderFromID(orderId, object : ValueEventListener {
                            override fun onCancelled(p0: DatabaseError) {
                                activity.dismissProgressBar()
                            }

                            override fun onDataChange(p0: DataSnapshot) {
                                activity.dismissProgressBar()
                                var model = p0.getValue(OrderModel::class.java)
                                model?.let {
                                    activity.updateElement(model, position)
                                }
                            }
                        })
                    }
                }
            }
        }
    }

    private fun showOrderStatusDialog(order: OrderModel, pos: Int) {
        val empty = arrayOf("No Selection",
                OrderStatus.PENDING.toString().capitalize(),
                OrderStatus.DELIVERED.toString().capitalize(),
                OrderStatus.PAYMENT.toString().capitalize(),
                OrderStatus.CONFIRM.toString().capitalize(),
                OrderStatus.CREATED.toString().capitalize(),
                OrderStatus.FINISH.toString().capitalize())

        val alertDialogBuilderUserInput = AlertDialog.Builder(activity)
        alertDialogBuilderUserInput
                .setCancelable(false)
                .setPositiveButton(activity.getString(R.string.ok)) { _, _ -> }
                .setNegativeButton(activity.getString(R.string.cancel)) { dialog, _ ->
                    dialog.dismiss()
                }
                .setSingleChoiceItems(empty, 0) { _, _ -> }
        val alertDialog = alertDialogBuilderUserInput.create()
        alertDialog.setCancelable(false)
        alertDialog.setCanceledOnTouchOutside(false)
        alertDialog.show()
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            if (alertDialog.listView.checkedItemPosition != 0) {
                var selected: String = alertDialog.listView.adapter.getItem(alertDialog.listView.checkedItemPosition) as String
                var status = selected.toUpperCase()
                var orderId = order.id ?: EMPTY_STRING
                FirebaseUtil.getInstance().getCustomerDao().updateOrderStatus(orderId, status, selected, CallBackListener {
                    if (it) {
                        order.orderStatus = status
                        order.orderStatusName = selected
                        activity.showSnackBarMessage(activity.getString(R.string.order_status_updated_successfully))
                        activity.updateElement(order, pos)
                        sendOrderStatusNotificationToCompany(order)
                    } else {
                        activity.showSnackBarMessage(activity.getString(R.string.failed_order_status_updated))
                    }
                    alertDialog.dismiss()
                })

            } else {
                alertDialog.dismiss()
            }
        }
    }

    private fun sendOrderStatusNotificationToCompany(order: OrderModel) {
        var map = HashMap<String, String>()
        map.put(NotificationUtil.BODY, Utils.getItemNames(order))
        map.put(NotificationUtil.ORDER_ID, order.id ?: EMPTY_STRING)
        map.put(NotificationUtil.CUSTOMER_ID, order.customerId ?: EMPTY_STRING)
        map.put(NotificationUtil.FLOW_ID, NotificationUtil.NOTIFICATION_FLOW)

        if (order.orderStatus?.equals(OrderStatus.DELIVERED.toString(), ignoreCase = true) == true
                || order.orderStatus?.equals(OrderStatus.PAYMENT.toString(), ignoreCase = true) == true) {
            FirebaseUtil.getInstance().getCustomerDao().getCustomerFromID(order.customerId
                    ?: EMPTY_STRING,
                    object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            var model = FirebaseUtil.getInstance().getClassData(dataSnapshot, CustomerModel::class.java)
                            model?.let {
                                CrashlaticsUtil.logInfo(CrashlaticsUtil.TAG_INFO, Gson().toJson(model))
                                model.notificationToken?.let { token ->
                                    if (order.orderStatus?.equals(OrderStatus.DELIVERED.toString(), ignoreCase = true) == true) {
                                        map.put(NotificationUtil.TITLE, NotificationUtil.ORDER_DELIVERED_TITLE)
                                        map.put(NotificationUtil.NOTIFICATION_TYPE, NotificationUtil.NotificationType.ORDER_DELIVERED_INDICATE_TO_COMPANY.toString())
                                        NotificationUtil.sendNotification(token, map, CallBackListener {})
                                    } else if (order.orderStatus?.equals(OrderStatus.PAYMENT.toString(), ignoreCase = true) == true) {
                                        map.put(NotificationUtil.TITLE, NotificationUtil.ORDER_PAYMENT_TITLE.plus(order.invoiceNumber
                                                ?: EMPTY_STRING))
                                        map.put(NotificationUtil.NOTIFICATION_TYPE, NotificationUtil.NotificationType.ORDER_PAYMENT_INDICATE_TO_COMPANY.toString())
                                        NotificationUtil.sendNotification(token, map, CallBackListener {})
                                    }
                                }
                            }
                        }

                        override fun onCancelled(databaseError: DatabaseError) {}
                    })
        }


    }

}

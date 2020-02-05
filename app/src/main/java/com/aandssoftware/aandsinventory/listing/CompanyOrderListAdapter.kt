package com.aandssoftware.aandsinventory.listing

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.view.*
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
import com.aandssoftware.aandsinventory.ui.activity.CompanyOrderListActivity
import com.aandssoftware.aandsinventory.ui.activity.ListingActivity
import com.aandssoftware.aandsinventory.ui.adapters.BaseAdapter.BaseViewHolder
import com.aandssoftware.aandsinventory.utilities.AppConstants
import com.aandssoftware.aandsinventory.utilities.AppConstants.Companion.EMPTY_STRING
import com.aandssoftware.aandsinventory.utilities.SharedPrefsUtils
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.order_item.view.*
import java.io.Serializable

class CompanyOrderListAdapter(private val activity: ListingActivity) : ListingOperations {

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
            cardView.setOnClickListener { showOrderInventoryActivity(activity, (itemView.tag as OrderModel).id) }
        }
    }

    private fun showOrderInventoryActivity(activity: Activity, orderId: String?) {
        Navigator.openOrderDetailsScreen(activity, orderId!!)
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
        holder.tvFinalAmount.text = Utils.isEmptyInt(mItem.finalBillAmount, "-")
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
        FirebaseUtil.getInstance().getCustomerDao().getCompanyOrders(getLoginCustomerId(), object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val list = FirebaseUtil.getInstance()
                        .getListData(dataSnapshot, OrderModel::class.java)
                if (list.isNotEmpty()) {
                    activity.loadData(list)
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

    private fun addOrderSelectCompany() {
        (activity as CompanyOrderListActivity).showInventoryListingActivity(getLoginCustomerId(), EMPTY_STRING)
    }

    fun deleteOrder(orderModel: OrderModel, context: Context, pos: Int) {
        val alertDialogBuilderUserInput = AlertDialog.Builder(context)
        alertDialogBuilderUserInput
                .setTitle(context.getString(R.string.remove_order_item_title))
                .setMessage(context.getString(R.string.remove_order_item_message))
                .setCancelable(false)
                .setPositiveButton(context.getString(R.string.yes)
                ) { dialogBox, id ->
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
            AppConstants.LISTING_REQUEST_CODE // open order List activity ,
            -> getResult()
        }
    }

    private fun getLoginCustomerId(): String {
        var customerId: String = "";
        val user = SharedPrefsUtils.getUserPreference(activity, SharedPrefsUtils.CURRENT_USER)
        user?.let {
            customerId = user.id
        }
        return customerId
    }
}

package com.aandssoftware.aandsinventory.listing

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import com.aandssoftware.aandsinventory.R
import com.aandssoftware.aandsinventory.common.Navigator
import com.aandssoftware.aandsinventory.firebase.FirebaseUtil
import com.aandssoftware.aandsinventory.models.CustomerModel
import com.aandssoftware.aandsinventory.models.ViewMode
import com.aandssoftware.aandsinventory.ui.activity.ListingActivity
import com.aandssoftware.aandsinventory.ui.activity.OrderListActivity
import com.aandssoftware.aandsinventory.ui.adapters.BaseAdapter.BaseViewHolder
import com.aandssoftware.aandsinventory.utilities.AppConstants
import com.aandssoftware.aandsinventory.utilities.AppConstants.Companion.RELOAD_LIST_RESULT_CODE
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_add_customer.*
import kotlinx.android.synthetic.main.customer_item.view.*
import java.io.Serializable
import java.util.*

class CustomerListAdapter(private val activity: ListingActivity) : ListingOperations {

    companion object {
        val CUSTOMER_ID = "customer_id"
    }

    private val isOrderSelectionCall: Boolean
        get() {
            val callingActivity = activity.callingActivity
            return callingActivity != null && callingActivity.className
                    .equals(OrderListActivity::class.java.name, ignoreCase = true)
        }

    inner class CustomerViewHolder(itemView: View) : BaseViewHolder(itemView) {

        var imgCustomerItemEdit: ImageView = itemView.imgCustomerItemEdit
        var imgCustomerItemDelete: ImageView = itemView.imgCustomerItemDelete
        var imgCustomerItemLogo: ImageView = itemView.imgCustomerItemLogo
        var tvCustomerName: TextView = itemView.tvCustomerName
        var tvContactNameAndNumber: TextView = itemView.tvContactNameAndNumber
        var tvCustomerGstNumber: TextView = itemView.tvCustomerGstNumber
        var cardView: CardView = itemView.cardView

        init {
            imgCustomerItemDelete.setOnClickListener {
                var pos: Int = itemView.getTag(R.string.tag) as Int
                deleteCustomer(itemView.tag as CustomerModel, itemView.context, pos)
            }
            imgCustomerItemEdit.setOnClickListener {
                openCustomerScreen((itemView.tag as CustomerModel).id,
                        (itemView.tag as CustomerModel).customerID, ViewMode.UPDATE.ordinal)
            }
        }
    }

    override fun getActivityLayoutId(): Int {
        return R.layout.activity_listing
    }


    override fun getBaseViewHolder(viewGroup: ViewGroup, i: Int): BaseViewHolder {
        return CustomerViewHolder(LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.customer_item, viewGroup, false))
    }

    override fun onBindSearchViewHolder(baseHolder: BaseViewHolder, position: Int, item: Serializable) {
        val holder = baseHolder as CustomerViewHolder
        val mItem = item as CustomerModel
        holder.tvCustomerName.text = mItem.customerName
        holder.tvContactNameAndNumber.text = mItem.contactPerson + " " + mItem
                .contactPersonNumber
        holder.tvCustomerGstNumber.text = mItem.customerGstNumber
        if (mItem.imagePath != null) {
            if (mItem.imagePath.contains(AppConstants.HTTP, ignoreCase = true)) {
                var uri: Uri = Uri.parse(mItem.imagePath)
                Glide.with(activity)
                        .load(uri)
                        .placeholder(android.R.drawable.ic_menu_gallery)
                        .crossFade()
                        .into(holder.imgCustomerItemLogo)
            } else {
                val bitmap = BitmapFactory.decodeFile(mItem.imagePath)
                if (null != holder.imgCustomerItemLogo && null != bitmap) {
                    holder.imgCustomerItemLogo.setImageBitmap(bitmap)
                }
            }
        }
        holder.cardView.setOnClickListener { v ->
            if (isOrderSelectionCall) {
                setResult(mItem)
            } else {
                openCustomerScreen(item.id, item.customerID,
                        ViewMode.VIEW_ONLY.ordinal)
            }
        }
    }

    private fun setResult(mItem: CustomerModel) {
        val intent = Intent()
        intent.putExtra(CustomerListAdapter.CUSTOMER_ID,
                if (!mItem.id.isEmpty()) mItem.id else mItem.customerID)
        activity.setResult(ListingActivity.SELECTED, intent)
        activity.finish()
    }

    override fun getTitle(): String {
        return activity.getString(R.string.customer_item)
    }

    override fun getResult() {
        activity.showProgressBar()
        FirebaseUtil.getInstance().getCustomerDao().getAllCustomers(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val result = ArrayList<CustomerModel>()
                if (null != dataSnapshot.value) {
                    for (children in dataSnapshot.children) {
                        val model = children.getValue(CustomerModel::class.java)
                        if (null != model) {
                            result.add(model)
                        }
                    }
                    if (!result.isEmpty()) {
                        activity.loadData(result)
                    }
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
                openCustomerScreen(AppConstants.EMPTY_STRING, AppConstants.EMPTY_STRING,
                        ViewMode.ADD.ordinal)
                return true
            }
        }
        return true
    }

    override fun onBackPressed() {
        activity.finish()
    }

    fun openCustomerScreen(customerId: String, numericcCustomerId: String, viewMode: Int) {
        Navigator.openCustomerScreen(activity, customerId, numericcCustomerId, viewMode, title)
    }

    fun deleteCustomer(customerModel: CustomerModel, context: Context, pos: Int) {
        val alertDialogBuilderUserInput = AlertDialog.Builder(context)
        alertDialogBuilderUserInput
                .setTitle(context.getString(R.string.remove_customer_item_title))
                .setMessage(context.getString(R.string.remove_customer_item_message))
                .setCancelable(false)
                .setPositiveButton(context.getString(R.string.yes)
                ) { _, _ ->
                    FirebaseUtil.getInstance().getCustomerDao().removeCustomer(customerModel,
                            DatabaseReference.CompletionListener { databaseError, _ ->
                                if (databaseError == null) {
                                    activity.removeAt(pos)

                                }
                            })
                }
                .setNegativeButton(
                        context.getString(R.string.no)
                ) { dialogBox, _ -> dialogBox.cancel() }

        val alertDialog = alertDialogBuilderUserInput.create()
        alertDialog.show()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            AppConstants.GET_CUSTOMER_UPDATE_REQUEST_CODE // when we open add customer screen
            -> when (resultCode) {
                RELOAD_LIST_RESULT_CODE ->
                    getResult()
            }
        }
    }

}

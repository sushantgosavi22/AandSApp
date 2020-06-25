package com.aandssoftware.aandsinventory.listing

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.view.*
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import com.aandssoftware.aandsinventory.R
import com.aandssoftware.aandsinventory.common.Navigator
import com.aandssoftware.aandsinventory.common.Utils
import com.aandssoftware.aandsinventory.firebase.FirebaseUtil
import com.aandssoftware.aandsinventory.models.*
import com.aandssoftware.aandsinventory.pdfgenarator.PdfGenerator
import com.aandssoftware.aandsinventory.ui.activity.ListingActivity
import com.aandssoftware.aandsinventory.ui.activity.OrderDetailsActivity
import com.aandssoftware.aandsinventory.ui.adapters.BaseAdapter.BaseViewHolder
import com.aandssoftware.aandsinventory.utilities.AppConstants
import com.aandssoftware.aandsinventory.utilities.AppConstants.Companion.DOUBLE_DEFAULT_ZERO
import com.aandssoftware.aandsinventory.utilities.AppConstants.Companion.EMPTY_STRING
import com.aandssoftware.aandsinventory.utilities.AppConstants.Companion.RELOAD_LIST_RESULT_CODE
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_order.*
import kotlinx.android.synthetic.main.inventory_item.view.*
import java.io.Serializable
import java.util.*

class OrderDetailsListAdapter(private val activity: ListingActivity) : ListingOperations {

    var orderModel: OrderModel? = null
    private val orderId: String?
        get() = activity.intent
                .getStringExtra(AppConstants.ORDER_ID)

    private val inventoryType: Int
        get() = activity.intent
                .getIntExtra(AppConstants.LISTING_TYPE, ListType.LIST_TYPE_MATERIAL.ordinal)

    inner class InventoryViewHolder(itemView: View) : BaseViewHolder(itemView) {
        var imgInventoryItemLogo: ImageView
        var inventoryItemName: TextView
        var inventoryItemQuantity: TextView
        var inventoryItemDetails: TextView
        var imgInventoryItemDelete: ImageView
        var cardView: CardView
        var llButtons: LinearLayout = itemView.llButtons

        init {
            imgInventoryItemLogo = itemView.imgInventoryItemLogo;
            inventoryItemName = itemView.inventoryItemName;
            inventoryItemQuantity = itemView.inventoryItemQuantity;
            inventoryItemDetails = itemView.inventoryItemDetails;
            imgInventoryItemDelete = itemView.imgInventoryItemDelete;
            cardView = itemView.cardView

            itemView.imgInventoryItemHistory.visibility = View.GONE
            itemView.imgInventoryItemEdit.visibility = View.GONE
            itemView.imgInventoryItemDelete.visibility = View.GONE

            itemView.imgInventoryItemDelete.setOnClickListener {
                var pos: Int = itemView.getTag(R.string.tag) as Int
                deleteInventory(itemView.tag as InventoryItem, itemView.context, pos)
            }
            llButtons.visibility = View.VISIBLE
        }
    }

    override fun getActivityLayoutId(): Int {
        return R.layout.activity_order
    }


    override fun getBaseViewHolder(viewGroup: ViewGroup, i: Int): BaseViewHolder {
        return InventoryViewHolder(LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.inventory_item, viewGroup, false))
    }

    override fun onBindSearchViewHolder(baseHolder: BaseViewHolder, position: Int, item: Serializable) {
        val holder = baseHolder as InventoryViewHolder
        val mItem = item as InventoryItem
        holder.inventoryItemName.text = mItem.inventoryItemName
        var priceForItem = if (mItem.finalBillAmount != DOUBLE_DEFAULT_ZERO) {
            " = " + Utils.currencyLocale(mItem.finalBillAmount.toDouble())
        } else {
            EMPTY_STRING
        }
        holder.inventoryItemQuantity.text = mItem.itemQuantity.plus(" ").plus(mItem.itemQuantityUnit).plus(priceForItem)
        holder.inventoryItemDetails.text = EMPTY_STRING.plus(mItem.inventoryItemBrandName).plus(" ").plus(mItem
                .inventoryItemModelName).plus(" ").plus(mItem.inventoryItemColor)
                .plus(" ").plus(mItem.inventoryItemSize)

        mItem.inventoryItemImagePath?.let {
            if (it.isNotEmpty()) {
                var firstImage = it.values.toMutableList().first()
                var uri: Uri = Uri.parse(firstImage)
                Glide.with(activity)
                        .load(uri)
                        .placeholder(android.R.drawable.ic_menu_gallery)
                        .crossFade()
                        .into(holder.imgInventoryItemLogo)
            }
        }
        holder.cardView.setOnClickListener {
            var pos: Int = baseHolder.itemView.getTag(R.string.tag) as Int
            performClick(mItem.id, pos)
        }

        orderModel?.orderStatus?.let {
            if (OrderStatus.valueOf(it) == OrderStatus.CREATED || Utils.isAdminUser(activity)) {
                holder.imgInventoryItemDelete.visibility = View.VISIBLE
            }
        }
    }

    private fun performClick(id: String?, pos: Int) {
        id?.let {
            Navigator.openInventoryScreen(activity, it,
                    ViewMode.VIEW_ONLY.ordinal, inventoryType,
                    title, orderId!!, pos)
        }
    }

    override fun getTitle(): String {
        return activity.getString(R.string.order_details)
    }


    override fun getResult() {
        activity.showProgressBar()
        FirebaseUtil.getInstance().getCustomerDao().getOrderFromID(orderId!!,
                object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        orderModel = FirebaseUtil.getInstance()
                                .getClassData(dataSnapshot, OrderModel::class.java)
                        orderModel?.let { orderModel ->
                            if (orderModel.orderItems.isNotEmpty()) {
                                val inventoryItems = ArrayList(
                                        orderModel.orderItems.values)
                                activity.loadData(inventoryItems)
                            }
                            (activity as OrderDetailsActivity).setValues(orderModel)
                            activity.dismissProgressBar()
                        }
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        activity.dismissProgressBar()
                    }
                })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        activity.menuInflater.inflate(R.menu.order_details_menu, menu)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            val menuItemAdd = menu.findItem(R.id.actionAdd).setVisible(true)
            var actionSave = menu.findItem(R.id.actionSave)
            actionSave.isVisible = Utils.isAdminUser(activity)
            (activity as OrderDetailsActivity).checkAndDisableOrder(menuItemAdd)
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                activity.finish()
                return true
            }
            R.id.actionAdd -> {
                actionAdd()
                return true
            }
            R.id.actionSave -> {
                onActionConfirmOrder()
                return true
            }
        }
        return true
    }

    fun actionAdd() {
        orderId?.let {
            activity.showProgressBar()
            FirebaseUtil.getInstance().getCustomerDao().getOrderFromID(it, object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                    activity.dismissProgressBar()
                }

                override fun onDataChange(p0: DataSnapshot) {
                    var model = p0.getValue(OrderModel::class.java)
                    model?.customerModel?.id?.let { customerId ->
                        showInventoryListingActivity(customerId, orderId)
                    }
                    activity.dismissProgressBar()
                }
            })
        }
    }

    private fun onActionConfirmOrder() {
        activity.showProgressBar()
        FirebaseUtil.getInstance().getCustomerDao().getOrderFromID(orderId!!,
                object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val orderModel = FirebaseUtil.getInstance()
                                .getClassData(dataSnapshot, OrderModel::class.java)
                        if (orderModel != null && null != orderModel.orderItems && !orderModel
                                        .orderItems.isEmpty()) {
                            val filePath = PdfGenerator(activity).generatePdf(orderModel)
                        }
                        activity.dismissProgressBar()
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        activity.dismissProgressBar()
                    }
                })
    }

    override fun onBackPressed() {
        activity.finish()
    }

    fun deleteInventory(inventoryItem: InventoryItem, context: Context, pos: Int) {
        val alertDialogBuilderUserInput = AlertDialog.Builder(context)
        alertDialogBuilderUserInput
                .setTitle(context.getString(R.string.remove_inventory_item_title))
                .setMessage(context.getString(R.string.remove_inventory_item_message))
                .setCancelable(false)
                .setPositiveButton(context.getString(R.string.yes)) { _, _ ->
                    FirebaseUtil.getInstance().getCustomerDao().removeInventoryFromOrder(orderId, inventoryItem,
                            DatabaseReference.CompletionListener { databaseError, _ ->
                                if (null == databaseError) {
                                    activity.removeAt(pos)
                                    getResult()
                                }
                            })
                }
                .setNegativeButton(context.getString(R.string.no))
                { dialogBox, _ -> dialogBox.cancel() }

        val alertDialog = alertDialogBuilderUserInput.create()
        alertDialog.show()
    }


    fun showInventoryListingActivity(customerId: String, orderId: String?) {
        val intent = Intent(activity, ListingActivity::class.java)
        intent.putExtra(AppConstants.LISTING_TYPE, ListType.LIST_TYPE_MATERIAL.ordinal)
        intent.putExtra(CustomerListAdapter.CUSTOMER_ID, customerId)
        intent.putExtra(AppConstants.ORDER_ID, orderId)
        activity.startActivityForResult(intent, AppConstants.LISTING_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            AppConstants.GET_CUSTOMER_UPDATE_REQUEST_CODE // when we open add customer screen
            -> when (resultCode) {
                RELOAD_LIST_RESULT_CODE -> getResult()
            }
        }
    }

}

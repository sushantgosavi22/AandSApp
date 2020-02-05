package com.aandssoftware.aandsinventory.listing

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import com.aandssoftware.aandsinventory.R
import com.aandssoftware.aandsinventory.common.Navigator
import com.aandssoftware.aandsinventory.firebase.FirebaseUtil
import com.aandssoftware.aandsinventory.models.CustomerModel
import com.aandssoftware.aandsinventory.models.InventoryItem
import com.aandssoftware.aandsinventory.models.OrderModel
import com.aandssoftware.aandsinventory.models.ViewMode
import com.aandssoftware.aandsinventory.pdfgenarator.PdfGenerator
import com.aandssoftware.aandsinventory.ui.activity.ListingActivity
import com.aandssoftware.aandsinventory.ui.activity.OrderDetailsActivity
import com.aandssoftware.aandsinventory.ui.adapters.BaseAdapter.BaseViewHolder
import com.aandssoftware.aandsinventory.utilities.AppConstants
import com.aandssoftware.aandsinventory.utilities.AppConstants.Companion.EMPTY_STRING
import com.aandssoftware.aandsinventory.utilities.AppConstants.Companion.RELOAD_LIST_RESULT_CODE
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.inventory_item.view.*
import java.io.Serializable
import java.util.*

class OrderDetailsListAdapter(private val activity: ListingActivity) : ListingOperations {

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
        var cardView: CardView

        init {
            imgInventoryItemLogo = itemView.imgInventoryItemLogo;
            inventoryItemName = itemView.inventoryItemName;
            inventoryItemQuantity = itemView.inventoryItemQuantity;
            inventoryItemDetails = itemView.inventoryItemDetails;
            cardView = itemView.cardView;
            itemView.imgInventoryItemHistory.setOnClickListener {
                showInventoryHistory((itemView.tag as InventoryItem).id)
            }
            itemView.imgInventoryItemDelete.setOnClickListener {
                var pos: Int = itemView.getTag(R.string.tag) as Int
                deleteInventory(itemView.tag as InventoryItem, itemView.context, pos)
            }
            itemView.imgInventoryItemEdit.setOnClickListener {
                val item = itemView.tag as InventoryItem
                if (null != item) {
                    var pos: Int = itemView.getTag(R.string.tag) as Int
                    showAddInventoryItemFragment(item.id, ViewMode.UPDATE.ordinal, pos)
                }
            }
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
        holder.inventoryItemQuantity.text = mItem.itemQuantity.plus(" ").plus(mItem.itemQuantityUnit)
        holder.inventoryItemDetails.text = EMPTY_STRING.plus(mItem.inventoryItemBrandName).plus(" ").plus(mItem
                .inventoryItemModelName).plus(" ").plus(mItem.inventoryItemColor)
                .plus(" ").plus(mItem.inventoryItemSize)
        if (mItem.inventoryItemImagePath != null) {
            val bitmap = BitmapFactory.decodeFile(mItem.inventoryItemImagePath)
            if (null != holder.imgInventoryItemLogo && null != bitmap) {
                holder.imgInventoryItemLogo.setImageBitmap(bitmap)
            }
        }
        holder.cardView.setOnClickListener {
            var pos: Int = baseHolder.itemView.getTag(R.string.tag) as Int
            performClick(mItem.id, pos)
        }
    }

    private fun performClick(id: String?, pos: Int) {
        Navigator.openInventoryScreen(activity, id!!, ViewMode.VIEW_ONLY.ordinal, inventoryType,
                title, pos)
    }

    private fun showInventoryHistory(id: String?) {
        val intent = Intent(activity, ListingActivity::class.java)
        intent.putExtra(AppConstants.LISTING_TYPE, ListType.LIST_TYPE_INVENTORY_HISTORY.ordinal)
        intent.putExtra(AppConstants.INVENTORY_ID, id)
        activity.startActivityForResult(intent, AppConstants.LISTING_REQUEST_CODE)
    }

    override fun getTitle(): String {
        return activity.getString(R.string.order_details)
    }


    override fun getResult() {
        activity.showProgressBar()
        FirebaseUtil.getInstance().getCustomerDao().getOrderFromID(orderId!!,
                object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val orderModel = FirebaseUtil.getInstance()
                                .getClassData(dataSnapshot, OrderModel::class.java)
                        if (null != orderModel) {
                            if (orderModel.orderItems.isNotEmpty()) {
                                val inventoryItems = ArrayList(
                                        orderModel.orderItems.values)
                                activity.loadData(inventoryItems)
                            }
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
            menu.findItem(R.id.actionSave).isVisible = true
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

    private fun actionAdd() {
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
        intent.putExtra(AppConstants.LISTING_TYPE, ListType.LIST_TYPE_INVENTORY.ordinal)
        intent.putExtra(CustomerListAdapter.CUSTOMER_ID, customerId)
        intent.putExtra(AppConstants.ORDER_ID, orderId)
        activity.startActivityForResult(intent, AppConstants.LISTING_REQUEST_CODE)
    }


    fun showAddInventoryItemFragment(id: String?, viewMode: Int, pos: Int) {
        Navigator.openInventoryScreen(activity, id!!, viewMode, inventoryType, title, pos)
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
package com.aandssoftware.aandsinventory.listing

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Typeface
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
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.inventory_item.view.*
import java.io.Serializable
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class CompanyDiscountedItemsListAdapter(private val activity: ListingActivity) : ListingOperations {

    private val customerId: String?
        get() = activity.intent
                .getStringExtra(AppConstants.FIRE_BASE_CUSTOMER_ID)

    inner class InventoryViewHolder(itemView: View) : BaseViewHolder(itemView) {
        var imgInventoryItemLogo: ImageView
        var inventoryItemName: TextView
        var inventoryItemQuantity: TextView
        var inventoryItemDetails: TextView
        var cardView: CardView
        var llButtons: LinearLayout = itemView.llButtons

        init {
            imgInventoryItemLogo = itemView.imgInventoryItemLogo;
            inventoryItemName = itemView.inventoryItemName;
            inventoryItemQuantity = itemView.inventoryItemQuantity
            inventoryItemDetails = itemView.inventoryItemDetails
            inventoryItemDetails.setTypeface(null, Typeface.BOLD);
            cardView = itemView.cardView

            itemView.imgInventoryItemHistory.visibility = View.GONE
            itemView.imgInventoryItemEdit.visibility = View.GONE

            itemView.imgInventoryItemDelete.setOnClickListener {
                var pos: Int = itemView.getTag(R.string.tag) as Int
                deleteInventory(itemView.tag as InventoryItem, itemView.context, pos)
            }
            llButtons.visibility = View.VISIBLE
        }
    }

    override fun getActivityLayoutId(): Int {
        return R.layout.activity_add_customer
    }


    override fun getBaseViewHolder(viewGroup: ViewGroup, i: Int): BaseViewHolder {
        return InventoryViewHolder(LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.inventory_item, viewGroup, false))
    }

    override fun onBindSearchViewHolder(baseHolder: BaseViewHolder, position: Int, item: Serializable) {
        val holder = baseHolder as InventoryViewHolder
        val mItem = item as InventoryItem
        holder.inventoryItemName.text = mItem.inventoryItemName
        holder.inventoryItemQuantity.text = EMPTY_STRING.plus(mItem.inventoryItemBrandName).plus(" ").plus(mItem
                .inventoryItemModelName).plus(" ").plus(mItem.inventoryItemColor)
                .plus(" ").plus(mItem.inventoryItemSize)
        holder.inventoryItemDetails.text = Utils.currencyLocale(item.discountRateForCompany)

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
    }

    private fun performClick(id: String?, pos: Int) {
        id?.let {

        }
    }

    override fun getTitle(): String {
        return activity.intent.getStringExtra(AppConstants.TITLE)
    }


    override fun getResult() {
        if (Utils.isAdminUser(activity)) {
            customerId?.let {
                FirebaseUtil.getInstance().getCustomerDao().getCustomerFromID(it, object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {}
                    override fun onDataChange(p0: DataSnapshot) {
                        var listOfInventory = ArrayList<InventoryItem>()
                        var customerModel = p0.getValue(CustomerModel::class.java)
                        customerModel?.let {
                            customerModel.discountedItems?.let { map ->
                                for (value in map.keys) {
                                    FirebaseUtil.getInstance().getInventoryDao().getMaterialInventoryItemFromId(value, object : ValueEventListener {
                                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                                            val inventoryItem = FirebaseUtil.getInstance().getClassData(dataSnapshot, InventoryItem::class.java)
                                            inventoryItem?.let {
                                                var model = it
                                                var discount = map.get(value)
                                                var defaultVal: Double = it.minimumSellingPrice?.toDouble()
                                                        ?: 0.0
                                                model.discountRateForCompany = Utils.isEmpty(discount, defaultVal)
                                                listOfInventory.add(it)
                                                activity.addElement(model, 0)
                                            }
                                        }

                                        override fun onCancelled(p0: DatabaseError) {}
                                    })
                                }
                            }
                        }
                    }
                })
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return true
    }


    override fun onBackPressed() {
        activity.finish()
    }

    fun deleteInventory(inventoryItem: InventoryItem, context: Context, pos: Int) {
        val alertDialogBuilderUserInput = AlertDialog.Builder(context)
        alertDialogBuilderUserInput
                .setTitle(context.getString(R.string.remove_discount_title))
                .setMessage(context.getString(R.string.remove_discount_message))
                .setCancelable(false)
                .setPositiveButton(context.getString(R.string.yes)) { _, _ ->
                    customerId?.let {
                        inventoryItem.id?.let { it1 ->
                            FirebaseUtil.getInstance().getCustomerDao().removeCustomerDiscountForItem(it, it1,
                                    DatabaseReference.CompletionListener { databaseError, _ ->
                                        if (null == databaseError) {
                                            activity.removeAt(pos)
                                        }
                                    })
                        }
                    }
                }
                .setNegativeButton(context.getString(R.string.no))
                { dialogBox, _ -> dialogBox.cancel() }

        val alertDialog = alertDialogBuilderUserInput.create()
        alertDialog.show()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            //AppConstants.GET_CUSTOMER_UPDATE_REQUEST_CODE // when we open add customer screen
        }
    }

}

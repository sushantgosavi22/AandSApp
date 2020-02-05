package com.aandssoftware.aandsinventory.listing

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.view.*
import android.widget.ImageView
import android.widget.SearchView
import android.widget.SearchView.OnQueryTextListener
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatTextView
import androidx.cardview.widget.CardView
import com.aandssoftware.aandsinventory.R
import com.aandssoftware.aandsinventory.common.Navigator
import com.aandssoftware.aandsinventory.firebase.FirebaseUtil
import com.aandssoftware.aandsinventory.models.InventoryItem
import com.aandssoftware.aandsinventory.models.ViewMode
import com.aandssoftware.aandsinventory.ui.activity.ListingActivity
import com.aandssoftware.aandsinventory.ui.activity.OrderDetailsActivity
import com.aandssoftware.aandsinventory.ui.activity.OrderListActivity
import com.aandssoftware.aandsinventory.ui.adapters.BaseAdapter.BaseViewHolder
import com.aandssoftware.aandsinventory.utilities.AppConstants
import com.aandssoftware.aandsinventory.utilities.AppConstants.Companion.EMPTY_STRING
import com.aandssoftware.aandsinventory.utilities.AppConstants.Companion.ORDER_RELOAD_LIST_RESULT_CODE
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


class InventoryListAdapter(private val activity: ListingActivity) : ListingOperations {

    private var search: SearchView? = null
    var query: String = EMPTY_STRING
    private val handler: Handler = Handler(Looper.getMainLooper())
    private val workRunnable: Runnable

    companion object {
        const val INVENTORY_ID = "inventory_id"
        const val RECORD_FETCH_AT_TIME = 11
        const val SEARCH_DELAY = 500
    }

    private val isOrderSelectionCall: Boolean
        get() {
            val callingActivity = activity.callingActivity
            return callingActivity != null && callingActivity.className
                    .equals(OrderListActivity::class.java.name, ignoreCase = true) || callingActivity!!.className
                    .equals(OrderDetailsActivity::class.java.name, ignoreCase = true)
        }

    internal var lastNodeKey: Double = 0.0

    private val inventoryType: Int
        get() = activity.intent
                .getIntExtra(AppConstants.LISTING_TYPE, ListType.LIST_TYPE_MATERIAL.ordinal)

    private val orderId: String?
        get() = activity.intent
                .getStringExtra(AppConstants.ORDER_ID)

    inner class InventoryViewHolder(itemView: View) : BaseViewHolder(itemView) {
        var imgInventoryItemHistory: ImageView = itemView.imgInventoryItemHistory
        var imgInventoryItemEdit: ImageView = itemView.imgInventoryItemEdit
        var imgInventoryItemDelete: ImageView = itemView.imgInventoryItemDelete
        var imgInventoryItemLogo: ImageView = itemView.imgInventoryItemLogo
        var inventoryItemName: AppCompatTextView = itemView.inventoryItemName
        var inventoryItemQuantity: AppCompatTextView = itemView.inventoryItemQuantity
        var inventoryItemDetails: AppCompatTextView = itemView.inventoryItemDetails
        var cardView: CardView = itemView.cardView

        init {
            imgInventoryItemHistory.setOnClickListener {
                showInventoryHistory((itemView.tag as InventoryItem).id)
            }
            imgInventoryItemDelete.setOnClickListener {
                var pos: Int = itemView.getTag(R.string.tag) as Int
                deleteInventory(itemView.tag as InventoryItem, itemView.context, pos)
            }
            imgInventoryItemEdit.setOnClickListener {
                var pos: Int = itemView.getTag(R.string.tag) as Int
                performClick(itemView.tag as InventoryItem, ViewMode.UPDATE.ordinal, pos)
            }
        }
    }

    init {
        workRunnable = Runnable { this.performSearch() }
    }

    private fun performSearch() {
        search?.let {
            if (query.isNotEmpty() && it.query.toString().equals(query, ignoreCase = true)) {
                activity.showProgressBar()
                FirebaseUtil.getInstance().getInventoryDao().getAllInventoryItemAtOnce(inventoryType,
                        object : ValueEventListener {
                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                val list = FirebaseUtil.getInstance()
                                        .getListData(dataSnapshot, InventoryItem::class.java)
                                val searchedList = ArrayList<InventoryItem>()
                                if (list.isNotEmpty()) {
                                    for (item in list) {
                                        val searchable = EMPTY_STRING.plus(item.inventoryItemName).plus(item.inventoryItemBrandName).plus(item.inventoryItemModelName).plus(item.description)
                                        if (searchable.toLowerCase().contains(query.toLowerCase())) {
                                            searchedList.add(item)
                                        }
                                    }
                                    activity.loadData(searchedList)
                                }
                                activity.dismissProgressBar()
                            }

                            override fun onCancelled(databaseError: DatabaseError) {
                                activity.dismissProgressBar()
                            }
                        })
            }
        }
    }

    override fun getActivityLayoutId(): Int {
        return R.layout.activity_listing
    }


    override fun getBaseViewHolder(viewGroup: ViewGroup, i: Int): BaseViewHolder {
        return InventoryViewHolder(LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.inventory_item, viewGroup, false))
    }

    override fun onBindSearchViewHolder(baseHolder: BaseViewHolder, position: Int, item: Serializable) {
        val holder = baseHolder as InventoryViewHolder
        val mItem = item as InventoryItem
        holder.inventoryItemName.text = mItem.inventoryItemName
        holder.inventoryItemQuantity.text = EMPTY_STRING.plus(mItem.itemQuantity).plus(" ").plus(mItem.itemQuantityUnit)
        holder.inventoryItemDetails.text = EMPTY_STRING.plus(mItem.inventoryItemBrandName).plus(" ").plus(mItem
                .inventoryItemModelName).plus(" ").plus(mItem.inventoryItemColor).plus(" ").plus(mItem.inventoryItemSize)

        mItem.inventoryItemImagePath?.let {
            if (it.contains(AppConstants.HTTP, ignoreCase = true)) {
                var uri: Uri = Uri.parse(mItem.inventoryItemImagePath)
                Glide.with(activity)
                        .load(uri)
                        .placeholder(android.R.drawable.ic_menu_gallery)
                        .crossFade()
                        .into(holder.imgInventoryItemLogo)
            } else {
                val bitmap = BitmapFactory.decodeFile(mItem.inventoryItemImagePath)
                bitmap?.let {
                    holder.imgInventoryItemLogo.setImageBitmap(bitmap)
                }
            }
        }
        holder.cardView.setOnClickListener {
            var pos: Int = baseHolder.itemView.getTag(R.string.tag) as Int
            performClick(mItem, ViewMode.VIEW_ONLY.ordinal, pos)
        }
    }

    private fun performClick(mItem: InventoryItem, viewMode: Int, pos: Int) {
        if (isOrderSelectionCall) {
            addOrderQuantity(mItem, pos)
        } else {
            showAddInventoryItemFragment(mItem.id, viewMode, pos)
        }
    }

    private fun showInventoryHistory(id: String?) {
        val intent = Intent(activity, ListingActivity::class.java)
        intent.putExtra(AppConstants.LISTING_TYPE, ListType.LIST_TYPE_INVENTORY_HISTORY.ordinal)
        intent.putExtra(AppConstants.INVENTORY_ID, id)
        activity.startActivityForResult(intent, AppConstants.LISTING_REQUEST_CODE)
    }

    override fun getTitle(): String = if (inventoryType == ListType.LIST_TYPE_INVENTORY.ordinal) {
        activity.getString(R.string.inventory_item)
    } else {
        activity.getString(R.string.material)
    }

    override fun getResult() {
        activity.showProgressBar()
        val inventoryType = inventoryType
        if (inventoryType == ListType.LIST_TYPE_INVENTORY.ordinal) {
            FirebaseUtil.getInstance().getInventoryDao().getAllInventoryItemAtOnce(lastNodeKey, RECORD_FETCH_AT_TIME,
                    object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            val list = FirebaseUtil.getInstance()
                                    .getListData(dataSnapshot, InventoryItem::class.java)
                            if (list.isNotEmpty()) {
                                list.reverse()
                                var shouldLoadMore: Boolean
                                val lastItem = list[list.size - 1]
                                lastItem.let {
                                    lastNodeKey = lastItem.inventoryItemLastUpdatedDate.toDouble()
                                    shouldLoadMore = list.size >= RECORD_FETCH_AT_TIME
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
        } else {
            FirebaseUtil.getInstance().getInventoryDao().getMaterialRecords(lastNodeKey, RECORD_FETCH_AT_TIME,
                    object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            val list = FirebaseUtil.getInstance()
                                    .getListData(dataSnapshot, InventoryItem::class.java)
                            if (list.isNotEmpty()) {
                                list.reverse()
                                var shouldLoadMore: Boolean
                                val lastItem = list[list.size - 1]
                                lastItem.let {
                                    lastNodeKey = lastItem.inventoryItemLastUpdatedDate.toDouble()
                                    shouldLoadMore = list.size >= RECORD_FETCH_AT_TIME
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
    }

    fun reloadAdapter(currentQuery: String) {
        query = currentQuery
        handler.removeCallbacks(workRunnable)
        if (currentQuery.isNotEmpty()) {
            handler.postDelayed(workRunnable, SEARCH_DELAY.toLong())
        } else {
            handleEmptyQuery()
        }
    }

    private fun handleEmptyQuery() {
        lastNodeKey = 0.0
        activity.loadData(ArrayList())
        getResult()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        activity.menuInflater.inflate(R.menu.inventory_menu, menu)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            val manager = activity.getSystemService(Context.SEARCH_SERVICE) as SearchManager
            menu.findItem(R.id.search).isVisible = true
            search = menu.findItem(R.id.search).actionView as SearchView
            search?.apply {
                search!!.visibility = View.VISIBLE
                search!!.setSearchableInfo(manager.getSearchableInfo(activity.componentName))
                search!!.setOnQueryTextListener(object : OnQueryTextListener {
                    override fun onQueryTextSubmit(s: String): Boolean {
                        return true
                    }

                    override fun onQueryTextChange(query: String): Boolean {
                        reloadAdapter(query)
                        return true
                    }
                })
            }

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
                showAddInventoryItemFragment(EMPTY_STRING, ViewMode.ADD.ordinal, AppConstants.INVALID_ID)
                return true
            }
        }
        return true
    }

    override fun onBackPressed() {
        activity.finish()
    }

    private fun showAddInventoryItemFragment(id: String?, viewMode: Int, pos: Int) {
        Navigator.openInventoryScreen(activity, id!!, viewMode, inventoryType, title, pos)
    }

    private fun addOrderQuantity(inventoryItem: InventoryItem, pos: Int) {
        Navigator.openInventoryScreen(activity, inventoryItem.id!!,
                ViewMode.GET_INVENTORY_QUANTITY.ordinal, inventoryType,
                activity.getString(R.string.add_order_inventory_title), orderId!!, pos)
    }


    fun deleteInventory(inventoryItem: InventoryItem, context: Context, pos: Int) {
        val alertDialogBuilderUserInput = AlertDialog.Builder(context)
        alertDialogBuilderUserInput
                .setTitle(context.getString(R.string.remove_inventory_item_title))
                .setMessage(context.getString(R.string.remove_inventory_item_message))
                .setCancelable(false)
                .setPositiveButton(context.getString(R.string.yes)
                ) { dialogBox, id ->
                    FirebaseUtil.getInstance().getInventoryDao()
                            .removeInventoryItem(inventoryItem, inventoryType, DatabaseReference.CompletionListener { databaseError, _ ->
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
            AppConstants.GET_INVENTORY_UPDATE_REQUEST_CODE// when we open add inventory screen
            -> when (resultCode) {
                ORDER_RELOAD_LIST_RESULT_CODE//Open add Inventory for getting quantity
                -> activity.finish()
                RELOAD_LIST_RESULT_CODE// added Inventory Item then
                -> callAfterActivityResult(data)
            }
        }
    }

    private fun callAfterActivityResult(data: Intent?) {
        data?.let {
            val viewMode = data.getIntExtra(AppConstants.VIEW_MODE, ViewMode.VIEW_ONLY.ordinal)
            if (viewMode == ViewMode.UPDATE.ordinal) {
                val pos = data.getIntExtra(AppConstants.POSITION_IN_LIST, AppConstants.INVALID_ID)
                if (pos != AppConstants.INVALID_ID) {
                    if (data.hasExtra(AppConstants.INVENTORY_INSTANCE)) {
                        val inventoryInstance: InventoryItem = data.getSerializableExtra(AppConstants.INVENTORY_INSTANCE) as InventoryItem
                        activity.updateElement(inventoryInstance, pos)
                    }
                }
            } else if (viewMode == ViewMode.ADD.ordinal) {
                if (data.hasExtra(AppConstants.INVENTORY_INSTANCE)) {
                    val inventoryInstance = data.getSerializableExtra(AppConstants.INVENTORY_INSTANCE)
                    if (inventoryInstance != null) {
                        val inventory: InventoryItem = inventoryInstance as InventoryItem
                        activity.addElement(inventory, 0)
                    }
                }
            }
        }
    }

}
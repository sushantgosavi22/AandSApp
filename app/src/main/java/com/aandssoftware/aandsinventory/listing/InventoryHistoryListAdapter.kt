package com.aandssoftware.aandsinventory.listing

import android.content.Intent
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.aandssoftware.aandsinventory.R
import com.aandssoftware.aandsinventory.common.DateUtils
import com.aandssoftware.aandsinventory.firebase.FirebaseUtil
import com.aandssoftware.aandsinventory.models.InventoryItem
import com.aandssoftware.aandsinventory.models.InventoryItemHistory
import com.aandssoftware.aandsinventory.ui.activity.ListingActivity
import com.aandssoftware.aandsinventory.ui.adapters.BaseAdapter.BaseViewHolder
import com.aandssoftware.aandsinventory.utilities.AppConstants.Companion.EMPTY_STRING
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.GenericTypeIndicator
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.item_history.view.*
import java.io.Serializable
import java.util.ArrayList
import java.util.HashMap

class InventoryHistoryListAdapter(private val activity: ListingActivity) : ListingOperations {

    private val valueEventListener: ValueEventListener = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            val list = FirebaseUtil.getInstance()
                    .getListData(dataSnapshot, InventoryItemHistory::class.java)
            if (null != list && list.isNotEmpty()) {
                activity.loadData(list)
            }
            activity.dismissProgressBar()
        }

        override fun onCancelled(databaseError: DatabaseError) {
            activity.dismissProgressBar()
        }
    }

    private val allRecordValueEventListener: ValueEventListener = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            val historyList = ArrayList<InventoryItemHistory>()
            for (childSnapshot in dataSnapshot.children) {
                val history = object : GenericTypeIndicator<HashMap<String, InventoryItemHistory>>() {}
                val map = childSnapshot.child(InventoryItem.INVENTORY_ITEM_HISTORY).getValue(history)
                if (map?.values != null) {
                    val itemList = ArrayList(map.values)
                    if (itemList.isNotEmpty()) {
                        historyList.addAll(itemList)
                    }
                }
            }
            if (historyList.isNotEmpty()) {
                activity.loadData(historyList)
            }
            activity.dismissProgressBar()
        }

        override fun onCancelled(databaseError: DatabaseError) {
            activity.dismissProgressBar()
        }
    }

    inner class InventoryHistoryViewHolder(itemView: View) : BaseViewHolder(itemView) {
        internal var tvModifiedParameterName: TextView = itemView.tvModifiedParameterName
        internal var tvActionMessage: TextView = itemView.tvActinonMessage
        internal var tvDate: TextView = itemView.tvDate
    }

    override fun getActivityLayoutId(): Int {
        return R.layout.activity_listing
    }


    override fun getBaseViewHolder(viewGroup: ViewGroup, i: Int): BaseViewHolder {
        val v = LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.item_history, viewGroup, false)
        return InventoryHistoryViewHolder(v)
    }

    override fun onBindSearchViewHolder(baseHolder: BaseViewHolder, position: Int, item: Serializable) {
        val holder = baseHolder as InventoryHistoryViewHolder
        val mItem = item as InventoryItemHistory
        holder.tvModifiedParameterName.text = EMPTY_STRING.plus(mItem.modifiedParameter).plus(" is ").plus(mItem.action)
        holder.tvActionMessage.text = EMPTY_STRING.plus(mItem.modifiedParameter).plus(" is ").plus(mItem.action).plus(" From ").plus(mItem
                .modifiedTo).plus(" To ").plus(mItem.modifiedFrom)
        holder.tvDate.text = DateUtils.getDateFormatted(mItem.modifiedDate)

    }

    override fun getTitle(): String = activity.getString(R.string.inventory_item_history)


    override fun getResult() {
        activity.showProgressBar()
        if (null != activity.intent) {
            val id = activity.intent.getStringExtra(InventoryListAdapter.INVENTORY_ID)
            if (id != null) {
                FirebaseUtil.getInstance().getInventoryDao()
                        .getAllInventoryItemHistory(id, valueEventListener)
            } else {
                FirebaseUtil.getInstance().getInventoryDao()
                        .getAllInventoryItemAtOnce(allRecordValueEventListener)
            }
        } else {
            FirebaseUtil.getInstance().getInventoryDao()
                    .getAllInventoryItemAtOnce(allRecordValueEventListener)
        }
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                activity.finish()
                return true
            }
        }
        return true
    }

    override fun onBackPressed() {
        activity.finish()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

    }
}

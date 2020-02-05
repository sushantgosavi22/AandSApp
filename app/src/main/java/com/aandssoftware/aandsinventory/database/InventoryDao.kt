package com.aandssoftware.aandsinventory.database

import com.aandssoftware.aandsinventory.application.AandSApplication
import com.aandssoftware.aandsinventory.firebase.FirebaseUtil
import com.aandssoftware.aandsinventory.firebase.GetAlphaNumericAndNumericIdListener
import com.aandssoftware.aandsinventory.listing.ListType
import com.aandssoftware.aandsinventory.models.callBackListener
import com.aandssoftware.aandsinventory.models.InventoryItem
import com.aandssoftware.aandsinventory.models.InventoryItemHistory
import com.google.firebase.database.*
import java.util.*

class InventoryDao {

    companion object {
        private var instance: InventoryDao? = null
        @Synchronized
        private fun createInstance() {
            if (instance == null) {
                instance = InventoryDao()
            }
        }

        @JvmStatic
        fun getInstance(): InventoryDao {
            if (instance == null) createInstance()
            return instance as InventoryDao
        }
    }


    constructor() {
    }

    private val inventoryItemTableReference: DatabaseReference
        get() {
            val reference = AandSApplication.getDatabaseInstance()
                    .getReference(InventoryItem.TABLE_INVENTORY_ITEM)
            reference.keepSynced(true)
            return reference
        }

    private val inventoryItemCounterTableReference: DatabaseReference
        get() {
            val reference = AandSApplication.getDatabaseInstance()
                    .getReference(InventoryItem.INVENTORY_ITEM_COUNTER)
            reference.keepSynced(true)
            return reference
        }

    private val materialInventoryItemTableReference: DatabaseReference
        get() {
            val reference = AandSApplication.getDatabaseInstance()
                    .getReference(InventoryItem.TABLE_MATERIAL_INVENTORY_ITEM)
            reference.keepSynced(true)
            return reference
        }

    private val materialInventoryItemCounterTableReference: DatabaseReference
        get() {
            val reference = AandSApplication.getDatabaseInstance()
                    .getReference(InventoryItem.MATERIAL_INVENTORY_ITEM_COUNTER)
            reference.keepSynced(true)
            return reference
        }


    private fun getInventoryItemHistoryTableReference(id: String): DatabaseReference {
        val reference = inventoryItemTableReference.child(id).child(InventoryItem.INVENTORY_ITEM_HISTORY)
        reference.keepSynced(true)
        return reference
    }


    fun getAllInventoryItemAtOnce(lastNodeKey: Double, itemToLoad: Int, valueEventListener: ValueEventListener) {
        var query = inventoryItemTableReference.limitToLast(itemToLoad)
        var defaultLong: Double = 0.0
        query = if (lastNodeKey == defaultLong) {
            query.orderByChild(InventoryItem.ORDER_BY_VALUE).startAt(lastNodeKey)
        } else {
            query.orderByChild(InventoryItem.ORDER_BY_VALUE).endAt(lastNodeKey)
        }
        query.addListenerForSingleValueEvent(valueEventListener)
    }

    fun getAllInventoryItemAtOnce(valueEventListener: ValueEventListener) {
        inventoryItemTableReference.orderByChild(InventoryItem.ORDER_BY_VALUE).addListenerForSingleValueEvent(valueEventListener)
    }


    fun getMaterialRecords(lastNodeKey: Double, itemToLoad: Int, valueEventListener: ValueEventListener) {
        var query = materialInventoryItemTableReference.limitToLast(itemToLoad)
        var defaultLong: Double = 0.0
        query = if (lastNodeKey == defaultLong) {
            query.orderByChild(InventoryItem.ORDER_BY_VALUE).startAt(lastNodeKey)
        } else {
            query.orderByChild(InventoryItem.ORDER_BY_VALUE).endAt(lastNodeKey)
        }
        query.addListenerForSingleValueEvent(valueEventListener)
    }

    fun getNextInventoryItemId(listener: GetAlphaNumericAndNumericIdListener) {
        inventoryItemCounterTableReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                var numericId = FirebaseUtil.getInstance().incrementCounter(dataSnapshot)
                var alphaNumericId = inventoryItemTableReference.push().key
                alphaNumericId = alphaNumericId?.let { alphaNumericId } ?: numericId
                listener.afterGettingIds(alphaNumericId, numericId)
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }


    fun getNextInventoryHistoryId(id: String): String? = getInventoryItemHistoryTableReference(id).push().key


    fun getInventoryItemFromId(inventoryId: String, valueEventListener: ValueEventListener) {
        inventoryItemTableReference.child(inventoryId).addValueEventListener(valueEventListener)
    }

    fun getMaterialInventoryItemFromId(inventoryId: String, valueEventListener: ValueEventListener) {
        materialInventoryItemTableReference.child(inventoryId).addValueEventListener(valueEventListener)
    }


    fun getAllInventoryItemAtOnce(inventoryType: Int, valueEventListener: ValueEventListener) {
        var refrance = if (inventoryType == ListType.LIST_TYPE_MATERIAL.ordinal)
            materialInventoryItemTableReference
        else
            inventoryItemTableReference
        refrance.orderByChild(InventoryItem.ORDER_BY_VALUE_NAME).addListenerForSingleValueEvent(valueEventListener)
    }


    fun getAllInventoryItemHistory(inventoryId: String, valueEventListener: ValueEventListener) {
        getInventoryItemHistoryTableReference(inventoryId).orderByChild(InventoryItemHistory.ORDER_BY_VALUE).addValueEventListener(valueEventListener)
    }

    fun saveInventoryItem(mInventoryItem: InventoryItem, inventoryType: Int, dataListener: callBackListener) {
        if (null != mInventoryItem.id) {
            var ref = if (inventoryType == ListType.LIST_TYPE_MATERIAL.ordinal)
                materialInventoryItemTableReference
            else
                inventoryItemTableReference

            ref.child(mInventoryItem.id!!).setValue(mInventoryItem) { databaseError, _ ->
                run {
                    if (null == databaseError) {
                        dataListener.getCallBack(true)
                    } else {
                        dataListener.getCallBack(false)
                    }
                }
            }
        } else {
            dataListener.getCallBack(false)
        }
    }

    fun removeInventoryItem(inventoryItem: InventoryItem, inventoryType: Int, completionListener: DatabaseReference.CompletionListener) {
        inventoryItem.id?.let {
            var reference = if (inventoryType == ListType.LIST_TYPE_MATERIAL.ordinal) {
                materialInventoryItemTableReference
            } else {
                inventoryItemTableReference
            }
            reference.child(it).removeValue(completionListener)
        }
    }

    fun saveInventoryItemHistory(alphanumericInventoryId: String, hashMap: HashMap<String, Any>, listner: DatabaseReference.CompletionListener) {
        getInventoryItemHistoryTableReference(alphanumericInventoryId).updateChildren(hashMap, listner)
    }

}

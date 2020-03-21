package com.aandssoftware.aandsinventory.database

import com.aandssoftware.aandsinventory.application.AandSApplication
import com.aandssoftware.aandsinventory.firebase.FirebaseUtil
import com.aandssoftware.aandsinventory.firebase.GetAlphaNumericAndNumericIdListener
import com.aandssoftware.aandsinventory.models.*
import com.aandssoftware.aandsinventory.utilities.AppConstants.Companion.ZERO_STRING
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.DatabaseReference.CompletionListener
import com.google.firebase.database.ValueEventListener
import java.util.*
import kotlin.collections.HashMap

class CustomerDao {

    companion object {
        private var instance: CustomerDao? = null
        @Synchronized
        private fun createInstance() {
            if (instance == null) {
                instance = CustomerDao()
            }
        }

        @JvmStatic
        fun getInstance(): CustomerDao {
            if (instance == null) createInstance()
            return instance as CustomerDao
        }
    }


    constructor() {
    }

    private val customerTableReference: DatabaseReference
        get() {
            val reference = AandSApplication.getDatabaseInstance()
                    .getReference(CustomerModel.TABLE_CUSTOMER)
            reference.keepSynced(true)
            return reference
        }

    private val customerCounterReference: DatabaseReference
        get() {
            val reference = AandSApplication.getDatabaseInstance()
                    .getReference(CustomerModel.CUSTOMER_COUNTER)
            reference.keepSynced(true)
            return reference
        }

    private val orderTableReference: DatabaseReference
        get() {
            val reference = AandSApplication.getDatabaseInstance()
                    .getReference(OrderModel.TABLE_ORDER)
            reference.keepSynced(true)
            return reference
        }


    private val orderCounterReference: DatabaseReference
        get() {
            val reference = AandSApplication.getDatabaseInstance()
                    .getReference(OrderModel.ORDER_COUNTER)
            reference.keepSynced(true)
            return reference
        }

    private val appVersionTableReference: DatabaseReference
        get() {
            val reference = AandSApplication.getDatabaseInstance()
                    .getReference(AppVersion.TABLE_APP_VERSION)
            reference.keepSynced(true)
            return reference
        }

    private val userLoginTableReference: DatabaseReference
        get() {
            val reference = AandSApplication.getDatabaseInstance()
                    .getReference(UserModel.TABLE_USER)
            reference.keepSynced(true)
            return reference
        }


    fun getAllCustomers(valueEventListener: ValueEventListener) {
        customerTableReference.orderByChild(CustomerModel.ORDER_BY_VALUE).addListenerForSingleValueEvent(valueEventListener)
    }

    fun saveCustomerItem(mCustomerModel: CustomerModel, dataListener: CallBackListener) {
        mCustomerModel.id?.let {
            customerTableReference.child(it).setValue(mCustomerModel) { databaseError, _ ->
                run {
                    if (null == databaseError) {
                        dataListener.getCallBack(true)
                    } else {
                        dataListener.getCallBack(false)
                    }
                }
            }
        }
    }

    fun storeNotificationTokenToCustomerItem(mCustomerModel: CustomerModel, token: String, dataListener: CallBackListener) {
        mCustomerModel.id?.let {
            customerTableReference.child(it).child(CustomerModel.PUSH_NOTIFICATION_TOKEN).setValue(token) { databaseError, _ ->
                run {
                    if (null == databaseError) {
                        dataListener.getCallBack(true)
                    } else {
                        dataListener.getCallBack(false)
                    }
                }
            }
        }
    }

    fun getNextCustomerItemId(listener: GetAlphaNumericAndNumericIdListener) {
        customerCounterReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                var numericId = FirebaseUtil.getInstance().incrementCounter(dataSnapshot)
                var fireBaseCustomerId = customerTableReference.push().key
                fireBaseCustomerId = fireBaseCustomerId?.let { fireBaseCustomerId } ?: numericId
                listener.afterGettingIds(fireBaseCustomerId, numericId)
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    fun getCustomerFromID(customerId: String, valueEventListener: ValueEventListener) {
        customerTableReference.child(customerId).addListenerForSingleValueEvent(valueEventListener)
    }

    fun getCustomerFromUserNameAndPassword(username: String, valueEventListener: ValueEventListener) {
        customerTableReference.orderByChild(CustomerModel.LOGIN_ORDER_BY_VALUE).equalTo(username).addListenerForSingleValueEvent(valueEventListener)
    }

    fun removeCustomer(customerModel: CustomerModel, completionListener: CompletionListener) {
        customerModel.id?.let {
            customerTableReference.child(it).removeValue(completionListener)
        }
    }


    fun removeCustomerDiscountForItem(customerId: String, inventoryId: String, completionListener: CompletionListener) {
        customerTableReference.child(customerId).child(CustomerModel.DISCOUNTED_ITEM).child(inventoryId).removeValue(completionListener)
    }


    fun addDiscountedItemToCustomer(model: CustomerModel, inventoryId: String, discountedAmount: String, dataListener: CallBackListener) {
        model.id?.let {
            var map = model.discountedItems
            if (map == null) {
                map = HashMap<String, String>()
                map.put(inventoryId, discountedAmount)
            } else {
                map.put(inventoryId, discountedAmount)
            }
            customerTableReference.child(it).child(CustomerModel.DISCOUNTED_ITEM).setValue(map) { databaseError, databaseReference ->
                if (databaseError == null) {
                    dataListener.getCallBack(true)
                } else {
                    dataListener.getCallBack(false)
                }
            }
        }
    }


    fun getOrders(lastNodeKey: Double, itemToLoad: Int, valueEventListener: ValueEventListener) {
        var query = orderTableReference.limitToLast(itemToLoad)
        var defaultLong: Double = 0.0
        query = if (lastNodeKey == defaultLong) {
            query.orderByChild(OrderModel.ORDER_BY_VALUE).startAt(lastNodeKey)
        } else {
            query.orderByChild(OrderModel.ORDER_BY_VALUE).endAt(lastNodeKey)
        }
        query.addListenerForSingleValueEvent(valueEventListener)
    }

    fun getCompanyOrders(companyId: String, valueEventListener: ValueEventListener) {
        orderTableReference.orderByChild(OrderModel.ID_ORDER_BY_VALUE).equalTo(companyId).addValueEventListener(valueEventListener)
    }

    fun getCompanyOrdersFromOrderId(companyId: String, orderId: String, inventoryId: String, valueEventListener: ValueEventListener) {
        orderTableReference.orderByChild(OrderModel.ID_ORDER_BY_VALUE).equalTo(companyId).ref.child(orderId).child(OrderModel.ORDER_ITEMS).child(inventoryId).addListenerForSingleValueEvent(valueEventListener)
    }


    fun getNextOrderItemId(listener: GetAlphaNumericAndNumericIdListener) {
        orderCounterReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                var numericId = FirebaseUtil.getInstance().incrementCounter(dataSnapshot)
                var alphaNumericId = orderTableReference.push().key
                alphaNumericId = alphaNumericId?.let { alphaNumericId } ?: numericId
                listener.afterGettingIds(alphaNumericId, numericId)
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }


    fun removeOrder(orderModel: OrderModel, completionListener: CompletionListener) {
        orderModel.id?.let {
            orderTableReference.child(it).removeValue(completionListener)
        }
    }

    fun removeInventoryFromOrder(orderId: String?, inventoryItem: InventoryItem, completionListener: CompletionListener) {
        orderId?.let {
            inventoryItem.id?.let { inventoryId ->
                orderTableReference.child(orderId).child(OrderModel.ORDER_ITEMS).child(inventoryId).removeValue(completionListener)
            }
        }
    }

    fun addInventoryToOrder(mainInventoryItem: InventoryItem, orderId: String,
                            quantity: String, dataListener: CallBackListener) {
        var itemReference = orderTableReference.child(orderId).child(OrderModel.ORDER_ITEMS)
        var key = itemReference.push().key ?: ZERO_STRING
        mainInventoryItem.parentId = mainInventoryItem.id
        mainInventoryItem.id = key
        mainInventoryItem.itemQuantity = quantity
        itemReference.child(key).setValue(mainInventoryItem) { databaseError, _ ->
            run {
                if (null == databaseError) {
                    orderTableReference.child(orderId).child(OrderModel.ORDER_BY_VALUE).setValue(System.currentTimeMillis())
                    dataListener.getCallBack(true)
                } else {
                    dataListener.getCallBack(false)
                }
            }
        }
    }

    fun saveOrder(alphaNumericOrderId: String, orderModel: OrderModel, dataListener: CallBackListener) {
        orderTableReference.child(alphaNumericOrderId).setValue(orderModel) { databaseError, _ ->
            run {
                if (null == databaseError) {
                    dataListener.getCallBack(true)
                } else {
                    dataListener.getCallBack(false)
                }
            }
        }
    }

    fun updateOrder(updatedOrderModel: OrderModel, dataListener: CallBackListener) {
        updatedOrderModel.id?.let {
            orderTableReference.child(updatedOrderModel.id!!).setValue(updatedOrderModel) { databaseError, _ ->
                run {
                    if (null == databaseError) {
                        dataListener.getCallBack(true)
                    } else {
                        dataListener.getCallBack(false)
                    }
                }
            }
        }
    }

    fun updateOrderStatus(orderId: String, status: String, statusName: String, dataListener: CallBackListener) {
        var map: HashMap<String, Any> = HashMap()
        map.put(OrderModel.ORDER_STATUS, status)
        map.put(OrderModel.ORDER_STATUS_NAME, statusName)
        orderTableReference.child(orderId).updateChildren(map) { databaseError, _ ->
            if (null == databaseError) {
                dataListener.getCallBack(true)
            } else {
                dataListener.getCallBack(false)
            }
        }
    }

    fun getOrderFromID(orderId: String, valueEventListener: ValueEventListener) {
        orderTableReference.child(orderId).addListenerForSingleValueEvent(valueEventListener)
    }

    fun getAppVersion(valueEventListener: ValueEventListener) {
        appVersionTableReference.addListenerForSingleValueEvent(valueEventListener)
    }
}

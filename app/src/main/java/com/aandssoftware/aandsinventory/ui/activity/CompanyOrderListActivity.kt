package com.aandssoftware.aandsinventory.ui.activity

import android.content.Intent
import android.os.Bundle
import com.aandssoftware.aandsinventory.common.Utils
import com.aandssoftware.aandsinventory.firebase.FirebaseUtil
import com.aandssoftware.aandsinventory.firebase.GetAlphaNumericAndNumericIdListener
import com.aandssoftware.aandsinventory.listing.CustomerListAdapter
import com.aandssoftware.aandsinventory.listing.ListType
import com.aandssoftware.aandsinventory.models.callBackListener
import com.aandssoftware.aandsinventory.models.CustomerModel
import com.aandssoftware.aandsinventory.models.OrderModel
import com.aandssoftware.aandsinventory.models.OrderStatus
import com.aandssoftware.aandsinventory.utilities.AppConstants
import com.aandssoftware.aandsinventory.utilities.AppConstants.Companion.EMPTY_STRING
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class CompanyOrderListActivity : ListingActivity() {

    fun showInventoryListingActivity(customerId: String, orderId: String) {
        showProgressBar()
        FirebaseUtil.getInstance().getCustomerDao().getCustomerFromID(customerId,
                object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        var model = FirebaseUtil.getInstance().getClassData(dataSnapshot, CustomerModel::class.java)
                        model?.let {
                            if (orderId.isNotEmpty()) {
                                saveOrder(orderId, EMPTY_STRING, model)
                            } else {
                                FirebaseUtil.getInstance().getCustomerDao().getNextOrderItemId(object : GetAlphaNumericAndNumericIdListener {
                                    override fun afterGettingIds(alphaNumericId: String, numericId: String) {
                                        saveOrder(alphaNumericId, numericId, model)
                                    }
                                })

                            }
                        }
                        dismissProgressBar()
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        dismissProgressBar()
                    }
                })
    }

    fun saveOrder(alphaNumericOrderId: String, numericOrderId: String, customerModel: CustomerModel) {
        showProgressBar()
        FirebaseUtil.getInstance().getCustomerDao().saveOrder(
                alphaNumericOrderId,
                getOrderModelNewlyCreated(alphaNumericOrderId, numericOrderId, customerModel),
                callBackListener { result ->
                    dismissProgressBar()
                    if (result) {
                        val intent = Intent(this@CompanyOrderListActivity, ListingActivity::class.java)
                        intent.putExtra(AppConstants.LISTING_TYPE, ListType.LIST_TYPE_INVENTORY.ordinal)
                        intent.putExtra(CustomerListAdapter.CUSTOMER_ID, customerModel.id)
                        intent.putExtra(AppConstants.ORDER_ID, alphaNumericOrderId)
                        startActivityForResult(intent, AppConstants.LISTING_REQUEST_CODE)
                    }
                })
    }

    private fun getOrderModelNewlyCreated(alphaNumericOrderId: String, numericOrderId: String, customerModel: CustomerModel): OrderModel {
        var model = OrderModel()
        model.id = alphaNumericOrderId
        model.customerId = customerModel.id
        model.orderId = numericOrderId
        model.orderStatus = OrderStatus.CREATED.name
        model.orderStatusName = Utils.capitalize(OrderStatus.CREATED.toString())
        model.orderDateUpdated = System.currentTimeMillis()
        model.orderDateCreated = System.currentTimeMillis()
        model.customerModel = customerModel
        return model
    }
}

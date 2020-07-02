package com.aandssoftware.aandsinventory.ui.activity

import android.annotation.TargetApi
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import com.aandssoftware.aandsinventory.R
import com.aandssoftware.aandsinventory.common.Utils
import com.aandssoftware.aandsinventory.firebase.FirebaseUtil
import com.aandssoftware.aandsinventory.firebase.GetAlphaNumericAndNumericIdListener
import com.aandssoftware.aandsinventory.listing.CustomerListAdapter
import com.aandssoftware.aandsinventory.listing.ListType
import com.aandssoftware.aandsinventory.models.*
import com.aandssoftware.aandsinventory.utilities.AppConstants
import com.aandssoftware.aandsinventory.utilities.AppConstants.Companion.CREATE_ORDER
import com.aandssoftware.aandsinventory.utilities.AppConstants.Companion.EMPTY_STRING
import com.aandssoftware.aandsinventory.utilities.CrashlaticsUtil
import com.aandssoftware.aandsinventory.utilities.SharedPrefsUtils
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fab_button_layout.*

class CompanyOrderListActivity : ListingActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fab?.setOnClickListener {
            showInventoryListingActivity(Utils.getLoginCustomerId(this), EMPTY_STRING)
        }
        getIntentData()
    }

    private fun getIntentData() {
       var shouldCreateOrder : Boolean = intent?.getBooleanExtra(AppConstants.CREATE_ORDER, false)?: false
        if(shouldCreateOrder){
            fab.performClick()
        }
    }

    fun showInventoryListingActivity(customerId: String, orderId: String) {
        if(FirebaseUtil.getInstance().isInternetConnected(this@CompanyOrderListActivity)){
            showProgressBar()
            FirebaseUtil.getInstance().getCustomerDao().getCustomerFromID(customerId,
                    object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            var model = FirebaseUtil.getInstance().getClassData(dataSnapshot, CustomerModel::class.java)
                            SharedPrefsUtils.setUserPreference(this@CompanyOrderListActivity, SharedPrefsUtils.CURRENT_USER, model)
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
        }else{
            showSnackBarMessage(getString(R.string.no_internet_connection))
        }

    }

    fun saveOrder(alphaNumericOrderId: String, numericOrderId: String, customerModel: CustomerModel) {
        showProgressBar()
        FirebaseUtil.getInstance().getCustomerDao().saveOrder(
                alphaNumericOrderId,
                getOrderModelNewlyCreated(alphaNumericOrderId, numericOrderId, customerModel),
                CallBackListener { result ->
                    dismissProgressBar()
                    if (result) {
                        val intent = Intent(this@CompanyOrderListActivity, ListingActivity::class.java)
                        intent.putExtra(AppConstants.LISTING_TYPE, ListType.LIST_TYPE_MATERIAL.ordinal)
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

package com.aandssoftware.aandsinventory.common

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import com.aandssoftware.aandsinventory.listing.ListType
import com.aandssoftware.aandsinventory.ui.activity.AddCustomerActivity
import com.aandssoftware.aandsinventory.ui.activity.AddInventoryActivity
import com.aandssoftware.aandsinventory.ui.activity.OrderDetailsActivity
import com.aandssoftware.aandsinventory.ui.activity.OrderListActivity
import com.aandssoftware.aandsinventory.utilities.AppConstants
import com.aandssoftware.aandsinventory.utilities.AppConstants.Companion.EMPTY_STRING
import com.aandssoftware.aandsinventory.utilities.AppConstants.Companion.INVALID_ID


class Navigator {

    companion object {

        @JvmStatic
        fun openInventoryScreen(activity: AppCompatActivity, id: String, viewMode: Int, listingType: Int, title: String, posInList: Int) {
            openInventoryScreen(activity, id, viewMode, listingType, title, EMPTY_STRING, posInList)
        }

        @JvmStatic
        fun openInventoryScreen(activity: AppCompatActivity, id: String, viewMode: Int, listingType: Int, title: String, orderId: String, posInList: Int) {
            val intent = Intent(activity, AddInventoryActivity::class.java)
            intent.putExtra(AppConstants.LISTING_TYPE, listingType)
            intent.putExtra(AppConstants.INVENTORY_ID, id)
            intent.putExtra(AppConstants.ORDER_ID, orderId)
            intent.putExtra(AppConstants.VIEW_MODE, viewMode)
            intent.putExtra(AppConstants.TITLE, title)
            intent.putExtra(AppConstants.POSITION_IN_LIST, posInList)
            activity.startActivityForResult(intent, AppConstants.GET_INVENTORY_UPDATE_REQUEST_CODE)
        }


        @JvmStatic
        fun openCustomerScreen(activity: AppCompatActivity, fireBaseCustomerId: String, numericCustomerId: String, viewMode: Int, title: String) {
            val intent = Intent(activity, AddCustomerActivity::class.java)
            intent.putExtra(AppConstants.FIRE_BASE_CUSTOMER_ID, fireBaseCustomerId)
            intent.putExtra(AppConstants.NUMERIC_CUSTOMER_ID, numericCustomerId)
            intent.putExtra(AppConstants.VIEW_MODE, viewMode)
            intent.putExtra(AppConstants.TITLE, title)
            activity.startActivityForResult(intent, AppConstants.GET_CUSTOMER_UPDATE_REQUEST_CODE)
        }

        @JvmStatic
        fun openOrderDetailsScreen(activity: Activity, orderId: String) {
            val intent = Intent(activity, OrderDetailsActivity::class.java)
            intent.putExtra(AppConstants.ORDER_ID, orderId)
            intent.putExtra(AppConstants.LISTING_TYPE, ListType.LIST_TYPE_ORDER_INVENTORY.ordinal)
            activity.startActivityForResult(intent, AppConstants.LISTING_REQUEST_CODE)
        }

    }


}
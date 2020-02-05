package com.aandssoftware.aandsinventory.utilities

import android.app.Activity
import android.content.Context
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.aandssoftware.aandsinventory.R
import com.google.android.material.snackbar.Snackbar

class AppConstants {

    val BASE_URL = ""

    companion object {
        const val BASE_URL = ""
        const val EMPTY_STRING: String = ""
        const val ZERO_STRING: String = "0"
        const val SNACK_BAR_MAX_LINES = 3
        const val PICK_IMAGE = 100
        const val INVALID_ID = -1
        const val COUNT_ONE = 1
        const val SPLASH_TIME = (3 * 1000).toLong()


        /*Intent Data Passing */
        const val LOG: String = "AandSLog"
        const val TITLE: String = "title"
        const val HTTP: String = "http"
        const val POSITION_IN_LIST: String = "positionInList"
        const val LISTING_TYPE: String = "listingType"
        const val INVENTORY_ID: String = "inventoryId"
        const val INVENTORY_INSTANCE: String = "inventoryInstance"
        const val UPDATED: String = "updated"
        const val ORDER_ID: String = "orderId"
        const val FIRE_BASE_CUSTOMER_ID: String = "fireBaseCustomerId"
        const val NUMERIC_CUSTOMER_ID: String = "NumericCustomerId"
        const val VIEW_MODE: String = "viewMode"
        const val CUSTOMER_IMAGES_STORAGE_PATH: String = "customerImages"
        const val INVENTORY_IMAGES_STORAGE_PATH: String = "inventoryImages"
        /*Intent Data Passing */

        /*Start Activity For Result ID */
        const val LISTING_REQUEST_CODE = 106
        const val GET_INVENTORY_UPDATE_REQUEST_CODE = 101
        const val GET_CUSTOMER_UPDATE_REQUEST_CODE = 103

        const val RELOAD_LIST_RESULT_CODE: Int = 102
        const val ORDER_RELOAD_LIST_RESULT_CODE: Int = 104
        const val ORDER_DETAIL_RELOAD_LIST_RESULT_CODE: Int = 105
        /*Start Activity For Result ID */

    }

}
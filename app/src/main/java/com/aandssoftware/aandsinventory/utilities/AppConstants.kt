package com.aandssoftware.aandsinventory.utilities

import android.app.Activity
import android.content.Context
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.aandssoftware.aandsinventory.R
import com.google.android.material.snackbar.Snackbar

class AppConstants {

    companion object {
        const val BASE_URL = ""
        const val EMPTY_STRING: String = ""
        const val ZERO_STRING: String = "0"
        const val DEFAULT_GST_DOUBLE: Double = 6.0
        const val DEFAULT_GST_STRING: String = DEFAULT_GST_DOUBLE.toString()
        const val DOUBLE_DEFAULT_ZERO: Double = 0.0
        const val DOUBLE_DEFAULT_ZERO_STRING: String = DOUBLE_DEFAULT_ZERO.toString()
        const val SNACK_BAR_MAX_LINES = 3
        const val ANDROID_APP_UPDATE = 4
        const val AD_COUNT_LIMIT = 4
        const val INVENTORY_IMAGES_LIMIT = 4
        const val PICK_IMAGE = 100
        const val INVALID_ID = -1
        const val COUNT_ONE = 1
        const val PICK_IMAGE_MULTIPLE = 101
        const val SPLASH_TIME = (3 * 1000).toLong()


        /*Intent Data Passing */
        const val LOG: String = "AandSLog"
        const val TITLE: String = "title"
        const val HTTP: String = "http"
        const val POSITION_IN_LIST: String = "positionInList"
        const val LISTING_TYPE: String = "listingType"
        const val INVENTORY_ID: String = "inventoryId"
        const val CUSTOMER_ID: String = "customerId"
        const val INVENTORY_INSTANCE: String = "inventoryInstance"
        const val UPDATED: String = "updated"
        const val ORDER_ID: String = "orderId"
        const val SHOW_INVENTORY_OF_CUSTOMER_TO_ADMIN: String = "showInvOfCustToAdmin"
        const val ORDER_INSTANCE: String = "orderInstance"
        const val FIRE_BASE_CUSTOMER_ID: String = "fireBaseCustomerId"
        const val NUMERIC_CUSTOMER_ID: String = "NumericCustomerId"
        const val VIEW_MODE: String = "viewMode"
        const val CUSTOMER_IMAGES_STORAGE_PATH: String = "customerImages"
        const val INVENTORY_IMAGES_STORAGE_PATH: String = "inventoryImages"
        const val DISCOUNTED_AMOUNT: String = "discountedAmount"
        const val CREATE_ORDER: String = "createOrder"
        const val CUSTOMER_EDIT_PROFILE_IS_ON: String = "customerEditProfile"
        var CUSTOMER_ID_FOR_INVENTORY_LIST_CHANGES : String = ""
        /*Intent Data Passing */

        /*Start Activity For Result ID */
        const val LISTING_REQUEST_CODE = 106
        const val GET_INVENTORY_UPDATE_REQUEST_CODE = 101
        const val GET_CUSTOMER_UPDATE_REQUEST_CODE = 103
        const val GET_CUSTOMER_DISCOUNT_REQUEST_CODE = 107

        const val RELOAD_LIST_RESULT_CODE: Int = 102
        const val ORDER_RELOAD_LIST_RESULT_CODE: Int = 104
        const val ORDER_DETAIL_RELOAD_LIST_RESULT_CODE: Int = 105
        const val CUSTOMER_DISCOUNT_RESULT_CODE: Int = 108
        /*Start Activity For Result ID */

        const val ENQUIRY_NOTIFICATION_MASSAGE : String ="#CompanyName# Required #Quantity# #Unit# of #ProductName#"
        const val ENQUIRY_MAIL_SUBJECT : String ="Enquiry for the product "
        const val ENQUIRY_MAIL_BODY : String = " #Date# \n" +
                "Mr. Nishant Gosavi\n" +
                "A and S Distributors and suppliers\n" +
                "A/p -Shindewadi , Tel- Bhor \n" +
                "pune 412205\n" +
                " \n" +
                "Dear Mr. Nishant\n" +
                "     \n" +
                "    I am from #CompanyName# at #Address#  We need below item \n" +
                "Required Quantiy    =  #Quantity# \n" +
                "Quantiy  Unit       =  #Unit# \n" +
                "Product Description =  #Description# \n" +
                "Product ID          =  #ID# \n" +
                "\n" +
                "\tPlease provide us details as soon as possible, waiting for your reply\n" +
                "\t\n" +
                "Thank you."
        const val ORDER_CONFIRM_MAIL_SUBJECT : String ="Order Request from "
        const val ORDER_CONFIRM_MAIL_BODY_UPPER : String =" #Date# \n" +
                "Mr. Nishant Gosavi\n" +
                "A and S Distributors and suppliers\n" +
                "A/p -Shindewadi , Tel- Bhor \n" +
                "pune 412205\n" +
                " \n" +
                "Dear Mr. Nishant\n" +
                "     \n" +
                "    I am #PERSON# from #CompanyName# at #Address#  We need below item \n"
        const val ORDER_CONFIRM_MAIL_BODY_COLUMN : String = "Product    HSN   QTY   UNIT   Description \n\n"
        const val ORDER_CONFIRM_MAIL_BODY_MIDDLE : String = "#I#) #NAME#    -#HSN#   -#QTY#   -#UNIT#  \n-#DESCRIPTION#\n\n"
        const val ORDER_CONFIRM_MAIL_BODY_LOWER : String = "\n" +
             "\t\n Please deliver this as early as possible\n" +
             "Thank you."




    }

}

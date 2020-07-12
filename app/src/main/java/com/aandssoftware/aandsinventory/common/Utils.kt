package com.aandssoftware.aandsinventory.common

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.net.Uri
import android.text.TextUtils
import android.util.Base64
import android.util.Patterns
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import com.aandssoftware.aandsinventory.BuildConfig
import com.aandssoftware.aandsinventory.R
import com.aandssoftware.aandsinventory.firebase.FirebaseUtil
import com.aandssoftware.aandsinventory.models.CustomerModel
import com.aandssoftware.aandsinventory.models.InventoryItem
import com.aandssoftware.aandsinventory.models.OrderModel
import com.aandssoftware.aandsinventory.models.OrderStatus
import com.aandssoftware.aandsinventory.ui.activity.BaseActivity
import com.aandssoftware.aandsinventory.ui.activity.ui.login.LoginActivity
import com.aandssoftware.aandsinventory.utilities.AppConstants
import com.aandssoftware.aandsinventory.utilities.AppConstants.Companion.DOUBLE_DEFAULT_ZERO
import com.aandssoftware.aandsinventory.utilities.AppConstants.Companion.EMPTY_STRING
import com.aandssoftware.aandsinventory.utilities.AppConstants.Companion.ZERO_STRING
import com.aandssoftware.aandsinventory.utilities.SharedPrefsUtils
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import java.math.BigDecimal
import java.net.URLEncoder
import java.text.NumberFormat
import java.util.*
import java.util.concurrent.atomic.AtomicInteger
import kotlin.math.roundToInt


class Utils {

    companion object {

        private var atomicInteger = AtomicInteger(0)
        @JvmStatic
        fun showToast(message: String, context: Context) {
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        }

        @JvmStatic
        fun isEmpty(message: String?): String {
            return isEmpty(message, "")
        }

        @JvmStatic
        fun isEmpty(message: String?, defaultVal: String): String {
            return if (null != message && message.isNotEmpty()) message else defaultVal
        }

        @JvmStatic
        fun isEmpty(message: String?, defaultVal: Double): Double {
            try {
                return if (null != message && message.isNotEmpty() && !message.equals(ZERO_STRING)) message?.toDouble() else defaultVal
            } catch (e: Exception) {
                e.printStackTrace()
                return defaultVal;
            }
        }

        @JvmStatic
        fun isEmptyIntFromString(message: String?, defaultVal: Int): Int {
            try {
                return if (null != message && message.isNotEmpty()) {
                    var valueInt: Int? = null
                    valueInt = message.toIntOrNull()
                    if (valueInt == null) {
                        valueInt = message.toDouble().roundToInt()
                    }
                    valueInt
                } else {
                    defaultVal
                }
            } catch (e: Exception) {
                e.printStackTrace()
                return defaultVal;
            }
        }

        @JvmStatic
        fun isEmptyInt(message: Int, defaultVal: String): String {
            return if (message > 0) message.toString() else defaultVal
        }

        @JvmStatic
        fun capitalize(message: String): String {
            return isEmpty(message, "").toLowerCase().capitalize()
        }

        /**
         * This method is used for showing snack bar with user defined duration.
         *
         * @param parentLayout this is the object of parent layout of the activity.
         * @param message to be shown in snack bar.
         * @return SnackBar object to be used for different purposes.
         */
        @JvmStatic
        fun showSnackBarMessage(parentLayout: ViewGroup, message: String): Snackbar {
            val snackbar = getSnackbar(parentLayout, message, Snackbar.LENGTH_LONG)
            showSnackbar(snackbar)
            return snackbar
        }

        @JvmStatic
        fun currencyLocale(value: Double): String {
            val formatter = NumberFormat.getCurrencyInstance(Locale("en", "in"))
            return formatter.format(value)
        }

        @JvmStatic
        fun parseCommaSeparatedCurrency(value: String): Double {
            val num = isEmpty(value, "0").replace(Regex("[^\\d.]"), "")
            return num.toDouble()
        }


        private fun getSnackbar(parentLayout: ViewGroup, message: String, duration: Int): Snackbar {
            val snackbar = Snackbar.make(parentLayout, message, duration)
            val sbView = snackbar.view
            sbView.setBackgroundColor(ContextCompat.getColor(parentLayout.context, R.color.gravel_color))
            val sbTextView = sbView.findViewById<TextView>(R.id.snackbar_text)
            sbTextView.setTextAppearance(parentLayout.context, R.style.bold_white_text_style_size_14)
            sbTextView.maxLines = AppConstants.SNACK_BAR_MAX_LINES // createAdditionalRows multiple line
            return snackbar
        }

        private fun showSnackbar(snackbar: Snackbar) {
            val snackbarActionTextView = snackbar.view.findViewById<TextView>(R.id.snackbar_action)
            if (snackbarActionTextView != null) {
                snackbarActionTextView.setTextAppearance(snackbar.context, R.style.roman_font)
                snackbar.show()
            }
        }

        @JvmStatic
        fun getFirebaseKey(): String {
            return BuildConfig.FIREBASE_SERVER_KEY
        }

        @JvmStatic
        fun Logout(context: Activity) {
            context.startActivity(Intent(context, LoginActivity::class.java))
            SharedPrefsUtils.setUserPreference(context, SharedPrefsUtils.CURRENT_USER, null)
            SharedPrefsUtils.setBooleanPreference(context, SharedPrefsUtils.ADMIN_USER, false)
            context.finish()
        }

        @JvmStatic
        fun getLoginCustomerId(context: Context): String {
            var customerId: String = EMPTY_STRING
            val user = SharedPrefsUtils.getUserPreference(context, SharedPrefsUtils.CURRENT_USER)
            user?.id?.let {
                customerId = it
            }
            return customerId
        }

        @JvmStatic
        fun getCustomerIdForCustomerInventory(context: Context): String {
            var customerId: String = AppConstants.CUSTOMER_ID_FOR_INVENTORY_LIST_CHANGES
            if(customerId.isEmpty()){
                customerId = getLoginCustomerId(context)
            }else{
                AppConstants.CUSTOMER_ID_FOR_INVENTORY_LIST_CHANGES  = ""
            }
            return customerId
        }

        @JvmStatic
        fun isAdminUser(context: Context): Boolean {
            return SharedPrefsUtils.getBooleanPreference(context, SharedPrefsUtils.ADMIN_USER, false)
        }


        @JvmStatic
        fun validateEmail(email: String): Boolean {
            val pattern = Patterns.EMAIL_ADDRESS
            return pattern.matcher(email).matches()
        }

        @JvmStatic
        fun getAmountOfPercentage(percent: Double, ofAmount: Double): Double {
            return (ofAmount * percent / 100)
        }


        @JvmStatic
        fun getItemNames(mItem: OrderModel): String {
            var result = EMPTY_STRING
            var arrayList = ArrayList<String>()
            mItem.orderItems.forEach {
                it.value.inventoryItemName?.let {
                    arrayList.add(it)
                }
            }
            if (arrayList.isNotEmpty()) {
                result = TextUtils.join(", ", arrayList)
            }
            return result
        }

        @JvmStatic
        fun getOrderFinalPrice(mItem: OrderModel): Double {
            var result: Double = 0.0
            mItem.orderItems.values.forEach {
                result += it.finalBillAmount
            }
            return result
        }

        @JvmStatic
        fun getOrderGstAmount(mItem: OrderModel): Double {
            var result: Double = 0.0
            mItem.orderItems.values.forEach {
                var amount = it.gstAmount + it.sgstAmount
                result += amount
            }
            return result
        }

        @JvmStatic
        fun getTaxableOrderAmount(mItem: OrderModel): Double {
            var result: Double = 0.0
            mItem.orderItems.values.forEach {
                result += it.taxableAmount
            }
            return result
        }

        @JvmStatic
        fun getSellingPriceAfterDiscount(context: Context, inventoryItem: InventoryItem): Double {
            var sellingPriceDouble = isEmpty(inventoryItem.minimumSellingPrice, DOUBLE_DEFAULT_ZERO)
            val user = SharedPrefsUtils.getUserPreference(context, SharedPrefsUtils.CURRENT_USER)
            user?.let {
                var isDiscountedItem = user.discountedItems?.containsKey(inventoryItem.id) ?: false
                if (isDiscountedItem) {
                    val discount = user.discountedItems?.get(inventoryItem.id)
                    discount?.let {
                        sellingPriceDouble = isEmpty(discount, DOUBLE_DEFAULT_ZERO)
                    }
                } else {
                    var discount = if (user.discountPercent == DOUBLE_DEFAULT_ZERO) {
                        user.discountPercent
                    } else {
                        var discountPercent = user.discountPercent
                        sellingPriceDouble * discountPercent / 100
                    }
                    sellingPriceDouble -= discount
                }
            }
            return sellingPriceDouble
        }


        @JvmStatic
        fun getDiscount(context: Context, inventoryItem: InventoryItem): Double {
            var sellingPriceDouble = isEmpty(inventoryItem.minimumSellingPrice, DOUBLE_DEFAULT_ZERO)
            val user = SharedPrefsUtils.getUserPreference(context, SharedPrefsUtils.CURRENT_USER)
            var discountValue: Double = DOUBLE_DEFAULT_ZERO
            user?.let {
                var isDiscountedItem = user.discountedItems?.containsKey(inventoryItem.id) ?: false
                if (isDiscountedItem) {
                    val discount = user.discountedItems?.get(inventoryItem.id)
                    var discountInDouble = isEmpty(discount, DOUBLE_DEFAULT_ZERO)
                    discountValue = sellingPriceDouble.minus(discountInDouble)
                } else {
                    var discount = if (user.discountPercent == DOUBLE_DEFAULT_ZERO) {
                        user.discountPercent
                    } else {
                        var discountPercent = user.discountPercent
                        sellingPriceDouble * discountPercent / 100
                    }
                    discountValue =  sellingPriceDouble.minus(discount)
                }
            }
            return discountValue
        }


        public fun round(d: Double, decimalPlace: Int): Double {
            var bd = BigDecimal(d.toString())
            bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP)
            return bd.toDouble()
        }

        fun getAtomicIntegerUniqueId() : Int{
            if(atomicInteger == null){
                atomicInteger = AtomicInteger(0)
            }
           return  atomicInteger.incrementAndGet()
        }

        fun sendMail(activity : Context,recipient: Array<String>,subject: String,body: String){
            val i = Intent(Intent.ACTION_SEND)
            i.type = "message/rfc822"
            i.putExtra(Intent.EXTRA_BCC, recipient)//EXTRA_EMAIL
            i.putExtra(Intent.EXTRA_SUBJECT, subject)
            i.putExtra(Intent.EXTRA_TEXT, body)
            try {
                activity.startActivity(Intent.createChooser(i, "Send mail..."))
            } catch (ex: ActivityNotFoundException) {
                Toast.makeText(activity, "There are no email clients installed.", Toast.LENGTH_SHORT).show()
            }
        }

        fun sendMailToAdmin(activity: BaseActivity,subject: String,body: String){
            var appVersion = SharedPrefsUtils.getAppVersionPreference(activity, SharedPrefsUtils.APP_VERSION)
            appVersion?.let {
                var customerId = appVersion.adminCustomerId ?: AppConstants.EMPTY_STRING
                activity.showProgressBar()
                FirebaseUtil.getInstance().getCustomerDao().getCustomerFromID(customerId,
                        object : ValueEventListener {
                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                activity.dismissProgressBar()
                                var model = FirebaseUtil.getInstance().getClassData(dataSnapshot, CustomerModel::class.java)
                                model?.companyMail?.let {mailId->
                                    sendMail(activity,arrayOf(mailId),subject,body)
                                }
                            }
                            override fun onCancelled(databaseError: DatabaseError) {
                                activity.dismissProgressBar()
                            }
                        })
            }
        }

        fun getBase64String(byteArray: ByteArray): String? {
            return Base64.encodeToString(byteArray, Base64.DEFAULT)
        }

        fun getStatusBackgroud(context: Context, statusCode: String): Drawable? {
            var drawable = ContextCompat.getDrawable(context, R.drawable.chip_background_blue)
            when (OrderStatus.valueOf(statusCode)) {
                OrderStatus.CREATED -> drawable = ContextCompat.getDrawable(context, R.drawable.chip_background_blue)
                OrderStatus.CONFIRM -> drawable = ContextCompat.getDrawable(context, R.drawable.chip_background_green)
                OrderStatus.PENDING -> drawable = ContextCompat.getDrawable(context, R.drawable.chip_background_orange)
                OrderStatus.DELIVERED -> drawable = ContextCompat.getDrawable(context, R.drawable.chip_background_purpule)
                OrderStatus.PAYMENT -> drawable = ContextCompat.getDrawable(context, R.drawable.chip_background_red)
                OrderStatus.FINISH -> drawable = ContextCompat.getDrawable(context, R.drawable.chip_background_white)
            }
            return drawable
        }

        fun sendWhatsAppMessage(context: Context ,number : String,message: String) {
           try {
               var url = "https://api.whatsapp.com/send?phone="+"+91 "+ number +"&text=" + URLEncoder.encode(message, "UTF-8");
               val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
               intent.setPackage("com.whatsapp");
               intent.data = Uri.parse(url);
               if(intent.resolveActivity(context.packageManager)!=null){
                   context.startActivity(intent)
               }
           } catch (e: PackageManager.NameNotFoundException) {
               Toast.makeText(context, "WhatsApp not Installed", Toast.LENGTH_SHORT).show()
           }
        }

    }


}

fun Context.hideKeyboard(view: View) {
    val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
}



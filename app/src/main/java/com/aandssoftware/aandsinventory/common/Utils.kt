package com.aandssoftware.aandsinventory.common

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.aandssoftware.aandsinventory.BuildConfig
import com.aandssoftware.aandsinventory.R
import com.aandssoftware.aandsinventory.ui.activity.ui.login.LoginActivity
import com.aandssoftware.aandsinventory.utilities.AppConstants
import com.aandssoftware.aandsinventory.utilities.AppConstants.Companion.EMPTY_STRING
import com.aandssoftware.aandsinventory.utilities.SharedPrefsUtils
import com.google.android.material.snackbar.Snackbar
import java.lang.Exception
import java.text.NumberFormat
import java.util.*
import android.util.Patterns

class Utils {

    companion object {

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
                return if (null != message && message.isNotEmpty()) message?.toDouble() else defaultVal
            } catch (e: Exception) {
                e.printStackTrace()
                return defaultVal;
            }
        }

        @JvmStatic
        fun isEmptyIntFromString(message: String?, defaultVal: Int): Int {
            try {
                return if (null != message && message.isNotEmpty()) isEmptyInt(message.toInt(), defaultVal.toString()).toInt() else defaultVal
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
        fun getAdminUsername(): String {
            return BuildConfig.USERNAME
        }

        @JvmStatic
        fun getAdminPass(): String {
            return BuildConfig.PASSWORD
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
        fun isAdminUser(context: Context): Boolean {
            return SharedPrefsUtils.getBooleanPreference(context, SharedPrefsUtils.ADMIN_USER, false)
        }


        @JvmStatic
        fun validateEmail(email: String): Boolean {
            val pattern = Patterns.EMAIL_ADDRESS
            return pattern.matcher(email).matches()
        }
    }


}

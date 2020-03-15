package com.aandssoftware.aandsinventory.utilities

import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.aandssoftware.aandsinventory.R
import com.crashlytics.android.Crashlytics
import com.google.android.material.snackbar.Snackbar

object CrashlaticsUtil {

    val TAG_INFO: String = "INFO"
    val TAG_ERROR: String = "ERROR"

    fun setVal(key: String, value: String) {
        Crashlytics.setString(key, value)
    }

    fun setVal(key: String, value: Boolean) {
        Crashlytics.setBool(key, value)
    }


    fun setVal(key: String, value: Double) {
        Crashlytics.setDouble(key, value)
    }

    fun setVal(key: String, value: Float) {
        Crashlytics.setFloat(key, value)
    }

    fun setVal(key: String, value: Int) {
        Crashlytics.setInt(key, value)
    }

    fun setVal(key: String, value: Long) {
        Crashlytics.setLong(key, value)
    }

    fun setUserName(value: String) {
        Crashlytics.setUserName(value)
    }

    fun setUserIdentifier(value: String) {
        Crashlytics.setUserIdentifier(value)
    }


    fun log(value: String) {
        Crashlytics.log(value)
    }

    fun logError(tag: String, value: String) {
        Crashlytics.log(Log.ERROR, tag, value)
        Log.e(tag, value)
    }

    fun logInfo(tag: String, value: String) {
        Crashlytics.log(Log.INFO, tag, value)
        Log.i(tag, value)
    }
}

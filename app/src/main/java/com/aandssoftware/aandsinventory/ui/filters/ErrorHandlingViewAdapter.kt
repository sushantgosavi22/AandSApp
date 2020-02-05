package com.aandssoftware.aandsinventory.ui.filters

import android.content.Context
import android.text.TextUtils
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatTextView
import com.aandssoftware.aandsinventory.R
import com.aandssoftware.aandsinventory.models.commonModel.CustomError

/** This class manage error handling for local error as well as server side errors.  */
class ErrorHandlingViewAdapter(
        private val context: Context,
        private var tvValue: AppCompatEditText,
        var tvError: AppCompatTextView,
        private val errorHandlingCallback: ErrorHandlingCallback?) {

    public var isErrorShowing: Boolean = false

    /** This method enable error indicator border for this view.  */
    private fun enableErrorBorder() {
        tvValue.background = context.resources.getDrawable(R.drawable.custome_edittext_error_background, context.theme)
    }

    private fun disableErrorBorder() {
        tvValue.background = context.resources.getDrawable(R.drawable.custome_editbox_selector, context.theme)
    }

    fun showErrorMessage(errorMsg: String?) {
        if (TextUtils.isEmpty(errorMsg)) {
            return
        }
        isErrorShowing = true
        if (tvError.visibility != View.VISIBLE) {
            tvError.visibility = View.VISIBLE
            tvError.text = errorMsg
            enableErrorBorder()
            errorHandlingCallback?.onErrorDisplayed()
        } else {
            tvError.text = errorMsg
            enableErrorBorder()
            errorHandlingCallback?.onErrorDisplayed()
        }
    }

    /** This method clear Error message and error border.  */
    internal fun dismissErrorMsg(): Boolean {
        tvError.text = ""
        disableErrorBorder()
        if (tvError.visibility == View.VISIBLE) {
            tvError.visibility = View.GONE
            errorHandlingCallback?.onErrorDismissed()
            return true
        }
        isErrorShowing = false
        return false
    }

    internal fun setErrorMsgView(tvValue: AppCompatEditText) {
        this.tvValue = tvValue
    }

    /**
     * This method will createAdditionalRows error messages for given error codes.
     *
     * @param errorSet Set of error code for this field.
     */
    fun showError(errorSet: Set<CustomError>) {
        for (error in errorSet) {
            if (null != error && null != error.errorMessage) {
                val errorMsg = error.errorMessage
                showErrorMessage(errorMsg)
            }
        }
    }

    /** Callback called when error get displayed / dismissed.  */
    interface ErrorHandlingCallback {

        /** Callback called when error get displayed.  */
        fun onErrorDisplayed() {
            // Do nothing.
        }

        /** Callback called when error get dismissed.  */
        fun onErrorDismissed()
    }
}

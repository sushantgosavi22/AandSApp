package com.aandssoftware.aandsinventory.ui.filters

import android.content.Context
import android.text.TextUtils
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatTextView
import com.aandssoftware.aandsinventory.R
import com.aandssoftware.aandsinventory.common.Utils
import com.aandssoftware.aandsinventory.models.commonModel.CustomError
import com.aandssoftware.aandsinventory.models.commonModel.ErrorShowType
import com.aandssoftware.aandsinventory.models.commonModel.ErrorType

/** This class manage error handling for local error as well as server side errors.  */
class ErrorHandlingViewAdapter(
        private val context: Context,
        private var tvValue: AppCompatEditText,
        var tvError: AppCompatTextView,
        private val errorHandlingCallback: ErrorHandlingCallback?) {

    var errorSet: MutableSet<CustomError> = HashSet<CustomError>()
    public var isErrorShowing: Boolean = false

    /** This method enable error indicator border for this view.  */
    private fun enableErrorBorder() {
        tvValue.background = context.resources.getDrawable(R.drawable.custome_edittext_error_background, context.theme)
    }

    private fun disableErrorBorder() {
        tvValue.background = context.resources.getDrawable(R.drawable.custome_editbox_selector, context.theme)
    }

    fun showErrorMessage(error: CustomError) {
        if (TextUtils.isEmpty(error.errorMessage)) {
            return
        }
        errorSet.add(error)
        isErrorShowing = true
        if (error.showType.toString().equals(ErrorShowType.INLINE_ERROR.toString(), ignoreCase = true)) {
            if (tvError.visibility != View.VISIBLE) {
                tvError.visibility = View.VISIBLE
                tvError.text = error.errorMessage
                enableErrorBorder()
                errorHandlingCallback?.onErrorDisplayed()
            } else {
                tvError.text = error.errorMessage
                enableErrorBorder()
                errorHandlingCallback?.onErrorDisplayed()
            }
        } else if (error.showType.toString().equals(ErrorShowType.SNACK_BAR.toString(), ignoreCase = true)) {
            if (tvValue.parent is LinearLayout) {
                var layout = tvValue.parent as LinearLayout
                error.errorMessage?.let { message ->
                    Utils.showSnackBarMessage(layout, message)
                    errorHandlingCallback?.onErrorDisplayed()
                }
            }
        } else if (error.showType.toString().equals(ErrorShowType.TOAST.toString(), ignoreCase = true)) {
            error.errorMessage?.let { message ->
                Utils.showToast(message, context)
                errorHandlingCallback?.onErrorDisplayed()
            }
        }
    }

    /** This method clear Error message and error border.  */
    internal fun dismissError(error: CustomError): Boolean {
        if (errorSet.contains(error)) {
            errorSet.remove(error)
        }

        if (errorSet.isEmpty()) {
            tvError.text = ""
            disableErrorBorder()
            if (tvError.visibility == View.VISIBLE) {
                tvError.visibility = View.GONE
                errorHandlingCallback?.onErrorDismissed()
            }
        }

        isErrorShowing = errorSet.isNotEmpty()
        return false
    }

    /** This method clear Error message and error border.  */
    internal fun dismissAllErrors(): Boolean {
        errorSet = HashSet<CustomError>()
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
            showErrorMessage(error)
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

    public fun isErrorStillShowing(): Boolean {
        if (isErrorShowing) {
            return true
        }
        if (errorSet.isNotEmpty()) {
            errorSet.forEach {
                showErrorMessage(it)
            }
        }
        return errorSet.isNotEmpty()
    }
}

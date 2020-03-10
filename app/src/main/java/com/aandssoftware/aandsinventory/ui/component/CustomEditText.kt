package com.aandssoftware.aandsinventory.ui.component

import android.content.Context
import android.content.res.TypedArray
import android.text.*
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.LinearLayoutCompat
import com.aandssoftware.aandsinventory.R
import com.aandssoftware.aandsinventory.common.Utils
import com.aandssoftware.aandsinventory.models.commonModel.CustomError
import com.aandssoftware.aandsinventory.models.commonModel.ErrorID
import com.aandssoftware.aandsinventory.models.commonModel.ErrorShowType
import com.aandssoftware.aandsinventory.models.commonModel.ErrorType
import com.aandssoftware.aandsinventory.ui.filters.DecimalDigitsInputFilter
import com.aandssoftware.aandsinventory.ui.filters.DigitsInputFilter
import com.aandssoftware.aandsinventory.ui.filters.ErrorHandlingViewAdapter
import java.util.*

open class CustomEditText : LinearLayoutCompat {

    private val INVALID_INT_ATTRIBUTE = -2
    private val DEFAULT_LINE = 1

    private var maxLength: Int = 0
    private var minLength: Int = 0
    private var title: String? = null
    private var maxLengthErrorMsg: String? = null
    private var mandatoryErrorMsg: String? = null
    private var validationErrorMsg: String? = null

    private lateinit var textChangedListener: TextChangedListener
    private lateinit var errorAdapter: ErrorHandlingViewAdapter
    protected lateinit var edtValue: AppCompatEditText
    private lateinit var tvError: AppCompatTextView
    private lateinit var tvTitle: AppCompatTextView
    private lateinit var tvMandatory: AppCompatTextView
    private var mandatory: Boolean = false
    var mobileNumberValidation: Boolean = false
    var emailValidation: Boolean = false

    constructor(context: Context) : super(context) {
        init(context, null)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context, attrs)
    }


    private fun init(context: Context, attrs: AttributeSet?) {
        val inflater = LayoutInflater.from(context)
        val viewEditText = getView(inflater)
        init(viewEditText)
        val attribute = context.obtainStyledAttributes(attrs, R.styleable.CustomEditTextViewStyle)
        setAttributes(attribute)
        addView(viewEditText)
        attribute.recycle()
    }

    private fun init(view: View) {
        edtValue = view.findViewById(R.id.edtValue)
        tvTitle = view.findViewById(R.id.tvName)
        tvError = view.findViewById(R.id.tvError)
        tvMandatory = view.findViewById(R.id.tvMandatory)
        errorAdapter = ErrorHandlingViewAdapter(context, edtValue, tvError, errorHandlingCallback)
    }


    private fun getView(layoutInflater: LayoutInflater): View {
        return layoutInflater.inflate(R.layout.custome_edit_text, null)
    }

    private fun setAttributes(attribute: TypedArray) {
        val hint = attribute.getString(R.styleable.CustomEditTextViewStyle_android_hint)
        val inputType = attribute.getInt(
                R.styleable.CustomEditTextViewStyle_android_inputType, InputType.TYPE_CLASS_TEXT)
        maxLength = attribute.getInt(R.styleable.CustomEditTextViewStyle_maxLength, INVALID_INT_ATTRIBUTE)
        minLength = attribute.getInt(R.styleable.CustomEditTextViewStyle_minLength, INVALID_INT_ATTRIBUTE)
        maxLengthErrorMsg = attribute.getString(R.styleable.CustomEditTextViewStyle_maxLengthErrorMsg)
        mandatoryErrorMsg = attribute.getString(R.styleable.CustomEditTextViewStyle_mandatoryErrorMsg)
        validationErrorMsg = attribute.getString(R.styleable.CustomEditTextViewStyle_validationErrorMsg)
        title = attribute.getString(R.styleable.CustomEditTextViewStyle_title)
        val gravity = attribute.getInt(R.styleable.CustomEditTextViewStyle_android_gravity, Gravity.LEFT)
        val ellipsize = attribute.getInt(R.styleable.CustomEditTextViewStyle_android_ellipsize, TextUtils.TruncateAt.START.ordinal)
        val lines = attribute.getInt(R.styleable.CustomEditTextViewStyle_android_lines, INVALID_INT_ATTRIBUTE)
        val maxLine = attribute.getInt(R.styleable.CustomEditTextViewStyle_android_maxLines, INVALID_INT_ATTRIBUTE)
        val enable = attribute.getBoolean(R.styleable.CustomEditTextViewStyle_android_enabled, true)
        mobileNumberValidation = attribute.getBoolean(R.styleable.CustomEditTextViewStyle_mobileValidation, false)
        emailValidation = attribute.getBoolean(R.styleable.CustomEditTextViewStyle_emailValidation, false)
        mandatory = attribute.getBoolean(R.styleable.CustomEditTextViewStyle_mandatory, false)
        val fieldContentDescription = attribute.getString(R.styleable.CustomEditTextViewStyle_fieldContentDescription)
        setEditTextContentDescription(fieldContentDescription)
        getInputFilterList(attribute)
        setInputType(inputType)
        setTitle(title)
        setMandatory(mandatory)
        setHint(hint)
        setGravityVal(gravity)
        setLines(lines)
        setMinLength(minLength)
        setMaxLines(maxLine)
        setEditableMode(enable)
        setEllipsize(ellipsize)
        val filterList = getInputFilterList(attribute)
        setInputFilters(filterList)
        defaultTextChangeListner()
    }


    private fun defaultTextChangeListner() {
        textChangedListener = object : CustomEditText.TextChangedListener {
            override fun afterTextChanged(text: String, isErrorMsgCleared: Boolean) {

            }
        }
        setTextChangedListener(textChangedListener)
    }

    /**
     * This method gives input filters to edit text.
     *
     * @param attribute attributes
     * @return List of input filters.
     */
    private fun getInputFilterList(attribute: TypedArray): List<InputFilter> {
        val filterList = ArrayList<InputFilter>()

        val digits = attribute.getString(R.styleable.CustomEditTextViewStyle_android_digits)
        digits?.let {
            val digitFilter = DigitsInputFilter(digits)
            filterList.add(digitFilter)
        }

        val decimalPlaces = attribute.getInt(R.styleable.CustomEditTextViewStyle_decimalPlaces, INVALID_INT_ATTRIBUTE)
        if (decimalPlaces != INVALID_INT_ATTRIBUTE) {
            val decimalInputFilter = DecimalDigitsInputFilter(decimalPlaces)
            filterList.add(decimalInputFilter)
        }
        return filterList
    }


    private fun checkMaxLength(textLength: Int) {
        var error = getLengthCustomError()
        if (textLength > maxLength) {
            if (maxLengthErrorMsg.isNullOrEmpty()) {
                val title = String.format(resources.getString(R.string.error_length_common), title, maxLength)
                error.errorMessage = title
                errorAdapter.showErrorMessage(error)
            } else {
                errorAdapter.showErrorMessage(error)
            }
        } else {
            if (errorAdapter.isErrorShowing) {
                errorAdapter.dismissError(error)
            }
        }
    }

    private fun getLengthCustomError(): CustomError {
        return CustomError(ErrorID.LENGTH_ERROR.ordinal, ErrorType.LOCAL_VALIDATION_ERROR.toString(), maxLengthErrorMsg, ErrorShowType.INLINE_ERROR.toString())
    }

    /**
     * This method set input filter's to edit text.
     *
     * @param filterList List of [InputFilter]
     */
    private fun setInputFilters(filterList: List<InputFilter>) {
        if (filterList.isNotEmpty()) {
            edtValue.filters = filterList.toTypedArray()
        }
    }

    /**
     * This method set content description to edit text.
     *
     * @param contentDescription content description string.
     */
    private fun setEditTextContentDescription(contentDescription: String?) {
        if (!TextUtils.isEmpty(contentDescription)) {
            edtValue.contentDescription = contentDescription
        }
    }

    /**
     * This method set input type for edit text.
     *
     * @param inputType input type.
     */
    private fun setInputType(inputType: Int) {
        edtValue.inputType = inputType
    }

    /**
     * This method set text to Edit text.
     *
     * @param text text.
     */
    fun setText(text: String?) {
        if (text != null) {
            edtValue.setText(text)
            if (maxLength != INVALID_INT_ATTRIBUTE) {
                checkMaxLength(text.length)
            }
        }
    }

    /**
     * This method return text entered by user in [EditText].
     *
     * @return text entered by user in [EditText].
     */
    fun getText(): String {
        return edtValue.text.toString()
    }

    /**
     * This method set max length attribute.
     *
     * @param maxLength allowed maximum length.
     */
    fun setMaxLength(maxLength: Int) {
        this.maxLength = maxLength
    }

    /**
     * This method set maximum length error message.
     *
     * @param maxLengthErrorMsg maximum length error message.
     */
    fun setMaxLengthErrorMsg(maxLengthErrorMsg: String) {
        this.maxLengthErrorMsg = maxLengthErrorMsg
    }

    /** This method set hint to Edit text.  */
    public fun setHint(hint: String?) {
        if (!TextUtils.isEmpty(hint)) {
            edtValue.hint = hint
        }
    }

    /** This method set hint to Edit text.  */
    private fun setGravityVal(gravity: Int) {
        edtValue.gravity = gravity
    }

    /** This method set title to Edit text.  */
    public fun setTitle(title: String?) {
        title?.let {
            tvTitle.text = title;
        }
    }

    /** This method set Mandatory to Edit text.  */
    private fun setMandatory(mandatory: Boolean) {
        tvMandatory.visibility = if (mandatory) View.VISIBLE else View.GONE
    }


    /** This method set Lines to Edit text.  */
    public fun setMaxLines(maxLines: Int) {
        maxLines.let {
            if (maxLines != INVALID_INT_ATTRIBUTE) {
                edtValue.maxLines = maxLines
            }
        }
    }

    /** This method set Lines to Edit text.  */
    public fun setLines(lines: Int) {
        lines.let {
            if (lines != INVALID_INT_ATTRIBUTE) {
                edtValue.setLines(lines)
            }
        }
    }


    /** This method set Lines to Edit text.  */
    private fun setMinLength(minLength: Int) {
        minLength.let {
            if (minLength != INVALID_INT_ATTRIBUTE) {
                //edtValue.setLines(minLength)
            }
        }
    }

    /** This method set ellipsize to Edit text.  */
    private fun setEllipsize(ellipsize: Int?) {
        ellipsize?.let {
            if (ellipsize == INVALID_INT_ATTRIBUTE) {
                val value = TextUtils.TruncateAt.values()[ellipsize]
                edtValue.ellipsize = value
            }
        }
    }

    /**
     * This method set the editable mode of Edit text based on boolean provided in param.
     *
     * @param isEditable - Status of Editable field in [Boolean].
     */
    public fun setEditableMode(isEditable: Boolean) {
        edtValue.isEnabled = isEditable
    }

    /** This method get the editable mode of Edit text.  */
    fun isEditableMode(): Boolean {
        return edtValue.isEnabled
    }

    var errorHandlingCallback: ErrorHandlingViewAdapter.ErrorHandlingCallback = object : ErrorHandlingViewAdapter.ErrorHandlingCallback {
        override fun onErrorDismissed() {
            //Not in use
        }
    }

    public fun setOnEditorActionListener(listner: TextView.OnEditorActionListener) {
        edtValue.setOnEditorActionListener(listner)
    }

    public fun setImeOptions(imeOption: Int) {
        edtValue.imeOptions = imeOption
    }


    /**
     * This method used to set callback for [TextChangedListener].
     *
     * @param textChangedListener instance of [TextChangedListener].
     */
    fun setTextChangedListener(textChangedListener: TextChangedListener) {
        edtValue.addTextChangedListener(
                object : TextWatcher {
                    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                        // We Don't need this callback.
                    }

                    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                        // We Don't need this callback.
                    }

                    override fun afterTextChanged(s: Editable) {
                        val input = s.toString()
                        if (maxLength != INVALID_INT_ATTRIBUTE) {
                            checkMaxLength(s.length)
                        }
                        textChangedListener.afterTextChanged(input, true)
                    }
                })
    }

    fun addTextChangedListener(textWatcher: TextWatcher) {
        edtValue.addTextChangedListener(textWatcher)
    }

    /** Interface provides callback for text change event for this view.  */
    interface TextChangedListener {

        /**
         * Callback called when text get changed.
         *
         * @param text updated text.
         * @param isErrorMsgCleared weather the error msg cleared of not.
         */
        fun afterTextChanged(text: String, isErrorMsgCleared: Boolean)
    }

    public fun onSubmitShowError(): Boolean {
        //check for mandatory filed
        checkAndInsertMandatoryFieldError()
        //check for mobile fields
        checkAndInsertMobileNumberFormatError()
        //check for Email fields
        checkAndInsertEmailFormatError()
        //check for Min Length fields
        checkAndInsertMinLengthValidationError(getText())

        return errorAdapter.isErrorStillShowing()
    }

    private fun checkAndInsertMandatoryFieldError() {
        var error = CustomError(ErrorID.MANDATORY_FIELD_ERROR.ordinal, ErrorType.LOCAL_VALIDATION_ERROR.toString(), context.getString(R.string.common_mandatory_error_message), ErrorShowType.INLINE_ERROR.toString())
        if (mandatory) {
            if (getText().trim().isEmpty()) {
                mandatoryErrorMsg?.let {
                    error.errorMessage = it
                }
                errorAdapter.showErrorMessage(error)
            } else {
                errorAdapter.dismissError(error)
            }
        } else {
            errorAdapter.dismissError(error)
        }
    }

    private fun checkAndInsertMobileNumberFormatError() {
        var error = CustomError(ErrorID.MAX_MOBILE_NO.ordinal, ErrorType.LOCAL_VALIDATION_ERROR.toString(), context.getString(R.string.common_error_message), ErrorShowType.INLINE_ERROR.toString())
        if (mobileNumberValidation) {
            if (getText().trim().isNotEmpty()) {
                if (maxLength != INVALID_INT_ATTRIBUTE) {
                    if (getText().trim().length != maxLength) {
                        validationErrorMsg?.let {
                            error.errorMessage = it
                        }
                        errorAdapter.showErrorMessage(error)
                    } else {
                        errorAdapter.dismissError(error)
                    }
                } else {
                    errorAdapter.dismissError(error)
                }
            } else {
                if (!mandatory) {
                    errorAdapter.dismissError(error)
                }
            }
        } else {
            errorAdapter.dismissError(error)
        }
    }

    private fun checkAndInsertEmailFormatError() {
        var error = CustomError(ErrorID.EMAIL_ERROR.ordinal, ErrorType.LOCAL_VALIDATION_ERROR.toString(), context.getString(R.string.common_error_message), ErrorShowType.INLINE_ERROR.toString())
        if (emailValidation) {
            if (!Utils.validateEmail(getText())) {
                validationErrorMsg?.let {
                    error.errorMessage = it
                }
                errorAdapter.showErrorMessage(error)
            } else {
                errorAdapter.dismissError(error)
            }
        } else {
            errorAdapter.dismissError(error)
        }
    }

    private fun checkAndInsertMinLengthValidationError(value: String) {
        var error = CustomError(ErrorID.MIN_LENGTH.ordinal, ErrorType.LOCAL_VALIDATION_ERROR.toString(), context.getString(R.string.common_error_message), ErrorShowType.INLINE_ERROR.toString())
        if (minLength != INVALID_INT_ATTRIBUTE) {
            if (value.length < minLength) {
                validationErrorMsg?.let {
                    error.errorMessage = it
                }
                errorAdapter.showErrorMessage(error)
            } else {
                errorAdapter.dismissError(error)
            }
        } else {
            errorAdapter.dismissError(error)
        }
    }
}


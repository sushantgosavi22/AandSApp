package com.aandssoftware.aandsinventory.ui.component

import android.content.Context
import android.content.res.TypedArray
import android.text.*
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.LinearLayoutCompat
import com.aandssoftware.aandsinventory.R
import com.aandssoftware.aandsinventory.ui.filters.DecimalDigitsInputFilter
import com.aandssoftware.aandsinventory.ui.filters.DigitsInputFilter
import com.aandssoftware.aandsinventory.ui.filters.ErrorHandlingViewAdapter
import java.util.*

class CustomEditText : LinearLayoutCompat {

    private val INVALID_INT_ATTRIBUTE = -2
    private val DEFAULT_LINE = 1

    private var maxLength: Int = 0
    private var minLength: Int = 0
    private var title: String? = null
    private var maxLengthErrorMsg: String? = null

    private lateinit var textChangedListener: TextChangedListener
    private lateinit var errorAdapter: ErrorHandlingViewAdapter
    private lateinit var edtValue: AppCompatEditText
    private lateinit var tvError: AppCompatTextView
    private lateinit var tvTitle: AppCompatTextView
    private lateinit var tvMandatory: AppCompatTextView

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
        title = attribute.getString(R.styleable.CustomEditTextViewStyle_title)
        val gravity = attribute.getInt(R.styleable.CustomEditTextViewStyle_android_gravity, Gravity.LEFT)
        val ellipsize = attribute.getInt(R.styleable.CustomEditTextViewStyle_android_ellipsize, TextUtils.TruncateAt.START.ordinal)
        val lines = attribute.getInt(R.styleable.CustomEditTextViewStyle_android_lines, INVALID_INT_ATTRIBUTE)
        val maxLine = attribute.getInt(R.styleable.CustomEditTextViewStyle_android_maxLines, INVALID_INT_ATTRIBUTE)
        val enable = attribute.getBoolean(R.styleable.CustomEditTextViewStyle_android_enabled, true)
        val mandatory = attribute.getBoolean(R.styleable.CustomEditTextViewStyle_mandatory, false)
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
        if (textLength > maxLength) {
            if (maxLengthErrorMsg.isNullOrEmpty()) {
                val title = String.format(resources.getString(R.string.error_length_common), title, maxLength)
                errorAdapter.showErrorMessage(title)
            } else {
                errorAdapter.showErrorMessage(maxLengthErrorMsg)
            }
        } else {
            if (errorAdapter.isErrorShowing) {
                errorAdapter.dismissErrorMsg()
            }
        }
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
    private fun setHint(hint: String?) {
        if (!TextUtils.isEmpty(hint)) {
            edtValue.hint = hint
        }
    }

    /** This method set hint to Edit text.  */
    private fun setGravityVal(gravity: Int) {
        edtValue.gravity = gravity
    }

    /** This method set title to Edit text.  */
    private fun setTitle(title: String?) {
        title?.let {
            tvTitle.text = title;
        }
    }

    /** This method set Mandatory to Edit text.  */
    private fun setMandatory(mandatory: Boolean) {
        tvMandatory.visibility = if (mandatory) View.VISIBLE else View.GONE
    }


    /** This method set Lines to Edit text.  */
    private fun setMaxLines(maxLines: Int) {
        maxLines.let {
            if (maxLines != INVALID_INT_ATTRIBUTE) {
                edtValue.maxLines = maxLines
            }
        }
    }

    /** This method set Lines to Edit text.  */
    private fun setLines(lines: Int) {
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
                        textChangedListener.afterTextChanged(input, true)
                        if (maxLength != INVALID_INT_ATTRIBUTE) {
                            checkMaxLength(s.length)
                        }
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
}


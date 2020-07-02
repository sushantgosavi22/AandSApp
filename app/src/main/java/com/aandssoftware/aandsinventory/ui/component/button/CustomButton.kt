package com.aandssoftware.aandsinventory.ui.component.button

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.widget.Button
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.aandssoftware.aandsinventory.R


open class CustomButton : AppCompatButton {
    var state: ButtonState = ButtonState.NORMAL
    var type: ButtonType = ButtonType.PRIMARY

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        setAttribute(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        setAttribute(context, attrs)
    }

    /**
     * Set the button attributes.
     *
     * @param [type] button type [ButtonType]
     * @param [buttonText] button text of type Android string resource,
     *                      if not passed default value is 0 in-case if you don't want to createAdditionalRows the text
     * @param [state] button visible state of type [ButtonState], by default is in NORMAL state of [ButtonState]
     */
    fun init(type: ButtonType, buttonText: Int = 0, state: ButtonState = ButtonState.NORMAL): Button {
        // Set the button selector
        setButtonType(type)
        setButtonState(state)
        setButtonText(buttonText)
        return this
    }

    /**
     * Set the button text.
     *
     * @param buttonTextId: String resource ID
     */
    fun setButtonText(buttonTextId: Int) {
        if (buttonTextId > 0) setButtonText(resources.getString(buttonTextId))
    }

    /**
     * Set the button text.
     *
     * @param buttonText: String button text
     */
    fun setButtonText(buttonText: String) {
        if (buttonText.isNotEmpty()) text = buttonText
        isAllCaps = false
        setTextSize(TypedValue.COMPLEX_UNIT_SP, 16.0f)
        typeface = ResourcesCompat.getFont(context, R.font.fontfamilybold)
        gravity = Gravity.CENTER
    }

    /**
     * Update the button state
     */
    fun setButtonState(state: ButtonState) {
        this.state = state
        when (state) {
            ButtonState.NORMAL -> {
                isEnabled = true
                isPressed = false
            }
            ButtonState.PRESSED -> {
                isEnabled = true
                isPressed = true
            }
            ButtonState.DISABLED -> isEnabled = false
        }
    }

    /**
     * Set the button background & selector
     */
    fun setButtonType(type: ButtonType) {
        this.type = type
        when (type) {
            /* ButtonType.CTA -> {
                 setBackgroundResource(R.drawable.selector_button_cta)
                 setTextColor(ContextCompat.getColor(context, R.color.color_button_cta_text))
             }*/
            ButtonType.PRIMARY -> {
                setBackgroundResource(R.drawable.custom_button_selector)
                setTextColor(ContextCompat.getColor(context, R.color.color_button_primary_text))
            }
            /* ButtonType.SECONDARY -> {
                 setBackgroundResource(R.drawable.selector_button_secondary)
                 setTextColor(ContextCompat.getColor(context, R.color.color_button_secondary_text))
             }*/
        }
    }


    /**
     * Set the attribute provided in xml.
     *
     * @param context : Activity context
     * @param attrs: Attributes provided in the xml.
     */
    private fun setAttribute(context: Context, attrs: AttributeSet?) {
        context.obtainStyledAttributes(attrs, R.styleable.CustomButton).apply {
            extractButtonState(this)
            extractButtonType(this)
            extractButtonText(this)
            recycle()
        }
    }

    /**
     * Extract the button text from the given [TypedArray].
     *
     * @param typedArray: array having the button attributes.
     */
    private fun extractButtonText(typedArray: TypedArray) =
            setButtonText(typedArray.getResourceId(R.styleable.CustomButton_android_text, 0))

    /**
     * Extract the button type from the given [TypedArray].
     *
     * @param typedArray: array having the button attributes.
     */
    private fun extractButtonType(typedArray: TypedArray) =
            typedArray.getInt(R.styleable.CustomButton_uiButtonType, 0).let { type ->
                when (type) {
                    0 -> setButtonType(ButtonType.PRIMARY)
                }
            }

    /**
     * Extract the button state from the given [TypedArray].
     *
     * @param typedArray: array having the button attributes.
     */
    private fun extractButtonState(typedArray: TypedArray) =
            typedArray.getInt(R.styleable.CustomButton_buttonState, 0).let {
                when (it) {
                    0 -> setButtonState(ButtonState.NORMAL)
                    1 -> setButtonState(ButtonState.PRESSED)
                    2 -> setButtonState(ButtonState.DISABLED)
                }
            }

}

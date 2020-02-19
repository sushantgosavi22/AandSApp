package com.aandssoftware.aandsinventory.ui.component

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.view.children

public class ContaintValidationLinerLayout : LinearLayoutCompat {

    var validate: Boolean = true
    var firstValidErrorOnEditText: CustomEditText? = null

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
        //currently nothing to do here
    }

    public fun validate(): Boolean {
        validate = true
        firstValidErrorOnEditText = null
        processAllViews(this)
        firstValidErrorOnEditText?.requestFocus()
        return validate
    }


    private fun processAllViews(viewGroup: ViewGroup) {
        for (child in viewGroup.children) {
            when (child) {
                is CustomEditText -> {
                    var editText = child
                    var validEditText = editText.onSubmitShowError()
                    if (validEditText) {
                        validate = validEditText.not()
                        if (firstValidErrorOnEditText == null) {
                            firstValidErrorOnEditText = editText
                        }
                    }
                }
                !is ViewGroup -> {
                    // Nothing to do here
                }
                else -> processAllViews(child as ViewGroup) // process inner layout
            }
        }
    }
}

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
import androidx.appcompat.widget.AppCompatAutoCompleteTextView
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

class CustomAutoCompleteEditText : CustomEditText {

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

    }


}


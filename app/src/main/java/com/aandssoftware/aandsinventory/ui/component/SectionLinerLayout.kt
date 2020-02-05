package com.aandssoftware.aandsinventory.ui.component

import android.content.Context
import android.content.res.TypedArray
import android.text.InputFilter
import android.text.InputType
import android.text.TextUtils
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.cardview.widget.CardView
import com.aandssoftware.aandsinventory.R
import com.aandssoftware.aandsinventory.ui.filters.DecimalDigitsInputFilter
import com.aandssoftware.aandsinventory.ui.filters.DigitsInputFilter
import java.util.*

public class SectionLinerLayout : LinearLayoutCompat {

    private lateinit var tvTitle: AppCompatTextView

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
        val attribute = context.obtainStyledAttributes(attrs, R.styleable.SectionLayoutStyle)
        setAttributes(attribute)
        addView(viewEditText)
        attribute.recycle()
    }

    private fun init(view: View) {
        tvTitle = view.findViewById(R.id.tvName)
    }

    private fun getView(layoutInflater: LayoutInflater): View {
        return layoutInflater.inflate(R.layout.section_layout, null)
    }

    private fun setAttributes(attribute: TypedArray) {
        var title = attribute.getString(R.styleable.SectionLayoutStyle_sectionHeader)
        setTitle(title)
    }

    /** This method set title to Edit text.  */
    private fun setTitle(title: String?) {
        title?.let {
            tvTitle.text = title;
        }
    }
}

package com.aandssoftware.aandsinventory.ui.adapters

import android.content.Context
import android.content.res.TypedArray
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import com.aandssoftware.aandsinventory.R


class CustomFontTextView(context: Context, attrs: AttributeSet) : AppCompatTextView(context, attrs) {

    private var typefaceType: Int = 0

    init {
        val array = context.theme.obtainStyledAttributes(
                attrs,
                R.styleable.CustomFontTextView,
                0, 0)
        try {
            typefaceType = array.getInteger(R.styleable.CustomFontTextView_font_name, 0)
        } finally {
            array.recycle()
        }
        if (VERSION.SDK_INT >= VERSION_CODES.CUPCAKE) {
            if (!isInEditMode) {
                //setTypeface(AandSApplication.getDatabaseInstance().getTypeFace(typefaceType));
            }
        }
    }

}

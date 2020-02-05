package com.aandssoftware.aandsinventory.ui.filters

import android.text.InputFilter
import android.text.SpannableString
import android.text.Spanned
import android.text.TextUtils

/** This is an Input filter to filter EditText text with digit attribute.  */
class DigitsInputFilter(private val digits: String) : InputFilter {

    override fun filter(
            source: CharSequence, start: Int, end: Int, dest: Spanned, dstart: Int, dend: Int): CharSequence? {
        return characterFilterText(source, start, end)
    }

    private fun characterFilterText(charSource: CharSequence, charStart: Int, end: Int): CharSequence? {
        var keepOriginalChar = true
        val builder = StringBuilder(end - charStart)

        for (i in charStart until end) {
            val c = charSource[i]
            if (digits.contains(c.toString())) {
                builder.append(c)
            } else {
                keepOriginalChar = false
            }
        }

        if (keepOriginalChar) {
            return null
        } else {
            if (charSource is Spanned) {
                val spString = SpannableString(builder)
                TextUtils.copySpansFrom(
                        charSource, charStart, builder.length, null, spString, 0)
                return spString
            } else {
                return builder
            }
        }
    }
}

package com.aandssoftware.aandsinventory.ui.filters

import android.text.InputFilter
import android.text.Spanned

/** This is am input filter used to restrict decimal places.  */
class DecimalDigitsInputFilter(private val decimalDigits: Int) : InputFilter {

    override fun filter(
            source: CharSequence, start: Int, end: Int, spanned: Spanned, decimalStart: Int, decimalEnd: Int): CharSequence? {
        return isMatched(spanned, decimalEnd)
    }

    protected fun isMatched(spanned: Spanned, decimalEnd: Int): String? {
        var dotPos = -1
        val len = spanned.length
        for (index in 0 until len) {
            val c = spanned[index]
            if (c == DOT) {
                dotPos = index
                break
            }
        }
        if (dotPos >= 0) {
            if (decimalEnd <= dotPos) {
                return null
            }
            if (len - dotPos > decimalDigits) {
                return EMPTY_STRING
            }
        }
        return null
    }

    companion object {

        private val DOT = '.'
        private val EMPTY_STRING = ""
    }
}

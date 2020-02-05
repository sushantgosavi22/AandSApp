package com.aandssoftware.aandsinventory.ui.component

import android.app.Activity
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.WindowManager
import android.widget.ProgressBar

class CustomProgressBar : ProgressBar {

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    private fun init() {
        // not in use now
    }

    fun showProgressBar() {
        if (context is Activity) {
            (this.context as Activity).window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }

        visibility = View.VISIBLE
    }

    /**
     * Hide Progress Bar.
     */
    fun dismissProgressBar() {
        if (context is Activity) {
            (this.context as Activity).window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        }
        visibility = View.GONE
    }
}

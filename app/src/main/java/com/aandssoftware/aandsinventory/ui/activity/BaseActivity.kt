package com.aandssoftware.aandsinventory.ui.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.aandssoftware.aandsinventory.R
import com.aandssoftware.aandsinventory.common.Utils
import kotlinx.android.synthetic.main.activity_listing.*

open class BaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    @JvmOverloads
    fun setupActionBar(title: String, backButtonVisibility: Boolean = true) {
        supportActionBar?.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
        supportActionBar?.setDisplayShowCustomEnabled(true)
        val inflater = LayoutInflater.from(this)
        val customView = inflater.inflate(R.layout.custom_action_bar_layout, null)
        supportActionBar?.customView = customView
        supportActionBar?.elevation = 4f
        val parent = customView.parent as Toolbar
        parent.setContentInsetsAbsolute(0, 0)
        setScreenTitle(title, backButtonVisibility)
    }

    private fun setScreenTitle(title: String, backButtonVisibility: Boolean) {
        val actionBarTitle = supportActionBar?.customView?.findViewById<TextView>(R.id.actionBarTitle)
        actionBarTitle?.text = title
        val backButton = supportActionBar?.customView?.findViewById<ImageButton>(R.id.navBarBack)
        backButton?.setOnClickListener { v -> finish() }
        backButton?.visibility = if (backButtonVisibility) View.VISIBLE else View.GONE
    }

    fun setActionBarTitleVisibility(visibility: Int) {
        val actionBarTitle = supportActionBar?.customView?.findViewById<TextView>(R.id.actionBarTitle)
        actionBarTitle?.visibility = visibility
    }

    private fun setScreenSubTitle(subTitle: String) {
        val actionBarSubTitle = supportActionBar?.customView?.findViewById<TextView>(R.id.actionBarSubTitle)
        actionBarSubTitle?.visibility = View.VISIBLE
        actionBarSubTitle?.text = subTitle
    }

    fun showSnackBarMessage(message: String) {
        Utils.showSnackBarMessage(findViewById<ViewGroup>(android.R.id.content), message)
    }

    /**
     * Show Progress Bar.
     */
    fun showProgressBar() {
        progress_bar?.let {
            progress_bar.showProgressBar()
        }
    }

    /**
     * Hide Progress Bar.
     */
    fun dismissProgressBar() {
        progress_bar?.let {
            progress_bar.dismissProgressBar()
        }
    }
}

package com.aandssoftware.aandsinventory.ui.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.aandssoftware.aandsinventory.R
import com.aandssoftware.aandsinventory.common.Utils
import com.aandssoftware.aandsinventory.firebase.FirebaseUtil
import com.aandssoftware.aandsinventory.models.CustomerModel
import com.aandssoftware.aandsinventory.ui.component.CustomEditText
import com.comix.overwatch.HiveProgressView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_listing.*
import java.util.ArrayList

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

    public fun setScreenSubTitle(subTitle: String) {
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

fun BaseActivity.feedbackDialog(activity : BaseActivity, sendMailToAllCustomer : Boolean) {
    val alertDialogBuilderUserInput = AlertDialog.Builder(activity)
    var view: View = LayoutInflater.from(activity).inflate(R.layout.feedback_dialog, null)
    alertDialogBuilderUserInput
            .setView(view)
            .setCancelable(false)
            .setPositiveButton(activity.getString(R.string.send)) {dialogBox, _ ->
                var feedbackTitle = view.findViewById<CustomEditText>(R.id.tvFeedbackTitle).getText()
                var feedbackDescription = view.findViewById<CustomEditText>(R.id.edtFeedbackDescription).getText()
                if (feedbackTitle.isNotEmpty() && feedbackDescription.isNotEmpty()) {
                    getAdminMailAndSendMail(sendMailToAllCustomer,feedbackTitle,feedbackDescription)
                    dialogBox.cancel()
                } else {
                    activity.showSnackBarMessage(activity.getString(R.string.common_mandatory_error_message))
                }
            }
            .setNegativeButton(activity.getString(R.string.cancel))
            { dialogBox, _ -> dialogBox.cancel() }

    val alertDialog = alertDialogBuilderUserInput.create()
    alertDialog.show()
}

fun BaseActivity. getAdminMailAndSendMail(sendMailToAllCustomer : Boolean,subject: String,body: String){
    var activity = this;
    if(sendMailToAllCustomer){
        activity.showProgressBar()
        FirebaseUtil.getInstance().getCustomerDao().getAllCustomers(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                activity.dismissProgressBar()
                val result = ArrayList<String>()
                if (null != dataSnapshot.value) {
                    for (children in dataSnapshot.children) {
                        val model = children.getValue(CustomerModel::class.java)
                        model?.companyMail?.let {
                            result.add(it)
                        }
                    }
                    if(result.isNotEmpty()){
                        Utils.sendMail(activity,result.toTypedArray(),subject,body)
                    }
                }

            }

            override fun onCancelled(databaseError: DatabaseError) {
                activity.dismissProgressBar()
            }
        })

    }else{
        Utils.sendMailToAdmin(activity,subject,body)
    }
}
package com.aandssoftware.aandsinventory.ui.activity.ui.login

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView.OnEditorActionListener
import androidx.appcompat.app.AlertDialog
import com.aandssoftware.aandsinventory.BuildConfig
import com.aandssoftware.aandsinventory.R
import com.aandssoftware.aandsinventory.common.Navigator
import com.aandssoftware.aandsinventory.firebase.FirebaseUtil
import com.aandssoftware.aandsinventory.models.*
import com.aandssoftware.aandsinventory.ui.activity.BaseActivity
import com.aandssoftware.aandsinventory.ui.activity.CarouselDashboardActivity
import com.aandssoftware.aandsinventory.ui.component.ContaintValidationLinerLayout
import com.aandssoftware.aandsinventory.ui.component.CustomEditText
import com.aandssoftware.aandsinventory.ui.component.SectionLinerLayout
import com.aandssoftware.aandsinventory.utilities.AppConstants
import com.aandssoftware.aandsinventory.utilities.SharedPrefsUtils
import com.aandssoftware.aandsinventory.utilities.SharedPrefsUtils.Companion.CURRENT_USER
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_login.*
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import com.aandssoftware.aandsinventory.utilities.CrashlaticsUtil
import com.crashlytics.android.Crashlytics
import org.apache.poi.ss.formula.functions.T

class LoginActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        setUpUI()
    }

    private fun setUpUI() {
        btnLogin.setOnClickListener {
            if (validate()) {
                checkVersionAndLogin()
            }
        }
        tvPassword.setImeOptions(EditorInfo.IME_ACTION_DONE)
        tvPassword.setOnEditorActionListener(OnEditorActionListener { _, actionId, event ->
            if (event != null && event.keyCode === KeyEvent.KEYCODE_ENTER || actionId == EditorInfo.IME_ACTION_DONE) {
                btnLogin.performClick()
            }
            false
        })

        btnRegister.setOnClickListener {
            Navigator.openCustomerScreen(this, AppConstants.EMPTY_STRING, AppConstants.EMPTY_STRING, ViewMode.ADD.ordinal, getString(R.string.company_details))
        }

        tvForgotPass.setOnClickListener {
            if (validaForgotPassword()) {
                getUserDetailsAndCheck(this)
            }
        }
    }

    private fun validate(): Boolean {
        var shouldLogin: Boolean = false
        var username = tvUsername.getText()
        var password = tvPassword.getText()
        if (username.isNotEmpty()) {
            if (password.isNotEmpty()) {
                shouldLogin = true
            } else {
                showSnackBarMessage(getString(R.string.enter_password))
            }
        } else {
            showSnackBarMessage(getString(R.string.enter_username))
        }
        return shouldLogin;
    }

    private fun performLogin() {
        showProgressBar()
        if (FirebaseUtil.getInstance().isInternetConnected(this)) {
            FirebaseUtil.getInstance().getCustomerDao().getCustomerFromUserNameAndPassword(tvUsername.getText(), object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                    dismissProgressBar()
                }

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    dismissProgressBar()
                    dataSnapshot.let {
                        var model = dataSnapshot.children.first().getValue(CustomerModel::class.java)
                        model?.let {
                            if (model.blockedUser.not()) {
                                val username = model.username
                                val password = model.password
                                if (tvUsername.getText().equals(username)) {
                                    if (tvPassword.getText().equals(password, ignoreCase = true)) {
                                        SharedPrefsUtils.setUserPreference(this@LoginActivity, CURRENT_USER, model)
                                        var isAdminUser = model.permission?.containsValue(Permissions.ADMIN.toString())
                                                ?: false
                                        SharedPrefsUtils.setBooleanPreference(this@LoginActivity, SharedPrefsUtils.ADMIN_USER, isAdminUser)
                                        model.id?.let { id -> CrashlaticsUtil.setUserIdentifier(id) }
                                        model.username?.let { username ->
                                            CrashlaticsUtil.setUserName(username)
                                            CrashlaticsUtil.logInfo(CrashlaticsUtil.TAG_INFO, username)
                                        }
                                        openDashboardActivity()
                                    } else {
                                        showSnackBarMessage(getString(R.string.password_not_match))
                                    }
                                } else {
                                    showSnackBarMessage(getString(R.string.username_not_found))
                                }
                            } else {
                                var message = model.blockedUserMessage
                                        ?: getString(R.string.common_blocked_user_message)
                                showSnackBarMessage(message)
                            }
                        }
                    }
                }
            })
        } else {
            showSnackBarMessage(getString(R.string.no_internet_connection))
        }
    }

    private fun openDashboardActivity() {
        var intent = Intent(this, CarouselDashboardActivity::class.java);
        startActivity(intent);
        finish()
    }


    private fun checkVersionAndLogin() {
        FirebaseUtil.getInstance().getCustomerDao().getAppVersion(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {}
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val appVersion = FirebaseUtil.getInstance()
                        .getClassData(dataSnapshot, AppVersion::class.java)
                appVersion?.let {
                    if (appVersion.forceUpdate) {
                        redirectToPlayStore()
                        finish()
                    } else if (appVersion.recomondedUpdate) {
                        redirectToPlayStore()
                    } else {
                        if ((BuildConfig.VERSION_CODE < appVersion.updatedVersionCode.toInt())) {
                            redirectToPlayStore()
                            finish()
                        } else {
                            performLogin()
                        }
                    }
                }
            }
        })
    }

    private fun redirectToPlayStore() {
        val appPackageName = packageName // package name of the app
        try {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$appPackageName")))
        } catch (anfe: android.content.ActivityNotFoundException) {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName")))
        }
    }

    private fun validaForgotPassword(): Boolean {
        if (tvUsername.getText().isEmpty()) {
            showSnackBarMessage(getString(R.string.enter_username))
            return false
        }
        return true
    }

    private fun checkForgotPasswordDetails(model: CustomerModel, context: Context) {
        val alertDialogBuilderUserInput = AlertDialog.Builder(context)
        var view: View = LayoutInflater.from(context).inflate(R.layout.forgot_password_check_dialog, null)
        var slForgotPassword = view.findViewById<ContaintValidationLinerLayout>(R.id.llContaint)
        var edtEmail = view.findViewById<CustomEditText>(R.id.edtEmail)
        var edtMobileNumber = view.findViewById<CustomEditText>(R.id.edtMobileNumber)
        alertDialogBuilderUserInput
                .setView(view)
                .setCancelable(false)
                .setPositiveButton(context.getString(R.string.yes)) { _, _ -> }
                .setNegativeButton(context.getString(R.string.no))
                { dialogBox, _ -> dialogBox.cancel() }

        val alertDialog = alertDialogBuilderUserInput.create()
        alertDialog.setCancelable(false)
        alertDialog.setCanceledOnTouchOutside(false)
        alertDialog.show()
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            if (slForgotPassword.validate()) {
                model.companyMail?.let { mail ->
                    model.customerNumber?.let { number ->
                        var customerId = model.id ?: AppConstants.EMPTY_STRING
                        var numericcCustomerId = model.customerID ?: AppConstants.EMPTY_STRING
                        if (mail.trim().equals(edtEmail.getText().trim(), false) &&
                                number.trim().equals(edtMobileNumber.getText().trim(), false)) {
                            Navigator.openCustomerScreen(LoginActivity@ this, customerId, numericcCustomerId, ViewMode.PASSWORD_UPDATE.ordinal, getString(R.string.forgot_password))
                        } else {
                            showSnackBarMessage(getString(R.string.email_and_number_not_match))
                        }
                    }
                }
            }
        }
    }

    private fun getUserDetailsAndCheck(context: Context) {
        showProgressBar()
        if (FirebaseUtil.getInstance().isInternetConnected(this)) {
            FirebaseUtil.getInstance().getCustomerDao().getCustomerFromUserNameAndPassword(tvUsername.getText(), object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                    dismissProgressBar()
                }

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    dismissProgressBar()
                    dataSnapshot.let {
                        var model = dataSnapshot.children.first().getValue(CustomerModel::class.java)
                        model?.let {
                            if (model.blockedUser.not()) {
                                checkForgotPasswordDetails(model, context)
                            } else {
                                var message = model.blockedUserMessage
                                        ?: getString(R.string.common_blocked_user_message)
                                showSnackBarMessage(message)
                            }
                        }
                    }
                }
            })
        } else {
            showSnackBarMessage(getString(R.string.no_internet_connection))
        }
    }
}


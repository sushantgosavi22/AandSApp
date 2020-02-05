package com.aandssoftware.aandsinventory.ui.activity.ui.login

import android.content.Intent
import android.os.Bundle
import com.aandssoftware.aandsinventory.R
import com.aandssoftware.aandsinventory.common.Navigator
import com.aandssoftware.aandsinventory.common.Utils
import com.aandssoftware.aandsinventory.firebase.FirebaseUtil
import com.aandssoftware.aandsinventory.models.CustomerModel
import com.aandssoftware.aandsinventory.models.ViewMode
import com.aandssoftware.aandsinventory.models.callBackListener
import com.aandssoftware.aandsinventory.ui.activity.BaseActivity
import com.aandssoftware.aandsinventory.ui.activity.CarouselDashboardActivity
import com.aandssoftware.aandsinventory.utilities.AppConstants
import com.aandssoftware.aandsinventory.utilities.SharedPrefsUtils
import com.aandssoftware.aandsinventory.utilities.SharedPrefsUtils.Companion.CURRENT_USER
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        btnLogin.setOnClickListener {
            if (validate() && check()) {
                performLogin()
            }
        }

        btnRegister.setOnClickListener {
            Navigator.openCustomerScreen(this, AppConstants.EMPTY_STRING, AppConstants.EMPTY_STRING, ViewMode.ADD.ordinal, getString(R.string.company_details))
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
        FirebaseUtil.getInstance().isConnected(callBackListener { online ->
            if (online) {
                FirebaseUtil.getInstance().getCustomerDao().getCustomerFromUserNameAndPassword(tvUsername.getText(), object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {
                        dismissProgressBar()
                    }

                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        dismissProgressBar()
                        dataSnapshot.let {
                            var model = dataSnapshot.children.first().getValue(CustomerModel::class.java)
                            model?.let {
                                val username = model.username
                                val password = model.password
                                if (tvUsername.getText().equals(username)) {
                                    if (tvPassword.getText().equals(password, ignoreCase = true)) {
                                        SharedPrefsUtils.setUserPreference(this@LoginActivity, CURRENT_USER, model)
                                        SharedPrefsUtils.setBooleanPreference(this@LoginActivity, SharedPrefsUtils.ADMIN_USER, false)
                                        openDashboardActivity()
                                    } else {
                                        showSnackBarMessage(getString(R.string.password_not_match))
                                    }
                                } else {
                                    showSnackBarMessage(getString(R.string.username_not_found))
                                }
                            }
                        }
                    }
                })
            } else {
                showSnackBarMessage(getString(R.string.no_internet_connection))
            }
        })
    }

    private fun openDashboardActivity() {
        var intent = Intent(this, CarouselDashboardActivity::class.java);
        startActivity(intent);
        finish()
    }

    private fun check(): Boolean {
        var check: Boolean = true
        if (tvUsername.getText().equals(Utils.getAdminUsername())) {
            if (tvPassword.getText().equals(Utils.getAdminPass(), ignoreCase = true)) {
                check = false
                SharedPrefsUtils.setBooleanPreference(this, SharedPrefsUtils.ADMIN_USER, true)
                openDashboardActivity()
                finish()
            }
        }
        return check
    }
}


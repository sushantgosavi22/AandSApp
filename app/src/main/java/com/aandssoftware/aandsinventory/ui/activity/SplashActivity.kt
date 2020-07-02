package com.aandssoftware.aandsinventory.ui.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import androidx.annotation.Nullable
import com.aandssoftware.aandsinventory.R
import com.aandssoftware.aandsinventory.ui.activity.ui.login.LoginActivity
import com.aandssoftware.aandsinventory.utilities.AppConstants.Companion.ANDROID_APP_UPDATE
import com.aandssoftware.aandsinventory.utilities.AppConstants.Companion.LOG
import com.aandssoftware.aandsinventory.utilities.AppConstants.Companion.SPLASH_TIME
import com.aandssoftware.aandsinventory.utilities.CrashlaticsUtil
import com.aandssoftware.aandsinventory.utilities.SharedPrefsUtils
import com.google.android.material.snackbar.Snackbar
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.InstallState
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability


class SplashActivity : BaseActivity(), InstallStateUpdatedListener {

    lateinit var mAppUpdateManager: AppUpdateManager

    override fun onStateUpdate(state: InstallState?) {
        if (state?.installStatus() == InstallStatus.DOWNLOADED) {
            popupSnackbarForCompleteUpdate()
        } else if (state?.installStatus() == InstallStatus.INSTALLED) {
            if (mAppUpdateManager != null) {
                mAppUpdateManager.unregisterListener(this)
            }
        } else {
            Log.i(LOG, "InstallStateUpdatedListener: state: " + state?.installStatus())
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        checkUpdate()
        showIntentData(intent)
    }

    private fun checkUpdate() {
        mAppUpdateManager = AppUpdateManagerFactory.create(this);
        mAppUpdateManager.registerListener(this)
        mAppUpdateManager.appUpdateInfo.addOnSuccessListener {
            if (it.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE &&
                    it.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)) {
                mAppUpdateManager.startUpdateFlowForResult(
                        it, AppUpdateType.FLEXIBLE, SplashActivity@ this, ANDROID_APP_UPDATE);
            } else if (it.installStatus() == InstallStatus.DOWNLOADED) {
                popupSnackbarForCompleteUpdate();
            } else {
                showLogin()
            }
        }
        mAppUpdateManager.appUpdateInfo.addOnFailureListener {
            showLogin()
        }
    }

    private fun showLogin() {
        Handler().postDelayed({
            val user = SharedPrefsUtils.getUserPreference(this, SharedPrefsUtils.CURRENT_USER)
            var intentLogin = Intent(this@SplashActivity, LoginActivity::class.java)
            if (user != null) {
                intentLogin = Intent(this@SplashActivity, CarouselDashboardActivity::class.java)
            }
            intent?.extras?.let {
                intentLogin.putExtras(it)
            }
            startActivity(intentLogin)
            finish()
        }, SPLASH_TIME)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, @Nullable data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ANDROID_APP_UPDATE) {
            if (resultCode != Activity.RESULT_OK) {
                showLogin()
                Log.e(LOG, "onActivityResult: app download failed")
            }
        }
    }

    private fun popupSnackbarForCompleteUpdate() {
        val snackbar = Snackbar.make(
                findViewById<View>(R.id.llSplash),
                "New app is ready!",
                Snackbar.LENGTH_INDEFINITE)
        snackbar.setAction("Install") { view ->
            if (mAppUpdateManager != null) {
                mAppUpdateManager.completeUpdate()
            }
        }
        snackbar.setActionTextColor(resources.getColor(R.color.brand_color_primary))
        snackbar.show()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        showIntentData(intent)
    }

    private fun showIntentData(intent: Intent?) {
        val extras = intent?.extras
        if (extras != null) {
            for (key in extras.keySet()) {
                val value = extras.get(key)
                //CrashlaticsUtil.logInfo(CrashlaticsUtil.TAG_INFO, "Extras received at onNewIntent:  Key: $key Value: $value")
            }
        }
    }
}

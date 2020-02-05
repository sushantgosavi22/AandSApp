package com.aandssoftware.aandsinventory.ui.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.aandssoftware.aandsinventory.R
import com.aandssoftware.aandsinventory.ui.activity.ui.login.LoginActivity
import com.aandssoftware.aandsinventory.utilities.AppConstants.Companion.SPLASH_TIME
import com.aandssoftware.aandsinventory.utilities.SharedPrefsUtils

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        Handler().postDelayed({
            val user = SharedPrefsUtils.getUserPreference(this, SharedPrefsUtils.CURRENT_USER)
            var intent = Intent(this@SplashActivity, LoginActivity::class.java)
            if (user != null) {
                intent = Intent(this@SplashActivity, CarouselDashboardActivity::class.java)
            }
            startActivity(intent)
            finish()
        }, SPLASH_TIME)
    }
}

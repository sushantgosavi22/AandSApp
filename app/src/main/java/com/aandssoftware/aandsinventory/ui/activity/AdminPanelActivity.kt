package com.aandssoftware.aandsinventory.ui.activity

import android.os.Bundle
import com.aandssoftware.aandsinventory.R
import kotlinx.android.synthetic.main.activity_admin_panel.*

class AdminPanelActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_panel)
        setupActionBar(getString(R.string.admin_panel),true)
        slMailToAllCustomer.setOnClickListener {
            feedbackDialog(this,true)
        }
    }
}
package com.aandssoftware.aandsinventory.ui.activity

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.GridLayoutManager
import com.aandssoftware.aandsinventory.R
import com.aandssoftware.aandsinventory.common.Navigator
import com.aandssoftware.aandsinventory.common.Utils
import com.aandssoftware.aandsinventory.firebase.FirebaseUtil
import com.aandssoftware.aandsinventory.models.CarouselMenuModel
import com.aandssoftware.aandsinventory.notification.NotificationUtil
import com.aandssoftware.aandsinventory.ui.adapters.CarouselMenuAdapter
import com.aandssoftware.aandsinventory.utilities.SharedPrefsUtils
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.iid.FirebaseInstanceId
import kotlinx.android.synthetic.main.activity_carousel_dashboard_actvity.*
import kotlinx.android.synthetic.main.custom_action_bar_layout.*
import java.util.*


class CarouselDashboardActivity : BaseActivity() {

    lateinit var menuAdapter: CarouselMenuAdapter
    private var carouselMenuModels: MutableList<CarouselMenuModel> = ArrayList()

    private var valueEventListener: ValueEventListener = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            dismissProgressBar()
            carouselMenuModels.clear()
            if (dataSnapshot.children.iterator().hasNext()) {
                var permission: HashMap<String, String>? = null
                var model = SharedPrefsUtils.getUserPreference(this@CarouselDashboardActivity, SharedPrefsUtils.CURRENT_USER)
                model?.let {
                    permission = it.permission
                }
                for (snapshot in dataSnapshot.children) {
                    val carouselItem = snapshot.getValue(CarouselMenuModel::class.java)
                    carouselItem?.permissions?.let { carouselPermission ->
                        val items = carouselPermission.split(",")
                        for (index in items.indices) {
                            if (permission?.values?.contains(items[index])!!) {
                                carouselMenuModels.add(carouselItem)
                                break
                            }
                        }
                    }
                    carouselMenuModels = carouselMenuModels.sortedWith(compareBy(CarouselMenuModel::permissions, CarouselMenuModel::tag)).toMutableList()

                }
                setUpList()
            } else {
                FirebaseUtil.getInstance().getCarouselDao().insertCarousalItems()
            }
        }

        override fun onCancelled(databaseError: DatabaseError) {}
    }


    private val adapterInstance: CarouselMenuAdapter
        get() {
            menuAdapter = CarouselMenuAdapter(this, carouselMenu)
            return menuAdapter
        }

    private val carouselMenu: List<CarouselMenuModel> get() = carouselMenuModels

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_carousel_dashboard_actvity)
        init()
        saveMenu()
        getToken()
    }

    private fun init() {
        setupActionBar(getString(R.string.dashboard))
        navBarBack?.setOnClickListener {
            onBackPressed()
        }
    }

    private fun saveMenu() {
        showProgressBar()
        FirebaseUtil.getInstance().getCarouselDao().getCarousalItems(valueEventListener)
    }

    private fun setUpList() {
        val gridLayoutManager = GridLayoutManager(this, 3)
        recyclerView.layoutManager = gridLayoutManager
        recyclerView.adapter = adapterInstance
        menuAdapter.notifyDataSetChanged()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onBackPressed() {
        val alertDialogBuilderUserInput = AlertDialog.Builder(this)
        alertDialogBuilderUserInput
                .setTitle(getString(R.string.exit_app_title))
                .setMessage(getString(R.string.exit_app_message))
                .setCancelable(false)
                .setPositiveButton(getString(R.string.exit)
                ) { dialogBox, id ->
                    dialogBox.cancel()
                    super@CarouselDashboardActivity.onBackPressed()
                    finish()
                }
                .setNegativeButton(getString(R.string.no)
                ) { dialogBox, id -> dialogBox.cancel() }

        val alertDialog = alertDialogBuilderUserInput.create()
        alertDialog.show()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.carousel_dashboard_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.actionLogout -> {
                Utils.Logout(this)
                return true
            }
        }
        return true
    }

    private fun getToken() {
        FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener(this) { instanceIdResult ->
            val token = instanceIdResult.token
            NotificationUtil.onNewToken(token, this)
        }

        var bundle = intent?.extras
        if (bundle?.containsKey(NotificationUtil.FLOW_ID) == true) {
            if (bundle.get(NotificationUtil.FLOW_ID).toString().equals(NotificationUtil.NOTIFICATION_FLOW)) {
                if (bundle.get(NotificationUtil.NOTIFICATION_TYPE).toString().equals(NotificationUtil.NotificationType.ORDER_CONFIRM_INDICATE_TO_ADMIN.toString(), ignoreCase = true)) {
                    sendToOrderDetail(bundle)
                } else if (bundle.get(NotificationUtil.NOTIFICATION_TYPE).toString().equals(NotificationUtil.NotificationType.ORDER_DELIVERED_INDICATE_TO_COMPANY.toString(), ignoreCase = true)) {
                    sendToOrderDetail(bundle)
                } else if (bundle.get(NotificationUtil.NOTIFICATION_TYPE).toString().equals(NotificationUtil.NotificationType.ORDER_PAYMNT_INDICATE_TO_COMPANY.toString(), ignoreCase = true)) {
                    sendToOrderDetail(bundle)
                }
            }
        }
    }

    private fun sendToOrderDetail(bundle: Bundle?) {
        var orderId = bundle?.get(NotificationUtil.ORDER_ID).toString()
        var customerId = bundle?.get(NotificationUtil.CUSTOMER_ID).toString()
        Navigator.openOrderDetailsScreen(this, orderId, intent)
    }
}

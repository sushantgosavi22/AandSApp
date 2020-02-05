package com.aandssoftware.aandsinventory.ui.activity

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.GridLayoutManager
import com.aandssoftware.aandsinventory.R
import com.aandssoftware.aandsinventory.common.Utils
import com.aandssoftware.aandsinventory.firebase.FirebaseUtil
import com.aandssoftware.aandsinventory.models.CarouselMenuModel
import com.aandssoftware.aandsinventory.models.Permissions
import com.aandssoftware.aandsinventory.ui.adapters.CarouselMenuAdapter
import com.aandssoftware.aandsinventory.utilities.AppConstants.Companion.EMPTY_STRING
import com.aandssoftware.aandsinventory.utilities.SharedPrefsUtils
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_carousel_dashboard_actvity.*
import kotlinx.android.synthetic.main.custom_action_bar_layout.*
import java.util.*
import kotlin.collections.HashMap
import java.util.Arrays.asList
import androidx.core.app.ComponentActivity.ExtraData
import org.apache.poi.ss.formula.functions.T
import androidx.core.content.ContextCompat.getSystemService


class CarouselDashboardActivity : BaseActivity() {

    lateinit var menuAdapter: CarouselMenuAdapter
    private var carouselMenuModels: MutableList<CarouselMenuModel> = ArrayList()

    private var valueEventListener: ValueEventListener = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            dismissProgressBar()
            carouselMenuModels.clear()
            if (dataSnapshot.children.iterator().hasNext()) {
                var userType = SharedPrefsUtils.getBooleanPreference(this@CarouselDashboardActivity, SharedPrefsUtils.ADMIN_USER, false)
                var permission: HashMap<String, String>? = null
                if (!userType) {
                    var model = SharedPrefsUtils.getUserPreference(this@CarouselDashboardActivity, SharedPrefsUtils.CURRENT_USER)
                    model?.let {
                        permission = it.permission
                    }
                } else {
                    permission = HashMap<String, String>()
                    permission?.put(Permissions.ADMIN.toString(), Permissions.ADMIN.toString())
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
                }
                setUpList()
            } else {
                FirebaseUtil.getInstance().getCarouselDao().insertCarousalItems()
            }
        }

        override fun onCancelled(databaseError: DatabaseError) {}
    }

    private fun getPermission(permissions: HashMap<String, String>): String {
        var value: String = ""
        permissions.forEach {
            value.plus(it.value)
        }
        return value
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
}

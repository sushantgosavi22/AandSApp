package com.aandssoftware.aandsinventory.firebase

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.util.Log
import com.aandssoftware.aandsinventory.application.AandSApplication
import com.aandssoftware.aandsinventory.database.CarouselDao
import com.aandssoftware.aandsinventory.database.CustomerDao
import com.aandssoftware.aandsinventory.database.InventoryDao
import com.aandssoftware.aandsinventory.models.CallBackListener
import com.aandssoftware.aandsinventory.utilities.AppConstants
import com.google.firebase.database.*


class FirebaseUtil {

    companion object {

        private var instance: FirebaseUtil? = null

        @Synchronized
        private fun createInstance() {
            if (instance == null) {
                instance = FirebaseUtil()
            }
        }

        @JvmStatic
        fun getInstance(): FirebaseUtil {
            if (instance == null) createInstance()
            return instance as FirebaseUtil
        }

    }

    fun getCustomerDao(): CustomerDao {
        return CustomerDao.getInstance()
    }

    fun getCarouselDao(): CarouselDao {
        return CarouselDao.getInstance()
    }


    fun getInventoryDao(): InventoryDao {
        return InventoryDao.getInstance()
    }


    fun <T> getListData(dataSnapshot: DataSnapshot, valueType: Class<T>): ArrayList<T> {
        var list = ArrayList<T>()
        if (null != dataSnapshot.value) {
            for (children in dataSnapshot.children) {
                val model = children.getValue(valueType)
                if (null != model) {
                    list.add(model)
                }
            }
        }
        return list
    }

    fun <T> getClassData(dataSnapshot: DataSnapshot, valueType: Class<T>): T? {
        var data: T? = null
        if (null != dataSnapshot.value) {
            var nullableData = dataSnapshot.getValue(valueType)
            nullableData?.apply {
                data = this
            }
        }
        return data
    }

    fun incrementCounter(dataSnapshot: DataSnapshot): String {
        var numericId: String
        if (null != dataSnapshot.value) {
            var longId = dataSnapshot.value as Long
            longId += AppConstants.COUNT_ONE.toLong()
            numericId = longId.toString()
            dataSnapshot.ref.setValue(longId)
        } else {
            dataSnapshot.ref.setValue(AppConstants.COUNT_ONE)
            numericId = AppConstants.COUNT_ONE.toString()
        }
        return numericId
    }

    fun isConnected(dataListener: CallBackListener) {
        val connectedRef = AandSApplication.getDatabaseInstance().getReference(".info/connected")
        connectedRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                dataListener.getCallBack(false)
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                val connected = snapshot.getValue(Boolean::class.java) ?: false
                if (connected) {
                    dataListener.getCallBack(true)
                    Log.d(AppConstants.LOG, "Connected")
                } else {
                    dataListener.getCallBack(false)
                    Log.d(AppConstants.LOG, "Not connected")
                }
            }
        })
    }


    fun isInternetConnected(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            connectivityManager?.getNetworkCapabilities(connectivityManager.activeNetwork)?.run {
                getNetworkCapabilities(this)
            } ?: false
        } else {
            @Suppress("DEPRECATION")
            connectivityManager?.activeNetworkInfo?.isConnectedOrConnecting ?: false
        }
    }

    /**
     * Get the network capabilities.
     *
     * @param networkCapabilities: Object of [NetworkCapabilities]
     */
    private fun getNetworkCapabilities(networkCapabilities: NetworkCapabilities): Boolean = networkCapabilities.run {
        when {
            hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
    }
}

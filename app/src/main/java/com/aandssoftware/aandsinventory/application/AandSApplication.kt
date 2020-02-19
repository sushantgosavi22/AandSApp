package com.aandssoftware.aandsinventory.application

import android.app.Application
import com.aandssoftware.aandsinventory.firebase.FirebaseUtil
import com.aandssoftware.aandsinventory.models.AppVersion
import com.google.firebase.database.*
import android.content.Intent
import android.net.Uri
import androidx.core.content.ContextCompat.getSystemService
import org.apache.poi.ss.formula.functions.T



class AandSApplication : Application() {

    companion object {
        lateinit var instanceApp: AandSApplication
        var firebaseDatabase: FirebaseDatabase? = null
        fun getInstance(): AandSApplication {
            return if (instanceApp == null) {
                var instanceAppVar = AandSApplication()
                instanceApp = instanceAppVar
                return instanceApp
            } else {
                instanceApp
            }
        }

        @JvmStatic
        public fun getDatabaseInstance(): FirebaseDatabase {
            return if (firebaseDatabase == null) {
                firebaseDatabase = initFirebaseDatabase()
                firebaseDatabase as FirebaseDatabase
            } else {
                firebaseDatabase as FirebaseDatabase
            }
        }


        private fun initFirebaseDatabase(): FirebaseDatabase {
            var firebaseDatabase = FirebaseDatabase.getInstance()
            firebaseDatabase.setPersistenceEnabled(true)
            return firebaseDatabase
        }

    }

    override fun onCreate() {
        super.onCreate()
        instanceApp = this
    }


}

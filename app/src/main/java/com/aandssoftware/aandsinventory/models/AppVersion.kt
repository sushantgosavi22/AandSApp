package com.aandssoftware.aandsinventory.models

import java.io.Serializable

data class AppVersion(var updatedVersionCode: String = "1", var forceUpdate: Boolean = false, var recomondedUpdate: Boolean = false) : Serializable {
    companion object {
        const val TABLE_APP_VERSION: String = "appVersion"
    }
}



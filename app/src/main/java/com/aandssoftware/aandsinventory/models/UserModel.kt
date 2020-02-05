package com.aandssoftware.aandsinventory.models

import java.io.Serializable

data class UserModel(var userId: String?, var userName: String?, var password: String?, var isAdmin: Boolean?, var customerId: String?, var customerModel: CustomerModel?, var permissions: List<String>?) : Serializable {
    companion object {
        const val TABLE_USER: String = "user"
    }
}



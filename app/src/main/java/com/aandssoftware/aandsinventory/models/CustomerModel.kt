package com.aandssoftware.aandsinventory.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.util.HashMap

class CustomerModel : Serializable {

    @SerializedName("id")
    @Expose
    var id: String? = null

    @SerializedName("customerID")
    @Expose
    var customerID: String? = null

    @SerializedName("invoiceNumber")
    @Expose
    var invoiceNumber: String? = null

    @SerializedName("customerName")
    @Expose
    var customerName: String? = null

    @SerializedName("companyMail")
    @Expose
    var companyMail: String? = null

    @SerializedName("customerGstNumber")
    @Expose
    var customerGstNumber: String? = null

    @SerializedName("address")
    @Expose
    var address: String? = null

    @SerializedName("customerNumber")
    @Expose
    var customerNumber: String? = null

    @SerializedName("contactPerson")
    @Expose
    var contactPerson: String? = null

    @SerializedName("contactPersonNumber")
    @Expose
    var contactPersonNumber: String? = null

    @SerializedName("alternateNumber")
    @Expose
    var alternateNumber: String? = null

    @SerializedName("description")
    @Expose
    var description: String? = null

    @SerializedName("dateCreated")
    @Expose
    var dateCreated: Long = 0

    @SerializedName("invoiceCreatedDate")
    @Expose
    var invoiceCreatedDate: Long = 0

    @SerializedName("dueDate")
    @Expose
    var dueDate: Long = 0

    @SerializedName("imagePath")
    @Expose
    var imagePath: String? = null

    @SerializedName("requirement")
    @Expose
    var requirement: String? = null

    @SerializedName("tag")
    @Expose
    var tag: String? = null

    @SerializedName("username")
    @Expose
    var username: String? = null

    @SerializedName("password")
    @Expose
    var password: String? = null

    @SerializedName("permission")
    @Expose
    var permission: HashMap<String, String>? = null

    @SerializedName("discountPercent")
    @Expose
    var discountPercent: Double = 0.0

    @SerializedName(DISCOUNTED_ITEM)
    @Expose
    var discountedItems: HashMap<String, String>? = null

    @SerializedName("blockedUser")
    @Expose
    var blockedUser: Boolean = false

    @SerializedName("blockedUserMessage")
    @Expose
    var blockedUserMessage: String? = null

    companion object {
        val TABLE_CUSTOMER = "customer"
        val CUSTOMER_COUNTER = "customerCounter"
        val ORDER_BY_VALUE = "customerID"
        val LOGIN_ORDER_BY_VALUE = "username"
        val PASSWORD = "password"
        val PERMISSION = "permission"
        const val DISCOUNTED_ITEM = "discountedItems"
    }
}

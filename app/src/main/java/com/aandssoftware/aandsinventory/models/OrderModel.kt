package com.aandssoftware.aandsinventory.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class OrderModel : Serializable {

    companion object {
        const val TABLE_ORDER = "order"
        const val ORDER_COUNTER = "orderCounter"
        const val ORDER_ITEMS = "orderItems"
        const val ORDER_STATUS = "orderStatus"
        const val ORDER_STATUS_NAME = "orderStatusName"
        const val ORDER_BY_VALUE = "orderDateUpdated"
        const val ID_ORDER_BY_VALUE = "customerId"
        const val ORDER_CUSTOMER_MODEL = "customerModel"
    }

    @SerializedName("id")
    @Expose
    var id: String? = null

    @SerializedName("customerModel")
    @Expose
    var customerModel: CustomerModel? = null

    @SerializedName("customerId")
    @Expose
    var customerId: String? = null

    @SerializedName("orderId")
    @Expose
    var orderId: String? = null

    @SerializedName("orderItems")
    @Expose
    var orderItems = HashMap<String, InventoryItem>()

    @SerializedName("orderDescription")
    @Expose
    var orderDescription: String? = null

    @SerializedName("orderDateCreated")
    @Expose
    var orderDateCreated: Long = 0

    @SerializedName("orderDateUpdated")
    @Expose
    var orderDateUpdated: Long = 0

    @SerializedName("orderCompletedDate")
    @Expose
    var orderCompletedDate: Long = 0

    @SerializedName("orderDeliveryDate")
    @Expose
    var orderDeliveryDate: Long = 0

    @SerializedName("orderStatus")
    @Expose
    var orderStatus: String? = null

    @SerializedName("orderStatusName")
    @Expose
    var orderStatusName: String? = null

    @SerializedName("orderContact")
    @Expose
    var orderContact: String? = null

    @SerializedName("invoiceNumber")
    @Expose
    var invoiceNumber: String? = null

    @SerializedName("chalanNumber")
    @Expose
    var chalanNumber: String? = null

    @SerializedName("invoiceDate")
    @Expose
    var invoiceDate: Long = 0

    @SerializedName("dueDate")
    @Expose
    var dueDate: Long = 0

    @SerializedName("dateOfSupply")
    @Expose
    var dateOfSupply: Long = 0

    @SerializedName("code")
    @Expose
    var code: Int = 0

    @SerializedName("discount")
    @Expose
    var discount: Int = 0

    @SerializedName("extraCharges")
    @Expose
    var extraCharges: Int = 0

    @SerializedName("extraChargesDescription")
    @Expose
    var extraChargesDescription: String? = null


    @SerializedName("sgstOrderTotalAmount")
    @Expose
    var sgstOrderTotalAmount: Double = 0.0

    @SerializedName("gstOrderTotalAmount")
    @Expose
    var gstOrderTotalAmount: Double = 0.0


    @SerializedName("totalTaxableAmount")
    @Expose
    var totalTaxableAmount: Double = 0.0


    @SerializedName("gstTotalAmount")
    @Expose
    var gstTotalAmount: Double = 0.0

    @SerializedName("taxableAmountBeforeDiscount")
    @Expose
    var taxableAmountBeforeDiscount: Double = 0.0


    @SerializedName("cessAmount")
    @Expose
    var cessAmount: Int = 0

    @SerializedName("roundOff")
    @Expose
    var roundOff: Double = 0.0

    @SerializedName("totalFigure")
    @Expose
    var totalFigure: Double = 0.0

    @SerializedName("paymentReceived")
    @Expose
    var paymentReceived: Int = 0


    @SerializedName("totalCreditApplied")
    @Expose
    var totalCreditApplied: Int = 0

    @SerializedName("totalDebitApplied")
    @Expose
    var totalDebitApplied: Int = 0


    @SerializedName("balanceDue")
    @Expose
    var balanceDue: Int = 0

    @SerializedName("finalBillAmount")
    @Expose
    var finalBillAmount: Double = 0.0

    constructor() {

    }
}

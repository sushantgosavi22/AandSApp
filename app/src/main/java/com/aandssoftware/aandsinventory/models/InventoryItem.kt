package com.aandssoftware.aandsinventory.models

import com.aandssoftware.aandsinventory.common.Utils
import com.aandssoftware.aandsinventory.firebase.FirebaseUtil
import com.aandssoftware.aandsinventory.utilities.AppConstants.Companion.EMPTY_STRING
import com.aandssoftware.aandsinventory.utilities.AppConstants.Companion.ZERO_STRING
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable
import kotlin.collections.HashMap

open class InventoryItem : Serializable {

    companion object {

        const val TABLE_INVENTORY_ITEM = "inventoryItem"
        const val TABLE_MATERIAL_INVENTORY_ITEM = "materialInventoryItem"
        const val INVENTORY_ITEM_COUNTER = "inventoryItemCounter"
        const val MATERIAL_INVENTORY_ITEM_COUNTER = "MaterialInventoryItemCounter"
        const val ORDER_BY_VALUE = "inventoryItemLastUpdatedDate"
        const val ORDER_BY_VALUE_NAME = "inventoryItemName"

        const val ID = "id"
        const val INVENTORY_ID = "inventoryId"
        const val INVENTORY_ITEM_IMAGE_PATH = "Product Image"
        const val INVENTORY_ITEM_ATTACHMENTS = "Attachments"
        const val INVENTORY_ITEM_NAME = "Product Name"
        const val INVENTORY_ITEM_PURCHASE_PRICE = "Product Purchase Price"
        const val INVENTORY_ITEM_UNIT_PRICE = "Product Unit Price"
        const val ITEM_QUANTITY = "Quantity"
        const val ITEM_UNIT_QUANTITY = "Quantity Unit"
        const val INVENTORY_ITEM_SELLING_PRICE = "Selling Price"
        const val INVENTORY_ITEM_QUNTITY_BY_SELLING_PRICE = "Quantity by Selling Price"
        const val INVENTORY_ITEM_DESCRIPTION = "Description"
        const val INVENTORY_ITEM_BRAND_NAME = "Brand Name"
        const val INVENTORY_ITEM_MODEL_NAME = "Model Name"
        const val INVENTORY_ITEM_COLOR = "Color"
        const val INVENTORY_ITEM_SIZE = "Size"
        const val INVENTORY_ITEM_SHOP_NAME = "Purchase Store Name"
        const val INVENTORY_ITEM_SHOP_CONTACT = "Purchase Store Contact"
        const val INVENTORY_ITEM_SHOP_ADDRESSS = "Purchase Store Address"
        const val INVENTORY_ITEM_HSN = "Product HSN number"
        const val INVENTORY_ITEM_PURCHASE_DATE = "Purchase Date"
        const val INVENTORY_ITEM_MODIFIED_DATE = "Modified Date"
        const val INVENTORY_ITEM_AVAILABLE = "Available"
        const val INVENTORY_ITEM_HISTORY = "History"
        const val INVENTORY_TYPE = "InventoryType"
        const val INVENTORY_PARENT_ID = "ParentId"
        const val ITEM_GST_PERCENTAGE = "gstPercentage"
        const val ITEM_GST_AMOUNT = "gstAmount"
        const val ITEM_SGST_PERCENTAGE = "sgstPeercentage"
        const val ITEM_SGST_AMOUNT = "sgstAmount"
        const val ITEM_MRP_AMOUNT = "mrpAmount"
        const val ITEM_TAXABLE_AMOUNT = "taxableAmount"
        const val ITEM_FINAL_BILL_AMOUNT = "Final Bill Amount"
        const val TAG = "tag"
        //Actions
        const val ACTION_CHANGED = "Changed"
        const val ACTION_DELETE = "Delete"
        const val ACTION_INCREASE = "increase"
        const val ACTION_DECREASE = "Decrease"
        //default value
        const val DEFAULT_QUANTITY_UNIT = "Piece"
    }

    @SerializedName(ID)
    @Expose
    var id: String? = null

    @SerializedName(INVENTORY_ID)
    @Expose
    var inventoryId: String? = null


    @SerializedName(INVENTORY_PARENT_ID)
    @Expose
    var parentId: String? = null

    @SerializedName(INVENTORY_ITEM_IMAGE_PATH)
    @Expose
    var inventoryItemImagePath: HashMap<String, String>? = null

    @SerializedName(INVENTORY_ITEM_ATTACHMENTS)
    @Expose
    var attachments: HashMap<String, String>? = null

    @SerializedName(INVENTORY_ITEM_NAME)
    @Expose
    var inventoryItemName: String? = null

    @SerializedName(INVENTORY_ITEM_PURCHASE_PRICE)
    @Expose
    var itemPurchasePrice: String? = null

    @SerializedName(INVENTORY_ITEM_UNIT_PRICE)
    @Expose
    var itemUnitPrice: String? = null

    @SerializedName(ITEM_QUANTITY)
    @Expose
    var itemQuantity: String? = null

    @SerializedName(ITEM_UNIT_QUANTITY)
    @Expose
    var itemQuantityUnit: String? = null

    @SerializedName(INVENTORY_ITEM_SELLING_PRICE)
    @Expose
    var minimumSellingPrice: String? = null


    @SerializedName(INVENTORY_ITEM_QUNTITY_BY_SELLING_PRICE)
    @Expose
    var quantityBySellingPrice: Int = 0

    @SerializedName(INVENTORY_ITEM_DESCRIPTION)
    @Expose
    var description: String? = null

    @SerializedName(INVENTORY_ITEM_BRAND_NAME)
    @Expose
    var inventoryItemBrandName: String? = null

    @SerializedName(INVENTORY_ITEM_MODEL_NAME)
    @Expose
    var inventoryItemModelName: String? = null

    @SerializedName(INVENTORY_ITEM_COLOR)
    @Expose
    var inventoryItemColor: String? = null

    @SerializedName(INVENTORY_ITEM_SIZE)
    @Expose
    var inventoryItemSize: String? = null

    @SerializedName(INVENTORY_ITEM_SHOP_NAME)
    @Expose
    var purchaseItemShopName: String? = null

    @SerializedName(INVENTORY_ITEM_SHOP_CONTACT)
    @Expose
    var purchaseItemShopContact: String? = null

    @SerializedName(INVENTORY_ITEM_SHOP_ADDRESSS)
    @Expose
    var purchaseItemShopAddress: String? = null

    @SerializedName(INVENTORY_ITEM_PURCHASE_DATE)
    @Expose
    var inventoryItemPurchaseDate: Long = 0

    @SerializedName(INVENTORY_ITEM_MODIFIED_DATE)
    @Expose
    var inventoryItemLastUpdatedDate: Long = 0

    @SerializedName(INVENTORY_ITEM_AVAILABLE)
    @Expose
    var isAvailable: Boolean = false

    @SerializedName(INVENTORY_ITEM_HISTORY)
    @Expose
    var inventoryItemHistories: HashMap<String, InventoryItemHistory>? = null

    @SerializedName(INVENTORY_ITEM_HSN)
    @Expose
    var hsnCode: String? = null

    @SerializedName(INVENTORY_TYPE)
    @Expose
    var inventoryType: Int = 0

    @SerializedName(ITEM_GST_PERCENTAGE)
    @Expose
    var gstPercentage: Double = 0.0

    @SerializedName(ITEM_GST_AMOUNT)
    @Expose
    var gstAmount: Double = 0.0

    @SerializedName(ITEM_SGST_PERCENTAGE)
    @Expose
    var sgstPercentage: Double = 0.0

    @SerializedName(ITEM_SGST_AMOUNT)
    @Expose
    var sgstAmount: Double = 0.0

    @SerializedName(ITEM_TAXABLE_AMOUNT)
    @Expose
    var taxableAmount: Double = 0.0

    @SerializedName(ITEM_MRP_AMOUNT)
    @Expose
    var mrpAmount: Double = 0.0


    @SerializedName(ITEM_FINAL_BILL_AMOUNT)
    @Expose
    var finalBillAmount: Double = 0.0

    @SerializedName("discountForCompany")
    @Expose
    var discountRateForCompany: Double = 0.0

    @SerializedName("createdBy")
    @Expose
    var createdBy: String = InventoryCreatedBy.ADMIN.toString()

    @SerializedName(TAG)
    @Expose
    var tag: String? = null


    constructor() {

    }

    constructor(item: InventoryItem) {
        this.id = item.id
        this.parentId = item.parentId
        this.inventoryItemImagePath = item.inventoryItemImagePath
        this.attachments = item.attachments
        this.inventoryItemName = item.inventoryItemName
        this.itemPurchasePrice = item.itemPurchasePrice
        this.itemUnitPrice = item.itemUnitPrice
        this.itemQuantity = item.itemQuantity
        this.itemQuantityUnit = item.itemQuantityUnit
        this.minimumSellingPrice = item.minimumSellingPrice
        this.description = item.description
        this.inventoryItemBrandName = item.inventoryItemBrandName
        this.inventoryItemModelName = item.inventoryItemModelName
        this.inventoryItemColor = item.inventoryItemColor
        this.inventoryItemSize = item.inventoryItemSize
        this.purchaseItemShopName = item.purchaseItemShopName
        this.purchaseItemShopContact = item.purchaseItemShopContact
        this.purchaseItemShopAddress = item.purchaseItemShopAddress
        this.inventoryItemPurchaseDate = item.inventoryItemPurchaseDate
        this.inventoryItemLastUpdatedDate = item.inventoryItemLastUpdatedDate
        this.inventoryType = item.inventoryType
        this.hsnCode = item.hsnCode
        this.taxableAmount = item.taxableAmount
        this.sgstPercentage = item.sgstPercentage
        this.sgstAmount = item.sgstAmount
        this.gstPercentage = item.gstPercentage
        this.gstAmount = item.gstAmount
        this.mrpAmount = item.mrpAmount
        this.quantityBySellingPrice = item.quantityBySellingPrice
        this.finalBillAmount = item.finalBillAmount
        this.discountRateForCompany = item.discountRateForCompany
        this.tag = item.tag
        this.isAvailable = item.isAvailable
    }

    fun getChangedParamList(item: InventoryItem): HashMap<String, Any> {
        val map = HashMap<String, Any>()
        try {
            val dateModified = System.currentTimeMillis()
            var id: String


            if (this.inventoryItemName?.compareTo(item?.inventoryItemName ?: EMPTY_STRING) !== 0) {
                id = getHistoryId(Utils.isEmpty(item.id))
                map[id] = getHistory(id, INVENTORY_ITEM_NAME, this.inventoryItemName,
                        item.inventoryItemName, item.description, ACTION_CHANGED, dateModified)
            }

            if (this.itemPurchasePrice?.compareTo(item?.itemPurchasePrice ?: EMPTY_STRING) !== 0) {
                id = getHistoryId(Utils.isEmpty(item.id))
                val action = getActionForInt(Integer.parseInt(item.itemPurchasePrice
                        ?: ZERO_STRING),
                        Integer.parseInt(this.itemPurchasePrice ?: ZERO_STRING))
                map[id] = getHistory(id, INVENTORY_ITEM_PURCHASE_PRICE, item.itemPurchasePrice,
                        this.itemPurchasePrice, item.description, action, dateModified)
            }

            if (this.itemUnitPrice?.compareTo(item?.itemUnitPrice ?: EMPTY_STRING) !== 0) {
                id = getHistoryId(Utils.isEmpty(item.id))
                val oldUnit = Utils.parseCommaSeparatedCurrency(item.itemUnitPrice ?: ZERO_STRING)
                val newUnit = Utils.parseCommaSeparatedCurrency(this.itemUnitPrice ?: ZERO_STRING)
                val action = getActionForInt(oldUnit.toInt(), newUnit.toInt())
                map[id] = getHistory(id, INVENTORY_ITEM_UNIT_PRICE, item.itemUnitPrice,
                        this.itemUnitPrice, item.description, action, dateModified)
            }

            if (this.itemQuantity?.compareTo(item?.itemQuantity ?: EMPTY_STRING) !== 0) {
                id = getHistoryId(Utils.isEmpty(item.id))
                val action = getActionForInt(Integer.parseInt(item.itemQuantity ?: ZERO_STRING),
                        Integer.parseInt(this.itemQuantity ?: ZERO_STRING))
                map[id] = getHistory(id, ITEM_QUANTITY, item.itemQuantity,
                        this.itemQuantity, item.description, action, dateModified)
            }

            if (this.itemQuantityUnit?.compareTo(item?.itemQuantityUnit ?: EMPTY_STRING) !== 0) {
                id = getHistoryId(Utils.isEmpty(item.id))
                map[id] = getHistory(id, ITEM_UNIT_QUANTITY, item.itemQuantityUnit,
                        this.itemQuantityUnit, item.description, ACTION_CHANGED, dateModified)
            }

            if (this.minimumSellingPrice?.compareTo(item?.minimumSellingPrice
                            ?: EMPTY_STRING) !== 0) {
                id = getHistoryId(Utils.isEmpty(item.id))
                val action = getActionForInt(Integer.parseInt(this.minimumSellingPrice
                        ?: ZERO_STRING),
                        Integer.parseInt(item.minimumSellingPrice ?: ZERO_STRING))
                map[id] = getHistory(id, INVENTORY_ITEM_SELLING_PRICE, item.minimumSellingPrice,
                        this.minimumSellingPrice, item.description, action, dateModified)
            }

            if (this.description?.compareTo(item?.description ?: EMPTY_STRING) !== 0) {
                id = getHistoryId(Utils.isEmpty(item.id))
                map[id] = getHistory(id, INVENTORY_ITEM_DESCRIPTION, this.description,
                        item.description, item.description, ACTION_CHANGED, dateModified)
            }

            if (this.inventoryItemBrandName?.compareTo(item?.inventoryItemBrandName
                            ?: EMPTY_STRING) !== 0) {
                id = getHistoryId(Utils.isEmpty(item.id))
                map[id] = getHistory(id, INVENTORY_ITEM_BRAND_NAME, this.inventoryItemBrandName,
                        item.inventoryItemBrandName, item.description, ACTION_CHANGED, dateModified)
            }

            if (this.inventoryItemModelName?.compareTo(item?.inventoryItemModelName
                            ?: EMPTY_STRING) !== 0) {
                id = getHistoryId(Utils.isEmpty(item.id))
                map[id] = getHistory(id, INVENTORY_ITEM_MODEL_NAME, this.inventoryItemModelName,
                        item.inventoryItemModelName, item.description, ACTION_CHANGED, dateModified)
            }

            if (this.inventoryItemColor?.compareTo(item?.inventoryItemColor
                            ?: EMPTY_STRING) !== 0) {
                id = getHistoryId(Utils.isEmpty(item.id))
                map[id] = getHistory(id, INVENTORY_ITEM_COLOR, this.inventoryItemColor,
                        item.inventoryItemColor, item.description, ACTION_CHANGED, dateModified)
            }

            if (this.inventoryItemSize?.compareTo(item?.inventoryItemSize ?: EMPTY_STRING) !== 0) {
                id = getHistoryId(Utils.isEmpty(item.id))
                map[id] = getHistory(id, INVENTORY_ITEM_SIZE, this.inventoryItemSize,
                        item.inventoryItemSize, item.description, ACTION_CHANGED, dateModified)
            }

            if (this.purchaseItemShopName?.compareTo(item?.purchaseItemShopName
                            ?: EMPTY_STRING) !== 0) {
                id = getHistoryId(Utils.isEmpty(item.id))
                map[id] = getHistory(id, INVENTORY_ITEM_SHOP_NAME, this.purchaseItemShopName,
                        item.purchaseItemShopName, item.description, ACTION_CHANGED, dateModified)
            }

            if (this.purchaseItemShopContact?.compareTo(item?.purchaseItemShopContact
                            ?: EMPTY_STRING) !== 0) {
                id = getHistoryId(Utils.isEmpty(item.id))
                map[id] = getHistory(id, INVENTORY_ITEM_SHOP_CONTACT, this.purchaseItemShopContact,
                        item.purchaseItemShopContact, item.description, ACTION_CHANGED, dateModified)
            }

            if (this.purchaseItemShopAddress?.compareTo(item?.purchaseItemShopAddress
                            ?: EMPTY_STRING) !== 0) {
                id = getHistoryId(Utils.isEmpty(item.id))
                map[id] = getHistory(id, INVENTORY_ITEM_SHOP_ADDRESSS, this.purchaseItemShopAddress,
                        item.purchaseItemShopAddress, item.description, ACTION_CHANGED, dateModified)
            }

            if (this.isAvailable !== item.isAvailable) {
                id = getHistoryId(Utils.isEmpty(item.id))
                map[id] = getHistory(id, INVENTORY_ITEM_AVAILABLE, "" + this.isAvailable,
                        "" + item.isAvailable, item.description, ACTION_CHANGED, dateModified)
            }

            if (this.hsnCode?.compareTo(item?.hsnCode ?: EMPTY_STRING) !== 0) {
                id = getHistoryId(Utils.isEmpty(item.id))
                map[id] = getHistory(id, INVENTORY_ITEM_HSN, this.hsnCode,
                        item.hsnCode, item.description, ACTION_CHANGED, dateModified)
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }

        return map
    }


    private fun getHistory(id: String?, modifiedParameter: String?, modifiedTo: String?,
                           modifiedFrom: String?,
                           description: String?, action: String, modifiedDate: Long): InventoryItemHistory {
        val history = InventoryItemHistory()
        history.id = id
        history.action = action
        history.description = description
        history.inventoryItemId = this.id
        history.modifiedDate = modifiedDate
        history.modifiedFrom = modifiedFrom
        history.modifiedParameter = modifiedParameter
        history.modifiedTo = modifiedTo
        return history
    }

    private fun getActionForInt(oldVal: Int, newValue: Int): String {
        return if (oldVal > newValue) {
            ACTION_DECREASE
        } else if (oldVal < newValue) {
            ACTION_INCREASE
        } else {
            ACTION_CHANGED
        }
    }

    private fun getHistoryId(id: String): String = FirebaseUtil.getInstance().getInventoryDao().getNextInventoryHistoryId(id)
            ?: ZERO_STRING


}

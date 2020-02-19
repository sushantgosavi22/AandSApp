package com.aandssoftware.aandsinventory.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class InventoryItemHistory(@SerializedName("id") var id: String? = null) : Serializable {

    companion object {
        const val TABLE_INVENTORY_ITEM_HISTORY = "itemHistory"
        const val ORDER_BY_VALUE = "modifiedDate"
    }

    @SerializedName("inventoryItemId")
    var inventoryItemId: String? = null

    @SerializedName("modifiedParameter")
    var modifiedParameter: String? = null

    @SerializedName("action")
    var action: String? = null

    @SerializedName("modifiedFrom")
    var modifiedFrom: String? = null

    @SerializedName("modifiedTo")
    var modifiedTo: String? = null

    @SerializedName("description")
    var description: String? = null

    @SerializedName("modifiedDate")
    var modifiedDate: Long = 0

    @SerializedName("inventoryItemName")
    var inventoryItemName: String? = null
}

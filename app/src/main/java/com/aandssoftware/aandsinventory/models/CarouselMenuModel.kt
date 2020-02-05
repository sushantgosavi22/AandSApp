package com.aandssoftware.aandsinventory.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class CarouselMenuModel : Serializable {

    @SerializedName("id")
    @Expose
    var id: Int = 0

    @SerializedName("aliceName")
    @Expose
    var aliceName: String? = null

    @SerializedName("imageId")
    @Expose
    var imageId: String? = null

    @SerializedName("expression")
    @Expose
    var expression: String? = null

    @SerializedName("tag")
    @Expose
    var tag: String? = null

    @SerializedName("description")
    @Expose
    var description: String? = null

    @SerializedName("dateCreated")
    @Expose
    var dateCreated: Long = 0

    @SerializedName("carouselId")
    @Expose
    var carouselId: String? = null

    @SerializedName("permissions")
    @Expose
    var permissions: String? = null

    @SerializedName("defaultImageId")
    @Expose
    var defaultImageId: Int = 0

    companion object {
        const val TABLE_CAROUSEL = "carousel"
    }
}

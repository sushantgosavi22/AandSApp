package com.aandssoftware.aandsinventory.database

import com.aandssoftware.aandsinventory.R
import com.aandssoftware.aandsinventory.application.AandSApplication
import com.aandssoftware.aandsinventory.models.CarouselMenuModel
import com.aandssoftware.aandsinventory.models.CarouselMenuType
import com.aandssoftware.aandsinventory.models.Permissions
import com.google.firebase.database.ValueEventListener

class CarouselDao {

    companion object {
        private var instance: CarouselDao? = null
        @Synchronized
        private fun createInstance() {
            if (instance == null) {
                instance = CarouselDao()
            }
        }

        @JvmStatic
        fun getInstance(): CarouselDao {
            if (instance == null) createInstance()
            return instance as CarouselDao
        }
    }

    constructor() {

    }


    fun getCarousalItems(listner: ValueEventListener) {
        var instance = AandSApplication.getDatabaseInstance()
        var reference = instance.getReference(CarouselMenuModel.TABLE_CAROUSEL)
        reference.addValueEventListener(listner)
    }

    fun insertCarousalItems() {
        var instance = AandSApplication.getDatabaseInstance()
        var reference = instance.getReference(CarouselMenuModel.TABLE_CAROUSEL)
        reference.keepSynced(true)
        addCarouselMenu().forEach { model ->
            var uniqueId = reference.push().key
            uniqueId?.let {
                reference.child(uniqueId).setValue(model)
            }
        }
    }

    private fun addCarouselMenu(): List<CarouselMenuModel> {
        val list = java.util.ArrayList<CarouselMenuModel>()
        val orders = CarouselMenuModel()
        orders.id = 3
        orders.carouselId = CarouselMenuType.ORDERS.orders
        orders.aliceName = "Orders"
        orders.dateCreated = System.currentTimeMillis()
        orders.description = "Orders Description"
        orders.expression = ""
        orders.permissions = Permissions.ADMIN.toString()
        orders.imageId = "https://firebasestorage.googleapis.com/v0/b/aandsstationary.appspot.com/o/carouselImages%2Fcall.png?alt=media&token=54dcb8c0-67f4-45aa-b6f8-f92d216854e2"
        orders.defaultImageId = R.drawable.ic_call
        orders.tag = "3"
        list.add(orders)

        val materials = CarouselMenuModel()
        materials.id = 4
        materials.carouselId = CarouselMenuType.MATERIALS.orders
        materials.aliceName = "Materials"
        materials.dateCreated = System.currentTimeMillis()
        materials.description = "Materials Description"
        materials.imageId = "https://firebasestorage.googleapis.com/v0/b/aandsstationary.appspot.com/o/carouselImages%2Farchive.png?alt=media&token=001f41eb-a7fa-41ae-bfa3-1974528b12fb"
        materials.defaultImageId = R.drawable.ic_archive
        materials.expression = ""
        materials.permissions = Permissions.ADMIN.toString()
        materials.tag = "4"
        list.add(materials)

        val customers = CarouselMenuModel()
        customers.id = 1
        customers.carouselId = CarouselMenuType.CUSTOMERS.orders
        customers.aliceName = "Customers"
        customers.dateCreated = System.currentTimeMillis()
        customers.description = "Customers Description"
        customers.imageId = "https://firebasestorage.googleapis.com/v0/b/aandsstationary.appspot.com/o/carouselImages%2Fcustomer.png?alt=media&token=d1797be0-df9f-41b4-866d-e7d134d96d96"
        customers.defaultImageId = R.drawable.ic_customer
        customers.expression = ""
        customers.permissions = Permissions.ADMIN.toString()
        customers.tag = "1"
        list.add(customers)

        val inventoryHistory = CarouselMenuModel()
        inventoryHistory.id = 2
        inventoryHistory.carouselId = CarouselMenuType.INVENTORY_HISTORY.orders
        inventoryHistory.aliceName = "History"
        inventoryHistory.dateCreated = System.currentTimeMillis()
        inventoryHistory.description = "Inventory History Description"
        inventoryHistory.imageId = "https://firebasestorage.googleapis.com/v0/b/aandsstationary.appspot.com/o/carouselImages%2Finventory_history.png?alt=media&token=951cead6-00c7-49bd-833e-bdcfec32228a"
        inventoryHistory.defaultImageId = R.drawable.ic_inventory_history
        inventoryHistory.expression = ""
        inventoryHistory.permissions = Permissions.ADMIN.toString()
        inventoryHistory.tag = "2"
        list.add(inventoryHistory)

        val inventoryList = CarouselMenuModel()
        inventoryList.id = 0
        inventoryList.carouselId = CarouselMenuType.INVENTORY.orders
        inventoryList.aliceName = "Inventory"
        inventoryList.dateCreated = System.currentTimeMillis()
        inventoryList.description = "Description"
        inventoryList.imageId = "https://firebasestorage.googleapis.com/v0/b/aandsstationary.appspot.com/o/carouselImages%2Finventory_carosal.png?alt=media&token=0ae5559f-3f5d-4fd0-95e7-174a1294a0af"
        inventoryList.defaultImageId = R.drawable.ic_inventory_carosal
        inventoryList.expression = ""
        inventoryList.permissions = Permissions.ADMIN.toString()
        inventoryList.tag = "0"
        list.add(inventoryList)


        val companyOrders = CarouselMenuModel()
        companyOrders.id = 0
        companyOrders.carouselId = CarouselMenuType.COMPANY_ORDER.orders
        companyOrders.aliceName = "Orders"
        companyOrders.dateCreated = System.currentTimeMillis()
        companyOrders.description = "Description"
        companyOrders.imageId = "https://firebasestorage.googleapis.com/v0/b/aandsstationary.appspot.com/o/carouselImages%2Fcall.png?alt=media&token=54dcb8c0-67f4-45aa-b6f8-f92d216854e2"
        companyOrders.defaultImageId = R.drawable.ic_call
        companyOrders.expression = ""
        companyOrders.permissions = Permissions.INITIAL_CREATED_PERMISSION.toString()
        companyOrders.tag = "5"
        list.add(companyOrders)

        val companyProfile = CarouselMenuModel()
        companyProfile.id = 0
        companyProfile.carouselId = CarouselMenuType.COMPANY_PROFILE.orders
        companyProfile.aliceName = "Profile"
        companyProfile.dateCreated = System.currentTimeMillis()
        companyProfile.description = "Description"
        companyProfile.imageId = "https://firebasestorage.googleapis.com/v0/b/aandsstationary.appspot.com/o/carouselImages%2Fcustomer.png?alt=media&token=d1797be0-df9f-41b4-866d-e7d134d96d96"
        companyProfile.defaultImageId = R.drawable.ic_customer
        companyProfile.expression = ""
        companyProfile.permissions = Permissions.INITIAL_CREATED_PERMISSION.toString()
        companyProfile.tag = "6"
        list.add(companyProfile)

        val companyMaterials = CarouselMenuModel()
        companyMaterials.id = 0
        companyMaterials.carouselId = CarouselMenuType.COMPANY_MATERIALS.orders
        companyMaterials.aliceName = "Materials"
        companyMaterials.dateCreated = System.currentTimeMillis()
        companyMaterials.description = "Materials Description"
        companyMaterials.imageId = "https://firebasestorage.googleapis.com/v0/b/aandsstationary.appspot.com/o/carouselImages%2Farchive.png?alt=media&token=001f41eb-a7fa-41ae-bfa3-1974528b12fb"
        companyMaterials.defaultImageId = R.drawable.ic_archive
        companyMaterials.permissions = Permissions.INITIAL_CREATED_PERMISSION.toString()
        companyMaterials.expression = ""
        companyMaterials.tag = "7"
        list.add(companyMaterials)

        return list
    }
}

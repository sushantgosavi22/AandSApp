package com.aandssoftware.aandsinventory.ui.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.ScrollView
import androidx.core.app.ActivityCompat
import com.aandssoftware.aandsinventory.R
import com.aandssoftware.aandsinventory.application.AandSApplication
import com.aandssoftware.aandsinventory.common.Utils
import com.aandssoftware.aandsinventory.firebase.FirebaseUtil
import com.aandssoftware.aandsinventory.firebase.GetAlphaNumericAndNumericIdListener
import com.aandssoftware.aandsinventory.listing.ListType
import com.aandssoftware.aandsinventory.models.callBackListener
import com.aandssoftware.aandsinventory.models.InventoryItem
import com.aandssoftware.aandsinventory.models.ViewMode
import com.aandssoftware.aandsinventory.ui.component.CustomEditText
import com.aandssoftware.aandsinventory.utilities.AppConstants
import com.aandssoftware.aandsinventory.utilities.AppConstants.Companion.EMPTY_STRING
import com.aandssoftware.aandsinventory.utilities.AppConstants.Companion.INVENTORY_ID
import com.aandssoftware.aandsinventory.utilities.AppConstants.Companion.INVENTORY_INSTANCE
import com.aandssoftware.aandsinventory.utilities.AppConstants.Companion.LISTING_TYPE
import com.aandssoftware.aandsinventory.utilities.AppConstants.Companion.ORDER_ID
import com.aandssoftware.aandsinventory.utilities.AppConstants.Companion.ORDER_RELOAD_LIST_RESULT_CODE
import com.aandssoftware.aandsinventory.utilities.AppConstants.Companion.PICK_IMAGE
import com.aandssoftware.aandsinventory.utilities.AppConstants.Companion.RELOAD_LIST_RESULT_CODE
import com.aandssoftware.aandsinventory.utilities.AppConstants.Companion.TITLE
import com.aandssoftware.aandsinventory.utilities.AppConstants.Companion.VIEW_MODE
import com.aandssoftware.aandsinventory.utilities.AppConstants.Companion.ZERO_STRING
import com.aandssoftware.aandsinventory.utilities.SharedPrefsUtils
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_add_inventory.*
import kotlinx.android.synthetic.main.activity_add_inventory.btnSave
import kotlinx.android.synthetic.main.custom_action_bar_layout.*
import java.math.BigDecimal
import java.util.*

class AddInventoryActivity : BaseActivity() {
    var inventory: InventoryItem? = null
    private var imagePath: String? = null
    lateinit var title: String
    private var inventoryType: Int = 0
    private lateinit var inventoryId: String
    private lateinit var orderId: String
    private var viewMode: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_inventory)
        getAndAssignIntentData()
        setUpUI()
    }

    private fun getAndAssignIntentData() {
        title = intent.getStringExtra(TITLE)
        inventoryType = intent.getIntExtra(LISTING_TYPE, ListType.LIST_TYPE_MATERIAL.ordinal)
        inventoryId = intent.getStringExtra(INVENTORY_ID)
        orderId = intent.getStringExtra(ORDER_ID)
        viewMode = intent.getIntExtra(VIEW_MODE, ViewMode.ADD.ordinal)
        checkUserAndHideData()
        if (inventoryId != null && inventoryId.isNotEmpty()) {
            showProgressBar()
            var listener = object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val inventoryItem = FirebaseUtil.getInstance().getClassData(dataSnapshot, InventoryItem::class.java)
                    inventoryItem?.let {
                        inventory = inventoryItem
                        setValues()
                    }
                    dismissProgressBar()
                }

                override fun onCancelled(p0: DatabaseError) {
                    dismissProgressBar()
                }
            }
            if (inventoryType == ListType.LIST_TYPE_ORDER_INVENTORY.ordinal) {
                FirebaseUtil.getInstance().getCustomerDao().getCompanyOrdersFromOrderId(Utils.getLoginCustomerId(this), orderId, inventoryId, listener)
            } else if (inventoryType == ListType.LIST_TYPE_MATERIAL.ordinal) {
                FirebaseUtil.getInstance().getInventoryDao().getMaterialInventoryItemFromId(inventoryId, listener)
            } else if (inventoryType == ListType.LIST_TYPE_INVENTORY.ordinal) {
                FirebaseUtil.getInstance().getInventoryDao().getInventoryItemFromId(inventoryId, listener)
            }
        }
    }

    private fun checkUserAndHideData() {
        if (!Utils.isAdminUser(this)) {
            llQuantityAndUnit.visibility = View.GONE
            edtSellingPrice.visibility = View.GONE
            edtPurchasePrice.visibility = View.GONE
            slShopDetails.visibility = View.GONE
            edtCgstAmount.visibility = View.GONE
            llSgstDetails.visibility = View.GONE
        }
    }


    private fun setUpUI() {
        setupActionBar(title)
        val watcher = CustomTextWatcher(edtQuantity, edtPurchasePrice, edtUnitPrice)
        val cGstWatcher = CustomGstWatcher(edtCgstPercent, edtUnitPrice, edtCgstAmount)
        val sGstWatcher = CustomGstWatcher(edtSgstPercent, edtUnitPrice, edtSgstAmount)
        edtCgstPercent.addTextChangedListener(cGstWatcher)
        edtSgstPercent.addTextChangedListener(sGstWatcher)
        edtPurchasePrice.addTextChangedListener(watcher)
        edtQuantity.addTextChangedListener(watcher)
        imgInventoryImg.setOnClickListener {
            if (askingForRequest().not()) {
                openGallery()
            }
        }
        setViewByMode()
        btnSave.setOnClickListener {
            onButtonClick()
        }
        navBarBack.setOnClickListener {
            onBackPressed()
        }
    }

    private fun setViewByMode() {
        if (viewMode == ViewMode.VIEW_ONLY.ordinal || viewMode == ViewMode.GET_INVENTORY_QUANTITY.ordinal) {
            enabledView(false)
            imgInventoryImg.setOnClickListener(null)
            setButton("", View.GONE)
            if (viewMode == ViewMode.GET_INVENTORY_QUANTITY.ordinal) {
                getInventoryQuantityView()
            }
        }
    }

    private fun getInventoryQuantityView() {
        sectionRequiredQuantity.visibility = View.VISIBLE
        setButton(resources.getString(R.string.done), View.VISIBLE)
        addItem.setOnClickListener {
            val value = edtOrderQuantity.text.toString()
            if (value.isNotEmpty()) {
                var orderQuantity = Integer.parseInt(value)
                if (orderQuantity > 0) {
                    orderQuantity += 1
                    edtOrderQuantity.setText(orderQuantity.toString())
                }
            }
        }
        removeItem.setOnClickListener {
            val value = edtOrderQuantity.text.toString()
            if (value.isNotEmpty()) {
                var orderQuantity = Integer.parseInt(value)
                if (orderQuantity > 0) {
                    orderQuantity -= 1
                    edtOrderQuantity.setText(orderQuantity.toString())
                }
            }
        }
        var enable = true
        edtBrandName.setEditableMode(enable)
        edtModelName.setEditableMode(enable)
        edtDescription.setEditableMode(enable)
        edtColor.setEditableMode(enable)
        edtSize.setEditableMode(enable)
        edtHsnCode.setEditableMode(enable)
    }

    private fun enabledView(enable: Boolean) {
        edtName.setEditableMode(enable)
        edtPurchasePrice.setEditableMode(enable)
        edtUnitPrice.setEditableMode(enable)
        edtQuantity.setEditableMode(enable)
        edtUnit.setEditableMode(enable)
        edtDescription.setEditableMode(enable)
        edtBrandName.setEditableMode(enable)
        edtModelName.setEditableMode(enable)
        edtSellingPrice.setEditableMode(enable)
        edtColor.setEditableMode(enable)
        edtSize.setEditableMode(enable)
        edtShopName.setEditableMode(enable)
        edtShopContact.setEditableMode(enable)
        edtShopAddress.setEditableMode(enable)
        edtHsnCode.setEditableMode(enable)
        edtCgstPercent.setEditableMode(enable)
        edtCgstAmount.setEditableMode(enable)
        edtSgstPercent.setEditableMode(enable)
        edtSgstAmount.setEditableMode(enable)
    }

    private fun clearText() {
        edtName.setText(EMPTY_STRING)
        edtPurchasePrice.setText(EMPTY_STRING)
        edtUnitPrice.setText(EMPTY_STRING)
        edtQuantity.setText(EMPTY_STRING)
        edtUnit.setText(EMPTY_STRING)
        edtDescription.setText(EMPTY_STRING)
        edtBrandName.setText(EMPTY_STRING)
        edtModelName.setText(EMPTY_STRING)
        edtSellingPrice.setText(EMPTY_STRING)
        edtColor.setText(EMPTY_STRING)
        edtSize.setText(EMPTY_STRING)
        edtShopName.setText(EMPTY_STRING)
        edtShopContact.setText(EMPTY_STRING)
        edtShopAddress.setText(EMPTY_STRING)
        edtHsnCode.setText(EMPTY_STRING)
        edtCgstPercent.setText(EMPTY_STRING)
        edtCgstAmount.setText(EMPTY_STRING)
        edtSgstPercent.setText(EMPTY_STRING)
        edtSgstAmount.setText(EMPTY_STRING)
        imgInventoryImg.setImageResource(R.drawable.ic_image_add)
        edtName.requestFocus()
        scrollView.post {
            scrollView.fullScroll(ScrollView.FOCUS_UP)
        }
    }

    private fun setValues() {
        if ((viewMode == ViewMode.UPDATE.ordinal || viewMode == ViewMode.VIEW_ONLY.ordinal
                        || viewMode == ViewMode.GET_INVENTORY_QUANTITY.ordinal) && null != inventory) {
            inventory?.let {
                edtName.setText(Utils.isEmpty(it.inventoryItemName))
                edtPurchasePrice.setText(Utils.isEmpty(it.itemPurchasePrice))
                edtUnitPrice.setText(Utils.isEmpty(it.itemUnitPrice))
                edtQuantity.setText(Utils.isEmpty(it.itemQuantity))
                edtUnit.setText(Utils.isEmpty(it.itemQuantityUnit))
                edtDescription.setText(Utils.isEmpty(it.description))
                edtBrandName.setText(Utils.isEmpty(it.inventoryItemBrandName))
                edtModelName.setText(Utils.isEmpty(it.inventoryItemModelName))
                edtHsnCode.setText(Utils.isEmpty(it.hsnCode))
                edtSellingPrice.setText(Utils.isEmpty(it.minimumSellingPrice))
                edtColor.setText(Utils.isEmpty(it.inventoryItemColor))
                edtSize.setText(Utils.isEmpty(it.inventoryItemSize))
                edtShopName.setText(Utils.isEmpty(it.purchaseItemShopName))
                edtShopContact.setText(Utils.isEmpty(it.purchaseItemShopContact))
                edtShopAddress.setText(Utils.isEmpty(it.purchaseItemShopAddress))
                edtCgstPercent.setText(Utils.isEmpty(it.gstPercentage.toString()))
                edtCgstAmount.setText(Utils.isEmpty(it.gstAmount.toString()))
                edtSgstPercent.setText(Utils.isEmpty(it.sgstPercentage.toString()))
                edtSgstAmount.setText(Utils.isEmpty(it.sgstAmount.toString()))

                imagePath = it.inventoryItemImagePath
                if (it.inventoryItemImagePath != null) {
                    var uri: Uri = Uri.parse(imagePath)
                    Glide.with(AandSApplication.getInstance())
                            .load(uri)
                            .placeholder(R.drawable.ic_image_add)
                            .into(imgInventoryImg)
                }

                if (!Utils.isAdminUser(this)) {
                    var sellingPriceDouble = Utils.isEmpty(it.minimumSellingPrice, ZERO_STRING).toDouble()
                    val user = SharedPrefsUtils.getUserPreference(this, SharedPrefsUtils.CURRENT_USER)
                    val unit = it.itemQuantityUnit
                    user?.let { customer ->
                        var custSellingPrice: String = EMPTY_STRING
                        var isDiscountedItem = user.discountedItems?.containsKey(it.id) ?: false
                        if (isDiscountedItem) {
                            val discount = user.discountedItems?.get(it.id)
                            discount?.let {
                                custSellingPrice = Utils.currencyLocale(discount.toDouble())
                            }
                        } else {
                            var discount = if (user.discountPercent == 0.0) {
                                user.discountPercent
                            } else {
                                var discountPercent = user.discountPercent
                                sellingPriceDouble * discountPercent / 100
                            }
                            sellingPriceDouble -= discount
                            custSellingPrice = Utils.currencyLocale(sellingPriceDouble)
                        }
                        custSellingPrice = custSellingPrice.plus(" /  ").plus(unit)
                        edtUnitPrice.setText(custSellingPrice)
                    }
                }
            }
        }
        if (viewMode == ViewMode.GET_INVENTORY_QUANTITY.ordinal) {
            inventory?.let {
                edtOrderQuantity.setText(it.itemQuantity)
            }
        }
        setButtonText(if (viewMode == ViewMode.UPDATE.ordinal) resources.getString(R.string.update) else resources.getString(R.string.save))
    }

    private fun setButton(text: String, visibility: Int) {
        setButtonText(text)
        btnSave.visibility = visibility
    }

    private fun setButtonText(text: String) {
        btnSave.text = text
    }


    override fun onBackPressed() {
        setResultToCallingActivity(inventoryId)
    }

    private fun onButtonClick() {
        if (viewMode == ViewMode.GET_INVENTORY_QUANTITY.ordinal) {
            onGetInventoryQuantityClick()
        } else {
            onSaveAndUpdateClick()
        }
    }

    private fun onGetInventoryQuantityClick() {
        val newQuantity = edtOrderQuantity.text.toString()
        if (newQuantity.toInt() > 0) {
            inventory?.let {
                val intUpdatedQuantity = Integer.parseInt(Utils.isEmpty(inventory?.itemQuantity)) - Integer.parseInt(newQuantity)
                val mainInventoryCopy = InventoryItem(it)
                mainInventoryCopy.itemQuantity = intUpdatedQuantity.toString()
                mainInventoryCopy.parentId = it.id
                mainInventoryCopy.inventoryItemBrandName = edtBrandName.getText()
                mainInventoryCopy.inventoryItemModelName = edtModelName.getText()
                mainInventoryCopy.description = edtDescription.getText()
                mainInventoryCopy.inventoryItemColor = edtColor.getText()
                mainInventoryCopy.inventoryItemSize = edtSize.getText()
                mainInventoryCopy.hsnCode = edtHsnCode.getText()
                val message = String.format(resources.getString(R.string.inventory_add_order), intUpdatedQuantity, mainInventoryCopy.inventoryItemName)
                FirebaseUtil.getInstance().isConnected(callBackListener { online ->
                    if (online) {
                        showProgressBar()
                        FirebaseUtil.getInstance().getCustomerDao().addInventoryToOrder(mainInventoryCopy, orderId, newQuantity, callBackListener {
                            dismissProgressBar()
                            showSnackBarMessage(message)
                            setResultToOrderActivity()
                        })
                    } else {
                        showSnackBarMessage(getString(R.string.no_internet_connection))
                    }
                })
            }
        } else {
            showSnackBarMessage(getString(R.string.enter_quantity))
        }
    }

    private fun onSaveAndUpdateClick() {
        if (llContaint.validate()) {
            showProgressBar()
            FirebaseUtil.getInstance().isConnected(callBackListener { online ->
                if (online) {
                    if ((viewMode == ViewMode.UPDATE.ordinal && inventoryId.isNotEmpty())) {
                        actionOnSaveAndUpdate(inventoryId, inventory?.inventoryId)
                    } else {
                        showProgressBar()
                        FirebaseUtil.getInstance().getInventoryDao().getNextInventoryItemId(object : GetAlphaNumericAndNumericIdListener {
                            override fun afterGettingIds(alphaNumericId: String, numericId: String) {
                                dismissProgressBar()
                                actionOnSaveAndUpdate(alphaNumericId, numericId)
                            }
                        })
                    }
                } else {
                    showSnackBarMessage(getString(R.string.no_internet_connection))
                }
            })
        }
    }


    private fun actionOnSaveAndUpdate(alphaNumericItemId: String, numericItemId: String?) {
        val item = InventoryItem()
        item.id = alphaNumericItemId
        item.inventoryId = numericItemId
        if (viewMode != ViewMode.UPDATE.ordinal) {
            item.inventoryType = inventoryType
        }
        item.inventoryItemName = edtName.getText()
        item.itemPurchasePrice = edtPurchasePrice.getText()
        item.itemUnitPrice = edtUnitPrice.getText()
        item.itemQuantity = edtQuantity.getText()
        item.itemQuantityUnit = Utils.isEmpty(edtUnit.getText(), InventoryItem.DEFAULT_QUANTITY_UNIT)
        item.description = edtDescription.getText()
        item.minimumSellingPrice = edtSellingPrice.getText()
        item.inventoryItemBrandName = edtBrandName.getText()
        item.inventoryItemModelName = edtModelName.getText()
        item.hsnCode = edtHsnCode.getText()
        item.inventoryItemColor = edtColor.getText()
        item.inventoryItemSize = edtSize.getText()
        item.inventoryItemImagePath = imagePath
        item.purchaseItemShopName = edtShopName.getText()
        item.purchaseItemShopContact = edtShopContact.getText()
        item.purchaseItemShopAddress = edtShopAddress.getText()
        item.inventoryItemPurchaseDate = System.currentTimeMillis()
        item.inventoryItemLastUpdatedDate = System.currentTimeMillis()
        item.gstPercentage = Utils.isEmptyIntFromString(edtCgstPercent.getText(), 12)
        item.gstAmount = Utils.isEmptyIntFromString(edtCgstAmount.getText(), 12)
        item.sgstPercentage = Utils.isEmptyIntFromString(edtSgstPercent.getText(), 12)
        item.sgstAmount = Utils.isEmptyIntFromString(edtSgstAmount.getText(), 12)

        showProgressBar()
        FirebaseUtil.getInstance().getInventoryDao().saveInventoryItem(item, inventoryType, callBackListener {
            clearText()
            inventoryId = alphaNumericItemId
            inventory = item
            showSnackBarMessage(if (viewMode == ViewMode.UPDATE.ordinal)
                resources.getString(R.string.inventory_updated_message)
            else resources.getString(R.string.inventory_save_message))
            dismissProgressBar()
        })

        if (viewMode == ViewMode.UPDATE.ordinal) {
            if (inventoryType != ListType.LIST_TYPE_MATERIAL.ordinal) {
                showProgressBar()
                inventory?.let {
                    val map = item.getChangedParamList(it)
                    if (map.isNotEmpty()) {
                        FirebaseUtil.getInstance().getInventoryDao().saveInventoryItemHistory(alphaNumericItemId, map, DatabaseReference.CompletionListener { databaseError, databaseReference ->
                            dismissProgressBar()
                        })
                    }
                }
            }
        }
    }

    private fun setResultToCallingActivity(id: String) {
        if ((viewMode == ViewMode.UPDATE.ordinal || viewMode == ViewMode.ADD.ordinal) && null != inventory) {
            val intent = Intent()
            intent.putExtra(INVENTORY_ID, id)
            intent.putExtra(INVENTORY_INSTANCE, inventory)
            intent.putExtras(this.intent)
            setResult(RELOAD_LIST_RESULT_CODE, intent)
        }
        finish()
    }

    private fun setResultToOrderActivity() {
        val intent = Intent()
        intent.putExtra(ORDER_ID, orderId)
        intent.putExtras(intent)
        setResult(ORDER_RELOAD_LIST_RESULT_CODE, intent)
        finish()
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        val pickIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        pickIntent.type = "image/*"
        val chooserIntent = Intent.createChooser(intent, resources.getString(R.string.select_image))
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(pickIntent))
        startActivityForResult(chooserIntent, PICK_IMAGE)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            PICK_IMAGE ->
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openGallery();
                } else {
                    showSnackBarMessage(resources.getString(R.string.accept_camera_permission_msg))
                }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == PICK_IMAGE) {
            val selectedImage = data?.data
            selectedImage?.let {
                uploadCompanyLogo(selectedImage)
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun uploadCompanyLogo(filePath: Uri) {
        var pathName = if (inventoryId.isEmpty()) UUID.randomUUID().toString() else inventoryId
        val ref = FirebaseStorage.getInstance().reference
                .child(AppConstants.INVENTORY_IMAGES_STORAGE_PATH.plus("/").plus(pathName))
        showProgressBar()
        ref.putFile(filePath)
                .addOnSuccessListener { taskSnapshot ->
                    dismissProgressBar()
                    ref.downloadUrl.addOnSuccessListener { uri ->
                        imagePath = uri.toString()
                        filePath.let {
                            val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
                            val cursor = contentResolver
                                    .query(filePath, filePathColumn, null, null, null)
                            if (null != cursor) {
                                cursor.moveToFirst()
                                val columnIndex = cursor.getColumnIndex(filePathColumn[0])
                                val bitmap = BitmapFactory.decodeFile(cursor.getString(columnIndex))
                                if (null != bitmap) {
                                    imgInventoryImg.setImageBitmap(bitmap)
                                }
                            }
                        }
                    }
                }
                .addOnFailureListener { e ->
                    dismissProgressBar()
                }
                .addOnProgressListener { taskSnapshot ->
                    val progress = 100.0 * taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount
                    showProgressBar()
                }
    }

    private fun askingForRequest(): Boolean {
        val permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
        if (permission) {
            ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE), PICK_IMAGE)
        }
        return permission
    }

    private class CustomTextWatcher(private var edtQuantity: CustomEditText,
                                    private var edtPurchasePrice: CustomEditText,
                                    private var edtUnitPrice: CustomEditText) : TextWatcher {
        override fun afterTextChanged(p0: Editable?) {}
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            try {
                val strQuantity = edtQuantity.getText()
                val quantity = if (strQuantity.isNotEmpty())
                    edtQuantity.getText().toDouble() else
                    "1".toDouble()
                val purchasePrice = edtPurchasePrice.getText().toDouble()
                var unitPrice = purchasePrice / quantity
                unitPrice = round(unitPrice, 2)
                if (unitPrice > -1) {
                    edtUnitPrice.setText("" + Utils.currencyLocale(unitPrice))
                } else {
                    edtUnitPrice.setText("")
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        private fun round(d: Double, decimalPlace: Int): Double {
            var bd = BigDecimal(d.toString())
            bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP)
            return bd.toDouble()
        }
    }

    private class CustomGstWatcher(private var edtPercent: CustomEditText,
                                   private var edtUnitPrice: CustomEditText,
                                   private var edtAmount: CustomEditText) : TextWatcher {
        override fun afterTextChanged(p0: Editable?) {}
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            try {
                val strPercent = edtPercent.getText()
                if (strPercent.isNotEmpty()) {
                    val unitPrice = Utils.parseCommaSeparatedCurrency(edtUnitPrice.getText())
                    if (unitPrice > 0) {
                        val percent = edtPercent.getText().toDouble()
                        var unitPrice = unitPrice * percent / 100
                        edtAmount.setText("".plus(unitPrice))
                    } else {
                        edtAmount.setText("")
                    }
                } else {
                    edtAmount.setText("")
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
    }

}

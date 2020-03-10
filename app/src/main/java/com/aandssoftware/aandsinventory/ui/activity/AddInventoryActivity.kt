package com.aandssoftware.aandsinventory.ui.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ScrollView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.aandssoftware.aandsinventory.R
import com.aandssoftware.aandsinventory.common.Utils
import com.aandssoftware.aandsinventory.firebase.FirebaseUtil
import com.aandssoftware.aandsinventory.firebase.GetAlphaNumericAndNumericIdListener
import com.aandssoftware.aandsinventory.listing.ListType
import com.aandssoftware.aandsinventory.models.InventoryCreatedBy
import com.aandssoftware.aandsinventory.models.CallBackListener
import com.aandssoftware.aandsinventory.models.InventoryItem
import com.aandssoftware.aandsinventory.models.ViewMode
import com.aandssoftware.aandsinventory.ui.adapters.MultiImageSelectionAdapter
import com.aandssoftware.aandsinventory.ui.component.CustomEditText
import com.aandssoftware.aandsinventory.utilities.AppConstants
import com.aandssoftware.aandsinventory.utilities.AppConstants.Companion.DEFAULT_GST_STRING
import com.aandssoftware.aandsinventory.utilities.AppConstants.Companion.EMPTY_STRING
import com.aandssoftware.aandsinventory.utilities.AppConstants.Companion.INVALID_ID
import com.aandssoftware.aandsinventory.utilities.AppConstants.Companion.INVENTORY_ID
import com.aandssoftware.aandsinventory.utilities.AppConstants.Companion.INVENTORY_INSTANCE
import com.aandssoftware.aandsinventory.utilities.AppConstants.Companion.LISTING_TYPE
import com.aandssoftware.aandsinventory.utilities.AppConstants.Companion.ORDER_ID
import com.aandssoftware.aandsinventory.utilities.AppConstants.Companion.ORDER_RELOAD_LIST_RESULT_CODE
import com.aandssoftware.aandsinventory.utilities.AppConstants.Companion.PICK_IMAGE_MULTIPLE
import com.aandssoftware.aandsinventory.utilities.AppConstants.Companion.RELOAD_LIST_RESULT_CODE
import com.aandssoftware.aandsinventory.utilities.AppConstants.Companion.TITLE
import com.aandssoftware.aandsinventory.utilities.AppConstants.Companion.VIEW_MODE
import com.aandssoftware.aandsinventory.utilities.AppConstants.Companion.ZERO_STRING
import com.aandssoftware.aandsinventory.utilities.SharedPrefsUtils
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
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class AddInventoryActivity : BaseActivity() {
    var inventory: InventoryItem? = null
    private var imagePath: HashMap<String, String>? = HashMap<String, String>()
    var imageAdapter: MultiImageSelectionAdapter? = null
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
        setViewByMode()
        btnSave.setOnClickListener {
            onButtonClick()
        }
        imgAddImageIcon.setOnClickListener {
            if (askingForRequest().not()) {
                callMultiImageSelection()
            }
        }
        navBarBack.setOnClickListener {
            onBackPressed()
        }
    }

    private fun callMultiImageSelection() {
        val intent = Intent()
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, getString(R.string.select_images)), AppConstants.PICK_IMAGE_MULTIPLE)
    }

    private fun setViewByMode() {
        if (viewMode == ViewMode.VIEW_ONLY.ordinal || viewMode == ViewMode.GET_INVENTORY_QUANTITY.ordinal) {
            enabledView(false)
            imgAddImageIcon.setOnClickListener(null)
            setButton("", View.GONE)
            if (viewMode == ViewMode.GET_INVENTORY_QUANTITY.ordinal) {
                getInventoryQuantityView()
            }
        }

        if (viewMode == ViewMode.ADD_INVENTORY_BY_CUSTOMER.ordinal) {
            slPrice.visibility = View.GONE
            edtPurchasePrice.setText(AppConstants.ZERO_STRING)
            edtUnitPrice.setText(ZERO_STRING)
            edtSellingPrice.setText(ZERO_STRING)
            slGstDetails.visibility = View.GONE
            edtCgstPercent.setText(DEFAULT_GST_STRING)
            edtSgstPercent.setText(DEFAULT_GST_STRING)
            slShopDetails.visibility = View.GONE
            llQuantityAndUnit.visibility = View.VISIBLE
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
                    var list = setMultiImageAdapterFromHashMap()
                    var adapter: MultiImageSelectionAdapter? = setMultiImageAdapter(list)
                    adapter?.loadData(list)
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
                FirebaseUtil.getInstance().isConnected(CallBackListener { online ->
                    if (online) {
                        showProgressBar()
                        FirebaseUtil.getInstance().getCustomerDao().addInventoryToOrder(mainInventoryCopy, orderId, newQuantity, CallBackListener {
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
            FirebaseUtil.getInstance().isConnected(CallBackListener { online ->
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
        if (viewMode == ViewMode.ADD_INVENTORY_BY_CUSTOMER.ordinal) {
            item.createdBy = InventoryCreatedBy.CUSTOMER.toString()
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
        item.gstPercentage = Utils.isEmptyIntFromString(edtCgstPercent.getText(), DEFAULT_GST_STRING.toInt())
        item.gstAmount = Utils.isEmptyIntFromString(edtCgstAmount.getText(), DEFAULT_GST_STRING.toInt())
        item.sgstPercentage = Utils.isEmptyIntFromString(edtSgstPercent.getText(), DEFAULT_GST_STRING.toInt())
        item.sgstAmount = Utils.isEmptyIntFromString(edtSgstAmount.getText(), DEFAULT_GST_STRING.toInt())

        showProgressBar()
        FirebaseUtil.getInstance().getInventoryDao().saveInventoryItem(item, inventoryType, CallBackListener {
            clearText()
            showSnackBarMessage(if (viewMode == ViewMode.UPDATE.ordinal)
                resources.getString(R.string.inventory_updated_message)
            else resources.getString(R.string.inventory_save_message))
            dismissProgressBar()
            saveHistory(alphaNumericItemId, item)
            inventoryId = alphaNumericItemId
            inventory = item
            onBackPressed()
        })


    }

    private fun saveHistory(alphaNumericItemId: String, item: InventoryItem) {
        if (viewMode == ViewMode.UPDATE.ordinal) {
            if (inventoryType != ListType.LIST_TYPE_MATERIAL.ordinal) {
                inventory?.let {
                    val map = item.getChangedParamList(it)
                    if (map.isNotEmpty()) {
                        showProgressBar()
                        FirebaseUtil.getInstance().getInventoryDao().saveInventoryItemHistory(alphaNumericItemId, map, DatabaseReference.CompletionListener { databaseError, databaseReference ->
                            dismissProgressBar()
                        })
                    }
                }
            }
        }
    }

    private fun setResultToCallingActivity(id: String) {
        if ((viewMode == ViewMode.UPDATE.ordinal || viewMode == ViewMode.ADD_INVENTORY_BY_CUSTOMER.ordinal || viewMode == ViewMode.ADD.ordinal) && null != inventory) {
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


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            PICK_IMAGE_MULTIPLE ->
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    callMultiImageSelection()
                } else {
                    showSnackBarMessage(resources.getString(R.string.accept_camera_permission_msg))
                }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == AppConstants.PICK_IMAGE_MULTIPLE) {
            val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
            if (data?.getData() != null) {
                val mImageUri = data.getData()
                mImageUri?.let {
                    val cursor = contentResolver.query(mImageUri,
                            filePathColumn, null, null, null)
                    cursor?.let {
                        cursor.moveToFirst()
                        cursor.close()
                        val mArrayUri = ArrayList<Uri>()
                        mArrayUri.add(mImageUri)
                        uploadInventoryImages(mArrayUri)
                    }
                }
            } else {
                if (data?.getClipData() != null) {
                    val mClipData = data.getClipData()
                    val mArrayUri = ArrayList<Uri>()
                    for (i in 0 until mClipData!!.itemCount) {
                        val item = mClipData.getItemAt(i)
                        val uri = item.uri
                        mArrayUri.add(uri)
                        // Get the cursor
                        val cursor = contentResolver.query(uri, filePathColumn, null, null, null)
                        // Move to first row
                        cursor?.moveToFirst()
                        cursor?.close()
                    }
                    uploadInventoryImages(mArrayUri)
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun uploadInventoryImages(filePath: ArrayList<Uri>) {
        var currentImages = imagePath?.size ?: 0
        var totalImages: Int = currentImages.plus(filePath.size)
        showProgressBar()
        var counter = 0
        filePath.forEachIndexed { pos, value ->
            if (currentImages < AppConstants.INVENTORY_IMAGES_LIMIT) {
                currentImages++
                var pathName = if (inventoryId.isEmpty()) UUID.randomUUID().toString() else inventoryId
                pathName = pathName.plus("_").plus(pos)
                val ref = FirebaseStorage.getInstance().reference
                        .child(AppConstants.INVENTORY_IMAGES_STORAGE_PATH.plus("/").plus(pathName))
                ref.putFile(value)
                        .addOnSuccessListener { taskSnapshot ->
                            ref.downloadUrl.addOnSuccessListener { uri ->
                                counter++
                                if (imagePath == null) {
                                    imagePath = HashMap<String, String>()
                                }
                                imagePath?.let {
                                    it.put(it.size.toString().plus("_key"), uri.toString())
                                    var list = setMultiImageAdapterFromHashMap()
                                    var adapter = setMultiImageAdapter(list)
                                    adapter?.addElement(uri, 0)
                                    if (counter == filePath.size) {
                                        dismissProgressBar()
                                    }
                                }
                            }
                        }
                        .addOnFailureListener { e ->
                            dismissProgressBar()
                        }
                        .addOnProgressListener { taskSnapshot ->

                        }
            } else {
                dismissProgressBar()
            }
        }

        if (totalImages > AppConstants.INVENTORY_IMAGES_LIMIT) {
            showSnackBarMessage(getString(R.string.inventory_image_limit))
        }
    }

    private fun askingForRequest(): Boolean {
        val permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
        if (permission) {
            ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE), PICK_IMAGE_MULTIPLE)
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

    private fun setMultiImageAdapter(list: MutableList<Uri>): MultiImageSelectionAdapter? {
        var adapter: MultiImageSelectionAdapter? = null
        if (list.isNotEmpty()) {
            rvImages.visibility = View.VISIBLE
            imgAddImageIcon.visibility = View.GONE
            adapter = getImagesAdapter()
            rvImages.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, true)
            rvImages.adapter = adapter
        } else {
            rvImages.visibility = View.GONE
            imgAddImageIcon.visibility = View.VISIBLE
        }
        return adapter
    }

    private fun setMultiImageAdapterFromHashMap(): MutableList<Uri> {
        val list: MutableList<String>? = imagePath?.values?.toMutableList()
        val newList: MutableList<Uri> = ArrayList<Uri>()
        list?.let {
            list.forEach {
                newList.add(Uri.parse(it))
            }
        }
        return newList
    }


    private fun getImagesAdapter(): MultiImageSelectionAdapter {
        var adapter = if (null == imageAdapter) {
            var rowClickListner = View.OnClickListener {
                if (askingForRequest().not()) {
                    callMultiImageSelection()
                }
            }

            var deleteClickListner = View.OnClickListener {
                showProgressBar()
                var uri: Uri = it.tag as Uri
                FirebaseUtil.getInstance().getInventoryDao().removeInventoryImage(uri.toString(), object : CallBackListener {
                    override fun getCallBack(result: Boolean) {
                        dismissProgressBar()
                        if (result) {
                            showSnackBarMessage(getString(R.string.inventory_deleted_successfully))
                            var pos: Int = imagePath?.values?.indexOf(uri.toString()) ?: INVALID_ID
                            var deleted = imagePath?.values?.remove(uri.toString()) ?: false
                            if (deleted) {
                                var list = setMultiImageAdapterFromHashMap()
                                var adapter = setMultiImageAdapter(list)
                                if (pos != INVALID_ID) {
                                    adapter?.removeAt(pos)
                                }
                            }
                        } else {
                            showSnackBarMessage(getString(R.string.inventory_deleted_failed))
                        }
                    }
                })

            }
            imageAdapter = MultiImageSelectionAdapter(this, rowClickListner, deleteClickListner)
            imageAdapter as MultiImageSelectionAdapter
        } else {
            imageAdapter as MultiImageSelectionAdapter
        }
        return adapter
    }

}

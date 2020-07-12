package com.aandssoftware.aandsinventory.ui.activity

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.widget.ScrollView
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.aandssoftware.aandsinventory.R
import com.aandssoftware.aandsinventory.common.DateUtils
import com.aandssoftware.aandsinventory.common.Utils
import com.aandssoftware.aandsinventory.firebase.FirebaseUtil
import com.aandssoftware.aandsinventory.firebase.GetAlphaNumericAndNumericIdListener
import com.aandssoftware.aandsinventory.listing.ListType
import com.aandssoftware.aandsinventory.models.*
import com.aandssoftware.aandsinventory.notification.NotificationUtil
import com.aandssoftware.aandsinventory.ui.adapters.CustomFontTextView
import com.aandssoftware.aandsinventory.ui.adapters.MultiImageSelectionAdapter
import com.aandssoftware.aandsinventory.ui.component.ContaintValidationLinerLayout
import com.aandssoftware.aandsinventory.ui.component.CustomEditText
import com.aandssoftware.aandsinventory.utilities.AppConstants
import com.aandssoftware.aandsinventory.utilities.AppConstants.Companion.DEFAULT_GST_DOUBLE
import com.aandssoftware.aandsinventory.utilities.AppConstants.Companion.DEFAULT_GST_STRING
import com.aandssoftware.aandsinventory.utilities.AppConstants.Companion.DOUBLE_DEFAULT_ZERO
import com.aandssoftware.aandsinventory.utilities.AppConstants.Companion.EMPTY_STRING
import com.aandssoftware.aandsinventory.utilities.AppConstants.Companion.ENQUIRY_MAIL_BODY
import com.aandssoftware.aandsinventory.utilities.AppConstants.Companion.ENQUIRY_MAIL_SUBJECT
import com.aandssoftware.aandsinventory.utilities.AppConstants.Companion.ENQUIRY_NOTIFICATION_MASSAGE
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
import com.aandssoftware.aandsinventory.utilities.CrashlaticsUtil
import com.aandssoftware.aandsinventory.utilities.SharedPrefsUtils
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_add_inventory.*
import kotlinx.android.synthetic.main.custom_action_bar_layout.*
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
    private var customerId : String = ""
    private lateinit var orderId: String
    private var viewMode: Int = 0

    private val showInvOfCustToAdmin: Boolean
        get() {
            return intent.getBooleanExtra(AppConstants.SHOW_INVENTORY_OF_CUSTOMER_TO_ADMIN, false)
        }

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
        customerId = intent?.getStringExtra(AppConstants.CUSTOMER_ID)?:""
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
                if(showInvOfCustToAdmin){
                    AppConstants.CUSTOMER_ID_FOR_INVENTORY_LIST_CHANGES = customerId
                    FirebaseUtil.getInstance().getInventoryDao().getInventoryItemFromId(inventoryId, listener)
                }else{
                    FirebaseUtil.getInstance().getInventoryDao().getInventoryItemFromId(inventoryId, listener)
                }
            }
        }
    }

    private fun checkUserAndHideData() {
        if(inventoryType == ListType.LIST_TYPE_MATERIAL.ordinal){
            if (!Utils.isAdminUser(this)) {
                llQuantityAndUnit.visibility = View.GONE
                edtSellingPrice.visibility = View.GONE
                edtPurchasePrice.visibility = View.GONE
                slShopDetails.visibility = View.GONE
                edtCgstAmount.visibility = View.GONE
                edtSgstAmount.visibility = View.GONE
            }
        }else{
            edtSellingPrice.setText("0.000")
            edtSellingPrice.visibility = View.GONE
        }
    }


    private fun setUpUI() {
        setupActionBar(title)
        val watcher = CustomTextWatcher(edtQuantity, edtPurchasePrice, edtUnitPrice)
        var edtTextToCalculate = if(inventoryType==ListType.LIST_TYPE_MATERIAL.ordinal){edtSellingPrice}else{edtPurchasePrice}
        val cGstWatcher = CustomGstWatcher(edtCgstPercent, edtTextToCalculate, edtCgstAmount, edtUnitPrice)
        val sGstWatcher = CustomGstWatcher(edtSgstPercent, edtTextToCalculate, edtSgstAmount, edtUnitPrice)
        edtCgstPercent.addTextChangedListener(cGstWatcher)
        edtSgstPercent.addTextChangedListener(sGstWatcher)
        edtPurchasePrice.addTextChangedListener(watcher)
        edtQuantity.addTextChangedListener(watcher)
        setViewByMode()
        initAndLoadBannerAd()
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
            imgAddImageIcon.isEnabled = false
            imgAddImageIcon.setOnClickListener(null)
            setButton("", View.GONE)
            if (viewMode == ViewMode.GET_INVENTORY_QUANTITY.ordinal) {
                getInventoryQuantityView()
            }
        }

        if(inventoryType == ListType.LIST_TYPE_MATERIAL.ordinal){
            edtSellingPrice.addTextChangedListener(ClearGstWatcher(edtCgstPercent,edtCgstAmount,edtSgstPercent,edtSgstAmount))
        }else{
            edtPurchasePrice.addTextChangedListener(ClearGstWatcher(edtCgstPercent,edtCgstAmount,edtSgstPercent,edtSgstAmount))
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

        if(Utils.isAdminUser(this)){
            if (viewMode==ViewMode.VIEW_ONLY.ordinal) {
                supportActionBar?.customView?.findViewById<CustomFontTextView>(R.id.actionBarTitle)?.setOnLongClickListener {
                    inventory?.let {
                        var duplicateCopy = InventoryItem(it)
                        duplicateCopy.id =""
                        showProgressBar()
                        FirebaseUtil.getInstance().getInventoryDao().getNextInventoryItemId(inventoryType,object : GetAlphaNumericAndNumericIdListener {
                            override fun afterGettingIds(alphaNumericId: String, numericId: String) {
                                duplicateCopy.id = alphaNumericId
                                duplicateCopy.inventoryId = numericId
                                dismissProgressBar()
                                showProgressBar()
                                FirebaseUtil.getInstance().getInventoryDao().saveInventoryItem(duplicateCopy, inventoryType, CallBackListener {
                                    dismissProgressBar()
                                    showSnackBarMessage(getString(R.string.duplicate_inventory_msg))
                                    finish()
                                })
                            }
                        })
                    }
                    false
                }
            }
        }
    }

    private fun getInventoryQuantityView() {
        sectionRequiredQuantity.visibility = View.VISIBLE
        setButton(resources.getString(R.string.done), View.VISIBLE)
        addItem.setOnClickListener {
            val value = edtOrderQuantity.text.toString()
            if (value.isNotEmpty()) {
                var orderQuantity = Utils.isEmptyIntFromString(value, 0)
                if (orderQuantity > 0) {
                    orderQuantity += 1
                    edtOrderQuantity.setText(orderQuantity.toString())
                }
            }
        }
        removeItem.setOnClickListener {
            val value = edtOrderQuantity.text.toString()
            if (value.isNotEmpty()) {
                var orderQuantity = Utils.isEmptyIntFromString(value, 0)
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
                    if(list.isNotEmpty()){
                        var adapter: MultiImageSelectionAdapter? = setMultiImageAdapter(list)
                        adapter?.loadData(list)
                    }
                }

                if(inventoryType == ListType.LIST_TYPE_MATERIAL.ordinal){
                    if (!Utils.isAdminUser(this)) {
                        val unit = it.itemQuantityUnit
                        var sellingPriceAfterDiscount = Utils.getSellingPriceAfterDiscount(this, it)
                        var custSellingPrice = Utils.currencyLocale(sellingPriceAfterDiscount)
                        custSellingPrice = custSellingPrice.plus(" /  ").plus(unit)
                        edtUnitPrice.setText(custSellingPrice)

                        rlNoPriceInventory.visibility  =  if(sellingPriceAfterDiscount==AppConstants.DOUBLE_DEFAULT_ZERO){
                            View.VISIBLE
                        }else{
                            View.GONE
                        }
                        btnEnquiry.setOnClickListener {
                            inventory?.let {
                                enquiryDialog(AddInventoryActivity@this,it)
                            }
                        }
                    }
                }else{
                    val unit = it.itemQuantityUnit
                    var purchasePrice = Utils.isEmpty(it.itemPurchasePrice, DOUBLE_DEFAULT_ZERO)
                    var itemQuantity = Utils.isEmpty(it.itemQuantity, DOUBLE_DEFAULT_ZERO)
                    var unitPrice=  purchasePrice / itemQuantity
                    var custPurchasePriceWihUnit = Utils.currencyLocale(unitPrice) .plus(" /  ").plus(unit)
                    edtUnitPrice.setText(custPurchasePriceWihUnit)
                    rlNoPriceInventory.visibility  = View.GONE
                }
                setViewByModeAfterSetValue()
            }
        }
        if (viewMode == ViewMode.GET_INVENTORY_QUANTITY.ordinal) {
            inventory?.let {
                edtOrderQuantity.setText(it.itemQuantity)
            }
        }
        setButtonText(if (viewMode == ViewMode.UPDATE.ordinal) resources.getString(R.string.update) else resources.getString(R.string.save))
    }

    private fun setViewByModeAfterSetValue() {
        if (inventoryType == ListType.LIST_TYPE_ORDER_INVENTORY.ordinal) {
            llQuantityAndUnit.visibility = View.VISIBLE
            edtSellingPrice.visibility = View.VISIBLE

            inventory?.let {
                var totalGstAmount = it.sgstAmount + it.gstAmount
                var sellingPrice = it.minimumSellingPrice?.let { Utils.parseCommaSeparatedCurrency(it) }
                        ?: 0
                var title = EMPTY_STRING.plus(" [ ").plus("Amount ( ")
                        .plus(it.itemQuantity).plus("*").plus(sellingPrice).plus(" ) ")
                        .plus(it.taxableAmount).plus(" + ").plus(" GST ").plus(totalGstAmount).plus(" ]")
                edtSellingPrice.setTitle(getString(R.string.final_bill_amount).plus(title))
                edtSellingPrice.setText(it.finalBillAmount.toString())
            }
        }
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
                val intUpdatedQuantity = Utils.isEmptyIntFromString(newQuantity, 1)
                val mainInventoryCopy = InventoryItem(it)
                mainInventoryCopy.itemQuantity = intUpdatedQuantity.toString()
                mainInventoryCopy.parentId = it.id
                var sellingPriceAfterDiscount = Utils.getSellingPriceAfterDiscount(this, mainInventoryCopy)
                mainInventoryCopy.minimumSellingPrice = sellingPriceAfterDiscount.toString()
                mainInventoryCopy.itemUnitPrice = sellingPriceAfterDiscount.toString()
                var amountPerQuntity: Double = intUpdatedQuantity * sellingPriceAfterDiscount
                var cgst: Double = Utils.getAmountOfPercentage(mainInventoryCopy.gstPercentage, amountPerQuntity)
                mainInventoryCopy.gstAmount = cgst
                var sgst: Double = Utils.getAmountOfPercentage(mainInventoryCopy.sgstPercentage, amountPerQuntity)
                mainInventoryCopy.sgstAmount = sgst
                var totalGstForItem = cgst + sgst
                mainInventoryCopy.finalBillAmount = (amountPerQuntity + totalGstForItem)
                mainInventoryCopy.taxableAmount = amountPerQuntity
                mainInventoryCopy.inventoryItemBrandName = edtBrandName.getText()
                mainInventoryCopy.inventoryItemModelName = edtModelName.getText()
                mainInventoryCopy.description = edtDescription.getText()
                mainInventoryCopy.inventoryItemColor = edtColor.getText()
                mainInventoryCopy.inventoryItemSize = edtSize.getText()
                mainInventoryCopy.hsnCode = edtHsnCode.getText()
                mainInventoryCopy.discountRateForCompany = Utils.getDiscount(this,it)


                val message = String.format(resources.getString(R.string.inventory_add_order), intUpdatedQuantity, mainInventoryCopy.inventoryItemName)
                if (FirebaseUtil.getInstance().isInternetConnected(this)) {
                    showProgressBar()
                    FirebaseUtil.getInstance().getCustomerDao().addInventoryToOrder(mainInventoryCopy, orderId, newQuantity, CallBackListener {
                        dismissProgressBar()
                        showSnackBarMessage(message)
                        setResultToOrderActivity()
                    })
                } else {
                    showSnackBarMessage(getString(R.string.no_internet_connection))
                }
            }
        } else {
            showSnackBarMessage(getString(R.string.enter_quantity))
        }
    }

    private fun onSaveAndUpdateClick() {
        if (llContaint.validate()) {
            showProgressBar()
            if (FirebaseUtil.getInstance().isInternetConnected(this)) {
                if ((viewMode == ViewMode.UPDATE.ordinal && inventoryId.isNotEmpty())) {
                    actionOnSaveAndUpdate(inventoryId, inventory?.inventoryId)
                } else {
                    showProgressBar()
                    FirebaseUtil.getInstance().getInventoryDao().getNextInventoryItemId(inventoryType,object : GetAlphaNumericAndNumericIdListener {
                        override fun afterGettingIds(alphaNumericId: String, numericId: String) {
                            dismissProgressBar()
                            actionOnSaveAndUpdate(alphaNumericId, numericId)
                        }
                    })
                }
            } else {
                showSnackBarMessage(getString(R.string.no_internet_connection))
            }
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
        item.finalBillAmount = Utils.isEmpty(edtSellingPrice.getText(), DOUBLE_DEFAULT_ZERO)
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
        item.gstPercentage = Utils.isEmpty(edtCgstPercent.getText(), DEFAULT_GST_DOUBLE)
        item.gstAmount = Utils.isEmpty(edtCgstAmount.getText(), DEFAULT_GST_DOUBLE)
        item.sgstPercentage = Utils.isEmpty(edtSgstPercent.getText(), DEFAULT_GST_DOUBLE)
        item.sgstAmount = Utils.isEmpty(edtSgstAmount.getText(), DEFAULT_GST_DOUBLE)

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
                   /* val map = item.getChangedParamList(it)
                    if (map.isNotEmpty()) {
                        showProgressBar()
                        FirebaseUtil.getInstance().getInventoryDao().saveInventoryItemHistory(alphaNumericItemId, map, DatabaseReference.CompletionListener { databaseError, databaseReference ->
                            dismissProgressBar()
                        })
                    }*/
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
                unitPrice =Utils.round(unitPrice, 2)
                if (unitPrice > -1) {
                    edtUnitPrice.setText("" + Utils.currencyLocale(unitPrice))
                } else {
                    edtUnitPrice.setText("")
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private class ClearGstWatcher(private var edtCgstPercent: CustomEditText,
                                 private var edtCgstAmount: CustomEditText,
                                 private var edtSgstPercent: CustomEditText,
                                 private var edtSgstAmount: CustomEditText) : TextWatcher {
        override fun afterTextChanged(p0: Editable?) {}
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            try {
                edtCgstPercent.setText("")
                edtCgstAmount.setText("")
                edtSgstPercent.setText("")
                edtSgstAmount.setText("")
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private class CustomGstWatcher(private var edtPercent: CustomEditText,
                                   private var edtSellingPrice: CustomEditText,
                                   private var edtAmount: CustomEditText,
                                   private var edtUnitPrice: CustomEditText) : TextWatcher {
        override fun afterTextChanged(p0: Editable?) {}
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            try {
                val strPercent = edtPercent.getText()
                if (strPercent.isNotEmpty()) {

                    val unitPrice: Double = if (edtSellingPrice.getText().isNotEmpty()) {
                        var price: Double = 0.0
                        try {
                            price = edtSellingPrice.getText().toDouble()
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                        price
                    } else {
                        Utils.parseCommaSeparatedCurrency(edtUnitPrice.getText())
                    }
                    if (unitPrice > 0) {
                        val percent = edtPercent.getText().toDouble()
                        var unitPrice = unitPrice * percent / 100
                        unitPrice =Utils.round(unitPrice, 2)
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
                if ((viewMode == ViewMode.VIEW_ONLY.ordinal || viewMode == ViewMode.GET_INVENTORY_QUANTITY.ordinal).not()) {
                    if (askingForRequest().not()) {
                        callMultiImageSelection()
                    }
                }
            }

            var deleteClickListner = View.OnLongClickListener {
                if (Utils.isAdminUser(this)) {
                    var uri: Uri = it.tag as Uri
                    deleteInventoryImage(uri, this)
                }
                false
            }
            imageAdapter = MultiImageSelectionAdapter(this, rowClickListner, deleteClickListner)
            imageAdapter as MultiImageSelectionAdapter
        } else {
            imageAdapter as MultiImageSelectionAdapter
        }
        return adapter
    }


    fun deleteInventoryImage(uri: Uri, context: Context) {
        val alertDialogBuilderUserInput = AlertDialog.Builder(context)
        alertDialogBuilderUserInput
                .setTitle(context.getString(R.string.delete_inventory_image_title))
                .setMessage(context.getString(R.string.delete_inventory_image))
                .setCancelable(false)
                .setPositiveButton(context.getString(R.string.yes)
                ) { dialogBox, id ->
                    showProgressBar()
                    FirebaseUtil.getInstance().getInventoryDao().removeInventoryImage(uri.toString(), object : CallBackListener {
                        override fun getCallBack(result: Boolean) {
                            dismissProgressBar()
                            if (result) {
                                showSnackBarMessage(getString(R.string.inventory_deleted_successfully))
                                var pos: Int = imagePath?.values?.indexOf(uri.toString())
                                        ?: INVALID_ID
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
                .setNegativeButton(
                        context.getString(R.string.no)
                ) { dialogBox, id -> dialogBox.cancel() }

        val alertDialog = alertDialogBuilderUserInput.create()
        alertDialog.show()
    }

    private fun initAndLoadBannerAd() {
        MobileAds.initialize(this, getString(R.string.app_id_for_adds))
        adViewBannerAddInventory01.loadAd(AdRequest.Builder().build())
        adViewBannerAddInventory02.loadAd(AdRequest.Builder().build())
    }

    private fun enquiryDialog(activity : Context,inventoryItem: InventoryItem) {
        val alertDialogBuilderUserInput = AlertDialog.Builder(activity)
        var view: View = LayoutInflater.from(activity).inflate(R.layout.price_enquiry_dialog, null)
        alertDialogBuilderUserInput
                .setView(view)
                .setCancelable(false)
                .setPositiveButton(activity.getString(R.string.send)) {dialogBox, _ ->
                    var enquiryQuantity = view.findViewById<CustomEditText>(R.id.edtEnquiryQuantity).getText()
                    var enquiryUnit = view.findViewById<CustomEditText>(R.id.edtEnquiryUnit).getText()
                    var enquiryDescription = view.findViewById<CustomEditText>(R.id.edtEnquiryDescription).getText()
                    var slEnquiry = view.findViewById<ContaintValidationLinerLayout>(R.id.cvlEnquiryContaint)
                    if(slEnquiry?.validate()==true){
                        dialogBox.cancel()
                        val user = SharedPrefsUtils.getUserPreference(activity, SharedPrefsUtils.CURRENT_USER)
                        var subject  = ENQUIRY_MAIL_SUBJECT.plus(inventoryItem.inventoryItemName)
                        var notificationMessage  = ENQUIRY_NOTIFICATION_MASSAGE
                                .replace("#CompanyName#",user?.customerName?:"")
                                .replace("#ProductName#",inventoryItem.inventoryItemName?:"")
                                .replace("#Quantity#",enquiryQuantity)
                                .replace("#Unit#",enquiryUnit)
                        showNotificationForEnquiry(subject,notificationMessage,inventoryItem,user?.id?:"")
                        var body  = ENQUIRY_MAIL_BODY
                                .replace("#Date#",DateUtils.getDateFormatted(System.currentTimeMillis()))
                                .replace("#CompanyName#",user?.customerName?:"")
                                .replace("#Address#",user?.address?:"")
                                .replace("#ProductName#",inventoryItem.inventoryItemName?:"")
                                .replace("#Quantity#",enquiryQuantity)
                                .replace("#Unit#",enquiryUnit)
                                .replace("#Description#",enquiryDescription)
                                .replace("#ID#",inventoryItem.id?:"")
                        Utils.sendMailToAdmin(this,subject,body)

                    }
                }
                .setNegativeButton(activity.getString(R.string.cancel))
                { dialogBox, _ -> dialogBox.cancel() }

        val alertDialog = alertDialogBuilderUserInput.create()
        alertDialog.show()
    }

    private fun showNotificationForEnquiry(title : String,message : String,inventoryItem: InventoryItem,custID : String) {
        var appVersion = SharedPrefsUtils.getAppVersionPreference(this, SharedPrefsUtils.APP_VERSION)
        appVersion?.let {
            var customerId = appVersion.adminCustomerId ?: AppConstants.EMPTY_STRING
            FirebaseUtil.getInstance().getCustomerDao().getCustomerFromID(customerId,
                    object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            var model = FirebaseUtil.getInstance().getClassData(dataSnapshot, CustomerModel::class.java)
                            model?.let {
                                CrashlaticsUtil.logInfo(CrashlaticsUtil.TAG_INFO, Gson().toJson(model))
                                model.notificationToken?.let { token ->
                                    var map = HashMap<String, String>()
                                    map.put(NotificationUtil.TITLE, title)
                                    map.put(NotificationUtil.BODY, message)
                                    map.put(NotificationUtil.INVENTORY_ID, inventoryItem.id ?: EMPTY_STRING)
                                    map.put(NotificationUtil.CUSTOMER_ID,custID)
                                    map.put(NotificationUtil.FLOW_ID, NotificationUtil.NOTIFICATION_FLOW)
                                    map.put(NotificationUtil.NOTIFICATION_TYPE, NotificationUtil.NotificationType.ENQUIRY_FOR_PRODUCT_PRICE.toString())
                                    NotificationUtil.sendNotification(token, map, CallBackListener {

                                    })
                                }
                            }
                        }

                        override fun onCancelled(databaseError: DatabaseError) {
                            dismissProgressBar()
                        }
                    })
        }

    }
}

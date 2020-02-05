package com.aandssoftware.aandsinventory.ui.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.ScrollView
import androidx.core.app.ActivityCompat
import androidx.core.view.isNotEmpty
import com.aandssoftware.aandsinventory.R
import com.aandssoftware.aandsinventory.common.Utils
import com.aandssoftware.aandsinventory.firebase.FirebaseUtil
import com.aandssoftware.aandsinventory.firebase.GetAlphaNumericAndNumericIdListener
import com.aandssoftware.aandsinventory.models.callBackListener
import com.aandssoftware.aandsinventory.models.CustomerModel
import com.aandssoftware.aandsinventory.models.Permissions
import com.aandssoftware.aandsinventory.models.ViewMode
import com.aandssoftware.aandsinventory.utilities.AppConstants
import com.aandssoftware.aandsinventory.utilities.AppConstants.Companion.EMPTY_STRING
import com.aandssoftware.aandsinventory.utilities.AppConstants.Companion.FIRE_BASE_CUSTOMER_ID
import com.aandssoftware.aandsinventory.utilities.AppConstants.Companion.NUMERIC_CUSTOMER_ID
import com.aandssoftware.aandsinventory.utilities.AppConstants.Companion.PICK_IMAGE
import com.aandssoftware.aandsinventory.utilities.AppConstants.Companion.RELOAD_LIST_RESULT_CODE
import com.aandssoftware.aandsinventory.utilities.AppConstants.Companion.TITLE
import com.aandssoftware.aandsinventory.utilities.AppConstants.Companion.VIEW_MODE
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_add_customer.*
import kotlinx.android.synthetic.main.activity_add_inventory.btnSave
import kotlinx.android.synthetic.main.custom_action_bar_layout.*
import java.util.UUID.randomUUID
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_add_customer.progress_bar
import kotlinx.android.synthetic.main.activity_login.*


class AddCustomerActivity : BaseActivity() {
    lateinit var item: CustomerModel
    private var imagePath: String? = null
    lateinit var title: String
    private var fireBaseCustomerId: String = EMPTY_STRING
    private var numericCustomerId: String = EMPTY_STRING
    private var viewMode: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_customer)
        getAndAssignIntentData()
        setUpUI()
    }


    private fun setUpUI() {
        setupActionBar(title)
        progressBar = progress_bar
        imgCustomerImg.setOnClickListener {
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
        if (viewMode == ViewMode.VIEW_ONLY.ordinal) {
            enabledView(false)
            imgCustomerImg.setOnClickListener(null)
            btnSave.visibility = View.GONE
        }
        if (viewMode == ViewMode.ADD.ordinal) {
            loginDetailsSection.visibility = View.VISIBLE
        }
    }

    private fun enabledView(enable: Boolean) {
        edtCustomerName.setEditableMode(enable)
        edtCustomerEmail.setEditableMode(enable)
        edtCustomerContact.setEditableMode(enable)
        edtCustomerGst.setEditableMode(enable)
        edtCustomerContactPerson.setEditableMode(enable)
        edtCustomerContactPersonNumber.setEditableMode(enable)
        edtCustomerAddress.setEditableMode(enable)
        edtCustomerDescription.setEditableMode(enable)
        edtCustomerRequirement.setEditableMode(enable)
    }

    private fun clearText() {
        edtCustomerName.setText(EMPTY_STRING)
        edtCustomerEmail.setText(EMPTY_STRING)
        edtCustomerContact.setText(EMPTY_STRING)
        edtCustomerGst.setText(EMPTY_STRING)
        edtCustomerContactPerson.setText(EMPTY_STRING)
        edtCustomerContactPersonNumber.setText(EMPTY_STRING)
        edtCustomerAddress.setText(EMPTY_STRING)
        edtCustomerDescription.setText(EMPTY_STRING)
        edtCustomerRequirement.setText(EMPTY_STRING)
        edtUsername.setText(EMPTY_STRING)
        edtPassword.setText(EMPTY_STRING)
        edtConfirmPassword.setText(EMPTY_STRING)
        edtCustomerName.requestFocus()
        imgCustomerImg.setImageResource(R.drawable.ic_image_add)
        imagePath = EMPTY_STRING
        scrollViewCustomer.post {
            scrollViewCustomer.fullScroll(ScrollView.FOCUS_UP)
        }
    }

    private fun setValues() {
        if ((viewMode == ViewMode.UPDATE.ordinal || viewMode == ViewMode.VIEW_ONLY.ordinal) && null != item) {
            edtCustomerName.setText(Utils.isEmpty(item.customerName))
            edtCustomerEmail.setText(Utils.isEmpty(item.companyMail))
            edtCustomerContact.setText(Utils.isEmpty(item.customerNumber))
            edtCustomerGst.setText(Utils.isEmpty(item.customerGstNumber))
            edtCustomerContactPerson.setText(Utils.isEmpty(item.contactPerson))
            edtCustomerContactPersonNumber.setText(Utils.isEmpty(item.contactPersonNumber))
            edtCustomerAddress.setText(Utils.isEmpty(item.address))
            edtCustomerDescription.setText(Utils.isEmpty(item.description))
            edtCustomerRequirement.setText(Utils.isEmpty(item.requirement))
            imagePath = item.imagePath
            if (item.imagePath != null) {
                var uri: Uri = Uri.parse(imagePath)
                Glide.with(this)
                        .load(uri)
                        .placeholder(R.drawable.ic_image_add)
                        .crossFade()
                        .into(imgCustomerImg)
            }
        }
        btnSave.text = if (viewMode == ViewMode.UPDATE.ordinal) resources.getString(R.string.update) else resources.getString(R.string.save)
    }

    override fun onBackPressed() {
        setResultToCallingActivity(fireBaseCustomerId)
    }

    private fun onButtonClick() {
        if (edtCustomerName.getText().isNotEmpty()) {
            if (edtCustomerContact.getText().isNotEmpty()) {
                if (edtCustomerGst.getText().isNotEmpty()) {
                    if (viewMode == ViewMode.ADD.ordinal) {
                        if (edtUsername.getText().isNotEmpty()) {
                            if (edtPassword.getText().isNotEmpty() && edtConfirmPassword.getText().isNotEmpty()) {
                                if (!edtPassword.getText().equals(edtConfirmPassword.getText())) {
                                    showSnackBarMessage(getString(R.string.password_not_match))
                                    return
                                }
                            } else {
                                showSnackBarMessage(getString(R.string.enter_password))
                                return
                            }
                        } else {
                            showSnackBarMessage(getString(R.string.enter_username))
                            return
                        }
                    }

                    FirebaseUtil.getInstance().isConnected(callBackListener {
                        if (it) {
                            if (viewMode == ViewMode.UPDATE.ordinal && fireBaseCustomerId.isNotEmpty()) {
                                onSubmit(fireBaseCustomerId, numericCustomerId.toLong())
                            } else {
                                showProgressBar()
                                FirebaseUtil.getInstance().getCustomerDao().getNextCustomerItemId(object : GetAlphaNumericAndNumericIdListener {
                                    override fun afterGettingIds(alphaNumericId: String, numericId: String) {
                                        dismissProgressBar()
                                        onSubmit(alphaNumericId, numericId.toLong())
                                    }
                                })
                            }
                        } else {
                            showSnackBarMessage(getString(R.string.no_internet_connection))
                        }
                    })
                } else {
                    showSnackBarMessage(getString(R.string.enter_customer_gst))
                }
            } else {
                showSnackBarMessage(getString(R.string.enter_customer_number))
            }
        } else {
            showSnackBarMessage(getString(R.string.enter_customer_name))
        }
    }

    private fun onSubmit(fireBaseUniqueCustomerId: String, customerId: Long) {
        val model = CustomerModel()
        model.id = fireBaseUniqueCustomerId
        model.customerID = customerId.toString()
        model.customerName = edtCustomerName.getText()
        model.customerNumber = edtCustomerContact.getText()
        model.companyMail = edtCustomerEmail.getText()
        model.customerGstNumber = edtCustomerGst.getText()
        model.contactPerson = edtCustomerContactPerson.getText()
        model.contactPersonNumber = edtCustomerContactPersonNumber.getText()
        model.address = edtCustomerAddress.getText()
        model.description = edtCustomerDescription.getText()
        model.requirement = edtCustomerRequirement.getText()
        model.dateCreated = System.currentTimeMillis()
        model.imagePath = imagePath;
        if (viewMode == ViewMode.ADD.ordinal) {
            model.username = edtUsername.getText()
            model.password = edtPassword.getText()
            var map = HashMap<String, String>()
            map[Permissions.INITIAL_CREATED_PERMISSION.toString()] = Permissions.INITIAL_CREATED_PERMISSION.toString()
            model.permission = map
        } else {
            item.let {
                model.username = item.username
                model.password = item.password
                model.permission = item.permission
            }
        }
        showProgressBar()
        FirebaseUtil.getInstance().getCustomerDao().saveCustomerItem(model, callBackListener {
            dismissProgressBar()
            clearText()
            fireBaseCustomerId = fireBaseUniqueCustomerId
            showSnackBarMessage(if (viewMode == ViewMode.UPDATE.ordinal) resources.getString(R.string.customer_updated_message) else resources.getString(R.string.customer_save_message))
        })
    }

    private fun setResultToCallingActivity(id: String) {
        var intent = Intent()
        intent.putExtra(FIRE_BASE_CUSTOMER_ID, id)
        intent.putExtras(intent)
        setResult(RELOAD_LIST_RESULT_CODE, intent)
        finish()
    }

    private fun getAndAssignIntentData() {
        title = intent.getStringExtra(TITLE)
        fireBaseCustomerId = intent.getStringExtra(FIRE_BASE_CUSTOMER_ID)
        numericCustomerId = intent.getStringExtra(NUMERIC_CUSTOMER_ID)
        viewMode = intent.getIntExtra(VIEW_MODE, ViewMode.ADD.ordinal)
        if (fireBaseCustomerId.isNotEmpty()) {
            FirebaseUtil.getInstance().getCustomerDao().getCustomerFromID(fireBaseCustomerId, object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {}
                override fun onDataChange(p0: DataSnapshot) {
                    var customerModel = p0.getValue(CustomerModel::class.java)
                    customerModel?.let {
                        item = customerModel;
                        setValues()
                    }
                }
            });
        }
    }

    private fun openGallery() {
        val getIntent = Intent(Intent.ACTION_GET_CONTENT)
        getIntent.type = "image/*"
        val pickIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        pickIntent.type = "image/*"
        val chooserIntent = Intent.createChooser(getIntent, resources.getString(R.string.select_image))
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(pickIntent))
        startActivityForResult(chooserIntent, PICK_IMAGE)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            PICK_IMAGE ->
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openGallery();
                } else {
                    Utils.showToast(resources.getString(R.string.accept_camera_permission_msg), this)
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
        var pathName = if (fireBaseCustomerId.isEmpty()) randomUUID().toString() else fireBaseCustomerId
        val ref = FirebaseStorage.getInstance().reference
                .child(AppConstants.CUSTOMER_IMAGES_STORAGE_PATH.plus("/").plus(pathName))
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
                                    imgCustomerImg.setImageBitmap(bitmap)
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
}
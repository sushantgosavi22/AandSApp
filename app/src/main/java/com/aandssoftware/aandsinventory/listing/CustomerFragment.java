package com.aandssoftware.aandsinventory.listing;

import android.Manifest;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import com.aandssoftware.aandsinventory.R;
import com.aandssoftware.aandsinventory.common.Utils;
import com.aandssoftware.aandsinventory.database.RealmManager;
import com.aandssoftware.aandsinventory.models.CallbackRealmObject;
import com.aandssoftware.aandsinventory.models.CustomerModel;
import java.math.BigDecimal;

public class CustomerFragment extends DialogFragment {
  
  private static final int PICK_IMAGE = 100;
  private boolean shouldUpdate;
  private CustomerModel customerModel;
  private String imagePath;
  private ImageView imgCustomerLogo;
  private CallbackRealmObject callbackRealmObject;
  
  public CallbackRealmObject getCallbackRealmObject() {
    return callbackRealmObject;
  }
  
  public CustomerFragment setCallbackRealmObject(
      CallbackRealmObject callbackRealmObject) {
    this.callbackRealmObject = callbackRealmObject;
    return this;
  }
  
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    RealmManager.open();
  }
  
  @Override
  public void onDestroy() {
    super.onDestroy();
    RealmManager.close();
  }
  
  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    LayoutInflater layoutInflaterAndroid = LayoutInflater.from(getActivity());
    View view = layoutInflaterAndroid.inflate(R.layout.add_customer_item, null);
    AlertDialog.Builder alertDialogBuilderUserInput =
        new AlertDialog.Builder(getActivity());
    alertDialogBuilderUserInput.setView(view);
    
    imgCustomerLogo = (ImageView) view.findViewById(R.id.imgCustomerLogo);
    final EditText edtCustomerName = (EditText) view.findViewById(R.id.edtCustomerName);
    final EditText edtCustomerMail = (EditText) view.findViewById(R.id.edtCustomerMail);
    final EditText edtCustomerNumber = (EditText) view.findViewById(R.id.edtCustomerNumber);
    final EditText edtCustomerGst = (EditText) view.findViewById(R.id.edtCustomerGst);
    final EditText edtContactPerson = (EditText) view.findViewById(R.id.edtContactPerson);
    final EditText edtContactPersonNumber = (EditText) view
        .findViewById(R.id.edtContactPersonNumber);
    final EditText edtAddress = (EditText) view.findViewById(R.id.edtAddress);
    final EditText edtDescription = (EditText) view.findViewById(R.id.edtDescription);
  
    if (!shouldUpdate && customerModel != null) {
      edtCustomerName.setEnabled(false);
      edtCustomerMail.setEnabled(false);
      edtCustomerNumber.setEnabled(false);
      edtCustomerGst.setEnabled(false);
      edtContactPerson.setEnabled(false);
      edtContactPersonNumber.setEnabled(false);
      edtAddress.setEnabled(false);
      edtDescription.setEnabled(false);
    }
    if (customerModel != null) {
      edtCustomerName.setText("" + Utils.isEmpty(customerModel.getCustomerName()));
      edtCustomerMail.setText("" + Utils.isEmpty(customerModel.getCompanyMail()));
      edtCustomerNumber.setText("" + Utils.isEmpty(customerModel.getCustomerNumber()));
      edtCustomerGst.setText("" + Utils.isEmpty(customerModel.getCustomerGstNumber()));
      edtDescription.setText("" + Utils.isEmpty(customerModel.getDescription()));
      edtContactPerson.setText("" + Utils.isEmpty(customerModel.getContactPerson()));
      edtContactPersonNumber.setText("" + Utils.isEmpty(customerModel.getContactPersonNumber()));
      edtAddress.setText("" + Utils.isEmpty(customerModel.getAddress()));
      if (customerModel.getImagePath() != null) {
        Bitmap bitmap = BitmapFactory
            .decodeFile(Utils.isEmpty(customerModel.getImagePath()));
        if (null != customerModel && null != bitmap) {
          imgCustomerLogo.setImageBitmap(bitmap);
        }
      }
    }
    imgCustomerLogo.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        if (ActivityCompat
            .checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED) {
          ActivityCompat.requestPermissions(getActivity(),
              new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                  Manifest.permission.WRITE_EXTERNAL_STORAGE}, PICK_IMAGE);
        } else {
          openGallery();
        }
      }
    });
    alertDialogBuilderUserInput
        .setCancelable(false)
        .setPositiveButton(
            shouldUpdate ? getString(R.string.update) : getString(R.string.save),
            new DialogInterface.OnClickListener() {
              public void onClick(DialogInterface dialogBox, int id) {
              
              }
            })
        .setNegativeButton(
            getString(R.string.cancel),
            new DialogInterface.OnClickListener() {
              public void onClick(DialogInterface dialogBox, int id) {
                dialogBox.cancel();
              }
            });
    
    final AlertDialog alertDialog = alertDialogBuilderUserInput.create();
    alertDialog.show();
    alertDialog
        .getButton(AlertDialog.BUTTON_POSITIVE)
        .setOnClickListener(
            new OnClickListener() {
              @Override
              public void onClick(View v) {
                if (edtCustomerName.getText().toString().length() > 0) {
                  if (edtCustomerNumber.getText().toString().length() > 0) {
                    if (edtCustomerGst.getText().toString().length() > 0) {
                      CustomerModel item = new CustomerModel();
                      int id = (shouldUpdate) ? customerModel.getId()
                          : RealmManager.getCustomerDao().getNextCustomerItemId();
                      item.setId(id);
                      item.setCustomerID(id);
                      item.setCustomerName(edtCustomerName.getText().toString());
                      item.setCustomerNumber(edtCustomerNumber.getText().toString());
                      item.setCompanyMail(edtCustomerMail.getText().toString());
                      item.setCustomerGstNumber(edtCustomerGst.getText().toString());
                      item.setContactPerson(edtContactPerson.getText().toString());
                      item.setContactPersonNumber(edtContactPersonNumber.getText().toString());
                      item.setAddress(edtAddress.getText().toString());
                      item.setDescription(edtDescription.getText().toString());
                      item.setDateCreated(System.currentTimeMillis());
                      RealmManager.getCustomerDao()
                          .saveCustomerItem(item, getCallbackRealmObject());
                    } else {
                      Utils.showToast(getString(R.string.enter_customer_gst), getActivity());
                    }
                  } else {
                    Utils.showToast(getString(R.string.enter_customer_number), getActivity());
                  }
                } else {
                  Utils.showToast(getString(R.string.enter_customer_name), getActivity());
                }
              }
            });
    
    return alertDialog;
  }
  
  
  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (requestCode == PICK_IMAGE) {
      Uri selectedImage = data.getData();
      String[] filePathColumn = {MediaStore.Images.Media.DATA};
      Cursor cursor = getActivity().getContentResolver()
          .query(selectedImage, filePathColumn, null, null, null);
      cursor.moveToFirst();
      int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
      imagePath = cursor.getString(columnIndex);
      
      Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
      if (null != imgCustomerLogo && null != bitmap) {
        imgCustomerLogo.setImageBitmap(bitmap);
      }
    }
    super.onActivityResult(requestCode, resultCode, data);
  }
  
  public void askingForRequest() {
    if (ActivityCompat
        .checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)
        != PackageManager.PERMISSION_GRANTED) {
      ActivityCompat.requestPermissions(getActivity(),
          new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
              Manifest.permission.WRITE_EXTERNAL_STORAGE}, PICK_IMAGE);
    }
  }
  
  
  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[],
      @NonNull int[] grantResults) {
    switch (requestCode) {
      case PICK_IMAGE:
        // If request is cancelled, the result arrays are empty.
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
          //openGallery();
        } else {
          Utils.showToast("Please provide permission to pick images", getActivity());
          //do something like displaying a message that he didn`t allow the app to access gallery and you wont be able to let him select from gallery
        }
        break;
    }
  }
  
  private void openGallery() {
    Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
    getIntent.setType("image/*");
    Intent pickIntent = new Intent(Intent.ACTION_PICK,
        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
    pickIntent.setType("image/*");
    Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
    chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{pickIntent});
    startActivityForResult(chooserIntent, PICK_IMAGE);
  }
  
  public boolean isShouldUpdate() {
    return shouldUpdate;
  }
  
  public CustomerFragment setShouldUpdate(boolean shouldUpdate) {
    this.shouldUpdate = shouldUpdate;
    return this;
  }
  
  public CustomerModel getCustomerModel() {
    return customerModel;
  }
  
  public CustomerFragment setCustomerModel(
      CustomerModel customerModel) {
    this.customerModel = customerModel;
    return this;
  }
  
  public static float round(float d, int decimalPlace) {
    BigDecimal bd = new BigDecimal(Float.toString(d));
    bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
    return bd.floatValue();
  }
}

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
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import com.aandssoftware.aandsinventory.R;
import com.aandssoftware.aandsinventory.common.Utils;
import com.aandssoftware.aandsinventory.database.RealmManager;
import com.aandssoftware.aandsinventory.models.CallbackRealmObject;
import com.aandssoftware.aandsinventory.models.InventoryItem;
import com.aandssoftware.aandsinventory.models.InventoryItemHistory;
import com.aandssoftware.aandsinventory.ui.ListingActivity;
import java.math.BigDecimal;
import java.util.HashMap;

public class AddInventoryFragment extends DialogFragment {
  
  private static final int PICK_IMAGE = 100;
  private boolean shouldUpdate;
  private InventoryItem inventoryItem;
  private String imagePath;
  private ImageView imgInventoryItem;
  private CallbackRealmObject callbackRealmObject;
  
  public CallbackRealmObject getCallbackRealmObject() {
    return callbackRealmObject;
  }
  
  public AddInventoryFragment setCallbackRealmObject(
      CallbackRealmObject callbackRealmObject) {
    this.callbackRealmObject = callbackRealmObject;
    return this;
  }
  
  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    LayoutInflater layoutInflaterAndroid = LayoutInflater.from(getActivity());
    View view = layoutInflaterAndroid.inflate(R.layout.add_inventory_item, null);
    AlertDialog.Builder alertDialogBuilderUserInput =
        new AlertDialog.Builder(getActivity());
    alertDialogBuilderUserInput.setView(view);
    
    imgInventoryItem = (ImageView) view.findViewById(R.id.imgInventoryItemDelete);
    final EditText edtItemName = (EditText) view.findViewById(R.id.edtItemName);
    final EditText edtItemPurchasePrice = (EditText) view.findViewById(R.id.edtItemPurchasePrice);
    final EditText edtDescription = (EditText) view.findViewById(R.id.edtDescription);
    final EditText edtItemBrandName = (EditText) view.findViewById(R.id.edtItemBrandName);
    final EditText edtItemColor = (EditText) view.findViewById(R.id.edtItemColor);
    final EditText edtItemSize = (EditText) view.findViewById(R.id.edtItemSize);
    final EditText edtItemModelName = (EditText) view.findViewById(R.id.edtItemModelName);
    final EditText edtItemQuantity = (EditText) view.findViewById(R.id.edtItemQuantity);
    final EditText edtItemUnit = (EditText) view.findViewById(R.id.edtItemUnit);
    final EditText edtItemUnitPrice = (EditText) view.findViewById(R.id.edtItemUnitPrice);
    final EditText edtSupposedSellingPrice = (EditText) view
        .findViewById(R.id.edtSupposedSellingPrice);
    final EditText edtShopName = (EditText) view.findViewById(R.id.edtShopName);
    final EditText edtShopMobileContact = (EditText) view.findViewById(R.id.edtShopMobileContact);
    final EditText edtShopAddress = (EditText) view.findViewById(R.id.edtShopAddress);
    edtItemPurchasePrice.addTextChangedListener(new TextWatcher() {
      @Override
      public void beforeTextChanged(CharSequence s, int start, int count, int after) {
      }
      
      @Override
      public void onTextChanged(CharSequence s, int start, int before, int count) {
        try {
          String strQuantity = edtItemQuantity.getText().toString();
          float quantity = (null != strQuantity && !strQuantity.isEmpty()) ? Float
              .parseFloat(edtItemQuantity.getText().toString()) : 1;
          float purchasePrice = Float.parseFloat(edtItemPurchasePrice.getText().toString());
          float unitPrice = (purchasePrice / quantity);
          unitPrice = round(unitPrice, 2);
          if (unitPrice > -1) {
            edtItemUnitPrice.setText("" + unitPrice);
          } else {
            edtItemUnitPrice.setText("");
          }
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
      
      @Override
      public void afterTextChanged(Editable s) {
      }
    });
    
    edtItemQuantity.addTextChangedListener(new TextWatcher() {
      @Override
      public void beforeTextChanged(CharSequence s, int start, int count, int after) {
      }
      
      @Override
      public void onTextChanged(CharSequence s, int start, int before, int count) {
        try {
          String strQuantity = edtItemQuantity.getText().toString();
          float quantity = (null != strQuantity && !strQuantity.isEmpty()) ? Float
              .parseFloat(edtItemQuantity.getText().toString()) : 1;
          float purchasePrice = Float.parseFloat(edtItemPurchasePrice.getText().toString());
          float unitPrice = (purchasePrice / quantity);
          unitPrice = round(unitPrice, 2);
          if (unitPrice > 0) {
            edtItemUnitPrice.setText("" + unitPrice);
          } else {
            edtItemUnitPrice.setText("");
          }
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
      
      @Override
      public void afterTextChanged(Editable s) {
      }
    });
    
    //dialog_title.setText("");
    if (shouldUpdate && inventoryItem != null) {
      edtItemName.setText("" + Utils.isEmpty(inventoryItem.getInventoryItemName()));
      edtItemPurchasePrice.setText("" + Utils.isEmpty(inventoryItem.getItemPurchasePrice()));
      edtItemUnitPrice.setText("" + Utils.isEmpty(inventoryItem.getItemUnitPrice()));
      edtItemQuantity.setText("" + Utils.isEmpty(inventoryItem.getItemQuantity()));
      edtItemUnit.setText("" + Utils.isEmpty(inventoryItem.getItemQuantityUnit()));
      edtDescription.setText("" + Utils.isEmpty(inventoryItem.getDescription()));
      edtItemBrandName.setText("" + Utils.isEmpty(inventoryItem.getInventoryItemBrandName()));
      edtItemModelName.setText("" + Utils.isEmpty(inventoryItem.getInventoryItemModelName()));
      edtSupposedSellingPrice.setText("" + Utils.isEmpty(inventoryItem.getMinimumSellingPrice()));
      edtItemColor.setText("" + Utils.isEmpty(inventoryItem.getInventoryItemColor()));
      edtItemSize.setText("" + Utils.isEmpty(inventoryItem.getInventoryItemSize()));
      edtShopName.setText("" + Utils.isEmpty(inventoryItem.getPurchaseItemShopName()));
      edtShopMobileContact.setText("" + Utils.isEmpty(inventoryItem.getPurchaseItemShopContact()));
      edtShopAddress.setText("" + Utils.isEmpty(inventoryItem.getPurchaseItemShopAddress()));
      if (inventoryItem.getInventoryItemImagePath() != null) {
        Bitmap bitmap = BitmapFactory
            .decodeFile(Utils.isEmpty(inventoryItem.getInventoryItemImagePath()));
        if (null != imgInventoryItem && null != bitmap) {
          imgInventoryItem.setImageBitmap(bitmap);
        }
      }
    }
    imgInventoryItem.setOnClickListener(new OnClickListener() {
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
            shouldUpdate ? "update" : "save",
            new DialogInterface.OnClickListener() {
              public void onClick(DialogInterface dialogBox, int id) {
              
              }
            })
        .setNegativeButton(
            "cancel",
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
            new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                // Show toast message when no text is entered
                
                if (edtItemName.getText().toString().length() > 0) {
                  if (edtItemPurchasePrice.getText().toString().length() > 0) {
                    InventoryItem item = new InventoryItem();
                    int id = (shouldUpdate) ? inventoryItem.getId()
                        : RealmManager.getInventoryDao().getNextInventoryItemId();
                    item.setId(id);
                    if (!shouldUpdate) {
                      item.setInventoryType(getInventoryType());
                    }
                    item.setInventoryItemName(edtItemName.getText().toString());
                    item.setItemPurchasePrice(edtItemPurchasePrice.getText().toString());
                    item.setItemUnitPrice(edtItemUnitPrice.getText().toString());
                    item.setItemQuantity(edtItemQuantity.getText().toString());
                    item
                        .setItemQuantityUnit(Utils.isEmpty(edtItemUnit.getText().toString(),
                            InventoryItem.DEFAULT_QUANTITY_UNIT));
                    item.setDescription(edtDescription.getText().toString());
                    item
                        .setMinimumSellingPrice(edtSupposedSellingPrice.getText().toString());
                    item
                        .setInventoryItemBrandName(edtItemBrandName.getText().toString());
                    item
                        .setInventoryItemModelName(edtItemModelName.getText().toString());
                    item.setInventoryItemColor(edtItemColor.getText().toString());
                    item.setInventoryItemSize(edtItemSize.getText().toString());
                    item.setInventoryItemImagePath(imagePath);
                    item.setPurchaseItemShopName(edtShopName.getText().toString());
                    item
                        .setPurchaseItemShopContact(edtShopMobileContact.getText().toString());
                    item.setPurchaseItemShopAddress(edtShopAddress.getText().toString());
                    item.setInventoryItemPurchaseDate(System.currentTimeMillis());
                    item.setInventoryItemLastUpdatedDate(System.currentTimeMillis());
                    RealmManager.getInventoryDao()
                        .saveInventoryItem(item, getCallbackRealmObject());
                    if (shouldUpdate) {
                      HashMap<Integer, InventoryItemHistory> map = inventoryItem
                          .getChangedParamList(item);
                      RealmManager.getInventoryDao().saveInventoryItemHistory(item, map);
                    }
                  } else {
                    Utils.showToast("Please Enter Purchase Price", getActivity());
                  }
                } else {
                  Utils.showToast("Please Enter Item Name", getActivity());
                }
                
                alertDialog.dismiss();
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
      if (null != imgInventoryItem && null != bitmap) {
        imgInventoryItem.setImageBitmap(bitmap);
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
        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
    pickIntent.setType("image/*");
    Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
    chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{pickIntent});
    startActivityForResult(chooserIntent, PICK_IMAGE);
  }
  
  public boolean isShouldUpdate() {
    return shouldUpdate;
  }
  
  public AddInventoryFragment setShouldUpdate(boolean shouldUpdate) {
    this.shouldUpdate = shouldUpdate;
    return this;
  }
  
  public InventoryItem getInventoryItem() {
    return inventoryItem;
  }
  
  public AddInventoryFragment setInventoryItem(
      InventoryItem inventoryItem) {
    this.inventoryItem = inventoryItem;
    return this;
  }
  
  public static float round(float d, int decimalPlace) {
    BigDecimal bd = new BigDecimal(Float.toString(d));
    bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
    return bd.floatValue();
  }
  
  private int getInventoryType() {
    return getActivity().getIntent()
        .getIntExtra(ListingActivity.LISTING_TYPE, ListType.LIST_TYPE_MATERIAL.ordinal());
  }
}

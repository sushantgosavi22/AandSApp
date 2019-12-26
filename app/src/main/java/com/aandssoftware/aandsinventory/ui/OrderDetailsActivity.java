package com.aandssoftware.aandsinventory.ui;

import static com.aandssoftware.aandsinventory.ui.OrderListActivity.ORDER_ID;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.aandssoftware.aandsinventory.R;
import com.aandssoftware.aandsinventory.common.Utils;
import com.aandssoftware.aandsinventory.database.RealmManager;
import com.aandssoftware.aandsinventory.listing.OrderDetailsListAdapter;
import com.aandssoftware.aandsinventory.models.CustomerModel;
import com.aandssoftware.aandsinventory.models.OrderModel;

public class OrderDetailsActivity extends ListingActivity {
  
  @BindView(R.id.tvCustomerName)
  TextView tvCustomerName;
  @BindView(R.id.tvContactNameAndNumber)
  TextView tvContactNameAndNumber;
  @BindView(R.id.tvCustomerGstNumber)
  TextView tvCustomerGstNumber;
  @BindView(R.id.imgCustomerItemLogo)
  ImageView imgCustomerItemLogo;
  
  private OrderModel orderModel;
  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    init();
  }
  
  public void init() {
    ButterKnife.bind(this);
    RealmManager.open();
    getOrderFromIntent();
  }
  
  private void getOrderFromIntent() {
    Intent intent = getIntent();
    if (intent != null && intent.hasExtra(ORDER_ID)) {
      int orderID = intent.getIntExtra(ORDER_ID, -1);
      if (orderID != -1) {
        orderModel = RealmManager.getCustomerDao().getOrderFromID(orderID);
        if (orderModel != null) {
          setCustomerDetails(orderModel.getCustomerModel());
        }
      }
    }
  }
  
  public void setCustomerDetails(CustomerModel customerDetails) {
    if (null != customerDetails) {
      tvCustomerName.setText(Utils.isEmpty(customerDetails.getCustomerName(), "-"));
      tvContactNameAndNumber.setText(
          customerDetails.getContactPerson() + " " + customerDetails
              .getContactPersonNumber());
      tvCustomerGstNumber.setText(customerDetails.getCustomerGstNumber());
      if (customerDetails.getImagePath() != null) {
        Bitmap bitmap = BitmapFactory.decodeFile(customerDetails.getImagePath());
        if (null != imgCustomerItemLogo && null != bitmap) {
          imgCustomerItemLogo.setImageBitmap(bitmap);
        }
      }
    }
  }
  
  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == ListingActivity.LISTING_CODE) {
      reloadAdapter(((OrderDetailsListAdapter) operations).getResult());
    }
  }
  
  @Override
  protected void onDestroy() {
    RealmManager.close();
    super.onDestroy();
  }
}

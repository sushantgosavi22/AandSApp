package com.aandssoftware.aandsinventory.ui;

import android.content.Intent;
import android.os.Bundle;
import com.aandssoftware.aandsinventory.database.RealmManager;
import com.aandssoftware.aandsinventory.listing.CustomerListAdapter;
import com.aandssoftware.aandsinventory.listing.ListType;
import com.aandssoftware.aandsinventory.models.CallbackRealmObject;

public class OrderListActivity extends ListingActivity {
  
  public static final String ORDER_ID = "orderId";
  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }
  
  public void showCustomerListingActivity() {
    Intent intent = new Intent(OrderListActivity.this, ListingActivity.class);
    intent.putExtra(ListingActivity.LISTING_TYPE, ListType.LIST_TYPE_CUSTOMERS.ordinal());
    startActivityForResult(intent, ListingActivity.LISTING_CODE);
  }
  
  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == ListingActivity.LISTING_CODE && resultCode == ListingActivity.SELECTED) {
      int customerId = data.getIntExtra(CustomerListAdapter.CUSTOMER_ID, -1);
      if (customerId != -1) {
        showInventoryListingActivity(customerId, -1);
      }
    }
    
  }
  
  public void showInventoryListingActivity(int customerId, int orderId) {
    int finalOrderId = (orderId == -1) ? RealmManager.getCustomerDao().getNextOrderId() : orderId;
    RealmManager.getCustomerDao().saveOrder(orderId, customerId, new CallbackRealmObject() {
      @Override
      public void getCallBack(boolean result) {
        Intent intent = new Intent(OrderListActivity.this, ListingActivity.class);
        intent.putExtra(ListingActivity.LISTING_TYPE, ListType.LIST_TYPE_INVENTORY.ordinal());
        intent.putExtra(CustomerListAdapter.CUSTOMER_ID, customerId);
        intent.putExtra(ORDER_ID, finalOrderId);
        startActivityForResult(intent, ListingActivity.LISTING_CODE);
      }
    });
  }
}

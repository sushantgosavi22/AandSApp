package com.aandssoftware.aandsinventory.listing;

import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import com.aandssoftware.aandsinventory.R;
import com.aandssoftware.aandsinventory.common.Utils;
import com.aandssoftware.aandsinventory.database.RealmManager;
import com.aandssoftware.aandsinventory.models.CallbackRealmObject;
import com.aandssoftware.aandsinventory.models.InventoryItem;
import com.aandssoftware.aandsinventory.models.OrderModel;
import com.aandssoftware.aandsinventory.ui.ListingActivity;
import com.aandssoftware.aandsinventory.ui.OrderListActivity;
import com.aandssoftware.aandsinventory.ui.adapters.BaseRealmAdapter.BaseViewHolder;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.RealmObject;
import io.realm.RealmResults;

import static com.aandssoftware.aandsinventory.ui.OrderListActivity.ORDER_ID;

public class OrderDetailsListAdapter implements ListingOperations {
  
  public static final String INVENTORY_ID = "inventory_id";
  private ListingActivity activity;
  
  public class InventoryViewHolder extends BaseViewHolder {
    
    @BindView(R.id.imgInventoryItemLogo)
    ImageView imgInventoryItemLogo;
    @BindView(R.id.inventoryItemName)
    TextView inventoryItemName;
    @BindView(R.id.inventoryItemQuantity)
    TextView inventoryItemQuantity;
    @BindView(R.id.inventoryItemDetails)
    TextView inventoryItemDetails;
    @BindView(R.id.card_view)
    CardView cardView;
    
    @OnClick(R.id.imgInventoryItemHistory)
    public void OnInventoryItemHistory() {
      showInventoryHistory(((InventoryItem) itemView.getTag()).getId());
    }
    
    @OnClick(R.id.imgInventoryItemDelete)
    public void onDeleteClick() {
      deleteInventory((InventoryItem) itemView.getTag(), itemView.getContext());
    }
    
    @OnClick(R.id.imgInventoryItemEdit)
    public void onEditClick() {
      showAddInventoryItemFragment((InventoryItem) itemView.getTag(), true);
    }
    
    public InventoryViewHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
    }
  }
  
  public OrderDetailsListAdapter(ListingActivity activity) {
    this.activity = activity;
  }
  
  @Override
  public int getActivityLayoutId() {
    return R.layout.activity_order;
  }
  
  
  @Override
  public BaseViewHolder getBaseViewHolder(ViewGroup viewGroup, int i) {
    return new InventoryViewHolder(LayoutInflater.from(viewGroup.getContext())
        .inflate(R.layout.inventory_item, viewGroup, false));
  }
  
  @Override
  public void onBindSearchViewHolder(BaseViewHolder baseHolder, int position, RealmObject item) {
    InventoryViewHolder holder = (InventoryViewHolder) baseHolder;
    InventoryItem mItem = (InventoryItem) item;
    holder.inventoryItemName.setText(mItem.getInventoryItemName());
    holder.inventoryItemQuantity.setText(
        mItem.getItemQuantity() + " " + mItem
            .getItemQuantityUnit());
    holder.inventoryItemDetails.setText(
        mItem.getInventoryItemBrandName() + " " + mItem
            .getInventoryItemModelName() + " " + mItem.getInventoryItemColor()
            + " " + mItem.getInventoryItemSize());
    if (mItem.getInventoryItemImagePath() != null) {
      Bitmap bitmap = BitmapFactory.decodeFile(mItem.getInventoryItemImagePath());
      if (null != holder.imgInventoryItemLogo && null != bitmap) {
        holder.imgInventoryItemLogo.setImageBitmap(bitmap);
      }
    }
    holder.cardView.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        //performClick(holder.itemView, false);
      }
    });
  }
  
  private void performClick(View itemView, boolean shouldUpdate) {
    OrderModel model = RealmManager.getCustomerDao().getOrderFromID(getOrderId());
    if (model != null && !model.getOrderItems().isEmpty()) {
    
    }
  }
  
  private void showInventoryHistory(int id) {
    Intent intent = new Intent(activity, ListingActivity.class);
    intent.putExtra(ListingActivity.LISTING_TYPE, ListType.LIST_TYPE_INVENTORY_HISTORY.ordinal());
    intent.putExtra(OrderDetailsListAdapter.INVENTORY_ID, id);
    activity.startActivityForResult(intent, ListingActivity.LISTING_CODE);
  }
  
  @Override
  public String getTitle() {
    return activity.getString(R.string.order_details);
  }
  
  private boolean isOrderSelectionCall() {
    ComponentName callingActivity = activity.getCallingActivity();
    return (callingActivity != null && callingActivity.getClassName()
        .equalsIgnoreCase(OrderListActivity.class.getName()));
  }
  
  @Override
  public RealmResults<? extends RealmObject> getResult() {
    OrderModel model = RealmManager.getCustomerDao().getOrderFromID(getOrderId());
    return model.getOrderItems().where().findAll();
  }
  
  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    activity.getMenuInflater().inflate(R.menu.inventory_menu, menu);
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
      menu.findItem(R.id.actionAdd).setVisible(true);
    }
    return true;
  }
  
  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case android.R.id.home:
        activity.finish();
        return true;
      case R.id.actionAdd:
        showInventoryListingActivity(getCustomerId(), getOrderId());
        return true;
      default:
    }
    return true;
  }
  
  @Override
  public void onBackPressed() {
    activity.finish();
  }
  
  public void deleteInventory(InventoryItem inventoryItem, Context context) {
    AlertDialog.Builder alertDialogBuilderUserInput =
        new AlertDialog.Builder(context);
    alertDialogBuilderUserInput
        .setTitle(context.getString(R.string.remove_inventory_item_title))
        .setMessage(context.getString(R.string.remove_inventory_item_message))
        .setCancelable(false)
        .setPositiveButton(context.getString(R.string.yes),
            new DialogInterface.OnClickListener() {
              public void onClick(DialogInterface dialogBox, int id) {
                RealmManager.getInventoryDao().removeInventoryItem(inventoryItem);
                activity.reloadAdapter(getResult());
              }
            })
        .setNegativeButton(
            context.getString(R.string.no),
            new DialogInterface.OnClickListener() {
              public void onClick(DialogInterface dialogBox, int id) {
                dialogBox.cancel();
              }
            });
    
    final AlertDialog alertDialog = alertDialogBuilderUserInput.create();
    alertDialog.show();
  }
  
  
  private int getOrderId() {
    return activity.getIntent()
        .getIntExtra(OrderListActivity.ORDER_ID, -1);
  }
  
  private int getCustomerId() {
    Intent intent = activity.getIntent();
    if (intent != null && intent.hasExtra(ORDER_ID)) {
      int orderID = intent.getIntExtra(ORDER_ID, -1);
      if (orderID != -1) {
        OrderModel orderModel = RealmManager.getCustomerDao().getOrderFromID(orderID);
        if (orderModel != null) {
          return orderModel.getCustomerModel().getId();
        }
      }
    }
    return -1;
  }
  
  
  public void showInventoryListingActivity(int customerId, int orderId) {
    Intent intent = new Intent(activity, ListingActivity.class);
    intent.putExtra(ListingActivity.LISTING_TYPE, ListType.LIST_TYPE_INVENTORY.ordinal());
    intent.putExtra(CustomerListAdapter.CUSTOMER_ID, customerId);
    intent.putExtra(ORDER_ID, orderId);
    activity.startActivityForResult(intent, ListingActivity.LISTING_CODE);
  }
  
  
  public void showAddInventoryItemFragment(InventoryItem mItem, boolean update) {
    AddInventoryFragment fragment = new AddInventoryFragment();
    fragment.setCallbackRealmObject(new CallbackRealmObject() {
      @Override
      public void getCallBack(boolean result) {
        if (result) {
          Utils.showToast(activity.getString(R.string.inventory_save_message), activity);
          activity.reloadAdapter(getResult());
        }
      }
    });
    fragment.setInventoryItem(mItem);
    fragment.setShouldUpdate(update);
    fragment.show(activity.getFragmentManager(), getTitle());
  }
  
}

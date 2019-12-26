package com.aandssoftware.aandsinventory.listing;

import android.app.SearchManager;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.cardview.widget.CardView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.aandssoftware.aandsinventory.R;
import com.aandssoftware.aandsinventory.common.Utils;
import com.aandssoftware.aandsinventory.database.RealmManager;
import com.aandssoftware.aandsinventory.models.CallbackRealmObject;
import com.aandssoftware.aandsinventory.models.InventoryItem;
import com.aandssoftware.aandsinventory.ui.ListingActivity;
import com.aandssoftware.aandsinventory.ui.OrderDetailsActivity;
import com.aandssoftware.aandsinventory.ui.OrderListActivity;
import com.aandssoftware.aandsinventory.ui.adapters.BaseRealmAdapter.BaseViewHolder;
import io.realm.RealmObject;
import io.realm.RealmResults;

public class InventoryListAdapter implements ListingOperations {
  
  public static final String INVENTORY_ID = "inventory_id";
  private ListingActivity activity;
  
  public class InventoryViewHolder extends BaseViewHolder {
    
    @BindView(R.id.imgInventoryItemHistory)
    ImageView imgInventoryItemHistory;
    @BindView(R.id.imgInventoryItemEdit)
    ImageView imgInventoryItemEdit;
    @BindView(R.id.imgInventoryItemDelete)
    ImageView imgInventoryItemDelete;
    @BindView(R.id.imgInventoryItemLogo)
    ImageView imgInventoryItemLogo;
    @BindView(R.id.inventoryItemName)
    AppCompatTextView inventoryItemName;
    @BindView(R.id.inventoryItemQuantity)
    AppCompatTextView inventoryItemQuantity;
    @BindView(R.id.inventoryItemDetails)
    AppCompatTextView inventoryItemDetails;
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
      performClick((InventoryItem) itemView.getTag(), itemView, true);
    }
    
    public InventoryViewHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
    }
  }
  
  public InventoryListAdapter(ListingActivity activity) {
    this.activity = activity;
  }
  
  @Override
  public int getActivityLayoutId() {
    return R.layout.activity_listing;
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
        performClick(mItem, holder.itemView, false);
      }
    });
  }
  
  private void performClick(InventoryItem mItem, View itemView, boolean shouldUpdate) {
    if (isOrderSelectionCall()) {
      ShowAddOrderQuantity(mItem, itemView.getContext());
    } else {
      showAddInventoryItemFragment(mItem, shouldUpdate);
    }
  }
  
  private void showInventoryHistory(int id) {
    Intent intent = new Intent(activity, ListingActivity.class);
    intent.putExtra(ListingActivity.LISTING_TYPE, ListType.LIST_TYPE_INVENTORY_HISTORY.ordinal());
    intent.putExtra(InventoryListAdapter.INVENTORY_ID, id);
    activity.startActivityForResult(intent, ListingActivity.LISTING_CODE);
  }
  
  @Override
  public String getTitle() {
    int inventoryType = getInventoryType();
    if (inventoryType == ListType.LIST_TYPE_INVENTORY.ordinal()) {
      return activity.getString(R.string.inventory_item);
    } else {
      return activity.getString(R.string.material);
    }
  }
  
  private boolean isOrderSelectionCall() {
    ComponentName callingActivity = activity.getCallingActivity();
    return (callingActivity != null && callingActivity.getClassName()
        .equalsIgnoreCase(OrderListActivity.class.getName()) || callingActivity.getClassName()
        .equalsIgnoreCase(OrderDetailsActivity.class.getName()));
  }
  
  @Override
  public RealmResults<? extends RealmObject> getResult() {
    int inventoryType = getInventoryType();
    if (inventoryType == ListType.LIST_TYPE_INVENTORY.ordinal()) {
      return RealmManager.getInventoryDao().getInventoryItemRecords();
    } else {
      return RealmManager.getInventoryDao().getMaterialRecords();
    }
  }
  
  public void reloadAdapter(String query) {
    activity.reloadAdapter(
        RealmManager.getInventoryDao().getInventoryItemRecords(getInventoryType(), query));
  }
  
  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    activity.getMenuInflater().inflate(R.menu.inventory_menu, menu);
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
      SearchManager manager = (SearchManager) activity.getSystemService(Context.SEARCH_SERVICE);
      menu.findItem(R.id.search).setVisible(true);
      SearchView search = (SearchView) menu.findItem(R.id.search).getActionView();
      search.setVisibility(View.VISIBLE);
      search.setSearchableInfo(manager.getSearchableInfo(activity.getComponentName()));
      search.setOnQueryTextListener(new OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String s) {
          return true;
        }
        
        @Override
        public boolean onQueryTextChange(String query) {
          reloadAdapter(query);
          return true;
        }
      });
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
        showAddInventoryItemFragment(null, false);
        return true;
      default:
    }
    return true;
  }
  
  @Override
  public void onBackPressed() {
    activity.finish();
  }

  public void showAddInventoryItemFragment(InventoryItem mItem, boolean update) {
    AddInventoryFragment fragment = new AddInventoryFragment();
    fragment.setCallbackRealmObject(result -> {
      if (result) {
        Utils.showToast(activity.getString(R.string.inventory_save_message), activity);
        activity.reloadAdapter(getResult());
      }
    });
    fragment.setInventoryItem(mItem);
    fragment.setShouldUpdate(update);
    fragment.show(activity.getFragmentManager(), getTitle());
  }
  
  public void ShowAddOrderQuantity(InventoryItem inventoryItem, Context context) {
    AlertDialog.Builder alertDialogBuilderUserInput =
        new AlertDialog.Builder(context);
    View view = LayoutInflater.from(context).inflate(R.layout.add_order_inventory_item, null);
    alertDialogBuilderUserInput.setView(view);
    setData(view, inventoryItem);
    alertDialogBuilderUserInput
        .setTitle(context.getString(R.string.add_order_inventory_title))
        .setCancelable(false)
        .setPositiveButton(context.getString(R.string.add),
            new DialogInterface.OnClickListener() {
              public void onClick(DialogInterface dialogBox, int id) {
                EditText edtOrderQuantity = ((EditText) view.findViewById(R.id.edtOrderQuantity));
                String newQuantity = edtOrderQuantity.getText().toString();
                int intUpdatedQuantity = Integer.parseInt(inventoryItem.getItemQuantity()) - Integer
                    .parseInt(newQuantity);
                
                InventoryItem mainInventoryCopy = new InventoryItem(inventoryItem);
                mainInventoryCopy.setItemQuantity(String.valueOf(intUpdatedQuantity));
                RealmManager.getInventoryDao().saveInventoryItem(mainInventoryCopy,
                    new CallbackRealmObject() {
                      @Override
                      public void getCallBack(boolean result) {
                      
                      }
                    });
                RealmManager.getCustomerDao()
                    .addInventoryToOrder(mainInventoryCopy, getOrderId(), mainInventoryCopy.getId(),
                        newQuantity,
                        new CallbackRealmObject() {
                          @Override
                          public void getCallBack(boolean result) {
                            Utils.showToast(activity.getString(R.string.inventory_add_order),
                                activity);
                          }
                        });
                dialogBox.cancel();
              }
            })
        .setNegativeButton(
            context.getString(R.string.cancel),
            new DialogInterface.OnClickListener() {
              public void onClick(DialogInterface dialogBox, int id) {
                dialogBox.cancel();
              }
            });
    
    final AlertDialog alertDialog = alertDialogBuilderUserInput.create();
    alertDialog.show();
  }
  
  
  private void setData(View view, InventoryItem inventoryItem) {
    ((EditText) view.findViewById(R.id.edtItemName)).setText(inventoryItem.getInventoryItemName());
    ((EditText) view.findViewById(R.id.edtItemQuantity)).setText(inventoryItem.getItemQuantity());
    ((EditText) view.findViewById(R.id.edtItemUnit)).setText(inventoryItem.getItemQuantityUnit());
    ((EditText) view.findViewById(R.id.edtItemPurchasePrice))
        .setText(inventoryItem.getItemPurchasePrice());
    ((EditText) view.findViewById(R.id.edtItemUnitPrice)).setText(inventoryItem.getItemUnitPrice());
    ((EditText) view.findViewById(R.id.edtSupposedSellingPrice))
        .setText(inventoryItem.getMinimumSellingPrice());
    ((EditText) view.findViewById(R.id.edtItemBrandName))
        .setText(inventoryItem.getInventoryItemBrandName());
    ((EditText) view.findViewById(R.id.edtItemModelName))
        .setText(inventoryItem.getInventoryItemModelName());
    ((EditText) view.findViewById(R.id.edtItemColor))
        .setText(inventoryItem.getInventoryItemColor());
    ((EditText) view.findViewById(R.id.edtItemSize)).setText(inventoryItem.getInventoryItemSize());
    EditText edtOrderQuantity = ((EditText) view.findViewById(R.id.edtOrderQuantity));
    edtOrderQuantity.setText(inventoryItem.getItemQuantity());
    ((AppCompatImageButton) view.findViewById(R.id.addItem)).setOnClickListener(
        new OnClickListener() {
          @Override
          public void onClick(View v) {
            String val = edtOrderQuantity.getText().toString();
            if (!val.isEmpty()) {
              int orderQuantity = Integer.parseInt(val);
              if (orderQuantity > 0) {
                orderQuantity = orderQuantity + 1;
                edtOrderQuantity.setText(String.valueOf(orderQuantity));
              }
            }
          }
        });
    
    ((AppCompatImageButton) view.findViewById(R.id.removeItem)).setOnClickListener(
        new OnClickListener() {
          @Override
          public void onClick(View v) {
            String val = edtOrderQuantity.getText().toString();
            if (!val.isEmpty()) {
              int orderQuantity = Integer.parseInt(val);
              if (orderQuantity > 0) {
                orderQuantity = orderQuantity - 1;
                edtOrderQuantity.setText(String.valueOf(orderQuantity));
              }
            }
          }
        });
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
  
  private int getInventoryType() {
    return activity.getIntent()
        .getIntExtra(ListingActivity.LISTING_TYPE, ListType.LIST_TYPE_MATERIAL.ordinal());
  }
  
  private int getOrderId() {
    return activity.getIntent()
        .getIntExtra(OrderListActivity.ORDER_ID, -1);
  }

}

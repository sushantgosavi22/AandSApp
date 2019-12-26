package com.aandssoftware.aandsinventory.listing;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.cardview.widget.CardView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.aandssoftware.aandsinventory.R;
import com.aandssoftware.aandsinventory.common.DateUtils;
import com.aandssoftware.aandsinventory.common.Utils;
import com.aandssoftware.aandsinventory.database.RealmManager;
import com.aandssoftware.aandsinventory.models.InventoryItem;
import com.aandssoftware.aandsinventory.models.OrderModel;
import com.aandssoftware.aandsinventory.ui.ListingActivity;
import com.aandssoftware.aandsinventory.ui.OrderDetailsActivity;
import com.aandssoftware.aandsinventory.ui.OrderListActivity;
import com.aandssoftware.aandsinventory.ui.adapters.BaseRealmAdapter.BaseViewHolder;
import io.realm.RealmObject;
import io.realm.RealmResults;

public class OrderListAdapter implements ListingOperations {
  
  private ListingActivity activity;
  
  public class OrderViewHolder extends BaseViewHolder {
    
    @BindView(R.id.tvOrderDate)
    AppCompatTextView tvOrderDate;
    @BindView(R.id.tvOrderStatus)
    AppCompatTextView tvOrderStatus;
    @BindView(R.id.tvFinalAmount)
    AppCompatTextView tvFinalAmount;
    @BindView(R.id.tvItemCount)
    AppCompatTextView tvItemCount;
    @BindView(R.id.tvInvoiceNumber)
    AppCompatTextView tvInvoiceNumber;
    @BindView(R.id.tvCustomerName)
    TextView tvCustomerName;
    @BindView(R.id.tvContactNameAndNumber)
    TextView tvContactNameAndNumber;
    @BindView(R.id.card_view)
    CardView cardView;
    
    @OnClick(R.id.imgDelete)
    public void onDeleteClick() {
      deleteOrder(((OrderModel) itemView.getTag()), activity);
    }
    
    @OnClick(R.id.card_view)
    public void onCardClick() {
      showOrderInventoryActivity(activity, ((OrderModel) itemView.getTag()).getId());
    }
    
    public OrderViewHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
    }
  }
  
  private void showOrderInventoryActivity(Activity activity, int orderId) {
    Intent intent = new Intent(activity, OrderDetailsActivity.class);
    intent.putExtra(OrderListActivity.ORDER_ID, orderId);
    intent.putExtra(ListingActivity.LISTING_TYPE, ListType.LIST_TYPE_ORDER_INVENTORY.ordinal());
    activity.startActivityForResult(intent, ListingActivity.LISTING_CODE);
  }
  
  public OrderListAdapter(ListingActivity activity) {
    this.activity = activity;
  }
  
  @Override
  public int getActivityLayoutId() {
    return R.layout.activity_listing;
  }
  
  
  @Override
  public BaseViewHolder getBaseViewHolder(ViewGroup viewGroup, int i) {
    return new OrderViewHolder(LayoutInflater.from(viewGroup.getContext())
        .inflate(R.layout.order_item, viewGroup, false));
  }
  
  @Override
  public void onBindSearchViewHolder(BaseViewHolder baseHolder, int position, RealmObject item) {
    OrderViewHolder holder = (OrderViewHolder) baseHolder;
    OrderModel mItem = (OrderModel) item;
    holder.tvCustomerName.setText(mItem.getCustomerModel().getCustomerName());
    holder.tvContactNameAndNumber.setText(
        mItem.getCustomerModel().getContactPerson() + " " + mItem.getCustomerModel()
            .getContactPersonNumber());
    holder.tvFinalAmount.setText(Utils.isEmptyInt(mItem.getFinalBillAmount(), "-"));
    holder.tvItemCount.setText(String.valueOf(mItem.getOrderItems().size()));
    holder.tvInvoiceNumber.setText(Utils.isEmpty(mItem.getInvoiceNumber(), "-"));
    holder.tvOrderDate.setText(DateUtils.getDateFormatted(mItem.getOrderDateCreated()));
    holder.tvOrderStatus.setText(Utils.isEmpty(mItem.getOrderStatusName()));
  }
  
  @Override
  public String getTitle() {
    return activity.getString(R.string.order);
  }
  
  @Override
  public RealmResults<? extends RealmObject> getResult() {
    return RealmManager.getCustomerDao().getOrders();
  }
  
  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    activity.getMenuInflater().inflate(R.menu.inventory_menu, menu);
    return true;
  }
  
  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case android.R.id.home:
        activity.finish();
        return true;
      case R.id.actionAdd:
        addOrderSelectCompany();
        return true;
      default:
    }
    return true;
  }
  
  @Override
  public void onBackPressed() {
    activity.finish();
  }
  
  public void addOrderSelectCompany() {
    ((OrderListActivity) activity).showCustomerListingActivity();
  }
  
  public void showInventoryListingActivity(int custId, int orderId) {
    ((OrderListActivity) activity).showInventoryListingActivity(custId, orderId);
  }
  
  
  public void deleteOrder(OrderModel orderModel, Context context) {
    AlertDialog.Builder alertDialogBuilderUserInput =
        new AlertDialog.Builder(context);
    alertDialogBuilderUserInput
        .setTitle(context.getString(R.string.remove_order_item_title))
        .setMessage(context.getString(R.string.remove_order_item_message))
        .setCancelable(false)
        .setPositiveButton(context.getString(R.string.yes),
            new DialogInterface.OnClickListener() {
              public void onClick(DialogInterface dialogBox, int id) {
                RealmManager.getCustomerDao().removeOrder(orderModel);
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
  
}

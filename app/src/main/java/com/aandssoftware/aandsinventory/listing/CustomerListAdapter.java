package com.aandssoftware.aandsinventory.listing;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.aandssoftware.aandsinventory.R;
import com.aandssoftware.aandsinventory.common.Utils;
import com.aandssoftware.aandsinventory.database.RealmManager;
import com.aandssoftware.aandsinventory.models.CallbackRealmObject;
import com.aandssoftware.aandsinventory.models.CustomerModel;
import com.aandssoftware.aandsinventory.ui.ListingActivity;
import com.aandssoftware.aandsinventory.ui.adapters.BaseRealmAdapter.BaseViewHolder;
import io.realm.RealmObject;
import io.realm.RealmResults;

public class CustomerListAdapter implements ListingOperations {
  
  private ListingActivity activity;
  
  public class CustomerViewHolder extends BaseViewHolder {
    
    @BindView(R.id.imgCustomerItemEdit)
    ImageView imgCustomerItemEdit;
    @BindView(R.id.imgCustomerItemDelete)
    ImageView imgCustomerItemDelete;
    @BindView(R.id.imgCustomerItemLogo)
    ImageView imgCustomerItemLogo;
    @BindView(R.id.tvCustomerName)
    TextView tvCustomerName;
    @BindView(R.id.tvContactNameAndNumber)
    TextView tvContactNameAndNumber;
    @BindView(R.id.tvCustomerGstNumber)
    TextView tvCustomerGstNumber;
    
    @OnClick(R.id.imgCustomerItemDelete)
    public void onDeleteClick() {
      deleteInventory((CustomerModel) itemView.getTag(), itemView.getContext());
    }
    
    @OnClick(R.id.imgCustomerItemEdit)
    public void onEditClick() {
      showAddCustomerFragment((CustomerModel) itemView.getTag(), true);
    }
    
    public CustomerViewHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
    }
  }
  
  public CustomerListAdapter(ListingActivity activity) {
    this.activity = activity;
  }
  
  @Override
  public int getActivityLayoutId() {
    return R.layout.activity_listing;
  }
  
  
  @Override
  public BaseViewHolder getBaseViewHolder(ViewGroup viewGroup, int i) {
    return new CustomerViewHolder(LayoutInflater.from(viewGroup.getContext())
        .inflate(R.layout.customer_item, viewGroup, false));
  }
  
  @Override
  public void onBindSearchViewHolder(BaseViewHolder baseHolder, int position, RealmObject item) {
    CustomerViewHolder holder = (CustomerViewHolder) baseHolder;
    CustomerModel mItem = (CustomerModel) item;
    holder.tvCustomerName.setText(mItem.getCustomerName());
    holder.tvContactNameAndNumber.setText(
        mItem.getContactPerson() + " " + mItem
            .getContactPersonNumber());
    holder.tvCustomerGstNumber.setText(mItem.getCustomerGstNumber());
    if (mItem.getImagePath() != null) {
      Bitmap bitmap = BitmapFactory.decodeFile(mItem.getImagePath());
      if (null != holder.imgCustomerItemLogo && null != bitmap) {
        holder.imgCustomerItemLogo.setImageBitmap(bitmap);
      }
    }
    holder.itemView.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        showAddCustomerFragment(mItem, false);
      }
    });
  }
  
  @Override
  public String getTitle() {
    return activity.getString(R.string.Customer_item);
  }
  
  @Override
  public RealmResults<? extends RealmObject> getResult() {
    return RealmManager.getCustomerDao().loadCompanyRecords();
  }
  
  @Override
  public int getMenuLayoutId() {
    return R.menu.inventory_menu;
  }
  
  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case android.R.id.home:
        activity.finish();
        return true;
      case R.id.actionAdd:
        showAddCustomerFragment(null, false);
        return true;
      default:
    }
    return true;
  }
  
  @Override
  public void onBackPressed() {
    activity.onBackPressed();
  }
  
  public void showAddCustomerFragment(CustomerModel mItem, boolean update) {
    CustomerFragment fragment = new CustomerFragment();
    fragment.setCallbackRealmObject(new CallbackRealmObject() {
      @Override
      public void getCallBack(boolean result) {
        if (result) {
          Utils.showToast(activity.getString(R.string.customer_save_message), activity);
          activity.reloadAdapter(getResult());
          fragment.dismiss();
        }
      }
    });
    fragment.setCustomerModel(mItem);
    fragment.setShouldUpdate(update);
    fragment.show(activity.getFragmentManager(), getTitle());
  }
  
  public void deleteInventory(CustomerModel customerModel, Context context) {
    AlertDialog.Builder alertDialogBuilderUserInput =
        new AlertDialog.Builder(context);
    alertDialogBuilderUserInput
        .setTitle(context.getString(R.string.remove_customer_item_title))
        .setMessage(context.getString(R.string.remove_customer_item_message))
        .setCancelable(false)
        .setPositiveButton(context.getString(R.string.yes),
            new DialogInterface.OnClickListener() {
              public void onClick(DialogInterface dialogBox, int id) {
                RealmManager.getCustomerDao().removeCompany(customerModel);
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

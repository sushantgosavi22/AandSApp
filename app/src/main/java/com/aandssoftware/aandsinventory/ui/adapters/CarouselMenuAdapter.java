package com.aandssoftware.aandsinventory.ui.adapters;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.aandssoftware.aandsinventory.R;
import com.aandssoftware.aandsinventory.listing.ListType;
import com.aandssoftware.aandsinventory.models.CarouselMenuModel;
import com.aandssoftware.aandsinventory.models.CarouselMenuType;
import com.aandssoftware.aandsinventory.ui.ListingActivity;
import com.aandssoftware.aandsinventory.ui.OrderListActivity;
import com.aandssoftware.aandsinventory.ui.adapters.CarouselMenuAdapter.ViewHolder;
import io.realm.OrderedRealmCollection;
import java.util.List;


public class CarouselMenuAdapter extends RecyclerView.Adapter<ViewHolder> {
  
  private static String HTTP = "http";
  OrderedRealmCollection<CarouselMenuModel> orderedRealmCollection;
  public CarouselMenuAdapter(OrderedRealmCollection<CarouselMenuModel> orderedRealmCollection) {
    this.orderedRealmCollection = orderedRealmCollection;
  }
  
  @Override
  public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View v = LayoutInflater.from(parent.getContext())
        .inflate(R.layout.item_carousel_menu, parent, false);
    return new ViewHolder(v);
  }
  
  @Override
  public int getItemCount() {
    return orderedRealmCollection.size();
  }
  
  private List<CarouselMenuModel> getData() {
    return orderedRealmCollection;
  }
  
  @Override
  public void onBindViewHolder(ViewHolder holder, int position) {
    CarouselMenuModel mCarouselMenuModel = getData().get(position);
    holder.carouselItemName.setText(getData().get(position).getAliceName());
    holder.itemView.setTag(mCarouselMenuModel);
  
    if (null != mCarouselMenuModel.getImageId()) {
      if (!mCarouselMenuModel.getImageId().contains(HTTP)) {
        int val = Integer.parseInt(mCarouselMenuModel.getImageId());
        holder.carouselItemImage
            .setBackgroundDrawable(ContextCompat.getDrawable(holder.itemView.getContext(), val));
      }
    }
    
    holder.itemView.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        String carouselId = getData().get(position).getCarouselId();
        switch (CarouselMenuType.valueOf(carouselId)) {
          case ORDERS:
            showOrderActivity((Activity) holder.itemView.getContext(), ListType.LIST_TYPE_ORDER);
            break;
          case CUSTOMERS:
            showListing((Activity) holder.itemView.getContext(), ListType.LIST_TYPE_CUSTOMERS);
            break;
          case INVENTORY:
            showListing((Activity) holder.itemView.getContext(), ListType.LIST_TYPE_INVENTORY);
            break;
          case INVENTORY_HISTORY:
            showListing((Activity) holder.itemView.getContext(),
                ListType.LIST_TYPE_INVENTORY_HISTORY);
            break;
          case MATERIALS:
            showListing((Activity) holder.itemView.getContext(), ListType.LIST_TYPE_MATERIAL);
            break;
          default:
            showListing((Activity) holder.itemView.getContext(), ListType.LIST_TYPE_MATERIAL);
            break;
          
        }
      }
    });
  }
  
  
  public class ViewHolder extends RecyclerView.ViewHolder {
    
    @BindView(R.id.carouselItemName)
    AppCompatTextView carouselItemName;
  
    @BindView(R.id.carouselItemImage)
    AppCompatImageView carouselItemImage;
    
    public ViewHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
    }
  }
  
  private void showListing(Activity activity, ListType type) {
    Intent intent = new Intent(activity, ListingActivity.class);
    intent.putExtra(ListingActivity.LISTING_TYPE, type.ordinal());
    activity.startActivityForResult(intent, ListingActivity.LISTING_CODE);
  }
  
  private void showOrderActivity(Activity activity, ListType type) {
    Intent intent = new Intent(activity, OrderListActivity.class);
    intent.putExtra(ListingActivity.LISTING_TYPE, type.ordinal());
    activity.startActivityForResult(intent, ListingActivity.LISTING_CODE);
  }
}

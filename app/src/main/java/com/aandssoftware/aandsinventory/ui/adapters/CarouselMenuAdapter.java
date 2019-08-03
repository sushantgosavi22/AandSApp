package com.aandssoftware.aandsinventory.ui.adapters;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.aandssoftware.aandsinventory.R;
import com.aandssoftware.aandsinventory.listing.ListType;
import com.aandssoftware.aandsinventory.models.CarouselMenuModel;
import com.aandssoftware.aandsinventory.models.CarouselMenuType;
import com.aandssoftware.aandsinventory.ui.ListingActivity;
import io.realm.OrderedRealmCollection;
import io.realm.RealmRecyclerViewAdapter;


public class CarouselMenuAdapter extends
    RealmRecyclerViewAdapter<CarouselMenuModel, CarouselMenuAdapter.ViewHolder> {
  
  public CarouselMenuAdapter(OrderedRealmCollection<CarouselMenuModel> orderedRealmCollection) {
    super(orderedRealmCollection, true);
  }
  
  @Override
  public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View v = LayoutInflater.from(parent.getContext())
        .inflate(R.layout.item_carousel_menu, parent, false);
    return new ViewHolder(v);
  }
  
  @Override
  public void onBindViewHolder(ViewHolder holder, int position) {
    holder.carouselItemName.setText(getData().get(position).getAliceName());
    holder.itemView.setTag(getData().get(position));
    holder.itemView.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        String carouselId = getData().get(position).getCarouselId();
        switch (CarouselMenuType.valueOf(carouselId)) {
          case ORDERS:
            showListing((Activity) holder.itemView.getContext(), ListType.LIST_TYPE_ORDER);
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
}

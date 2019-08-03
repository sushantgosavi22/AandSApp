package com.aandssoftware.aandsinventory.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.ButterKnife;
import com.aandssoftware.aandsinventory.listing.ListingOperations;
import io.realm.OrderedRealmCollection;
import io.realm.RealmObject;
import io.realm.RealmRecyclerViewAdapter;

public class BaseRealmAdapter<T extends RealmObject> extends
    RealmRecyclerViewAdapter<T, BaseRealmAdapter.BaseViewHolder> {
  
  private ListingOperations operation;
  
  public BaseRealmAdapter(OrderedRealmCollection<T> orderedRealmCollection,
      ListingOperations operation) {
    super(orderedRealmCollection, true);
    this.operation = operation;
  }
  
  @Override
  public BaseViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
    return operation.getBaseViewHolder(viewGroup, i);
  }
  
  @Override
  public void onBindViewHolder(BaseViewHolder holder, int position) {
    holder.itemView.setTag(getData().get(position));
    operation.onBindSearchViewHolder(holder, position, getData().get(position));
  }
  
  public static class BaseViewHolder extends RecyclerView.ViewHolder {
    
    public BaseViewHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
    }
  }
}

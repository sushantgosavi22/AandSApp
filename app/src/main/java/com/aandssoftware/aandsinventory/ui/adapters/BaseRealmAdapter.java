package com.aandssoftware.aandsinventory.ui.adapters;


import android.view.View;
import android.view.ViewGroup;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.ButterKnife;
import com.aandssoftware.aandsinventory.listing.ListingOperations;
import com.aandssoftware.aandsinventory.ui.adapters.BaseRealmAdapter.BaseViewHolder;
import io.realm.OrderedRealmCollection;
import io.realm.RealmObject;
import io.realm.RealmResults;
import java.util.List;

public class BaseRealmAdapter<T extends RealmObject> extends RecyclerView.Adapter<BaseViewHolder> {
  
  private ListingOperations operation;
  OrderedRealmCollection<T> orderedRealmCollection;
  public BaseRealmAdapter(OrderedRealmCollection<T> orderedRealmCollection,
      ListingOperations operation) {
    this.orderedRealmCollection = orderedRealmCollection;
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
  
  @Override
  public int getItemCount() {
    return orderedRealmCollection.size();
  }
  
  private List<T> getData() {
    return orderedRealmCollection;
  }
  
  public void updateData(RealmResults<? extends RealmObject> realmResults) {
    orderedRealmCollection.clear();
    orderedRealmCollection = (OrderedRealmCollection<T>) realmResults;
  }
  
  public static class BaseViewHolder extends RecyclerView.ViewHolder {
    
    public BaseViewHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
    }
  }
  
  
}

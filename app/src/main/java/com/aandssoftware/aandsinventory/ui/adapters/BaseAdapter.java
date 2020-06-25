package com.aandssoftware.aandsinventory.ui.adapters;


import android.view.View;
import android.view.ViewGroup;
import com.aandssoftware.aandsinventory.R;
import com.aandssoftware.aandsinventory.listing.ListingOperations;
import com.aandssoftware.aandsinventory.ui.adapters.BaseAdapter.BaseViewHolder;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

public class BaseAdapter<T extends Serializable> extends RecyclerView.Adapter<BaseViewHolder> {

    private ListingOperations operation;
    private ArrayList<T> orderedRealmCollection;

    public BaseAdapter(ArrayList<T> orderedRealmCollection,
                       ListingOperations operation) {
        this.orderedRealmCollection = new ArrayList(orderedRealmCollection);
        this.operation = operation;
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return operation.getBaseViewHolder(viewGroup, i);
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        holder.itemView.setTag(getData().get(position));
        holder.itemView.setTag(R.string.tag, position);
        operation.onBindSearchViewHolder(holder, position, (Serializable) getData().get(position));
    }

    @Override
    public int getItemCount() {
        return orderedRealmCollection.size();
    }

    private List<T> getData() {
        return orderedRealmCollection;
    }

    public void loadData(ArrayList<T> realmResults) {
        if (orderedRealmCollection.isEmpty()) {
            orderedRealmCollection = new ArrayList(realmResults);
        } else {
            orderedRealmCollection.addAll(realmResults);
        }
        notifyDataSetChanged();
    }

    public ArrayList<T> getList() {
        return orderedRealmCollection;
    }

    public void clearList() {
        if (null != orderedRealmCollection) {
            orderedRealmCollection.clear();
        }
    }


    public static class BaseViewHolder extends RecyclerView.ViewHolder {
        public BaseViewHolder(View itemView) {
            super(itemView);
        }
    }

    public void removeAt(int position) {
        orderedRealmCollection.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, orderedRealmCollection.size());
    }

}

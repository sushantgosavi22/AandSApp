package com.aandssoftware.aandsinventory.listing;

import android.view.MenuItem;
import android.view.ViewGroup;
import com.aandssoftware.aandsinventory.ui.adapters.BaseRealmAdapter.BaseViewHolder;
import io.realm.RealmObject;
import io.realm.RealmResults;

public interface ListingOperations {
  
  String getTitle();
  
  RealmResults<? extends RealmObject> getResult();
  
  int getMenuLayoutId();
  
  boolean onOptionsItemSelected(MenuItem menuItem);
  
  void onBackPressed();
  
  int getActivityLayoutId();
  
  BaseViewHolder getBaseViewHolder(ViewGroup viewGroup, int i);
  
  void onBindSearchViewHolder(BaseViewHolder holder, int position, RealmObject item);
}


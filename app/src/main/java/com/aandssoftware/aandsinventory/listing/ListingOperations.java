package com.aandssoftware.aandsinventory.listing;

import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import androidx.annotation.Nullable;
import com.aandssoftware.aandsinventory.ui.adapters.BaseAdapter.BaseViewHolder;
import java.io.Serializable;

public interface ListingOperations {
  
  String getTitle();
  
  void getResult();
  
  boolean onCreateOptionsMenu(Menu menu);
  
  boolean onOptionsItemSelected(MenuItem menuItem);
  
  void onBackPressed();
  
  int getActivityLayoutId();
  
  BaseViewHolder getBaseViewHolder(ViewGroup viewGroup, int i);
  
  void onBindSearchViewHolder(BaseViewHolder holder, int position, Serializable item);
  
  void onActivityResult(int requestCode, int resultCode, @Nullable Intent data);
}


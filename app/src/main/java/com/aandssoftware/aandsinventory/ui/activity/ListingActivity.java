package com.aandssoftware.aandsinventory.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.widget.NestedScrollView;
import androidx.core.widget.NestedScrollView.OnScrollChangeListener;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.aandssoftware.aandsinventory.R;
import com.aandssoftware.aandsinventory.listing.ListType;
import com.aandssoftware.aandsinventory.listing.ListingOperations;
import com.aandssoftware.aandsinventory.ui.adapters.BaseAdapter;
import com.aandssoftware.aandsinventory.utilities.AppConstants;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ListingActivity extends BaseActivity {
  
  private int currentPage = 0;
  private boolean isLoading = false;
  private BaseAdapter baseAdapter;
  public static final int SELECTED = 104;
  RecyclerView recyclerView;
  AppCompatTextView noResultFoundLayout;
  NestedScrollView nestedScrollView;
  protected ListingOperations operations;
  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    init();
  }
  
  private void init() {
    
    getListingOperations();
    setContentView(operations.getActivityLayoutId());
    setupActionBar(operations.getTitle());
    setView();
    setUpList();
  }

  private void setView() {
    recyclerView = findViewById(R.id.recyclerView);
    noResultFoundLayout = findViewById(R.id.noResultFoundLayout);
    nestedScrollView = findViewById(R.id.nestedScrollView);
  }

  private void getListingOperations() {
    if (null != getIntent() && getIntent().hasExtra(AppConstants.LISTING_TYPE)) {
      ListType result = ListType.values()[getIntent().getIntExtra(AppConstants.LISTING_TYPE, -1)];
      operations = result.getInstance(ListingActivity.this);
    }
  }
  
  @Override
  public void onBackPressed() {
    operations.onBackPressed();
  }
  
  @Override
  protected void onDestroy() {
    super.onDestroy();
  }
  
  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    return operations.onCreateOptionsMenu(menu);
  }
  
  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    operations.onOptionsItemSelected(item);
    return super.onOptionsItemSelected(item);
  }
  
  private BaseAdapter getAdapterInstance() {
    ArrayList<? extends Serializable> realmResults = new ArrayList<>();
    baseAdapter = new BaseAdapter(realmResults, operations);
    return baseAdapter;
  }
  
  private void setUpList() {
    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
    recyclerView.setLayoutManager(linearLayoutManager);
    recyclerView.setAdapter(getAdapterInstance());
    getBaseAdapter().notifyDataSetChanged();
    nestedScrollView.setOnScrollChangeListener((OnScrollChangeListener) (v, x, y, oldX, oldY) -> {
      View child = recyclerView.getChildAt(recyclerView.getChildCount() - 1);
      if (((child != null) && (y >= ((child.getY()) - v.getMeasuredHeight())))
          && (y > oldY && !isLoading())) {
        setLoading(true);
        loadMoreData();
      }
    });
    operations.getResult();
  }
  
  private void loadMoreData() {
    currentPage++;
    operations.getResult();
  }
  
  public boolean isLoading() {
    return isLoading;
  }
  
  public void setLoading(boolean loading) {
    this.isLoading = loading;
  }
  
  public int getCurrentPage() {
    return currentPage;
  }
  
  
  public BaseAdapter getBaseAdapter() {
    return baseAdapter;
  }
  
  public RecyclerView getRecyclerView() {
    return recyclerView;
  }
  
  public void loadData(ArrayList<? extends Serializable> mList) {
    getBaseAdapter().clearList();
    getBaseAdapter().loadData(mList);
    validateRecyclerView();
  }
  
  public void reloadNewData(ArrayList<? extends Serializable> mList) {
    getBaseAdapter().loadData(mList);
    validateRecyclerView();
  }
  
  public void validateRecyclerView() {
    List list = getResults();
    if (list != null && list.isEmpty()) {
      noResultFoundLayout.setVisibility(View.VISIBLE);
      noResultFoundLayout.setText(getString(R.string.no_result_found));
      recyclerView.setVisibility(View.GONE);
    } else {
      noResultFoundLayout.setVisibility(View.GONE);
      noResultFoundLayout.setText("");
      recyclerView.setVisibility(View.VISIBLE);
    }
  }
  
  public ArrayList<? extends Serializable> getResults() {
    return getBaseAdapter().getList();
  }
  
  @Override
  protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    operations.onActivityResult(requestCode, resultCode, data);
  }
  
  public void removeAt(int position) {
    getBaseAdapter().removeAt(position);
  }
  
  public void addElement(Serializable item, int pos) {
    getBaseAdapter().getList().add(pos, item);
    getBaseAdapter().notifyDataSetChanged();
  }
  
  public void updateElement(Serializable item, int pos) {
    getBaseAdapter().getList().set(pos, item);
    getBaseAdapter().notifyItemChanged(pos);
  }
}

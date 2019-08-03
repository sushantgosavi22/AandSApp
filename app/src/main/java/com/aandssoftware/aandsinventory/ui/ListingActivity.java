package com.aandssoftware.aandsinventory.ui;

import android.os.Bundle;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.aandssoftware.aandsinventory.R;
import com.aandssoftware.aandsinventory.listing.ListType;
import com.aandssoftware.aandsinventory.listing.ListingOperations;
import com.aandssoftware.aandsinventory.database.RealmManager;
import com.aandssoftware.aandsinventory.ui.adapters.BaseRealmAdapter;
import io.realm.RealmObject;
import io.realm.RealmResults;

public class ListingActivity extends BaseActivity {
  
  public static final int LISTING_CODE = 101;
  public static final String LISTING_TYPE = "listingType";
  private BaseRealmAdapter baseRealmAdapter;
  private RealmResults<? extends RealmObject> realmResults;
  
  @BindView(R.id.recyclerView)
  RecyclerView recyclerView;
  
  @BindView(R.id.noResultFoundLayout)
  AppCompatTextView noResultFoundLayout;
  
  private ListingOperations operations;
  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    init();
  }
  
  private void init() {
    RealmManager.open();
    getListingOperations();
    setContentView(operations.getActivityLayoutId());
    setupActionBar(operations.getTitle());
    ButterKnife.bind(this);
    setUpList();
  }
  
  private void getListingOperations() {
    if (null != getIntent() && getIntent().hasExtra(LISTING_TYPE)) {
      ListType result = ListType.values()[getIntent().getIntExtra(LISTING_TYPE, -1)];
      operations = result.getInstance(ListingActivity.this);
    }
  }
  
  @Override
  public void onBackPressed() {
    operations.onBackPressed();
  }
  
  @Override
  protected void onDestroy() {
    RealmManager.close();
    super.onDestroy();
  }
  
  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    if (operations.getMenuLayoutId() != -1) {
      getMenuInflater().inflate(operations.getMenuLayoutId(), menu);
    }
    return true;
  }
  
  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    operations.onOptionsItemSelected(item);
    return super.onOptionsItemSelected(item);
  }
  
  private BaseRealmAdapter getAdapterInstance() {
    baseRealmAdapter = new BaseRealmAdapter(getRealmResults(), operations);
    return baseRealmAdapter;
  }
  
  private void setUpList() {
    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
    recyclerView.setLayoutManager(linearLayoutManager);
    setRealmResults(operations.getResult());
    recyclerView.setAdapter(getAdapterInstance());
    getBaseRealmAdapter().notifyDataSetChanged();
    validateRecyclerView();
  }
  
  public BaseRealmAdapter getBaseRealmAdapter() {
    return baseRealmAdapter;
  }
  
  public RecyclerView getRecyclerView() {
    return recyclerView;
  }
  
  public void reloadAdapter(RealmResults<? extends RealmObject> mList) {
    setRealmResults(mList);
    getBaseRealmAdapter().updateData(getRealmResults());
    getBaseRealmAdapter().notifyDataSetChanged();
    validateRecyclerView();
  }
  
  
  public void validateRecyclerView() {
    if (getRealmResults() != null && getRealmResults().isEmpty()) {
      noResultFoundLayout.setVisibility(View.VISIBLE);
      noResultFoundLayout.setText(getString(R.string.no_result_found));
      recyclerView.setVisibility(View.GONE);
    } else {
      noResultFoundLayout.setVisibility(View.GONE);
      noResultFoundLayout.setText("");
      recyclerView.setVisibility(View.VISIBLE);
    }
  }
  
  public RealmResults<? extends RealmObject> getRealmResults() {
    return realmResults;
  }
  
  public ListingActivity setRealmResults(RealmResults<? extends RealmObject> realmResults) {
    this.realmResults = realmResults;
    return this;
  }
  
}

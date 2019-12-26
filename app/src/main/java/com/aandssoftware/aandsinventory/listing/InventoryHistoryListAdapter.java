package com.aandssoftware.aandsinventory.listing;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.aandssoftware.aandsinventory.R;
import com.aandssoftware.aandsinventory.database.RealmManager;
import com.aandssoftware.aandsinventory.models.InventoryItemHistory;
import com.aandssoftware.aandsinventory.ui.ListingActivity;
import com.aandssoftware.aandsinventory.ui.adapters.BaseRealmAdapter.BaseViewHolder;
import io.realm.RealmObject;
import io.realm.RealmResults;
import java.util.Calendar;

public class InventoryHistoryListAdapter implements ListingOperations {
  
  private ListingActivity activity;
  
  public class InventoryHistoryViewHolder extends BaseViewHolder {
    
    @BindView(R.id.tvModifiedParameterName)
    TextView tvModifiedParameterName;
    @BindView(R.id.tvActinonMessage)
    TextView tvActionMessage;
    @BindView(R.id.tvDate)
    TextView tvDate;
    
    public InventoryHistoryViewHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
    }
  }
  
  public InventoryHistoryListAdapter(ListingActivity activity) {
    this.activity = activity;
  }
  
  @Override
  public int getActivityLayoutId() {
    return R.layout.activity_listing;
  }
  
  
  @Override
  public BaseViewHolder getBaseViewHolder(ViewGroup viewGroup, int i) {
    View v = LayoutInflater.from(viewGroup.getContext())
        .inflate(R.layout.item_history, viewGroup, false);
    return new InventoryHistoryViewHolder(v);
  }
  
  @Override
  public void onBindSearchViewHolder(BaseViewHolder baseHolder, int position, RealmObject item) {
    InventoryHistoryViewHolder holder = (InventoryHistoryViewHolder) baseHolder;
    InventoryItemHistory mItem = (InventoryItemHistory) item;
    holder.tvModifiedParameterName
        .setText(mItem.getModifiedParameter() + " is " + mItem.getAction());
    holder.tvActionMessage.setText(
        mItem.getModifiedParameter() + " is " + mItem.getAction() + " From " + mItem
            .getModifiedFrom() + " To " + mItem.getModifiedTo());
    holder.tvDate.setText(getStringFromTimeStamp(String.valueOf(mItem.getModifiedDate())));
    
  }
  
  public String getStringFromTimeStamp(String timestamp) {
    String result = "-";
    if (null != timestamp && !timestamp.isEmpty()) {
      Calendar calendar = Calendar.getInstance();
      calendar.setTimeInMillis(Long.valueOf(timestamp));
    }
    return result;
  }
  
  @Override
  public String getTitle() {
    return activity.getString(R.string.inventory_item_history);
  }
  
  @Override
  public RealmResults<? extends RealmObject> getResult() {
    if (null != activity.getIntent()) {
      int id = activity.getIntent().getIntExtra(InventoryListAdapter.INVENTORY_ID, -1);
      if (id != -1) {
        return RealmManager.getInventoryDao().getAllInventoryItemHistory(id);
      }
    }
    return RealmManager.getInventoryDao().getAllInventoryItemHistory();
  }
  
  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    //activity.getMenuInflater().inflate(R.menu.inventory_menu, menu);
    return true;
  }
  
  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case android.R.id.home:
        activity.finish();
        return true;
      default:
    }
    return true;
  }
  
  @Override
  public void onBackPressed() {
    activity.finish();
  }
  
}

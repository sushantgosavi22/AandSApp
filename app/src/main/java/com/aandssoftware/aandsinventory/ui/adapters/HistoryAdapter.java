package com.aandssoftware.aandsinventory.ui.adapters;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.aandssoftware.aandsinventory.models.InventoryItemHistory;
import io.realm.RealmList;
import java.util.Calendar;
import com.aandssoftware.aandsinventory.R;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {
  
  public RealmList<InventoryItemHistory> inventoryItemHistories;
  
  public HistoryAdapter(RealmList<InventoryItemHistory> orderedRealmCollection) {
    inventoryItemHistories = orderedRealmCollection;
  }
  
  @Override
  public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_history, parent, false);
    return new ViewHolder(v);
  }
  
  @Override
  public void onBindViewHolder(ViewHolder holder, int position) {
    holder.tvModifiedParameterName.setText(
        inventoryItemHistories.get(position).getModifiedParameter() + " is "
            + inventoryItemHistories.get(position).getAction());
    holder.tvActinonMessage.setText(
        inventoryItemHistories.get(position).getModifiedParameter() + " is "
            + inventoryItemHistories.get(position).getAction() + " From " + inventoryItemHistories
            .get(position).getModifiedFrom() + " To " + inventoryItemHistories.get(position)
            .getModifiedTo());
    holder.tvDate.setText(getStringFromTimeStamp(
        String.valueOf(inventoryItemHistories.get(position).getModifiedDate())));
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
  public int getItemCount() {
    return inventoryItemHistories.size();
  }
  
  
  public class ViewHolder extends RecyclerView.ViewHolder {
    
    
    @BindView(R.id.tvModifiedParameterName)
    TextView tvModifiedParameterName;
    @BindView(R.id.tvActinonMessage)
    TextView tvActinonMessage;
    @BindView(R.id.tvDate)
    TextView tvDate;
    
    public ViewHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
    }
  }
  
  public void updateData(RealmList<InventoryItemHistory> tasksUpdated) {
    inventoryItemHistories = new RealmList<>();
    inventoryItemHistories.addAll(tasksUpdated);
    notifyDataSetChanged();
  }
  
}

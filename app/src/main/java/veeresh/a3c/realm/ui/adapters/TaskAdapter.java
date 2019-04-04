package veeresh.a3c.realm.ui.adapters;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.applandeo.materialcalendarview.CalendarUtils;
import io.realm.RealmList;
import java.util.Calendar;
import org.greenrobot.eventbus.EventBus;
import veeresh.a3c.realm.R;
import veeresh.a3c.realm.events.TaskEvents;
import veeresh.a3c.realm.events.VehicalEvents;
import veeresh.a3c.realm.models.Task;

/**
 * Created by Veeresh on 3/11/17.
 */
public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.ViewHolder> {
  
  //RealmRecyclerViewAdapter<Task, TaskAdapter.ViewHolder>
  //private Context context;
  
  /*  public TaskAdapter(OrderedRealmCollection<Task> orderedRealmCollection) {
        super(orderedRealmCollection, true);
    }*/
  public RealmList<Task> tasks;
  
  public TaskAdapter(RealmList<Task> orderedRealmCollection) {
    tasks = orderedRealmCollection;
  }
  
  
  @Override
  public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_task, parent, false);
    return new ViewHolder(v);
  }
  
  @Override
  public void onBindViewHolder(ViewHolder holder, int position) {
    holder.contactName.setText(tasks.get(position).getContactName());
    holder.contactNumber.setText(tasks.get(position).getContactNumber());
    holder.tvHours.setText(""+tasks.get(position).getHour());
    holder.fromDate.setText(""+getStringFromTimeStamp(tasks.get(position).getFromDate()));
    holder.toDate.setText(""+getStringFromTimeStamp(tasks.get(position).getToDate()));
    holder.DesidedAmountValue.setText("" + tasks.get(position).getDecidedAmount());
    holder.PayedAmountValue.setText("" + tasks.get(position).getPayedAmount());
    holder.RemainingAmountValue.setText("" + tasks.get(position).getRemainingAmount());
    holder.tvDescription.setText("" + tasks.get(position).getDesciption());
    holder.tvWorkPlace.setText(tasks.get(position).getWorkPlace());
    holder.tvHours.setText(tasks.get(position).getHour() +" Hours");
    holder.tvDiseal.setText("" + tasks.get(position).getDieselForTask()+" Liter Diesel");
    
  }
  
  public String getStringFromTimeStamp(String timestamp) {
    String result = "-";
    if (null != timestamp && !timestamp.isEmpty()) {
      Calendar calendar = Calendar.getInstance();
      calendar.setTimeInMillis(Long.valueOf(timestamp));
      result = CalendarUtils.getCalanderDisplayDate(calendar.getTime());
    }
    return result;
  }
  
  @Override
  public int getItemCount() {
    return tasks.size();
  }
  
  
  public class ViewHolder extends RecyclerView.ViewHolder {
    
    
    @BindView(R.id.contactName)
    TextView contactName;
    @BindView(R.id.contactNumber)
    TextView contactNumber;
    @BindView(R.id.tvHours)
    TextView tvHours;
    
    @BindView(R.id.fromDate)
    TextView fromDate;
    @BindView(R.id.toDate)
    TextView toDate;
    
    @BindView(R.id.DesidedAmountValue)
    TextView DesidedAmountValue;
    @BindView(R.id.PayedAmountValue)
    TextView PayedAmountValue;
    @BindView(R.id.RemainingAmountValue)
    TextView RemainingAmountValue;
    
    @BindView(R.id.card_view)
    CardView cardView;
  
    @BindView(R.id.tvDescription)
    TextView tvDescription;
    @BindView(R.id.tvWorkPlace)
    TextView tvWorkPlace;
    @BindView(R.id.tvDiseal)
    TextView tvDiseal;
    
    @OnClick(R.id.card_view)
    public void navigate() {
      EventBus.getDefault().post(new TaskEvents.RecordEvent(tasks.get(getAdapterPosition()), VehicalAdapter.ACTION_ITEM_CLICKED));
    }
    
    @OnClick(R.id.imgEditTask)
    public void edit() {
      EventBus.getDefault().post(new TaskEvents.RecordEvent(tasks.get(getAdapterPosition()), VehicalAdapter.ACTION_ITEM_EDITED));
    }
    
    @OnClick(R.id.imgDeleteTask)
    public void delete() {
      EventBus.getDefault().post(new TaskEvents.RecordEvent(tasks.get(getAdapterPosition()), VehicalAdapter.ACTION_ITEM_DELETED));
    }
  
   /* @OnClick(R.id.contactNumber)
    public void callPhone() {
    
    }*/
    
    public ViewHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
    }
  }
  
  public void updateData(RealmList<Task> tasksUpdated) {
    tasks = new RealmList<>();
    tasks.addAll(tasksUpdated);
    notifyDataSetChanged();
  }
  
}

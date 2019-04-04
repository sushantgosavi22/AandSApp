package veeresh.a3c.realm.ui;

import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import io.realm.RealmList;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import veeresh.a3c.realm.R;
import veeresh.a3c.realm.common.Utils;
import veeresh.a3c.realm.database.RealmManager;
import veeresh.a3c.realm.events.TaskEvents;
import veeresh.a3c.realm.models.Task;
import veeresh.a3c.realm.models.callbackRealmObject;
import veeresh.a3c.realm.ui.adapters.TaskAdapter;
import veeresh.a3c.realm.ui.adapters.VehicalAdapter;

public class TaskListActivity extends AppCompatActivity implements callbackRealmObject {
  
  
  private TaskAdapter taskAdapter;
  private RealmList<Task> tasksRealmResults;
  @BindView(R.id.rv_fav)
  RecyclerView recyclerView;
  @BindView(R.id.loading_layout)
  RelativeLayout loadingLayout;
  @BindView(R.id.search_view)
  MaterialSearchView searchView;
  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_tasklist);
    overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
    ButterKnife.bind(this);
    initUI();
  }
  
  private void initUI() {
    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.abc_ic_ab_back_material));
    toolbar.setNavigationOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        onBackPressed();
      }
    });
    /*getActionBar().setDisplayHomeAsUpEnabled(true);
    getActionBar().setDisplayShowHomeEnabled(true);
    getActionBar().setHomeButtonEnabled(true);*/
    recyclerView.setLayoutManager(new LinearLayoutManager(this));
    taskAdapter = new TaskAdapter(tasksRealmResults);
    recyclerView.setAdapter(taskAdapter);
    updateTaskList();
    validateRecyclerView();
    searchView.setVoiceSearch(false);
    searchView.setCursorDrawable(R.drawable.custom_cursor);
    searchView.setEllipsize(true);
    searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
      @Override
      public boolean onQueryTextSubmit(String query) {
        return false;
      }
      
      @Override
      public boolean onQueryTextChange(String newText) {
        RealmList<Task> tasksRealmResults;
        if (newText.length() == 0) {
          tasksRealmResults = RealmManager.recordsDao().getAllTask();
        } else {
          tasksRealmResults = RealmManager.recordsDao().loadSearchListTask(newText);
        }
        updateTaskList(tasksRealmResults);
        return false;
      }
    });
  }
  
  public void updateTaskList() {
    tasksRealmResults = RealmManager.recordsDao().getAllTask();
    taskAdapter.updateData(tasksRealmResults);
    taskAdapter.notifyDataSetChanged();
    validateRecyclerView();
  }
  
  public void updateTaskList(RealmList<Task> tasks) {
    tasksRealmResults.clear();
    tasksRealmResults = tasks;
    taskAdapter.updateData(tasksRealmResults);
    taskAdapter.notifyDataSetChanged();
    validateRecyclerView();
  }
  
  @OnClick(R.id.action_sort)
  public void sort() {
    showSortDialog();
  }
  
  @Override
  protected void onStart() {
    super.onStart();
    EventBus.getDefault().register(this);
  }
  
  @Override
  protected void onStop() {
    EventBus.getDefault().unregister(this);
    super.onStop();
  }
  private void showSortDialog() {
    
    final CharSequence[] choice = {"Name Filter", "Remaining Amount", "From Date", "To Date",
        "Date Submited"};
    
    new AlertDialog.Builder(this)
        .setTitle("Sort By")
        .setSingleChoiceItems(choice, -1, null)
        .setPositiveButton("OK", (dialog, whichButton) -> {
          dialog.dismiss();
          RealmList<Task> tasksRealmResults;
          int selectedPosition = ((AlertDialog) dialog).getListView().getCheckedItemPosition();
          if (selectedPosition == 0) {
            tasksRealmResults = RealmManager.recordsDao().getAllTaskByName();
          } else if (selectedPosition == 1) {
            tasksRealmResults = RealmManager.recordsDao().getAllTaskByRemainingAmount();
          } else if (selectedPosition == 2) {
            tasksRealmResults = RealmManager.recordsDao().getAllTaskByFromDate();
          } else if (selectedPosition == 3) {
            tasksRealmResults = RealmManager.recordsDao().getAllTaskByToDate();
          } else if (selectedPosition == 4) {
            tasksRealmResults = RealmManager.recordsDao().getAllTaskBDateSubmited();
          } else {
            tasksRealmResults = RealmManager.recordsDao().getAllTask();
          }
          updateTaskList(tasksRealmResults);
        })
        .show();
  }
  
  public void validateRecyclerView() {
    if (tasksRealmResults.isEmpty()) {
      loadingLayout.setVisibility(View.VISIBLE);
      recyclerView.setVisibility(View.GONE);
    } else {
      loadingLayout.setVisibility(View.GONE);
      recyclerView.setVisibility(View.VISIBLE);
    }
  }
  
  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case android.R.id.home:
        onBackPressed();
        return true;
    }
    return super.onOptionsItemSelected(item);
  }
  
  
  @Override
  public void onBackPressed() {
    if (searchView.isSearchOpen()) {
      searchView.closeSearch();
    } else {
      super.onBackPressed();
    }
    overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
    super.onBackPressed();
  }
  
  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.toolbar_menu, menu);
    MenuItem item = menu.findItem(R.id.action_search);
    searchView.setMenuItem(item);
    return true;
  }
  
  @Subscribe
  public void navigateEvent(TaskEvents.RecordEvent recordEvent) {
    if(recordEvent.getActionId() == VehicalAdapter.ACTION_ITEM_CLICKED){
    }else if(recordEvent.getActionId() == VehicalAdapter.ACTION_ITEM_EDITED){
      AddTaskDialogFragment addTaskDialogFragment = new AddTaskDialogFragment();
      addTaskDialogFragment.setTask(recordEvent.getRecord());
      addTaskDialogFragment.setCallbackRealmObject(this);
      addTaskDialogFragment.show(getFragmentManager(),""+recordEvent.getRecord().getVehicalId());
    }else if(recordEvent.getActionId() == VehicalAdapter.ACTION_ITEM_DELETED){
      Utils.deleteTask(recordEvent.getRecord(),TaskListActivity.this,this,recordEvent.getRecord().getVehicalId());
    }
  }
  
  
  
  
  @Override
  public void getCallBack(boolean result) {
    if (result) {
      updateTaskList();
    }
  }
}

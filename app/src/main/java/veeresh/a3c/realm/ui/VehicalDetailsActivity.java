package veeresh.a3c.realm.ui;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import io.realm.RealmList;
import java.util.ArrayList;
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
import veeresh.a3c.realm.utilities.MyCustomTextView;

public class VehicalDetailsActivity extends AppCompatActivity implements callbackRealmObject,
    AppBarLayout.OnOffsetChangedListener {
  
  @BindView(R.id.imgVehicalPhoto)
  CircleImageView imgVehicalPhoto;
  /* @BindView(R.id.name)
   MyCustomTextView name;*/
  @BindView(R.id.vehicalNumber)
  MyCustomTextView vehicalNumber;
  @BindView(R.id.currentPlace)
  MyCustomTextView currentPlace;
  @BindView(R.id.dieselRemain)
  MyCustomTextView dieselRemain;
  @BindView(R.id.desc)
  MyCustomTextView desc;
  
  private String description;
  private String vehicalName;
  
  private TaskAdapter taskAdapter;
  private RealmList<Task> tasksRealmResults;
  @BindView(R.id.rv_tasks)
  RecyclerView recyclerView;
  int vehicalID;
  
  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_details);
    ButterKnife.bind(this);
    checkIntent();
    setUpUI();
  }
  
  private void setUpUI() {
    setUpToolBar();
    RealmManager.open();
    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
    recyclerView.setLayoutManager(linearLayoutManager);
    tasksRealmResults = RealmManager.recordsDao().getTaskByVehicalID(vehicalID);
    taskAdapter = new TaskAdapter(tasksRealmResults);
    recyclerView.setAdapter(taskAdapter);
    updateTaskList();
  }
  
  
  public void updateTaskList() {
    tasksRealmResults = RealmManager.recordsDao().getTaskByVehicalID(vehicalID);
    taskAdapter.updateData(tasksRealmResults);
    taskAdapter.notifyDataSetChanged();
    setVisibilityToList(tasksRealmResults.size());
  }
  
  public void setVisibilityToList(int size){
    if(size<=0){
      findViewById(R.id.flexible_example_cardview).setVisibility(View.GONE);
    }else {
      findViewById(R.id.flexible_example_cardview).setVisibility(View.VISIBLE);
    }
  }
  
  private void checkIntent() {
    Intent intent = getIntent();
    
    if (intent.getIntExtra(MainActivity.VEHICAL_ID, 0) != -1) {
      vehicalID = intent.getIntExtra(MainActivity.VEHICAL_ID, 0);
    }
    if (intent.getStringExtra(MainActivity.VEHICAL_NAME) != null) {
      vehicalName = intent.getStringExtra(MainActivity.VEHICAL_NAME);
      //name.setText(vehicalName);
    }
    if (intent.getStringExtra(MainActivity.VEHICAL_NUMBER) != null) {
      vehicalNumber.setText(intent.getStringExtra(MainActivity.VEHICAL_NUMBER));
    }
    if (intent.getStringExtra(MainActivity.VEHICAL_DESC) != null) {
      description = intent.getStringExtra(MainActivity.VEHICAL_DESC);
      desc.setText(description);
      
    }
    if (intent.getStringExtra(MainActivity.VEHICAL_IMAGE) != null) {
      try {
        Bitmap bitmap = BitmapFactory.decodeFile(intent.getStringExtra(MainActivity.VEHICAL_IMAGE));
        imgVehicalPhoto.setImageBitmap(bitmap);
      } catch (Exception e) {
        imgVehicalPhoto.setImageDrawable(
            Resources.getSystem().getDrawable(android.R.drawable.ic_menu_gallery));
        e.printStackTrace();
      }
    }
    currentPlace
        .setText("Currently on -  " + String
            .valueOf(intent.getStringExtra(MainActivity.VEHICAL_CURRENT_PLACE)));
    dieselRemain
        .setText("Disele - " + intent.getStringExtra(MainActivity.VEHICAL_DISELE));
    
    boolean isFav = intent.getBooleanExtra(MainActivity.IS_AVAILABLE, false);
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
  
  @Subscribe
  public void navigateEvent(TaskEvents.RecordEvent recordEvent) {
    if (recordEvent.getActionId() == VehicalAdapter.ACTION_ITEM_CLICKED) {
    
    } else if (recordEvent.getActionId() == VehicalAdapter.ACTION_ITEM_EDITED) {
      AddTaskDialogFragment addTaskDialogFragment = new AddTaskDialogFragment();
      addTaskDialogFragment.setTask(recordEvent.getRecord());
      addTaskDialogFragment.setCallbackRealmObject(this);
      addTaskDialogFragment.show(getFragmentManager(), "" + vehicalID);
    } else if (recordEvent.getActionId() == VehicalAdapter.ACTION_ITEM_DELETED) {
      Utils.deleteTask(recordEvent.getRecord(), VehicalDetailsActivity.this, this, vehicalID);
    }
  }
  
  
  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case android.R.id.home:
        supportFinishAfterTransition();
        return true;
    }
    return super.onOptionsItemSelected(item);
  }
  
  public static ArrayList<Task> getAllTask() {
    ArrayList<Task> tasks = new ArrayList<>();
    Task task1 = new Task();
    task1.setId(1);
    task1.setContactName("Test Name");
    tasks.add(task1);
    
    Task task2 = new Task();
    task2.setId(2);
    task2.setContactName("Test Name 2");
    tasks.add(task2);
    
    Task task3 = new Task();
    task3.setId(3);
    task3.setContactName("Test Name 3");
    tasks.add(task3);
    
    Task task4 = new Task();
    task4.setId(4);
    task4.setContactName("Test Name 4");
    tasks.add(task4);
    
    return tasks;
  }
  
  @Override
  protected void onDestroy() {
    RealmManager.close();
    super.onDestroy();
  }
  
  @Override
  public void getCallBack(boolean result) {
    if (result) {
      updateTaskList();
    }
  }
  
  private static final int PERCENTAGE_TO_SHOW_IMAGE = 20;
  private View mFab;
  private int mMaxScrollSize;
  private boolean mIsImageHidden;
  
  @Override
  public void onOffsetChanged(AppBarLayout appBarLayout, int i) {
    if (mMaxScrollSize == 0) {
      mMaxScrollSize = appBarLayout.getTotalScrollRange();
    }
    
    int currentScrollPercentage = (Math.abs(i)) * 100
        / mMaxScrollSize;
    
    if (currentScrollPercentage >= PERCENTAGE_TO_SHOW_IMAGE) {
      if (!mIsImageHidden) {
        mIsImageHidden = true;
        
        ViewCompat.animate(mFab).scaleY(0).scaleX(0).start();
      }
    }
    
    if (currentScrollPercentage < PERCENTAGE_TO_SHOW_IMAGE) {
      if (mIsImageHidden) {
        mIsImageHidden = false;
        ViewCompat.animate(mFab).scaleY(1).scaleX(1).start();
      }
    }
  }
  
  public void setUpToolBar() {
    mFab = (FloatingActionButton) findViewById(R.id.fab);
    callbackRealmObject object = this;
    mFab.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            AddTaskDialogFragment addTaskDialogFragment = new AddTaskDialogFragment();
            addTaskDialogFragment.setCallbackRealmObject(object);
            addTaskDialogFragment.show(getFragmentManager(), "" + vehicalID);
          }
        });
    Toolbar toolbar = (Toolbar) findViewById(R.id.flexible_example_toolbar);
    toolbar.setNavigationOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        onBackPressed();
      }
    });
    AppBarLayout appbar = (AppBarLayout) findViewById(R.id.flexible_example_appbar);
    appbar.addOnOffsetChangedListener(this);
    CollapsingToolbarLayout mCollapsingToolbarLayout = (CollapsingToolbarLayout) findViewById( R.id.mCollapsingToolbarLayout);
    mCollapsingToolbarLayout.setTitle(vehicalName);
  }
}

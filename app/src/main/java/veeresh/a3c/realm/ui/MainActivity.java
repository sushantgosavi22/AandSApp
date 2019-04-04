package veeresh.a3c.realm.ui;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import io.realm.RealmResults;
import java.util.List;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import veeresh.a3c.realm.R;
import veeresh.a3c.realm.common.Utils;
import veeresh.a3c.realm.database.RealmManager;
import veeresh.a3c.realm.events.VehicalEvents;
import veeresh.a3c.realm.models.InventoryItem;
import veeresh.a3c.realm.models.Record;
import veeresh.a3c.realm.models.RecordList;
import veeresh.a3c.realm.models.callbackRealmObject;
import veeresh.a3c.realm.networking.RetrofitConnection;
import veeresh.a3c.realm.ui.adapters.VehicalAdapter;
import veeresh.a3c.realm.utilities.APIOptions;
import veeresh.a3c.realm.utilities.EndlessRecyclerViewScrollListener;

public class MainActivity extends AppCompatActivity implements callbackRealmObject {
  
  private static final String TAG = MainActivity.class.getSimpleName();
  
  public static final String VEHICAL_ID = "VehicalID";
  public static final String VEHICAL_NAME = "playerName";
  public static final String VEHICAL_NUMBER = "playerScore";
  public static final String VEHICAL_CURRENT_PLACE = "playerMatches";
  public static final String VEHICAL_DESC = "playerDesc";
  public static final String VEHICAL_DISELE = "playerCountry";
  public static final String VEHICAL_IMAGE = "playerImage";
  public static final String IS_AVAILABLE = "playerFav";
  private static final int PICK_IMAGE = 100;
  
  
  private VehicalAdapter vehicalAdapter;
  private RealmResults<InventoryItem> vehicalsRealmResults;
  
  /* private RecordsAdapter adapter;
   private RealmResults<Record> recordRealmResults;*/
  @BindView(R.id.rv_records)
  RecyclerView recyclerView;
  @BindView(R.id.loading_layout)
  RelativeLayout loadingLayout;
  @BindView(R.id.search_view)
  MaterialSearchView searchView;
  @BindView(R.id.action_sort)
  ImageView sort;
  
  ImageView imgVehical;
  String Path = "";
  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    ButterKnife.bind(this);
    RealmManager.open();
    initUI();
    search();
  }
  
  private void search() {
    
    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    
    searchView.setVoiceSearch(false);
    searchView.setCursorDrawable(R.drawable.custom_cursor);
    searchView.setEllipsize(true);
    searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
      @Override
      public boolean onQueryTextSubmit(String query) {
        Log.d(TAG, query);
        return false;
      }
      
      @Override
      public boolean onQueryTextChange(String newText) {
        if (newText.length() == 0) {
          reloadAdapter();
        } else {
          RealmResults<InventoryItem> vehicalsRealmResults = RealmManager.recordsDao()
              .loadSearchListVehical(newText.toLowerCase());
          reloadAdapter(vehicalsRealmResults);
        }
        return false;
      }
    });
  }
  
  
  @Override
  public void onBackPressed() {
    if (searchView.isSearchOpen()) {
      searchView.closeSearch();
    } else {
      super.onBackPressed();
    }
  }
  
  public void reloadAdapter() {
    vehicalsRealmResults = RealmManager.recordsDao().loadRecords();
    vehicalAdapter = new VehicalAdapter(vehicalsRealmResults);
    recyclerView.setAdapter(vehicalAdapter);
    vehicalAdapter.notifyDataSetChanged();
    /*vehicalsRealmResults = RealmManager.recordsDao().loadRecords();
    vehicalAdapter.updateData(vehicalsRealmResults);
    vehicalAdapter.notifyDataSetChanged();*/
  }
  
  public void reloadAdapter(RealmResults<InventoryItem> mVehicalsRealmResults) {
    vehicalsRealmResults.clear();
    vehicalsRealmResults = mVehicalsRealmResults;
    vehicalAdapter.updateData(vehicalsRealmResults);
    vehicalAdapter.notifyDataSetChanged();
  }
  
  private void initUI() {
    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
    recyclerView.setLayoutManager(linearLayoutManager);
    vehicalsRealmResults = RealmManager.recordsDao().loadRecords();
    vehicalAdapter = new VehicalAdapter(vehicalsRealmResults);
    recyclerView.setAdapter(vehicalAdapter);
    addScrollListener(linearLayoutManager, recyclerView);
    validateRecyclerView();
    
    FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
    fab.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            showAddVehicalDialog(false, null);
          }
        });
    vehicalAdapter.updateData(RealmManager.recordsDao().loadRecords());
    validateRecyclerView();
    askingForRequest();
  }
  
  private void addScrollListener(LinearLayoutManager linearLayoutManager,
      RecyclerView recyclerView) {
    EndlessRecyclerViewScrollListener scrollListener = new EndlessRecyclerViewScrollListener(
        linearLayoutManager) {
      @Override
      public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
        Log.d(TAG, "Load More");
      }
    };
    recyclerView.addOnScrollListener(scrollListener);
  }
  
  private void getBatsman() {
    if (isNetworkAvailable()) {
      RetrofitConnection.getService().getBatsmen(APIOptions.getOptions())
          .enqueue(new Callback<RecordList>() {
            @Override
            public void onResponse(Call<RecordList> call, Response<RecordList> response) {
              if (response.isSuccessful()) {
                runOnUiThread(() -> {
                  saveResponse(response.body().getRecords());
                  loadFromDB();
                });
              }
            }
            
            @Override
            public void onFailure(Call<RecordList> call, Throwable t) {
              Log.d(TAG, t.getMessage());
            }
          });
    } else {
      Toast.makeText(this, "No Internet Connection", Toast.LENGTH_SHORT).show();
    }
  }
  
  private void loadFromDB() {
    vehicalsRealmResults = RealmManager.recordsDao().loadRecords();
    vehicalAdapter.updateData(vehicalsRealmResults);
    validateRecyclerView();
  }
  
  @Override
  protected void onStart() {
    super.onStart();
    EventBus.getDefault().register(this);
   /* RealmResults<InventoryItem> realmResults = RealmManager.recordsDao().loadRecords();
    if (realmResults.size() == 0) {
      getBatsman();
    } else {
      vehicalAdapter.updateData(RealmManager.recordsDao().loadRecords());
      validateRecyclerView();
    }*/
  }
  
  private void saveResponse(List<Record> response) {
    RealmManager.recordsDao().saveRecords(response);
  }
  
  @Override
  protected void onDestroy() {
    RealmManager.close();
    super.onDestroy();
  }
  
  public void validateRecyclerView() {
    if (vehicalsRealmResults != null && vehicalsRealmResults.isEmpty()) {
      loadingLayout.setVisibility(View.VISIBLE);
      recyclerView.setVisibility(View.GONE);
    } else {
      loadingLayout.setVisibility(View.GONE);
      recyclerView.setVisibility(View.VISIBLE);
    }
  }
  
  private void showAddVehicalDialog(final boolean shouldUpdate, final InventoryItem note) {
    LayoutInflater layoutInflaterAndroid = LayoutInflater.from(getApplicationContext());
    View view = layoutInflaterAndroid.inflate(R.layout.add_vehical, null);
    
    AlertDialog.Builder alertDialogBuilderUserInput =
        new AlertDialog.Builder(MainActivity.this);
    alertDialogBuilderUserInput.setView(view);
    
    imgVehical = (ImageView) view.findViewById(R.id.imgVehical);
    final EditText edtName = (EditText) view.findViewById(R.id.edtName);
    final EditText edtNumber = (EditText) view.findViewById(R.id.edtNumber);
    final EditText edtDescription = (EditText) view.findViewById(R.id.edtDescription);
    final EditText edtCurrentPlace = (EditText) view.findViewById(R.id.edtCurrentPlace);
    final EditText edtDiesel = (EditText) view.findViewById(R.id.edtDiesel);
    TextView dialog_title = (TextView) view.findViewById(R.id.dialog_title);
    //dialog_title.setText("");
    if (shouldUpdate && note != null) {
      edtName.setText("" + note.getName());
      edtNumber.setText("" + note.getVehicalNumber());
      edtDescription.setText("" + note.getDescription());
      edtCurrentPlace.setText("" + note.getCurrentPlace());
      edtDiesel.setText("" + note.getDieselRemain());
      if (note.getImagePath() != null) {
        Bitmap bitmap = BitmapFactory.decodeFile(note.getImagePath());
        if (null != imgVehical && null != bitmap) {
          imgVehical.setImageBitmap(bitmap);
        }
      }
    }
    imgVehical.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        if (ActivityCompat
            .checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED) {
          ActivityCompat.requestPermissions(MainActivity.this,
              new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                  Manifest.permission.WRITE_EXTERNAL_STORAGE}, PICK_IMAGE);
        } else {
          openGallary();
        }
      }
    });
    alertDialogBuilderUserInput
        .setCancelable(false)
        .setPositiveButton(
            shouldUpdate ? "update" : "save",
            new DialogInterface.OnClickListener() {
              public void onClick(DialogInterface dialogBox, int id) {
              }
            })
        .setNegativeButton(
            "cancel",
            new DialogInterface.OnClickListener() {
              public void onClick(DialogInterface dialogBox, int id) {
                dialogBox.cancel();
              }
            });
    
    final AlertDialog alertDialog = alertDialogBuilderUserInput.create();
    alertDialog.show();
    callbackRealmObject object = this;
    alertDialog
        .getButton(AlertDialog.BUTTON_POSITIVE)
        .setOnClickListener(
            new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                // Show toast message when no text is entered
                
                if (null != edtName.getText().toString()
                    && edtName.getText().toString().length() > 0) {
                  if (null != edtNumber.getText().toString()
                      && edtNumber.getText().toString().length() > 0) {
                    
                    if (shouldUpdate && note != null) {
                      int id = note.getId();
                      InventoryItem note = new InventoryItem();
                      note.setId(id);
                      note.setName(edtName.getText().toString());
                      note.setVehicalNumber(edtNumber.getText().toString());
                      note.setDescription(edtDescription.getText().toString());
                      note.setCurrentPlace(edtCurrentPlace.getText().toString());
                      note.setDieselRemain(edtDiesel.getText().toString());
                      note.setImagePath(Path);
                      RealmManager.recordsDao().saveVehical(note, object);
                      //reloadAdapter();
                    } else {
                      //InventoryItem inventoryItem = Realm.getDefaultInstance().createObject(InventoryItem.class,RealmManager.recordsDao().getNextVehicalID());
                      InventoryItem inventoryItem = new InventoryItem();
                      inventoryItem.setId(RealmManager.recordsDao().getNextVehicalID());
                      inventoryItem.setName(edtName.getText().toString());
                      inventoryItem.setVehicalNumber(edtNumber.getText().toString());
                      inventoryItem.setDescription(edtDescription.getText().toString());
                      inventoryItem.setCurrentPlace(edtCurrentPlace.getText().toString());
                      inventoryItem.setDieselRemain(edtDiesel.getText().toString());
                      inventoryItem.setImagePath(Path);
                      RealmManager.recordsDao().saveVehical(inventoryItem, object);
                      //reloadAdapter();
                    }
                  } else {
                    Utils.showToast("Please enter vehical number", MainActivity.this);
                  }
                } else {
                  Utils.showToast("Please enter vehical name", MainActivity.this);
                }
                alertDialog.dismiss();
              }
            });
  }
  
  private void openGallary() {
    Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
    getIntent.setType("image/*");
    Intent pickIntent = new Intent(Intent.ACTION_PICK,
        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
    pickIntent.setType("image/*");
    Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
    chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{pickIntent});
    startActivityForResult(chooserIntent, PICK_IMAGE);
  }
  
  public void askingForRequest() {
    if (ActivityCompat
        .checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
        != PackageManager.PERMISSION_GRANTED) {
      ActivityCompat.requestPermissions(MainActivity.this,
          new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
              Manifest.permission.WRITE_EXTERNAL_STORAGE}, PICK_IMAGE);
    }
  }
  
  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[],
      @NonNull int[] grantResults) {
    switch (requestCode) {
      case PICK_IMAGE:
        // If request is cancelled, the result arrays are empty.
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
          //openGallary();
        } else {
          Utils.showToast("Please provide permission to pick images", MainActivity.this);
          //do something like displaying a message that he didn`t allow the app to access gallery and you wont be able to let him select from gallery
        }
        break;
    }
  }
  
  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.toolbar_menu, menu);
    
    MenuItem item = menu.findItem(R.id.action_search);
    searchView.setMenuItem(item);
    
    return true;
  }
  
  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (requestCode == PICK_IMAGE) {
      
      Uri selectedImage = data.getData();
      String[] filePathColumn = {MediaStore.Images.Media.DATA};
      Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
      cursor.moveToFirst();
      int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
      Path = cursor.getString(columnIndex);
      
      Bitmap bitmap = BitmapFactory.decodeFile(Path);
      if (null != imgVehical && null != bitmap) {
        imgVehical.setImageBitmap(bitmap);
      }
    }
  }
  
  @OnClick(R.id.action_sort)
  public void sort() {
    showSortDialog();
  }
  
  @OnClick(R.id.actionCalender)
  public void actionCalender() {
    Intent intent = new Intent(this, CalendarActivity.class);
    startActivity(intent);
  }
  
  
  @OnClick(R.id.action_favorites)
  public void navigateToFavorites() {
    Intent intent = new Intent(MainActivity.this, TaskListActivity.class);
    startActivity(intent);
  }
  
  private void showSortDialog() {
    
    final CharSequence[] choice = {"Most Runs Scored", "Most Matches Played", "Least Runs Scored",
        "Least Matches Played"};
    
    new AlertDialog.Builder(this)
        .setTitle("Sort By")
        .setSingleChoiceItems(choice, -1, null)
        .setPositiveButton("OK", (dialog, whichButton) -> {
          dialog.dismiss();
          int selectedPosition = ((AlertDialog) dialog).getListView().getCheckedItemPosition();
          if (selectedPosition == 0) {
            sortByRuns();
          } else if (selectedPosition == 1) {
            sortByMatches();
          } else if (selectedPosition == 2) {
            sortByLeastRuns();
          } else if (selectedPosition == 3) {
            sortByLeastMatches();
          } else if (selectedPosition == 4) {
            ///RealmManager.recordsDao().saveVehicals(loadDemoData());
          }
          loadFromDB();
          
          vehicalAdapter = new VehicalAdapter(vehicalsRealmResults);
          recyclerView.setAdapter(vehicalAdapter);
        })
        .show();
  }
  
  
  private void sortByLeastMatches() {
//        vehicalsRealmResults = RealmManager.recordsDao().loadLeastMatches();
//        vehicalAdapter.updateData(vehicalsRealmResults);
  }
  
  private void sortByLeastRuns() {
      /*  vehicalsRealmResults = RealmManager.recordsDao().loadLeastRuns();
        vehicalAdapter.updateData(vehicalsRealmResults);*/
  }
  
  private void sortByMatches() {
       /* vehicalsRealmResults = RealmManager.recordsDao().loadMostMatches();
        vehicalAdapter.updateData(vehicalsRealmResults);*/
  }
  
  private void sortByRuns() {
      /*  vehicalsRealmResults = RealmManager.recordsDao().loadMostRuns();
        vehicalAdapter.updateData(vehicalsRealmResults);*/
  }
  
  
  @Override
  protected void onStop() {
    EventBus.getDefault().unregister(this);
    super.onStop();
  }
  
  
  @Subscribe
  public void navigateEvent(VehicalEvents.RecordEvent recordEvent) {
//    ActivityOptionsCompat options = ActivityOptionsCompat
//        .makeSceneTransitionAnimation(this, recordEvent.getImageView(), "source");
    if (recordEvent.getActionId() == VehicalAdapter.ACTION_ITEM_CLICKED) {
      Intent intent = new Intent(MainActivity.this, VehicalDetailsActivity.class);
      intent.putExtra(VEHICAL_ID, recordEvent.getRecord().getId());
      intent.putExtra(VEHICAL_NAME, recordEvent.getRecord().getName());
      intent.putExtra(VEHICAL_DISELE, recordEvent.getRecord().getDieselRemain());
      intent.putExtra(VEHICAL_DESC, recordEvent.getRecord().getDescription());
      intent.putExtra(VEHICAL_NUMBER, recordEvent.getRecord().getVehicalNumber());
      intent.putExtra(VEHICAL_CURRENT_PLACE, recordEvent.getRecord().getCurrentPlace());
      intent.putExtra(IS_AVAILABLE, recordEvent.getRecord().isAvailable());
      intent.putExtra(VEHICAL_IMAGE, recordEvent.getRecord().getImagePath());
      startActivity(intent);
      
    } else if (recordEvent.getActionId() == VehicalAdapter.ACTION_ITEM_EDITED) {
      showAddVehicalDialog(true, recordEvent.getRecord());
    }
   
    /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      startActivity(intent, options.toBundle());
    } else {
      startActivity(intent);
    }*/
    
  }
  
  private boolean isNetworkAvailable() {
    ConnectivityManager connectivityManager
        = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
    return activeNetworkInfo != null && activeNetworkInfo.isConnected();
  }
  
  @Override
  public void getCallBack(boolean result) {
    if (result) {
      Utils.showToast("Save data successfully", MainActivity.this);
      reloadAdapter();
    }
  }
}

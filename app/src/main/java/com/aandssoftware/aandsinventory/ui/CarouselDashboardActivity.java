package com.aandssoftware.aandsinventory.ui;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import com.aandssoftware.aandsinventory.R;
import com.aandssoftware.aandsinventory.database.RealmManager;
import com.aandssoftware.aandsinventory.models.CallbackRealmObject;
import com.aandssoftware.aandsinventory.models.CarouselMenuModel;
import com.aandssoftware.aandsinventory.models.CarouselMenuType;
import com.aandssoftware.aandsinventory.ui.adapters.CarouselMenuAdapter;
import io.realm.RealmResults;
import java.util.ArrayList;
import java.util.List;

public class CarouselDashboardActivity extends BaseActivity {
  
  RecyclerView recyclerView;
  CarouselMenuAdapter menuAdapter;
  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_carousel_dashboard_actvity);
    init();
    saveMenu();
  }
  
  private void init() {
    RealmManager.open();
    setupActionBar(getString(R.string.dashboard));
    recyclerView = findViewById(R.id.recyclerView);
  }
  
  private void saveMenu() {
    RealmResults<CarouselMenuModel> menuModels = getCarouselMenu();
    if (menuModels.isEmpty()) {
      List<CarouselMenuModel> models = addCarouselMenu();
      RealmManager.getCarouselDao().saveCarouselMenuList(models, new CallbackRealmObject() {
        @Override
        public void getCallBack(boolean result) {
          setUpList();
        }
      });
    } else {
      setUpList();
    }
  }
  
  private void setUpList() {
    GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);
    recyclerView.setLayoutManager(gridLayoutManager);
    recyclerView.setAdapter(getAdapterInstance());
    getMenuAdapter().notifyDataSetChanged();
  }
  
  
  private CarouselMenuAdapter getAdapterInstance() {
    menuAdapter = new CarouselMenuAdapter(getCarouselMenu());
    return menuAdapter;
  }
  
  private CarouselMenuAdapter getMenuAdapter() {
    return menuAdapter;
  }
  
  private RealmResults<CarouselMenuModel> getCarouselMenu() {
    return RealmManager.getCarouselDao().getCarouselMenus();
  }
  
  
  private List<CarouselMenuModel> addCarouselMenu() {
    List<CarouselMenuModel> list = new ArrayList<>();
    
    CarouselMenuModel orders = new CarouselMenuModel();
    orders.setId(3);
    orders.setCarouselId(CarouselMenuType.ORDERS.getOrders());
    orders.setAliceName("Orders");
    orders.setDateCreated(System.currentTimeMillis());
    orders.setDescription("Orders Description");
    orders.setExpression("");
    orders.setTag("3");
    list.add(orders);
    
    CarouselMenuModel materials = new CarouselMenuModel();
    materials.setId(4);
    materials.setCarouselId(CarouselMenuType.MATERIALS.getOrders());
    materials.setAliceName("Materials");
    materials.setDateCreated(System.currentTimeMillis());
    materials.setDescription("Materials Description");
    materials.setExpression("");
    materials.setTag("4");
    list.add(materials);
    
    CarouselMenuModel customers = new CarouselMenuModel();
    customers.setId(1);
    customers.setCarouselId(CarouselMenuType.CUSTOMERS.getOrders());
    customers.setAliceName("Customers");
    customers.setDateCreated(System.currentTimeMillis());
    customers.setDescription("Customers Description");
    customers.setExpression("");
    customers.setTag("1");
    list.add(customers);
    
    CarouselMenuModel inventoryHistory = new CarouselMenuModel();
    inventoryHistory.setId(2);
    inventoryHistory.setCarouselId(CarouselMenuType.INVENTORY_HISTORY.getOrders());
    inventoryHistory.setAliceName("Inventory History");
    inventoryHistory.setDateCreated(System.currentTimeMillis());
    inventoryHistory.setDescription("Inventory History Description");
    inventoryHistory.setExpression("");
    inventoryHistory.setTag("2");
    list.add(inventoryHistory);
    
    CarouselMenuModel inventoryList = new CarouselMenuModel();
    inventoryList.setId(0);
    inventoryList.setCarouselId(CarouselMenuType.INVENTORY.getOrders());
    inventoryList.setAliceName("Inventory");
    inventoryList.setDateCreated(System.currentTimeMillis());
    inventoryList.setDescription("Description");
    inventoryList.setExpression("");
    inventoryList.setTag("0");
    list.add(inventoryList);
    
    return list;
  }
  
  @Override
  protected void onDestroy() {
    RealmManager.close();
    super.onDestroy();
  }
}

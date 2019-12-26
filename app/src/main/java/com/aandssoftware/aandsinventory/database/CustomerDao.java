package com.aandssoftware.aandsinventory.database;

import android.support.annotation.NonNull;
import com.aandssoftware.aandsinventory.models.CallbackRealmObject;
import com.aandssoftware.aandsinventory.models.CustomerModel;
import com.aandssoftware.aandsinventory.models.InventoryItem;
import com.aandssoftware.aandsinventory.models.OrderModel;
import com.aandssoftware.aandsinventory.models.OrderStatus;
import io.realm.Realm;
import io.realm.Realm.Transaction;
import io.realm.Realm.Transaction.OnSuccess;
import io.realm.RealmResults;
import io.realm.Sort;

public class CustomerDao {
  
  private Realm mRealm;
  
  public CustomerDao(@NonNull Realm realm) {
    mRealm = realm;
  }
  
  
  public RealmResults<CustomerModel> getAllCustomers() {
    return mRealm.where(CustomerModel.class).sort("id", Sort.ASCENDING).findAll();
    
  }
  
  public RealmResults<OrderModel> getOrders() {
    return mRealm.where(OrderModel.class)
        .sort("orderDateUpdated", Sort.ASCENDING).findAll();
  }
  
  
  public void saveCustomerItem(CustomerModel mCustomerModel, CallbackRealmObject object) {
    mRealm.executeTransactionAsync(new Transaction() {
      @Override
      public void execute(Realm realm) {
        CustomerModel customerModelItem = new CustomerModel(mCustomerModel);
        realm.copyToRealmOrUpdate(customerModelItem);
      }
    }, new OnSuccess() {
      @Override
      public void onSuccess() {
        object.getCallBack(true);
      }
    });
  }
  
  
  public int getNextCustomerItemId() {
    Number currentIdNum = mRealm.where(CustomerModel.class).max("id");
    int nextId;
    if (currentIdNum == null) {
      nextId = 1;
    } else {
      nextId = currentIdNum.intValue() + 1;
    }
    return nextId;
  }
  
  public CustomerModel getCustomerFromID(int customerId) {
    CustomerModel result = mRealm.where(CustomerModel.class)
        .equalTo("id", customerId).findFirst();
    return result;
  }
  
  public void removeCompany(CustomerModel customerModel) {
    mRealm.executeTransaction(new Transaction() {
      @Override
      public void execute(Realm realm) {
        CustomerModel result = mRealm.where(CustomerModel.class)
            .equalTo("id", customerModel.getId()).findFirst();
        if (null != result) {
          result.deleteFromRealm();
        }
      }
    });
  }
  
  public void removeOrder(OrderModel orderModel) {
    mRealm.executeTransaction(new Realm.Transaction() {
      @Override
      public void execute(Realm realm) {
        OrderModel result = realm.where(OrderModel.class)
            .equalTo("id", orderModel.getId()).findFirst();
        if (null != result) {
          result.deleteFromRealm();
        }
      }
    });
  }
  
  public void addInventoryToOrder(InventoryItem inventoryItem, int OrderId,
      CallbackRealmObject object) {
    mRealm.executeTransactionAsync(new Transaction() {
      @Override
      public void execute(Realm realm) {
        OrderModel model = realm.where(OrderModel.class)
            .equalTo("id", OrderId).findFirst();
        if (model != null) {
          model.getOrderItems().add(inventoryItem);
          realm.copyToRealmOrUpdate(model);
        }
      }
    }, new OnSuccess() {
      @Override
      public void onSuccess() {
        object.getCallBack(true);
      }
    });
  }
  
  public void addInventoryToOrder(InventoryItem mainInventoryItem, int OrderId, int parentId,
      String quantity, CallbackRealmObject object) {
    mRealm.beginTransaction();
    OrderModel model = mRealm.where(OrderModel.class)
        .equalTo("id", OrderId).findFirst();
    if (model != null) {
      InventoryItem item = new InventoryItem(mainInventoryItem);
      item.setId(RealmManager.getInventoryDao().getNextInventoryItemId());
      item.setParentId(parentId);
      item.setItemQuantity(quantity);
      model.getOrderItems().add(item);
      mRealm.commitTransaction();
      object.getCallBack(true);
    } else {
      mRealm.cancelTransaction();
      object.getCallBack(false);
    }
  }
  
  
  public void saveOrder(OrderModel orderModel, int customerId, CallbackRealmObject object) {
    mRealm.executeTransactionAsync(new Transaction() {
      @Override
      public void execute(Realm realm) {
        CustomerModel customerModel = realm.where(CustomerModel.class)
            .equalTo("id", customerId).findFirst();
        if (customerModel != null) {
          orderModel.setCustomerModel(customerModel);
        }
        OrderModel model = new OrderModel(orderModel);
        realm.copyToRealmOrUpdate(model);
      }
    }, new OnSuccess() {
      @Override
      public void onSuccess() {
        object.getCallBack(true);
      }
    });
  }
  
  public void saveOrder(final int orderId, int customerId, CallbackRealmObject object) {
    mRealm.executeTransactionAsync(new Transaction() {
      @Override
      public void execute(Realm realm) {
        OrderModel model;
        if (orderId != -1) {
          model = realm.where(OrderModel.class)
              .equalTo("id", orderId).findFirst();
        } else {
          model = new OrderModel();
          Number currentIdNum = realm.where(OrderModel.class).max("id");
          int orderId = (currentIdNum == null) ? 1 : currentIdNum.intValue() + 1;
          model.setId(orderId);
          model.setOrderStatus(OrderStatus.CREATED.name());
          model.setOrderStatusName(OrderStatus.CREATED.toString());
          model.setOrderDateUpdated(System.currentTimeMillis());
          model.setOrderDateCreated(System.currentTimeMillis());
          CustomerModel customerModel = realm.where(CustomerModel.class)
              .equalTo("id", customerId).findFirst();
          if (customerModel != null) {
            model.setCustomerModel(customerModel);
          }
        }
        OrderModel modelUpdated = new OrderModel(model);
        realm.copyToRealmOrUpdate(modelUpdated);
      }
    }, new OnSuccess() {
      @Override
      public void onSuccess() {
        object.getCallBack(true);
      }
    });
  }
  
  public OrderModel getOrderFromID(int orderId) {
    OrderModel result = mRealm.where(OrderModel.class)
        .equalTo("id", orderId).findFirst();
    return result;
  }
  
  public int getNextOrderId() {
    Number currentIdNum = mRealm.where(OrderModel.class).max("id");
    int nextId;
    if (currentIdNum == null) {
      nextId = 1;
    } else {
      nextId = currentIdNum.intValue() + 1;
    }
    return nextId;
  }
}

package com.aandssoftware.aandsinventory.database;

import android.support.annotation.NonNull;
import com.aandssoftware.aandsinventory.listing.ListType;
import com.aandssoftware.aandsinventory.models.CallbackRealmObject;
import com.aandssoftware.aandsinventory.models.InventoryItem;
import com.aandssoftware.aandsinventory.models.InventoryItemHistory;
import io.realm.Case;
import io.realm.Realm;
import io.realm.Realm.Transaction;
import io.realm.Realm.Transaction.OnSuccess;
import io.realm.RealmList;
import io.realm.RealmResults;
import io.realm.Sort;
import java.util.HashMap;
import java.util.Map.Entry;

public class InventoryDao {
  
  private Realm mRealm;
  
  public InventoryDao(@NonNull Realm realm) {
    mRealm = realm;
  }
  
  
  public RealmResults<InventoryItem> getInventoryItemRecords() {
    return mRealm.where(InventoryItem.class)
        .equalTo("inventoryType", ListType.LIST_TYPE_INVENTORY.ordinal())
        .findAll();
  }
  
  public RealmResults<InventoryItem> getMaterialRecords() {
    return mRealm.where(InventoryItem.class).findAllSorted("inventoryItemName");
  }
  
  
  public RealmResults<InventoryItem> loadSearchListInventoryItem(String query) {
    return mRealm.where(InventoryItem.class)
        .contains("inventntoryItemName", query, Case.INSENSITIVE).or()
        .contains("description", query, Case.INSENSITIVE).or()
        .contains("inventoryItemBrandName", query, Case.INSENSITIVE).or()
        .contains("inventoryItemModelName", query, Case.INSENSITIVE).findAll();
  }
  
  
  public RealmResults<InventoryItemHistory> getAllInventoryItemHistory() {
    RealmResults<InventoryItemHistory> realmResultList = mRealm.where(InventoryItemHistory.class)
        .findAllSorted("id", Sort.DESCENDING);
    return realmResultList;
  }
  
  public RealmResults<InventoryItemHistory> getAllInventoryItemHistory(int inventoryId) {
    return mRealm.where(InventoryItemHistory.class).equalTo("id", inventoryId)
        .findAllSorted("modifiedDate");
  }
  
  public void saveInventoryItem(InventoryItem mInventoryItem, CallbackRealmObject object) {
    mRealm.executeTransactionAsync(new Transaction() {
      @Override
      public void execute(Realm realm) {
        InventoryItem inventoryItem = new InventoryItem(mInventoryItem);
        realm.copyToRealmOrUpdate(inventoryItem);
      }
    }, new OnSuccess() {
      @Override
      public void onSuccess() {
        object.getCallBack(true);
      }
    });
  }
  
  
  public int getNextInventoryItemId() {
    Number currentIdNum = mRealm.where(InventoryItem.class).max("id");
    int nextId;
    if (currentIdNum == null) {
      nextId = 1;
    } else {
      nextId = currentIdNum.intValue() + 1;
    }
    return nextId;
  }
  
  public int getNextInventoryHistoryId() {
    Number currentIdNum = mRealm.where(InventoryItemHistory.class).max("id");
    int nextId;
    if (currentIdNum == null) {
      nextId = 1;
    } else {
      nextId = currentIdNum.intValue() + 1;
    }
    return nextId;
  }
  
  public void removeInventoryItem(InventoryItem inventoryItem) {
    mRealm.executeTransaction(new Realm.Transaction() {
      @Override
      public void execute(Realm realm) {
        InventoryItem result = mRealm.where(InventoryItem.class)
            .equalTo("id", inventoryItem.getId()).findFirst();
        if (null != result) {
          result.deleteFromRealm();
        }
      }
    });
  }
  
  
  public boolean saveInventoryItemHistory(InventoryItem mInventoryItem,
      HashMap<Integer, InventoryItemHistory> hashMap) {
    try {
      mRealm.executeTransactionAsync(new Transaction() {
        @Override
        public void execute(Realm realm) {
          RealmList<InventoryItemHistory> list = mInventoryItem.getInventoryItemHistories();
          list = (null != list) ? list : new RealmList<>();
          for (Entry<Integer, InventoryItemHistory> integerInventoryItemHistoryEntry : hashMap
              .entrySet()) {
            list.add(integerInventoryItemHistoryEntry.getValue());
          }
          mInventoryItem.setInventoryItemHistories(list);
          realm.copyToRealmOrUpdate(mInventoryItem);
        }
      }, new OnSuccess() {
        @Override
        public void onSuccess() {
        }
      });
      
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
    return true;
  }
  
}

package veeresh.a3c.realm.database;

import android.support.annotation.NonNull;
import io.realm.Case;
import io.realm.Realm;
import io.realm.Realm.Transaction;
import io.realm.Realm.Transaction.OnSuccess;
import io.realm.RealmList;
import io.realm.RealmResults;
import io.realm.Sort;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import veeresh.a3c.realm.models.InventoryItem;
import veeresh.a3c.realm.models.Record;
import veeresh.a3c.realm.models.Task;
import veeresh.a3c.realm.models.callbackRealmObject;

/**
 * Created by Veeresh on 3/11/17.
 */
public class RecordsDao {
  
  private Realm mRealm;
  
  public RecordsDao(@NonNull Realm realm) {
    mRealm = realm;
  }
  
  public void saveRecords(final List<Record> recordLists) {
    mRealm.executeTransaction(realm -> mRealm.copyToRealmOrUpdate(recordLists));
  }
  
  public void saveVehicals(final List<InventoryItem> inventoryItemList) {
    mRealm.executeTransaction(realm -> mRealm.copyToRealmOrUpdate(inventoryItemList));
  }
  
  public void saveToFavorites(Record record) {
    mRealm.executeTransaction(realm -> {
      record.setFavourite(true);
      mRealm.copyToRealmOrUpdate(record);
    });
  }
  
  public void removeFromFavorites(Record record) {
    mRealm.executeTransaction(realm -> {
      record.setFavourite(false);
      mRealm.copyToRealmOrUpdate(record);
    });
  }
  
  public void saveToAvailable(InventoryItem record) {
    mRealm.executeTransaction(realm -> {
      record.setAvailable(true);
      mRealm.copyToRealmOrUpdate(record);
    });
  }
  
  public void removeFromAvailable(InventoryItem record) {
    mRealm.executeTransaction(realm -> {
      record.setAvailable(false);
      mRealm.copyToRealmOrUpdate(record);
    });
  }
  
  public RealmResults<InventoryItem> loadRecords() {
    return mRealm.where(InventoryItem.class).findAllSorted("id");
    //return mRealm.where(Record.class).findAllSorted("id");
  }
  
  public RealmResults<Record> loadFavRecords() {
    return mRealm.where(Record.class).equalTo("isFavourite", true).findAll();
  }
  
  public RealmResults<Record> loadMostRuns() {
    RealmResults<Record> result = mRealm.where(Record.class).findAll();
    result = result.sort("totalScore", Sort.DESCENDING);
    return result;
  }
  
  public RealmResults<Record> loadSearch(String query) {
    return mRealm.where(Record.class).contains("name", query, Case.INSENSITIVE).findAll();
  }
  
  public RealmResults<Record> loadMostMatches() {
    RealmResults<Record> result = mRealm.where(Record.class).findAll();
    result = result.sort("matchesPlayed", Sort.DESCENDING);
    return result;
  }
  
  public RealmResults<Record> loadLeastRuns() {
    RealmResults<Record> result = mRealm.where(Record.class).findAll();
    result = result.sort("totalScore");
    return result;
  }
  
  public RealmResults<Record> loadLeastMatches() {
    RealmResults<Record> result = mRealm.where(Record.class).findAll();
    result = result.sort("matchesPlayed");
    return result;
  }
  
  public RealmResults<InventoryItem> loadSearchListVehical(String query) {
    return mRealm.where(InventoryItem.class).contains("name", query, Case.INSENSITIVE).or()
        .contains("vehicalNumber", query, Case.INSENSITIVE).or()
        .contains("currentPlace", query, Case.INSENSITIVE).or()
        .contains("description", query, Case.INSENSITIVE).findAll();
  }
  
  public RealmResults<InventoryItem> getAllVehicals() {
    return mRealm.where(InventoryItem.class).findAllSorted("id");
  }
  
  public RealmList<Task> getAllTask() {
    RealmResults<Task> realmResultList = mRealm.where(Task.class).findAllSorted("currentTimestamp",Sort.DESCENDING);
    RealmList<Task> results = new RealmList<Task>();
    results.addAll(realmResultList.subList(0, realmResultList.size()));
    return results;
  }
  
  public RealmList<Task> getAllTaskByRemainingAmount() {
    RealmResults<Task> result = mRealm.where(Task.class).findAll();
    RealmList<Task> results = new RealmList<Task>();
    results.addAll(result.subList(0, result.size()));
    Collections.sort(results, (s1, s2) -> Integer.valueOf(s2.getRemainingAmount())
        .compareTo(Integer.valueOf(s1.getRemainingAmount())));
    return results;
  }
  
  public RealmList<Task> getAllTaskByFromDate() {
    RealmResults<Task> result = mRealm.where(Task.class).findAll();
    result = result.sort("fromDate", Sort.DESCENDING);
    RealmList<Task> results = new RealmList<Task>();
    results.addAll(result.subList(0, result.size()));
    return results;
  }
  
  public RealmList<Task> getAllTaskByName() {
    RealmResults<Task> result = mRealm.where(Task.class)
        .findAllSorted("contactName", Sort.ASCENDING);
    RealmList<Task> results = new RealmList<Task>();
    results.addAll(result.subList(0, result.size()));
    return results;
  }
  
  public RealmList<Task> getAllTaskByToDate() {
    RealmResults<Task> result = mRealm.where(Task.class).findAll();
    result = result.sort("toDate", Sort.DESCENDING);
    RealmList<Task> results = new RealmList<Task>();
    results.addAll(result.subList(0, result.size()));
    return results;
  }
  
  public RealmList<Task> getAllTaskBDateSubmited() {
    RealmResults<Task> result = mRealm.where(Task.class).findAll();
    result = result.sort("currentTimestamp", Sort.DESCENDING);
    RealmList<Task> results = new RealmList<Task>();
    results.addAll(result.subList(0, result.size()));
    Collections.sort(results, (s1, s2) -> Long.valueOf(s1.getCurrentTimestamp())
        .compareTo(Long.valueOf(s2.getCurrentTimestamp())));
    return results;
  }
  
  public RealmList<Task> loadSearchListTask(String query) {
    RealmResults<Task> result = mRealm.where(Task.class)
        .contains("contactName", query, Case.INSENSITIVE).or()
        .contains("workPlace", query, Case.INSENSITIVE).or()
        .contains("desciption", query, Case.INSENSITIVE).findAll();
    RealmList<Task> results = new RealmList<Task>();
    results.addAll(result.subList(0, result.size()));
    return results;
  }
  
  public void saveVehical(InventoryItem mInventoryItem, callbackRealmObject object) {
    mRealm.executeTransactionAsync(new Transaction() {
      @Override
      public void execute(Realm realm) {
        InventoryItem inventoryItem = new InventoryItem();
        inventoryItem.setId(mInventoryItem.getId());
        inventoryItem.setName(mInventoryItem.getName());
        inventoryItem.setVehicalNumber(mInventoryItem.getVehicalNumber());
        inventoryItem.setDescription(mInventoryItem.getDescription());
        inventoryItem.setCurrentPlace(mInventoryItem.getCurrentPlace());
        inventoryItem.setDieselRemain(mInventoryItem.getDieselRemain());
        inventoryItem.setImagePath(mInventoryItem.getImagePath());
        realm.copyToRealmOrUpdate(inventoryItem);
      }
    }, new OnSuccess() {
      @Override
      public void onSuccess() {
        object.getCallBack(true);
      }
    });
    // mRealm.commitTransaction();
    // using insert API
  }
  
  
  public int getNextVehicalID() {
    Number currentIdNum = mRealm.where(InventoryItem.class).max("id");
    int nextId;
    if (currentIdNum == null) {
      nextId = 1;
    } else {
      nextId = currentIdNum.intValue() + 1;
    }
    return nextId;
  }
  
  public int getNextTaskID() {
    //  mRealm.beginTransaction();
    Number currentIdNum = mRealm.where(Task.class).max("id");
    int nextId;
    if (currentIdNum == null) {
      nextId = 1;
    } else {
      nextId = currentIdNum.intValue() + 1;
    }
    
    //  mRealm.commitTransaction();
    return nextId;
  }
  
  public void removeVehical(InventoryItem inventoryItem) {
    mRealm.executeTransaction(new Realm.Transaction() {
      @Override
      public void execute(Realm realm) {
        InventoryItem result = mRealm.where(InventoryItem.class).equalTo("id", inventoryItem.getId()).findFirst();
        if (null != result) {
          result.deleteFromRealm();
        }
      }
    });
  }
  
  public void removeTask(int taskID, int vehicalId, callbackRealmObject object) {
    mRealm.executeTransactionAsync(new Realm.Transaction() {
      @Override
      public void execute(Realm realm) {
        InventoryItem result = realm.where(InventoryItem.class).equalTo("id", vehicalId).findFirst();
        Iterator<Task> iterator = result.getTasks().iterator();
        while (iterator.hasNext()) {
          Task a = iterator.next();
          if (a.getId() == taskID) {
            iterator.remove();
            a.deleteFromRealm();
            // removes from realm list, but not from Realm
            break;
          }
        }
      }
    }, new OnSuccess() {
      @Override
      public void onSuccess() {
        object.getCallBack(true);
      }
    });
  }
  
  public boolean saveTask(Task task, int vehicalID, callbackRealmObject object) {
    try {
      // mRealm.beginTransaction();
      mRealm.executeTransactionAsync(new Transaction() {
        @Override
        public void execute(Realm realm) {
          InventoryItem mInventoryItem = realm.where(InventoryItem.class).equalTo("id", vehicalID).findFirst();
          Task taskfinal = new Task();
          taskfinal.setVehicalId(task.getVehicalId());
          taskfinal.setId(task.getId());
          taskfinal.setContactName(task.getContactName());
          taskfinal.setContactNumber(task.getContactNumber());
          taskfinal.setFromDate(task.getFromDate());
          taskfinal.setToDate(task.getToDate());
          taskfinal.setCurrentTimestamp(task.getCurrentTimestamp());
          taskfinal.setHour(task.getHour());
          taskfinal.setDieselForTask(task.getDieselForTask());
          taskfinal.setDecidedAmount(task.getDecidedAmount());
          taskfinal.setPayedAmount(task.getPayedAmount());
          taskfinal.setRemainingAmount(task.getRemainingAmount());
          taskfinal.setPayedAmount(task.getPayedAmount());
          taskfinal.setAdvanceToDriver(task.getAdvanceToDriver());
          taskfinal.setDesciption(task.getDesciption());
          taskfinal.setWorkPlace(task.getWorkPlace());
          taskfinal.setPaymentRemain(task.isPaymentRemain());
          
          Task mTask1 = mInventoryItem.getTasks().where().equalTo("id", taskfinal.getId())
              .findFirst();//add(taskfinal);
          if (null != mTask1 && mInventoryItem.getTasks().indexOf(mTask1) != -1) {
            mInventoryItem.getTasks().set(mInventoryItem.getTasks().indexOf(mTask1), taskfinal);
          } else {
            mInventoryItem.getTasks().add(taskfinal);
          }
          realm.copyToRealmOrUpdate(mInventoryItem);
        }
      }, new OnSuccess() {
        @Override
        public void onSuccess() {
          object.getCallBack(true);
        }
      });
      
      //mRealm.commitTransaction();
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
    return true;
  }
  
  public RealmList<Task> getTaskByVehicalID(int vehicalID) {
    RealmList<Task> tasks = new RealmList<>();
    try {
      InventoryItem mInventoryItem = mRealm.where(InventoryItem.class).equalTo("id", vehicalID).findFirst();
      tasks = mInventoryItem.getTasks();
    } catch (Exception e) {
      e.printStackTrace();
    }
    return tasks;
  }
  
  
}

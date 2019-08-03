package com.aandssoftware.aandsinventory.database;

import android.support.annotation.NonNull;
import com.aandssoftware.aandsinventory.models.CustomerModel;
import com.aandssoftware.aandsinventory.models.CallbackRealmObject;
import io.realm.Realm;
import io.realm.Realm.Transaction;
import io.realm.Realm.Transaction.OnSuccess;
import io.realm.RealmList;
import io.realm.RealmResults;
import io.realm.Sort;

public class CustomerDao {
  
  private Realm mRealm;
  
  public CustomerDao(@NonNull Realm realm) {
    mRealm = realm;
  }
  
  
  public RealmResults<CustomerModel> loadCompanyRecords() {
    return mRealm.where(CustomerModel.class).findAllSorted("id");
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
  
  
  public CustomerModel getCompanyByID(int companyID) {
    RealmList<CustomerModel> mCustomerModelList = new RealmList<>();
    try {
      CustomerModel mCustomerModel = mRealm.where(CustomerModel.class).equalTo("id", companyID)
          .findFirst();
      return mCustomerModel;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }
  
  public RealmList<CustomerModel> getAllCompany() {
    RealmResults<CustomerModel> realmResultList = mRealm.where(CustomerModel.class)
        .findAllSorted("id", Sort.DESCENDING);
    RealmList<CustomerModel> results = new RealmList<CustomerModel>();
    results.addAll(realmResultList.subList(0, realmResultList.size()));
    return results;
  }
}

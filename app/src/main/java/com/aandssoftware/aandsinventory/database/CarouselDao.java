package com.aandssoftware.aandsinventory.database;

import android.support.annotation.NonNull;
import com.aandssoftware.aandsinventory.models.CallbackRealmObject;
import com.aandssoftware.aandsinventory.models.CarouselMenuModel;
import io.realm.Realm;
import io.realm.Realm.Transaction;
import io.realm.Realm.Transaction.OnSuccess;
import io.realm.RealmResults;
import io.realm.Sort;
import java.util.List;

public class CarouselDao {
  
  private Realm mRealm;
  
  public CarouselDao(@NonNull Realm realm) {
    mRealm = realm;
  }
  
  
  public RealmResults<CarouselMenuModel> getCarouselMenus() {
    return mRealm.where(CarouselMenuModel.class).sort("id", Sort.ASCENDING).findAll();
  }
  
  public void saveCarouselMenuList(List<CarouselMenuModel> carouselMenuModelList,
      CallbackRealmObject object) {
    mRealm.executeTransactionAsync(new Transaction() {
      @Override
      public void execute(Realm realm) {
        realm.delete(CarouselMenuModel.class);
        realm.insertOrUpdate(carouselMenuModelList);
      }
    }, new OnSuccess() {
      @Override
      public void onSuccess() {
        object.getCallBack(true);
      }
    });
  }
  
  
  public int getNextCarouselMenuModelId() {
    Number currentIdNum = mRealm.where(CarouselMenuModel.class).max("id");
    int nextId;
    if (currentIdNum == null) {
      nextId = 1;
    } else {
      nextId = currentIdNum.intValue() + 1;
    }
    return nextId;
  }
}

package com.aandssoftware.aandsinventory.database;

import io.realm.Realm;

public class RealmManager {

    private static Realm mRealm;

    public static Realm open() {
        mRealm = Realm.getDefaultInstance();
        return mRealm;
    }

    public static void close() {
        if (mRealm != null) {
            mRealm.close();
        }
    }
  
  public static InventoryDao getInventoryDao() {
    checkForOpenRealm();
    return new InventoryDao(mRealm);
  }
  
  public static CarouselDao getCarouselDao() {
        checkForOpenRealm();
    return new CarouselDao(mRealm);
  }
  
  public static CustomerDao getCustomerDao() {
    checkForOpenRealm();
    return new CustomerDao(mRealm);
    }
  
  private static void checkForOpenRealm() {
        if (mRealm == null || mRealm.isClosed()) {
            throw new IllegalStateException("RealmManager: Realm is closed, call open() method first");
        }
    }
}

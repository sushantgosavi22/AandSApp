package com.aandssoftware.aandsinventory.application;

import android.app.Application;
import android.graphics.Typeface;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import com.aandssoftware.aandsinventory.utilities.FontFactory;

/**
 * Created by Veeresh on 3/11/17.
 */
public class RealmApplication extends Application {
  private static RealmApplication instance;
  private FontFactory mFontFactory;
  private static String REALM_DB_NAME = "aandsinfotech_aandsenterprise.db";
  
  public static RealmApplication getInstance() {
    if (instance == null) {
      return instance = new RealmApplication();
    } else {
      return instance;
    }
  }
  
  @Override
  public void onCreate() {
    super.onCreate();
    instance = this;
    initRealm();
  }
  
  private void initRealm() {
    Realm.init(this);
    RealmConfiguration realmConfiguration = new RealmConfiguration.Builder()
        .name(REALM_DB_NAME)
        .schemaVersion(1)
        .deleteRealmIfMigrationNeeded()
        .build();
    
    Realm.setDefaultConfiguration(realmConfiguration);
  }
  
  public Typeface getTypeFace(int type) {
    if (mFontFactory == null) {
      mFontFactory = new FontFactory(this);
    }
    
    switch (type) {
      case Constants.REGULAR:
        return mFontFactory.getRegular();
      
      case Constants.BOLD:
        return mFontFactory.getBold();
      
      case Constants.HEAVY:
        return mFontFactory.getHeavy();
      
      case Constants.SEMI_BOLD:
        return mFontFactory.getSemibold();
      
      default:
        return mFontFactory.getRegular();
    }
  }
  
  public interface Constants {
    
    int REGULAR = 1, BOLD = 2, SEMI_BOLD = 3, HEAVY = 4;
  }
}

package com.aandssoftware.aandsinventory.common;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;
import com.aandssoftware.aandsinventory.database.RealmManager;
import com.aandssoftware.aandsinventory.models.CallbackRealmObject;
import com.aandssoftware.aandsinventory.models.InventoryItemHistory;

public class Utils {
  
  
  public static void showToast(String message, Context context){
    Toast.makeText(context,message,Toast.LENGTH_LONG).show();
  }
  
  public static String isEmpty(String message) {
    return Utils.isEmpty(message, "");
  }
  
  public static String isEmpty(String message, String defaultVal) {
    return (null != message && !message.isEmpty()) ? message : defaultVal;
  }
}

package veeresh.a3c.realm.common;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;
import veeresh.a3c.realm.database.RealmManager;
import veeresh.a3c.realm.models.Task;
import veeresh.a3c.realm.models.callbackRealmObject;

public class Utils {
  
  
  public static void showToast(String message, Context context){
    Toast.makeText(context,message,Toast.LENGTH_LONG).show();
  }
  
  public static void deleteTask(Task task, Context context, callbackRealmObject object,int vehicalID){
    AlertDialog.Builder alertDialogBuilderUserInput =
        new AlertDialog.Builder(context);
    alertDialogBuilderUserInput
        .setTitle("Remove Task")
        .setMessage("Do you really want to delete task ?")
        .setCancelable(false)
        .setPositiveButton( "OK",
            new DialogInterface.OnClickListener() {
              public void onClick(DialogInterface dialogBox, int id) {
                RealmManager.recordsDao().removeTask(task.getId(),vehicalID,object);
              }
            })
        .setNegativeButton(
            "NO",
            new DialogInterface.OnClickListener() {
              public void onClick(DialogInterface dialogBox, int id) {
                dialogBox.cancel();
              }
            });
    
    final AlertDialog alertDialog = alertDialogBuilderUserInput.create();
    alertDialog.show();
  }
}

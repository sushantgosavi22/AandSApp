package com.aandssoftware.aandsinventory.common;

import android.content.Context;
import android.widget.Toast;
import java.text.DateFormat;
import java.util.Date;

public class DateUtils {
  //https://stackoverflow.com/questions/454315/how-do-you-format-date-and-time-in-android
  
  
  public static String getDateFormatted(long dateTimestamp) {
    Date date = new Date(dateTimestamp);
    //: Tuesday, April 12, 1952
    return DateFormat.getDateInstance(DateFormat.FULL).format(date);
  }
}

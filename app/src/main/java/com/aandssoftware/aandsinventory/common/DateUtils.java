package com.aandssoftware.aandsinventory.common;

import java.text.DateFormat;
import java.util.Date;

public class DateUtils {

  public static String getDateFormatted(long dateTimestamp) {
    Date date = new Date(dateTimestamp);
    return DateFormat.getDateInstance(DateFormat.FULL).format(date);
  }

  public static String getFormatedDatePickerFormat(long timeInMillies) {
    Date date = new Date(timeInMillies);
    return DateFormat.getDateInstance(DateFormat.SHORT).format(date);
  }

  public static long getLongFromDatePickerFormat(String dateFromDatePickerFormat){
    long result = System.currentTimeMillis();
    try{
      return DateFormat.getDateInstance(DateFormat.SHORT).parse(dateFromDatePickerFormat).getTime();
    }catch (Exception e){
      e.printStackTrace();
    }
    return result;
  }

}

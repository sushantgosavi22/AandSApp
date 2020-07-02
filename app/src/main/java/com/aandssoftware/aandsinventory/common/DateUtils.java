package com.aandssoftware.aandsinventory.common;

import android.content.Context;
import android.widget.Toast;

import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.util.IOUtils;

import java.io.FileInputStream;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {

  public static String getDateFormatted(long dateTimestamp) {
    Date date = new Date(dateTimestamp);
    //: Tuesday, April 12, 1952
    return DateFormat.getDateInstance(DateFormat.FULL).format(date);
  }

  public static String getFormatedDatePickerFormat(long timeInMillies) {
    Date date = new Date(timeInMillies);
    SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
    return format.format(date);
  }

}

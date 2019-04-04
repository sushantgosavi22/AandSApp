package veeresh.a3c.realm.ui;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.applandeo.materialcalendarview.CalendarUtils;
import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.DatePicker;
import com.applandeo.materialcalendarview.builders.DatePickerBuilder;
import com.applandeo.materialcalendarview.listeners.OnSelectDateListener;
import com.applandeo.materialcalendarview.utils.DateUtils;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import veeresh.a3c.realm.R;
import veeresh.a3c.realm.common.Utils;
import veeresh.a3c.realm.database.RealmManager;
import veeresh.a3c.realm.models.Task;
import veeresh.a3c.realm.models.callbackRealmObject;


public class AddTaskDialogFragment extends DialogFragment implements OnSelectDateListener,
    callbackRealmObject {
  
  private Task task;
  private callbackRealmObject callbackRealmObject;
  private boolean isForUpdate;
  private List<Calendar> selectedDays = new ArrayList<>();
  private TextView fromDate;
  private TextView toDate;
  private Calendar fromDateCal;
  private Calendar toDateCal;
  
  public AddTaskDialogFragment() {
    // Required empty public constructor
  }
  
  
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    RealmManager.open();
  }
  
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View rootView = inflater.inflate(R.layout.fragment_add_task_dialog, container, false);
    EditText edtContactName = (EditText) rootView.findViewById(R.id.edtContactName);
    EditText edtContactNumber = (EditText) rootView.findViewById(R.id.edtContactNumber);
    EditText edtCurrentPlace = (EditText) rootView.findViewById(R.id.edtCurrentPlace);
    EditText edtHour = (EditText) rootView.findViewById(R.id.edtHour);
    EditText edtDiesel = (EditText) rootView.findViewById(R.id.edtDiesel);
    EditText edtDesidedAmount = (EditText) rootView.findViewById(R.id.edtDesidedAmount);
    EditText edtPayedAmount = (EditText) rootView.findViewById(R.id.edtPayedAmount);
    EditText edtRemainingAmount = (EditText) rootView.findViewById(R.id.edtRemainingAmount);
    edtRemainingAmount.setEnabled(false);
    EditText edtDescription = (EditText) rootView.findViewById(R.id.edtDescription);
    LinearLayout llDatePicker = (LinearLayout) rootView.findViewById(R.id.llDatePicker);
    fromDate = (TextView) rootView.findViewById(R.id.fromDate);
    toDate = (TextView) rootView.findViewById(R.id.toDate);
    Button btnCancel = (Button) rootView.findViewById(R.id.btnCancel);
    btnCancel.setText("CANCEL");
    Button btnAction = (Button) rootView.findViewById(R.id.btnAction);
    btnAction.setText(isForUpdate ? "UPDATE" : "SAVE");
    
    if (isForUpdate) {
      edtContactName.setText(task.getContactName());
      edtContactNumber.setText(task.getContactNumber());
      edtCurrentPlace.setText(task.getWorkPlace());
      edtHour.setText(task.getHour());
      edtDiesel.setText(task.getDieselForTask());
      edtDesidedAmount.setText(task.getDecidedAmount());
      edtContactName.setText(task.getContactName());
      edtPayedAmount.setText(task.getPayedAmount());
      edtRemainingAmount.setText(task.getRemainingAmount());
      edtDescription.setText(task.getDesciption());
      
      long fromTimestamp = Long.valueOf(task.getFromDate());
      fromDateCal = Calendar.getInstance();
      fromDateCal.setTimeInMillis(fromTimestamp);
      fromDate.setText(CalendarUtils.getCalanderDisplayDate(fromDateCal.getTime()));
      
      long toTimestamp = Long.valueOf(task.getToDate());
      toDateCal = Calendar.getInstance();
      toDateCal.setTimeInMillis(toTimestamp);
      toDate.setText(CalendarUtils.getCalanderDisplayDate(toDateCal.getTime()));
      
      selectedDays.add(fromDateCal);
      if (fromDateCal.compareTo(toDateCal) != 0) {
        selectedDays.addAll(CalendarUtils.getDatesRange(fromDateCal, toDateCal));
      }
      selectedDays.add(toDateCal);
    }
    edtPayedAmount.addTextChangedListener(new TextWatcher() {
      @Override
      public void beforeTextChanged(CharSequence s, int start, int count, int after) {
      }
      
      @Override
      public void onTextChanged(CharSequence s, int start, int before, int count) {
        try {
          int payedAmount = Integer.valueOf("" + s);
          int desidedAmount = Integer.valueOf(edtDesidedAmount.getText().toString());
          int remainingAmount = desidedAmount - payedAmount;
          if (remainingAmount >= 0) {
            edtRemainingAmount.setText("" + remainingAmount);
          }
          
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
      
      @Override
      public void afterTextChanged(Editable s) {
      }
    });
    llDatePicker.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        openRangePicker();
      }
    });
    AddTaskDialogFragment fragment = this;
    callbackRealmObject object = this;
    btnAction.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        if (!edtContactName.getText().toString().isEmpty()) {
          if (!edtContactNumber.getText().toString().isEmpty()) {
            if (!edtCurrentPlace.getText().toString().isEmpty()) {
              if (!edtDesidedAmount.getText().toString().isEmpty()) {
                if (!fromDate.getText().toString().isEmpty()) {
                  int id = RealmManager.recordsDao().getNextTaskID();
                  id = isForUpdate ? task.getId() : id;
                  Task task = new Task();
                  task.setId(id);
                  task.setContactName(edtContactName.getText().toString());
                  task.setContactNumber(edtContactNumber.getText().toString());
                  task.setWorkPlace(edtCurrentPlace.getText().toString());
                  task.setHour(edtHour.getText().toString());
                  task.setDieselForTask(edtDiesel.getText().toString());
                  task.setPayedAmount(edtPayedAmount.getText().toString());
                  task.setDecidedAmount(edtDesidedAmount.getText().toString());
                  task.setRemainingAmount(edtRemainingAmount.getText().toString());
                  task.setDesciption(edtDescription.getText().toString());
                  task.setCurrentDate(String.valueOf(Calendar.getInstance().getTimeInMillis()));
                  task.setCurrentTimestamp(Calendar.getInstance().getTimeInMillis());
                  task.setFromDate(String.valueOf(fromDateCal.getTimeInMillis()));
                  task.setToDate(String.valueOf(toDateCal.getTimeInMillis()));
                  task.setVehicalId(Integer.valueOf(fragment.getTag()));
                  if (null != edtRemainingAmount.getText().toString() &&
                      edtRemainingAmount.getText().toString().length() > 0
                      && Integer.parseInt(edtRemainingAmount.getText().toString()) == 0) {
                    task.setPaymentRemain(false);
                  } else {
                    task.setPaymentRemain(true);
                  }
                  RealmManager.recordsDao()
                      .saveTask(task, Integer.valueOf(fragment.getTag()), object);
                } else {
                  Utils.showToast("Please select dates", getActivity());
                }
                
              } else {
                Utils.showToast("Please enter amout of work done", getActivity());
              }
            } else {
              Utils.showToast("Please enter work location", getActivity());
            }
          } else {
            Utils.showToast("Please enter contact person number", getActivity());
          }
        } else {
          Utils.showToast("Please enter contact person name", getActivity());
        }
        
      }
    });
    
    btnCancel.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        dismiss();
      }
    });
    
    return rootView;
  }
  
  
  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    Dialog dialog = super.onCreateDialog(savedInstanceState);
    dialog.setCancelable(false);
    return dialog;
  }
  
  public Task getTask() {
    return task;
  }
  
  public AddTaskDialogFragment setTask(Task task) {
    this.task = task;
    if (null != task) {
      isForUpdate = true;
    }
    return this;
  }
  
  public veeresh.a3c.realm.models.callbackRealmObject getCallbackRealmObject() {
    return callbackRealmObject;
  }
  
  public AddTaskDialogFragment setCallbackRealmObject(
      veeresh.a3c.realm.models.callbackRealmObject callbackRealmObject) {
    this.callbackRealmObject = callbackRealmObject;
    return this;
  }
  
  private void openRangePicker() {
    
    DatePickerBuilder rangeBuilder = new DatePickerBuilder(getActivity(), this)
        .pickerType(CalendarView.RANGE_PICKER)
        .headerColor(R.color.sampleDark)
        .abbreviationsBarColor(R.color.sampleLight)
        .abbreviationsLabelsColor(android.R.color.white)
        .pagesColor(R.color.sampleLighter)
        .selectionColor(android.R.color.white)
        .selectionLabelColor(R.color.sampleDark)
        .todayLabelColor(R.color.dialogAccent)
        .dialogButtonsColor(android.R.color.white)
        .daysLabelsColor(android.R.color.white)
        .anotherMonthsDaysLabelsColor(R.color.sampleLighter)
        .selectedDays(selectedDays);
    
    
    /*DatePickerBuilder rangeBuilder = new DatePickerBuilder(getActivity(), this)
        .pickerType(CalendarView.RANGE_PICKER)
        .headerColor(R.color.sampleDark)
        .abbreviationsBarColor(R.color.sampleLight)
        .abbreviationsLabelsColor(android.R.color.white)
        .pagesColor(R.color.sampleLighter)
        .selectionColor(android.R.color.white)
        .selectionLabelColor(R.color.sampleDark)
        .todayLabelColor(R.color.dialogAccent)
        .dialogButtonsColor(android.R.color.white)
        .daysLabelsColor(android.R.color.white)
        .anotherMonthsDaysLabelsColor(R.color.sampleLighter)
        .selectedDays(selectedDays)
        .disabledDays(getDisabledDays());*/
    
    DatePicker rangePicker = rangeBuilder.build();
    rangePicker.show();
  }
  
  private List<Calendar> getDisabledDays() {
    Calendar firstDisabled = DateUtils.getCalendar();
    firstDisabled.add(Calendar.DAY_OF_MONTH, 2);
    
    Calendar secondDisabled = DateUtils.getCalendar();
    secondDisabled.add(Calendar.DAY_OF_MONTH, 1);
    
    Calendar thirdDisabled = DateUtils.getCalendar();
    thirdDisabled.add(Calendar.DAY_OF_MONTH, 18);
    
    List<Calendar> calendars = new ArrayList<>();
    calendars.add(firstDisabled);
    calendars.add(secondDisabled);
    calendars.add(thirdDisabled);
    return calendars;
  }
  
  @Override
  public void onSelect(List<Calendar> calendars) {
    
    String tost = "";
    if (calendars.size() > 0) {
      fromDateCal = calendars.get(0);
      toDateCal = calendars.get(calendars.size() - 1);
      fromDate.setText(CalendarUtils.getCalanderDisplayDate(calendars.get(0).getTime()));
      toDate.setText(CalendarUtils
          .getCalanderDisplayDate(calendars.get(calendars.size() - 1).getTime()));
      tost = "From " + CalendarUtils.getCalanderDisplayDate(calendars.get(0).getTime()) + " To "
          + CalendarUtils.getCalanderDisplayDate(calendars.get(calendars.size() - 1).getTime());
    }
    Toast.makeText(getActivity(), tost, Toast.LENGTH_SHORT).show();
    /*Stream.of(calendars).forEach(calendar ->
        Toast.makeText(getActivity(),
            calendar.getTime().toString(),
            Toast.LENGTH_SHORT).show());*/
  }
  
  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    
    
  }
  
  @Override
  public void onDestroy() {
    super.onDestroy();
    RealmManager.close();
  }
  
  @Override
  public void getCallBack(boolean result) {
    if (result) {
      Utils.showToast("Task save successfully", getActivity());
      callbackRealmObject.getCallBack(result);
      dismiss();
    }
  }
}

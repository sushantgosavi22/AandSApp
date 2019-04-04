
package veeresh.a3c.realm.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import io.realm.RealmObject;
import io.realm.TaskRealmProxy;
import io.realm.annotations.PrimaryKey;
import org.parceler.Parcel;

@Parcel(implementations = {TaskRealmProxy.class},
    value = Parcel.Serialization.BEAN,
    analyze = {Task.class})
public class Task extends RealmObject {
  
  
  @SerializedName("id")
  @Expose
  @PrimaryKey
  private int id;
  @SerializedName("contactName")
  @Expose
  private String contactName;
  
  @SerializedName("contactNumber")
  @Expose
  private String contactNumber;
  
  
  @SerializedName("fromDate")
  @Expose
  private String fromDate;
  @SerializedName("toDate")
  @Expose
  private String toDate;
  
  @SerializedName("currentDate")
  @Expose
  private String currentDate;
  
  @SerializedName("currentTimestamp")
  @Expose
  private long currentTimestamp;
  
  @SerializedName("hour")
  @Expose
  private String hour;
  
  @SerializedName("dieselForTask")
  @Expose
  private String dieselForTask;
  @SerializedName("decidedAmount")
  @Expose
  private String decidedAmount;
  @SerializedName("payedAmount")
  @Expose
  private String payedAmount;
  @SerializedName("remainingAmount")
  @Expose
  private String remainingAmount;
  @SerializedName("advanceToDriver")
  @Expose
  private String advanceToDriver;
  
  @SerializedName("desciption")
  @Expose
  private String desciption;
  
  @SerializedName("workPlace")
  @Expose
  private String workPlace;
  
  private boolean isPaymentRemain;
  
  @SerializedName("vehicalId")
  @Expose
  private int vehicalId;
  
  public Task() {
  
  }
  
  public int getVehicalId() {
    return vehicalId;
  }
  
  public Task setVehicalId(int vehicalId) {
    this.vehicalId = vehicalId;
    return this;
  }
  
  public Task(int id) {
    this.id = id;
  }
  
  public int getId() {
    return id;
  }
  
  public Task setId(int id) {
    this.id = id;
    return this;
  }
  
  public String getDesciption() {
    return desciption;
  }
  
  public Task setDesciption(String desciption) {
    this.desciption = desciption;
    return this;
  }
  
  public String getWorkPlace() {
    return workPlace;
  }
  
  public Task setWorkPlace(String workPlace) {
    this.workPlace = workPlace;
    return this;
  }
  
  public String getContactName() {
    return contactName;
  }
  
  public Task setContactName(String contactName) {
    this.contactName = contactName;
    return this;
  }
  
  public String getFromDate() {
    return fromDate;
  }
  
  public Task setFromDate(String fromDate) {
    this.fromDate = fromDate;
    return this;
  }
  
  public String getToDate() {
    return toDate;
  }
  
  public String getContactNumber() {
    return contactNumber;
  }
  
  public Task setContactNumber(String contactNumber) {
    this.contactNumber = contactNumber;
    return this;
  }
  
  public Task setToDate(String toDate) {
    this.toDate = toDate;
    return this;
  }
  
  public String getCurrentDate() {
    return currentDate;
  }
  
  public Task setCurrentDate(String currentDate) {
    this.currentDate = currentDate;
    return this;
  }
  
  public long getCurrentTimestamp() {
    return currentTimestamp;
  }
  
  public Task setCurrentTimestamp(long currentTimestamp) {
    this.currentTimestamp = currentTimestamp;
    return this;
  }
  
  public String getDieselForTask() {
    return dieselForTask;
  }
  
  public Task setDieselForTask(String dieselForTask) {
    this.dieselForTask = dieselForTask;
    return this;
  }
  
  public String getDecidedAmount() {
    return decidedAmount;
  }
  
  public Task setDecidedAmount(String decidedAmount) {
    this.decidedAmount = decidedAmount;
    return this;
  }
  
  public String getPayedAmount() {
    return payedAmount;
  }
  
  public Task setPayedAmount(String payedAmount) {
    this.payedAmount = payedAmount;
    return this;
  }
  
  public String getRemainingAmount() {
    return remainingAmount;
  }
  
  public Task setRemainingAmount(String remainingAmount) {
    this.remainingAmount = remainingAmount;
    return this;
  }
  
  public String getAdvanceToDriver() {
    return advanceToDriver;
  }
  
  public Task setAdvanceToDriver(String advanceToDriver) {
    this.advanceToDriver = advanceToDriver;
    return this;
  }
  
  public boolean isPaymentRemain() {
    return isPaymentRemain;
  }
  
  public Task setPaymentRemain(boolean paymentRemain) {
    isPaymentRemain = paymentRemain;
    return this;
  }
  
  public String getHour() {
    return hour;
  }
  
  public Task setHour(String hour) {
    this.hour = hour;
    return this;
  }
}

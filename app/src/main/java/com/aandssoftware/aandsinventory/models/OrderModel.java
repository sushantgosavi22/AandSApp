
package com.aandssoftware.aandsinventory.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class OrderModel extends RealmObject {
  
  @SerializedName("id")
  @Expose
  @PrimaryKey
  private int id;
  
  @SerializedName("customerModel")
  @Expose
  private CustomerModel customerModel;
  
  @SerializedName("orderId")
  @Expose
  private int orderId;
  
  @SerializedName("orderItems")
  @Expose
  private RealmList<InventoryItem> orderItems = new RealmList<InventoryItem>();
  
  @SerializedName("orderDescription")
  @Expose
  private String orderDescription;
  
  @SerializedName("orderDateCreated")
  @Expose
  private long orderDateCreated;
  
  @SerializedName("orderDateUpdated")
  @Expose
  private long orderDateUpdated;
  
  @SerializedName("orderCompletedDate")
  @Expose
  private long orderCompletedDate;
  
  @SerializedName("orderDeliveryDate")
  @Expose
  private long orderDeliveryDate;
  
  @SerializedName("orderStatus")
  @Expose
  private String orderStatus;
  
  @SerializedName("orderStatusName")
  @Expose
  private String orderStatusName;
  
  @SerializedName("orderContact")
  @Expose
  private String orderContact;
  
  @SerializedName("orderNumber")
  @Expose
  private String orderNumber;
  
  @SerializedName("chalanNumber")
  @Expose
  private String chalanNumber;
  
  @SerializedName("invoiceNumber")
  @Expose
  private String invoiceNumber;
  
  @SerializedName("invoiceDate")
  @Expose
  private long invoiceDate;
  
  @SerializedName("dateOfSupply")
  @Expose
  private long dateOfSupply;
  
  @SerializedName("code")
  @Expose
  private int code;
  
  @SerializedName("discount")
  @Expose
  private int discount;
  
  @SerializedName("extraCharges")
  @Expose
  private int extraCharges;
  
  @SerializedName("extraChargesDescription")
  @Expose
  private String extraChargesDescription;
  
  @SerializedName("gstTotalPercentage")
  @Expose
  private int gstTotalPercentage;
  
  @SerializedName("gstTotalAmount")
  @Expose
  private int gstTotalAmount;
  
  @SerializedName("totalTaxableAmount")
  @Expose
  private int totalTaxableAmount;
  
  @SerializedName("finalBillAmount")
  @Expose
  private int finalBillAmount;
  
  public OrderModel() {
  
  }
  
  public OrderModel(OrderModel orderModel) {
    this.id = orderModel.getId();
    this.orderId = orderModel.getOrderId();
    this.customerModel = orderModel.getCustomerModel();
    this.orderItems = orderModel.getOrderItems();
    this.orderDescription = orderModel.getOrderDescription();
    this.orderDateCreated = orderModel.getOrderDateCreated();
    this.orderDateUpdated = orderModel.getOrderDateUpdated();
    this.orderCompletedDate = orderModel.getOrderCompletedDate();
    this.orderDeliveryDate = orderModel.getOrderDeliveryDate();
    this.orderStatus = orderModel.getOrderStatus();
    this.orderStatusName = orderModel.getOrderStatusName();
    this.orderContact = orderModel.getOrderContact();
    this.orderNumber = orderModel.getOrderNumber();
    this.chalanNumber = orderModel.getChalanNumber();
    this.invoiceNumber = orderModel.getInvoiceNumber();
    this.chalanNumber = orderModel.getChalanNumber();
    this.discount = orderModel.getDiscount();
    this.extraCharges = orderModel.getExtraCharges();
    this.extraChargesDescription = orderModel.getExtraChargesDescription();
    this.gstTotalPercentage = orderModel.getGstTotalPercentage();
    this.gstTotalAmount = orderModel.getGstTotalAmount();
    this.finalBillAmount = orderModel.getFinalBillAmount();
    this.invoiceDate = orderModel.getInvoiceDate();
    this.dateOfSupply = orderModel.getDateOfSupply();
    this.code = orderModel.getCode();
    this.totalTaxableAmount = orderModel.getTotalTaxableAmount();
  }
  
  public RealmList<InventoryItem> getOrderItems() {
    return orderItems;
  }
  
  public OrderModel setOrderItems(
      RealmList<InventoryItem> orderItems) {
    this.orderItems = orderItems;
    return this;
  }
  
  public int getOrderId() {
    return orderId;
  }
  
  public OrderModel setOrderId(int orderId) {
    this.orderId = orderId;
    return this;
  }
  
  public String getOrderDescription() {
    return orderDescription;
  }
  
  public OrderModel setOrderDescription(String orderDescription) {
    this.orderDescription = orderDescription;
    return this;
  }
  
  public long getOrderDateCreated() {
    return orderDateCreated;
  }
  
  public OrderModel setOrderDateCreated(long orderDateCreated) {
    this.orderDateCreated = orderDateCreated;
    return this;
  }
  
  public long getOrderDateUpdated() {
    return orderDateUpdated;
  }
  
  public OrderModel setOrderDateUpdated(long orderDateUpdated) {
    this.orderDateUpdated = orderDateUpdated;
    return this;
  }
  
  public long getOrderCompletedDate() {
    return orderCompletedDate;
  }
  
  public OrderModel setOrderCompletedDate(long orderCompletedDate) {
    this.orderCompletedDate = orderCompletedDate;
    return this;
  }
  
  public long getOrderDeliveryDate() {
    return orderDeliveryDate;
  }
  
  public OrderModel setOrderDeliveryDate(long orderDeliveryDate) {
    this.orderDeliveryDate = orderDeliveryDate;
    return this;
  }
  
  public String getOrderStatus() {
    return orderStatus;
  }
  
  public OrderModel setOrderStatus(String orderStatus) {
    this.orderStatus = orderStatus;
    return this;
  }
  
  public String getOrderStatusName() {
    return orderStatusName;
  }
  
  public OrderModel setOrderStatusName(String orderStatusName) {
    this.orderStatusName = orderStatusName;
    return this;
  }
  
  public String getOrderContact() {
    return orderContact;
  }
  
  public OrderModel setOrderContact(String orderContact) {
    this.orderContact = orderContact;
    return this;
  }
  
  public String getOrderNumber() {
    return orderNumber;
  }
  
  public OrderModel setOrderNumber(String orderNumber) {
    this.orderNumber = orderNumber;
    return this;
  }
  
  public String getChalanNumber() {
    return chalanNumber;
  }
  
  public OrderModel setChalanNumber(String chalanNumber) {
    this.chalanNumber = chalanNumber;
    return this;
  }
  
  public String getInvoiceNumber() {
    return invoiceNumber;
  }
  
  public OrderModel setInvoiceNumber(String invoiceNumber) {
    this.invoiceNumber = invoiceNumber;
    return this;
  }
  
  public int getDiscount() {
    return discount;
  }
  
  public OrderModel setDiscount(int discount) {
    this.discount = discount;
    return this;
  }
  
  public int getExtraCharges() {
    return extraCharges;
  }
  
  public OrderModel setExtraCharges(int extraCharges) {
    this.extraCharges = extraCharges;
    return this;
  }
  
  public String getExtraChargesDescription() {
    return extraChargesDescription;
  }
  
  public OrderModel setExtraChargesDescription(String extraChargesDescription) {
    this.extraChargesDescription = extraChargesDescription;
    return this;
  }
  
  public int getGstTotalPercentage() {
    return gstTotalPercentage;
  }
  
  public OrderModel setGstTotalPercentage(int gstTotalPercentage) {
    this.gstTotalPercentage = gstTotalPercentage;
    return this;
  }
  
  public int getGstTotalAmount() {
    return gstTotalAmount;
  }
  
  public OrderModel setGstTotalAmount(int gstTotalAmount) {
    this.gstTotalAmount = gstTotalAmount;
    return this;
  }
  
  public int getFinalBillAmount() {
    return finalBillAmount;
  }
  
  public OrderModel setFinalBillAmount(int finalBillAmount) {
    this.finalBillAmount = finalBillAmount;
    return this;
  }
  
  
  public long getInvoiceDate() {
    return invoiceDate;
  }
  
  public OrderModel setInvoiceDate(long invoiceDate) {
    this.invoiceDate = invoiceDate;
    return this;
  }
  
  public long getDateOfSupply() {
    return dateOfSupply;
  }
  
  public OrderModel setDateOfSupply(long dateOfSupply) {
    this.dateOfSupply = dateOfSupply;
    return this;
  }
  
  public int getCode() {
    return code;
  }
  
  public OrderModel setCode(int code) {
    this.code = code;
    return this;
  }
  
  public int getTotalTaxableAmount() {
    return totalTaxableAmount;
  }
  
  public OrderModel setTotalTaxableAmount(int totalTaxableAmount) {
    this.totalTaxableAmount = totalTaxableAmount;
    return this;
  }
  
  public int getId() {
    return id;
  }
  
  public OrderModel setId(int id) {
    this.id = id;
    return this;
  }
  
  public CustomerModel getCustomerModel() {
    return customerModel;
  }
  
  public OrderModel setCustomerModel(
      CustomerModel customerModel) {
    this.customerModel = customerModel;
    return this;
  }
}

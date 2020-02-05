
package com.aandssoftware.aandsinventory.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.HashMap;

public class CustomerModel implements Serializable {
  
  public static final String TABLE_CUSTOMER = "customer";
  public static final String CUSTOMER_COUNTER = "customerCounter";
  public static final String ORDER_BY_VALUE = "customerID";
  public static final String LOGIN_ORDER_BY_VALUE = "username";
  public static final String PASSWORD = "password";
  public static final String PERMISSION = "permission";
  
  @SerializedName("id")
  @Expose
  private String id;
  
  @SerializedName("customerID")
  @Expose
  private String customerID;
  
  @SerializedName("invoiceNumber")
  @Expose
  private String invoiceNumber;
  
  @SerializedName("customerName")
  @Expose
  private String customerName;
  
  @SerializedName("companyMail")
  @Expose
  private String companyMail;
  
  @SerializedName("customerGstNumber")
  @Expose
  private String customerGstNumber;
  
  @SerializedName("address")
  @Expose
  private String address;
  
  @SerializedName("customerNumber")
  @Expose
  private String customerNumber;
  
  @SerializedName("contactPerson")
  @Expose
  private String contactPerson;
  
  @SerializedName("contactPersonNumber")
  @Expose
  private String contactPersonNumber;
  
  @SerializedName("alternateNumber")
  @Expose
  private String alternateNumber;
  
  @SerializedName("description")
  @Expose
  private String description;
  
  @SerializedName("dateCreated")
  @Expose
  private long dateCreated;
  
  @SerializedName("invoiceCreatedDate")
  @Expose
  private long invoiceCreatedDate;
  
  @SerializedName("dueDate")
  @Expose
  private long dueDate;
  
  @SerializedName("imagePath")
  @Expose
  private String imagePath;
  
  @SerializedName("requirement")
  @Expose
  private String requirement;
  
  @SerializedName("tag")
  @Expose
  private String tag;
  
  @SerializedName("username")
  @Expose
  private String username;
  
  @SerializedName("password")
  @Expose
  private String password;
  
  @SerializedName("permission")
  @Expose
  private HashMap<String, String> permission;
  
  public CustomerModel() {
  
  }
  
  public String getId() {
    return id;
  }
  
  public CustomerModel setId(String id) {
    this.id = id;
    return this;
  }
  
  public String getCustomerName() {
    return customerName;
  }
  
  public CustomerModel setCustomerName(String customerName) {
    this.customerName = customerName;
    return this;
  }
  
  public String getCompanyMail() {
    return companyMail;
  }
  
  public CustomerModel setCompanyMail(String companyMail) {
    this.companyMail = companyMail;
    return this;
  }
  
  public String getCustomerGstNumber() {
    return customerGstNumber;
  }
  
  public CustomerModel setCustomerGstNumber(String customerGstNumber) {
    this.customerGstNumber = customerGstNumber;
    return this;
  }
  
  public String getAddress() {
    return address;
  }
  
  public CustomerModel setAddress(String address) {
    this.address = address;
    return this;
  }
  
  public String getCustomerNumber() {
    return customerNumber;
  }
  
  public CustomerModel setCustomerNumber(String customerNumber) {
    this.customerNumber = customerNumber;
    return this;
  }
  
  public String getContactPerson() {
    return contactPerson;
  }
  
  public CustomerModel setContactPerson(String contactPerson) {
    this.contactPerson = contactPerson;
    return this;
  }
  
  public String getContactPersonNumber() {
    return contactPersonNumber;
  }
  
  public CustomerModel setContactPersonNumber(String contactPersonNumber) {
    this.contactPersonNumber = contactPersonNumber;
    return this;
  }
  
  public String getAlternateNumber() {
    return alternateNumber;
  }
  
  public CustomerModel setAlternateNumber(String alternateNumber) {
    this.alternateNumber = alternateNumber;
    return this;
  }
  
  public String getDescription() {
    return description;
  }
  
  public CustomerModel setDescription(String description) {
    this.description = description;
    return this;
  }
  
  public long getDateCreated() {
    return dateCreated;
  }
  
  public CustomerModel setDateCreated(long dateCreated) {
    this.dateCreated = dateCreated;
    return this;
  }
  
  public String getImagePath() {
    return imagePath;
  }
  
  public CustomerModel setImagePath(String imagePath) {
    this.imagePath = imagePath;
    return this;
  }
  
  public String getCustomerID() {
    return customerID;
  }
  
  public CustomerModel setCustomerID(String customerID) {
    this.customerID = customerID;
    return this;
  }
  
  public String getRequirement() {
    return requirement;
  }
  
  public void setRequirement(String requirement) {
    this.requirement = requirement;
  }
  
  public String getTag() {
    return tag;
  }
  
  public void setTag(String tag) {
    this.tag = tag;
  }
  
  public String getInvoiceNumber() {
    return invoiceNumber;
  }
  
  public void setInvoiceNumber(String invoiceNumber) {
    this.invoiceNumber = invoiceNumber;
  }
  
  public long getInvoiceCreatedDate() {
    return invoiceCreatedDate;
  }
  
  public void setInvoiceCreatedDate(long invoiceCreatedDate) {
    this.invoiceCreatedDate = invoiceCreatedDate;
  }
  
  public long getDueDate() {
    return dueDate;
  }
  
  public void setDueDate(long dueDate) {
    this.dueDate = dueDate;
  }
  
  public String getUsername() {
    return username;
  }
  
  public void setUsername(String username) {
    this.username = username;
  }
  
  public String getPassword() {
    return password;
  }
  
  public void setPassword(String password) {
    this.password = password;
  }
  
  public HashMap<String, String> getPermission() {
    return permission;
  }
  
  public void setPermission(HashMap<String, String> permission) {
    this.permission = permission;
  }
}

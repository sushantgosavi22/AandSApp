
package com.aandssoftware.aandsinventory.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class CustomerModel extends RealmObject {
  @SerializedName("id")
  @Expose
  @PrimaryKey
  private int id;
  
  @SerializedName("customerID")
  @Expose
  private int customerID;
  
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
  
  @SerializedName("imagePath")
  @Expose
  private String imagePath;
  
  public CustomerModel() {
  
  }
  
  public CustomerModel(CustomerModel item) {
    this.id = item.getId();
    this.customerID = item.getCustomerID();
    this.customerName = item.getCustomerName();
    this.companyMail = item.getCompanyMail();
    this.customerGstNumber = item.getCustomerGstNumber();
    this.address = item.getAddress();
    this.customerNumber = item.getCustomerNumber();
    this.contactPerson = item.getContactPerson();
    this.contactPersonNumber = item.getContactPersonNumber();
    this.alternateNumber = item.getAlternateNumber();
    this.description = item.getDescription();
    this.dateCreated = item.getDateCreated();
    this.imagePath = item.getImagePath();
  }
  
  public int getId() {
    return id;
  }
  
  public CustomerModel setId(int id) {
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
  
  public int getCustomerID() {
    return customerID;
  }
  
  public CustomerModel setCustomerID(int customerID) {
    this.customerID = customerID;
    return this;
  }
}

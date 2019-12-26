
package com.aandssoftware.aandsinventory.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class InventoryItemHistory extends RealmObject {
  
  @SerializedName("id")
  @Expose
  @PrimaryKey
  private int id;
  @SerializedName("inventoryItemId")
  @Expose
  private int inventoryItemId;
  @SerializedName("modifiedParameter")
  @Expose
  private String modifiedParameter;
  
  @SerializedName("action")
  @Expose
  private String action;
  @SerializedName("modifiedFrom")
  @Expose
  private String modifiedFrom;
  
  @SerializedName("modifiedTo")
  @Expose
  private String modifiedTo;
  
  @SerializedName("description")
  @Expose
  private String description;
  
  @SerializedName("modifiedDate")
  @Expose
  private long modifiedDate;
  
  public InventoryItemHistory() {
  
  }
  
  public InventoryItemHistory(InventoryItemHistory inventoryItemHistory) {
    this.id = inventoryItemHistory.getId();
    this.inventoryItemId = inventoryItemHistory.getInventoryItemId();
    this.modifiedParameter = inventoryItemHistory.getModifiedParameter();
    this.modifiedFrom = inventoryItemHistory.getModifiedFrom();
    this.modifiedTo = inventoryItemHistory.getModifiedTo();
    this.description = inventoryItemHistory.getDescription();
    this.modifiedDate = inventoryItemHistory.getModifiedDate();
    this.action = inventoryItemHistory.getAction();
  }
  
  public int getId() {
    return id;
  }
  
  public InventoryItemHistory setId(int id) {
    this.id = id;
    return this;
  }
  
  public int getInventoryItemId() {
    return inventoryItemId;
  }
  
  public InventoryItemHistory setInventoryItemId(int inventoryItemId) {
    this.inventoryItemId = inventoryItemId;
    return this;
  }
  
  public String getModifiedParameter() {
    return modifiedParameter;
  }
  
  public InventoryItemHistory setModifiedParameter(String modifiedParameter) {
    this.modifiedParameter = modifiedParameter;
    return this;
  }
  
  public String getModifiedFrom() {
    return modifiedFrom;
  }
  
  public InventoryItemHistory setModifiedFrom(String modifiedFrom) {
    this.modifiedFrom = modifiedFrom;
    return this;
  }
  
  public String getModifiedTo() {
    return modifiedTo;
  }
  
  public InventoryItemHistory setModifiedTo(String modifiedTo) {
    this.modifiedTo = modifiedTo;
    return this;
  }
  
  public String getDescription() {
    return description;
  }
  
  public InventoryItemHistory setDescription(String description) {
    this.description = description;
    return this;
  }
  
  public long getModifiedDate() {
    return modifiedDate;
  }
  
  public InventoryItemHistory setModifiedDate(long modifiedDate) {
    this.modifiedDate = modifiedDate;
    return this;
  }
  
  public String getAction() {
    return action;
  }
  
  public InventoryItemHistory setAction(String action) {
    this.action = action;
    return this;
  }
}

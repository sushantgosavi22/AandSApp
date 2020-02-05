package com.aandssoftware.aandsinventory.models;

public enum OrderStatus {
  CREATED("Created"),
  CONFIRM("Confirm"),
  PENDING("Pending"),
  DELIVERED("Delivered"),
  PAYMENT("Payment"),
  FINISH("Finish");
  
  private final String status;
  
  OrderStatus(String status) {
    this.status = status;
  }
  
  @Override
  public String toString() {
    return status;
  }
}

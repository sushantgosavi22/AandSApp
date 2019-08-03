package com.aandssoftware.aandsinventory.models;

public enum CarouselMenuType {
  
  ORDERS("ORDERS"),
  MATERIALS("MATERIALS"),
  CUSTOMERS("CUSTOMERS"),
  INVENTORY_HISTORY("INVENTORY_HISTORY"),
  INVENTORY("INVENTORY");
  
  String orders;
  
  CarouselMenuType(String orders) {
    this.orders = orders;
  }
  
  public String getOrders() {
    return orders;
  }
}

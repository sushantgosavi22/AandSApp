package com.aandssoftware.aandsinventory.models;

public enum CarouselMenuType {
  
  ORDERS("ORDERS"),
  MATERIALS("MATERIALS"),
  CUSTOMERS("CUSTOMERS"),
  INVENTORY_HISTORY("INVENTORY_HISTORY"),
  INVENTORY("INVENTORY"),
  COMPANY_ORDER("COMPANY_ORDER"),
  COMPANY_PROFILE("COMPANY_PROFILE"),
  COMPANY_MATERIALS("COMPANY_MATERIALS"),
  ABOUT_US("ABOUT_US"),
  ADMIN_PANNEL("ADMIN_PANNEL"),
  FEEDBACK("FEEDBACK");

  String orders;
  
  CarouselMenuType(String orders) {
    this.orders = orders;
  }
  
  public String getOrders() {
    return orders;
  }
}

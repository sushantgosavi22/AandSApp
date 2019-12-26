package com.aandssoftware.aandsinventory.listing;

import com.aandssoftware.aandsinventory.ui.ListingActivity;

public enum ListType {
  LIST_TYPE_INVENTORY {
    @Override
    public ListingOperations getInstance(ListingActivity activity) {
      return new InventoryListAdapter(activity);
    }
  },
  LIST_TYPE_MATERIAL {
    @Override
    public ListingOperations getInstance(ListingActivity activity) {
      return new InventoryListAdapter(activity);
    }
  },
  LIST_TYPE_CUSTOMERS {
    @Override
    public ListingOperations getInstance(ListingActivity activity) {
      return new CustomerListAdapter(activity);
    }
  },
  LIST_TYPE_INVENTORY_HISTORY {
    @Override
    public ListingOperations getInstance(ListingActivity activity) {
      return new InventoryHistoryListAdapter(activity);
    }
  },
  LIST_TYPE_ORDER {
    @Override
    public ListingOperations getInstance(ListingActivity activity) {
      return new OrderListAdapter(activity);
    }
  },
  
  LIST_TYPE_ORDER_INVENTORY {
    @Override
    public ListingOperations getInstance(ListingActivity activity) {
      return new OrderDetailsListAdapter(activity);
    }
  };
  
  public abstract ListingOperations getInstance(ListingActivity activity);
}

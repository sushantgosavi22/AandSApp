package com.aandssoftware.aandsinventory.listing

import com.aandssoftware.aandsinventory.ui.activity.ListingActivity

enum class ListType {
    LIST_TYPE_INVENTORY {
        override fun getInstance(activity: ListingActivity): ListingOperations {
            return InventoryListAdapter(activity)
        }
    },
    LIST_TYPE_MATERIAL {
        override fun getInstance(activity: ListingActivity): ListingOperations {
            return InventoryListAdapter(activity)
        }
    },
    LIST_TYPE_CUSTOMERS {
        override fun getInstance(activity: ListingActivity): ListingOperations {
            return CustomerListAdapter(activity)
        }
    },
    LIST_TYPE_INVENTORY_HISTORY {
        override fun getInstance(activity: ListingActivity): ListingOperations {
            return InventoryHistoryListAdapter(activity)
        }
    },
    LIST_TYPE_ORDER {
        override fun getInstance(activity: ListingActivity): ListingOperations {
            return OrderListAdapter(activity)
        }
    },

    LIST_TYPE_ORDER_INVENTORY {
        override fun getInstance(activity: ListingActivity): ListingOperations {
            return OrderDetailsListAdapter(activity)
        }
    },


    LIST_TYPE_COMPANY_ORDER {
        override fun getInstance(activity: ListingActivity): ListingOperations {
            return CompanyOrderListAdapter(activity)
        }
    },

    LIST_TYPE_COMPANY_MATERIALS {
        override fun getInstance(activity: ListingActivity): ListingOperations {
            return OrderListAdapter(activity)
        }
    };

    abstract fun getInstance(activity: ListingActivity): ListingOperations
}

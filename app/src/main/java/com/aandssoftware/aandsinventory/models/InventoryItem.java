
package com.aandssoftware.aandsinventory.models;

import com.aandssoftware.aandsinventory.database.RealmManager;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import io.realm.InventoryItemRealmProxy;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.Index;
import io.realm.annotations.PrimaryKey;
import java.util.HashMap;
import org.parceler.Parcel;

@org.parceler.Parcel(implementations = {InventoryItemRealmProxy.class},
    value = Parcel.Serialization.BEAN,
    analyze = {InventoryItem.class})
public class InventoryItem extends RealmObject {
  
  public static final String DEFAULT_QUANTITY_UNIT = "Pec";
  public static final String DEFAULT_BRAND_NAME = "NA";
  public static final String DEFAULT_MODEL_NAME = "NA";
  
  
  public static final String ID = "id";
  public static final String INVENTORY_ITEM_IMAGE_PATH = "Product Image";
  public static final String INVENTORY_ITEM_NAME = "Product Name";
  public static final String INVENTORY_ITEM_PURCHASE_PRICE = "Product Purchase Price";
  public static final String INVENTORY_ITEM_UNIT_PRICE = "Product Unit Price";
  public static final String ITEM_QUANTITY = "Quantity";
  public static final String ITEM_UNIT_QUANTITY = "Quantity Unit";
  public static final String INVENTORY_ITEM_SELLING_PRICE = "Selling Price";
  public static final String INVENTORY_ITEM_DESCRIPTION = "Description";
  public static final String INVENTORY_ITEM_BRAND_NAME = "Brand Name";
  public static final String INVENTORY_ITEM_MODEL_NAME = "Model Name";
  public static final String INVENTORY_ITEM_COLOR = "Color";
  public static final String INVENTORY_ITEM_SIZE = "Size";
  public static final String INVENTORY_ITEM_SHOP_NAME = "Purchase Store Name";
  public static final String INVENTORY_ITEM_SHOP_CONTACT = "Purchase Store Contact";
  public static final String INVENTORY_ITEM_SHOP_ADDRESSS = "Purchase Store Address";
  public static final String INVENTORY_ITEM_PURCHASE_DATE = "Purchase Date";
  public static final String INVENTORY_ITEM_MODIFIED_DATE = "Modified Date";
  public static final String INVENTORY_ITEM_AVAILABLE = "Available";
  public static final String INVENTORY_ITEM_HISTORY = "History";
  public static final String INVENTORY_TYPE = "InventoryType";
  public static final String TAG = "tag";
  
  
  public static final String ACTION_CHANGED = "Changed";
  public static final String ACTION_DELETE = "Delete";
  public static final String ACTION_INCREASE = "increase";
  public static final String ACTION_DECREASE = "Decrease";
  @SerializedName(ID)
  @Expose
  @PrimaryKey
  private int id;
  
  @SerializedName(INVENTORY_ITEM_IMAGE_PATH)
  @Expose
  private String inventoryItemImagePath;
  
  @SerializedName(INVENTORY_ITEM_NAME)
  @Expose
  private String inventoryItemName;
  
  @SerializedName(INVENTORY_ITEM_PURCHASE_PRICE)
  @Expose
  private String itemPurchasePrice;
  
  @SerializedName(INVENTORY_ITEM_UNIT_PRICE)
  @Expose
  private String itemUnitPrice;
  
  @SerializedName(ITEM_QUANTITY)
  @Expose
  private String itemQuantity;
  
  @SerializedName(ITEM_UNIT_QUANTITY)
  @Expose
  private String itemQuantityUnit;
  
  @SerializedName(INVENTORY_ITEM_SELLING_PRICE)
  @Expose
  private String minimumSellingPrice;
  
  @SerializedName(INVENTORY_ITEM_DESCRIPTION)
  @Expose
  private String description;
  
  @SerializedName(INVENTORY_ITEM_BRAND_NAME)
  @Expose
  private String inventoryItemBrandName;
  
  @SerializedName(INVENTORY_ITEM_MODEL_NAME)
  @Expose
  private String inventoryItemModelName;
  
  @SerializedName(INVENTORY_ITEM_COLOR)
  @Expose
  private String inventoryItemColor;
  
  @SerializedName(INVENTORY_ITEM_SIZE)
  @Expose
  private String inventoryItemSize;
  
  @SerializedName(INVENTORY_ITEM_SHOP_NAME)
  @Expose
  private String purchaseItemShopName;
  
  @SerializedName(INVENTORY_ITEM_SHOP_CONTACT)
  @Expose
  private String purchaseItemShopContact;
  
  @SerializedName(INVENTORY_ITEM_SHOP_ADDRESSS)
  @Expose
  private String purchaseItemShopAddress;
  
  @SerializedName(INVENTORY_ITEM_PURCHASE_DATE)
  @Expose
  private long inventoryItemPurchaseDate;
  
  @SerializedName(INVENTORY_ITEM_MODIFIED_DATE)
  @Expose
  private long inventoryItemLastUpdatedDate;
  
  @SerializedName(INVENTORY_ITEM_AVAILABLE)
  @Expose
  private boolean isAvailable;
  
  @SerializedName(INVENTORY_ITEM_HISTORY)
  @Expose
  public RealmList<InventoryItemHistory> inventoryItemHistories;
  
  
  @SerializedName(INVENTORY_TYPE)
  @Expose
  @Index
  private int inventoryType;
  
  @SerializedName(TAG)
  @Expose
  private String tag;
  
  public InventoryItem() {
  
  }
  
  public InventoryItem(InventoryItem item) {
    this.id = item.getId();
    this.inventoryItemImagePath = item.getInventoryItemImagePath();
    this.inventoryItemName = item.getInventoryItemName();
    this.itemPurchasePrice = item.getItemPurchasePrice();
    this.itemUnitPrice = item.getItemUnitPrice();
    this.itemQuantity = item.getItemQuantity();
    this.itemQuantityUnit = item.getItemQuantityUnit();
    this.minimumSellingPrice = item.getMinimumSellingPrice();
    this.description = item.getDescription();
    this.inventoryItemBrandName = item.getInventoryItemBrandName();
    this.inventoryItemModelName = item.getInventoryItemModelName();
    this.inventoryItemColor = item.getInventoryItemColor();
    this.inventoryItemSize = item.getInventoryItemSize();
    this.purchaseItemShopName = item.getPurchaseItemShopName();
    this.purchaseItemShopContact = item.getPurchaseItemShopContact();
    this.purchaseItemShopAddress = item.getPurchaseItemShopAddress();
    this.inventoryItemPurchaseDate = item.getInventoryItemPurchaseDate();
    this.inventoryItemLastUpdatedDate = item.getInventoryItemLastUpdatedDate();
    this.inventoryType = item.getInventoryType();
    this.tag = item.getTag();
    this.isAvailable = item.isAvailable();
  }
  
  public HashMap<Integer, InventoryItemHistory> getChangedParamList(InventoryItem item) {
    HashMap<Integer, InventoryItemHistory> map = new HashMap<Integer, InventoryItemHistory>();
    long dateModified = System.currentTimeMillis();
    int id = RealmManager.getInventoryDao().getNextInventoryHistoryId();
    if (this.getInventoryItemName().compareToIgnoreCase(item.getInventoryItemName()) != 0) {
      id = id++;
      map.put(id, getHistory(id, INVENTORY_ITEM_NAME, item.getInventoryItemName(),
          this.getInventoryItemName(), item.getDescription(), ACTION_CHANGED, dateModified));
    }
    
    if (this.getItemPurchasePrice().compareToIgnoreCase(item.getItemPurchasePrice()) != 0) {
      id = id++;
      String action = getActionForInt(Integer.parseInt(this.getItemPurchasePrice()),
          Integer.parseInt(item.getItemPurchasePrice()));
      map.put(id, getHistory(id, INVENTORY_ITEM_PURCHASE_PRICE, item.getItemPurchasePrice(),
          this.getItemPurchasePrice(), item.getDescription(), action, dateModified));
    }
    
    if (this.getItemUnitPrice().compareToIgnoreCase(item.getItemUnitPrice()) != 0) {
      id = id++;
      String action = getActionForInt(Integer.parseInt(this.getItemUnitPrice()),
          Integer.parseInt(item.getItemUnitPrice()));
      map.put(id, getHistory(id, INVENTORY_ITEM_UNIT_PRICE, item.getItemUnitPrice(),
          this.getItemUnitPrice(), item.getDescription(), action, dateModified));
    } else if (this.getItemQuantity().compareToIgnoreCase(item.getItemQuantity()) != 0) {
      id = id++;
      String action = getActionForInt(Integer.parseInt(this.getItemQuantity()),
          Integer.parseInt(item.getItemQuantity()));
      map.put(id, getHistory(id, ITEM_QUANTITY, item.getItemQuantity(),
          this.getItemQuantity(), item.getDescription(), action, dateModified));
    }
    
    if (this.getItemQuantityUnit().compareToIgnoreCase(item.getItemQuantityUnit()) != 0) {
      id = id++;
      map.put(id, getHistory(id, ITEM_UNIT_QUANTITY, item.getItemQuantityUnit(),
          this.getItemQuantityUnit(), item.getDescription(), ACTION_CHANGED, dateModified));
    }
    
    if (this.getMinimumSellingPrice().compareToIgnoreCase(item.getMinimumSellingPrice()) != 0) {
      id = id++;
      String action = getActionForInt(Integer.parseInt(this.getMinimumSellingPrice()),
          Integer.parseInt(item.getMinimumSellingPrice()));
      map.put(id, getHistory(id, INVENTORY_ITEM_SELLING_PRICE, item.getMinimumSellingPrice(),
          this.getMinimumSellingPrice(), item.getDescription(), action, dateModified));
    }
    
    if (this.getDescription().compareToIgnoreCase(item.getDescription()) != 0) {
      id = id++;
      map.put(id, getHistory(id, INVENTORY_ITEM_DESCRIPTION, item.getDescription(),
          this.getDescription(), item.getDescription(), ACTION_CHANGED, dateModified));
    }
    
    if (this.getInventoryItemBrandName().compareToIgnoreCase(item.getInventoryItemBrandName())
        != 0) {
      id = id++;
      map.put(id, getHistory(id, INVENTORY_ITEM_BRAND_NAME, item.getInventoryItemBrandName(),
          this.getInventoryItemBrandName(), item.getDescription(), ACTION_CHANGED, dateModified));
    }
    
    if (this.getInventoryItemModelName().compareToIgnoreCase(item.getInventoryItemModelName())
        != 0) {
      id = id++;
      map.put(id, getHistory(id, INVENTORY_ITEM_MODEL_NAME, item.getInventoryItemModelName(),
          this.getInventoryItemModelName(), item.getDescription(), ACTION_CHANGED, dateModified));
    }
    
    if (this.getInventoryItemColor().compareToIgnoreCase(item.getInventoryItemColor()) != 0) {
      id = id++;
      map.put(id, getHistory(id, INVENTORY_ITEM_COLOR, item.getInventoryItemColor(),
          this.getInventoryItemColor(), item.getDescription(), ACTION_CHANGED, dateModified));
    }
    
    if (this.getInventoryItemSize().compareToIgnoreCase(item.getInventoryItemSize()) != 0) {
      id = id++;
      map.put(id, getHistory(id, INVENTORY_ITEM_SIZE, item.getInventoryItemSize(),
          this.getInventoryItemSize(), item.getDescription(), ACTION_CHANGED, dateModified));
    }
    
    if (this.getPurchaseItemShopName().compareToIgnoreCase(item.getPurchaseItemShopName()) != 0) {
      id = id++;
      map.put(id, getHistory(id, INVENTORY_ITEM_SHOP_NAME, item.getPurchaseItemShopName(),
          this.getPurchaseItemShopName(), item.getDescription(), ACTION_CHANGED, dateModified));
    }
    
    if (this.getPurchaseItemShopContact().compareToIgnoreCase(item.getPurchaseItemShopContact())
        != 0) {
      id = id++;
      map.put(id, getHistory(id, INVENTORY_ITEM_SHOP_CONTACT, item.getPurchaseItemShopContact(),
          this.getPurchaseItemShopContact(), item.getDescription(), ACTION_CHANGED, dateModified));
    }
    
    if (this.getPurchaseItemShopAddress().compareToIgnoreCase(item.getPurchaseItemShopAddress())
        != 0) {
      id = id++;
      map.put(id, getHistory(id, INVENTORY_ITEM_SHOP_ADDRESSS, item.getPurchaseItemShopAddress(),
          this.getPurchaseItemShopAddress(), item.getDescription(), ACTION_CHANGED, dateModified));
    }
    
    if (this.isAvailable() != item.isAvailable()) {
      id = id++;
      map.put(id, getHistory(id, INVENTORY_ITEM_AVAILABLE, "" + item.isAvailable(),
          "" + item.isAvailable(), item.getDescription(), ACTION_CHANGED, dateModified));
    }
    return map;
  }
  
  
  private InventoryItemHistory getHistory(int id, String modifiedParameter, String modifiedTo,
      String modifiedFrom,
      String description, String action, long modifiedDate) {
    InventoryItemHistory history = new InventoryItemHistory();
    history.setId(id);
    history.setAction(action);
    history.setDescription(description);
    history.setInventoryItemId(this.id);
    history.setModifiedDate(modifiedDate);
    history.setModifiedFrom(modifiedFrom);
    history.setModifiedParameter(modifiedParameter);
    history.setModifiedTo(modifiedTo);
    return history;
  }
  
  private String getActionForInt(int oldVal, int newValue) {
    if (oldVal > newValue) {
      return ACTION_DECREASE;
    } else if (oldVal < newValue) {
      return ACTION_INCREASE;
    } else {
      return ACTION_CHANGED;
    }
  }
  
  public InventoryItem(int id) {
    this.id = id;
  }
  
  public String getInventoryItemImagePath() {
    return inventoryItemImagePath;
  }
  
  public InventoryItem setInventoryItemImagePath(String inventoryItemImagePath) {
    this.inventoryItemImagePath = inventoryItemImagePath;
    return this;
  }
  
  public String getInventoryItemModelName() {
    return inventoryItemModelName;
  }
  
  public InventoryItem setInventoryItemModelName(String inventoryItemModelName) {
    this.inventoryItemModelName = inventoryItemModelName;
    return this;
  }
  
  public int getId() {
    return id;
  }
  
  public InventoryItem setId(int id) {
    this.id = id;
    return this;
  }
  
  public String getInventoryItemName() {
    return inventoryItemName;
  }
  
  public InventoryItem setInventoryItemName(String inventntoryItemNAme) {
    this.inventoryItemName = inventntoryItemNAme;
    return this;
  }
  
  
  public String getItemPurchasePrice() {
    return itemPurchasePrice;
  }
  
  public InventoryItem setItemPurchasePrice(String itemPurchasePrice) {
    this.itemPurchasePrice = itemPurchasePrice;
    return this;
  }
  
  public String getDescription() {
    return description;
  }
  
  public InventoryItem setDescription(String description) {
    this.description = description;
    return this;
  }
  
  public String getMinimumSellingPrice() {
    return minimumSellingPrice;
  }
  
  public InventoryItem setMinimumSellingPrice(String minimumSellingPrice) {
    this.minimumSellingPrice = minimumSellingPrice;
    return this;
  }
  
  
  public boolean isAvailable() {
    return isAvailable;
  }
  
  public InventoryItem setAvailable(boolean available) {
    isAvailable = available;
    return this;
  }
  
  public RealmList<InventoryItemHistory> getInventoryItemHistories() {
    return inventoryItemHistories;
  }
  
  public InventoryItem setInventoryItemHistories(
      RealmList<InventoryItemHistory> inventoryItemHistories) {
    this.inventoryItemHistories = inventoryItemHistories;
    return this;
  }
  
  
  public String getInventoryItemBrandName() {
    return inventoryItemBrandName;
  }
  
  public InventoryItem setInventoryItemBrandName(String inventoryItemBrandName) {
    this.inventoryItemBrandName = inventoryItemBrandName;
    return this;
  }
  
  public String getInventoryItemColor() {
    return inventoryItemColor;
  }
  
  public InventoryItem setInventoryItemColor(String inventoryItemColor) {
    this.inventoryItemColor = inventoryItemColor;
    return this;
  }
  
  public String getInventoryItemSize() {
    return inventoryItemSize;
  }
  
  public InventoryItem setInventoryItemSize(String inventoryItemSize) {
    this.inventoryItemSize = inventoryItemSize;
    return this;
  }
  
  public String getPurchaseItemShopName() {
    return purchaseItemShopName;
  }
  
  public InventoryItem setPurchaseItemShopName(String purchaseItemShopName) {
    this.purchaseItemShopName = purchaseItemShopName;
    return this;
  }
  
  public String getPurchaseItemShopContact() {
    return purchaseItemShopContact;
  }
  
  public InventoryItem setPurchaseItemShopContact(String purchaseItemShopContact) {
    this.purchaseItemShopContact = purchaseItemShopContact;
    return this;
  }
  
  public String getPurchaseItemShopAddress() {
    return purchaseItemShopAddress;
  }
  
  public InventoryItem setPurchaseItemShopAddress(String purchaseItemShopAddress) {
    this.purchaseItemShopAddress = purchaseItemShopAddress;
    return this;
  }
  
  public String getItemUnitPrice() {
    return itemUnitPrice;
  }
  
  public InventoryItem setItemUnitPrice(String itemUnitPrice) {
    this.itemUnitPrice = itemUnitPrice;
    return this;
  }
  
  public String getItemQuantity() {
    return itemQuantity;
  }
  
  public InventoryItem setItemQuantity(String itemQuantity) {
    this.itemQuantity = itemQuantity;
    return this;
  }
  
  public long getInventoryItemPurchaseDate() {
    return inventoryItemPurchaseDate;
  }
  
  public InventoryItem setInventoryItemPurchaseDate(long inventoryItemPurchaseDate) {
    this.inventoryItemPurchaseDate = inventoryItemPurchaseDate;
    return this;
  }
  
  public long getInventoryItemLastUpdatedDate() {
    return inventoryItemLastUpdatedDate;
  }
  
  public InventoryItem setInventoryItemLastUpdatedDate(long inventoryItemLastUpdatedDate) {
    this.inventoryItemLastUpdatedDate = inventoryItemLastUpdatedDate;
    return this;
  }
  
  public String getItemQuantityUnit() {
    return itemQuantityUnit;
  }
  
  public InventoryItem setItemQuantityUnit(String itemQuantityUnit) {
    this.itemQuantityUnit = itemQuantityUnit;
    return this;
  }
  
  public int getInventoryType() {
    return inventoryType;
  }
  
  public InventoryItem setInventoryType(int inventoryType) {
    this.inventoryType = inventoryType;
    return this;
  }
  
  public String getTag() {
    return tag;
  }
  
  public InventoryItem setTag(String tag) {
    this.tag = tag;
    return this;
  }
}

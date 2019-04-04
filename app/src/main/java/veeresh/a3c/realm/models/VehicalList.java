
package veeresh.a3c.realm.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import io.realm.RealmList;
import io.realm.RealmObject;

public class VehicalList extends RealmObject {

    @SerializedName("inventoryItems")
    @Expose
    private RealmList<InventoryItem> inventoryItems = null;
    @SerializedName("quote_max")
    @Expose
    private String quoteMax;
    @SerializedName("quote_available")
    @Expose
    private String quoteAvailable;

    public RealmList<InventoryItem> getInventoryItems() {
        return inventoryItems;
    }

    public void setInventoryItems(RealmList<InventoryItem> inventoryItems) {
        this.inventoryItems = inventoryItems;
    }

    public String getQuoteMax() {
        return quoteMax;
    }

    public void setQuoteMax(String quoteMax) {
        this.quoteMax = quoteMax;
    }

    public String getQuoteAvailable() {
        return quoteAvailable;
    }

    public void setQuoteAvailable(String quoteAvailable) {
        this.quoteAvailable = quoteAvailable;
    }

}

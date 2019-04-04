
package veeresh.a3c.realm.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.InventoryItemRealmProxy;
import io.realm.annotations.PrimaryKey;
import org.parceler.Parcel;

@org.parceler.Parcel(implementations = {InventoryItemRealmProxy.class},
    value = Parcel.Serialization.BEAN,
    analyze = {InventoryItem.class})
public class InventoryItem extends RealmObject {
  
  @SerializedName("id")
  @Expose
  @PrimaryKey
  private int id;
  @SerializedName("name")
  @Expose
  private String name;
  @SerializedName("image")
  @Expose
  private String image;
  @SerializedName("vehicalNumber")
  @Expose
  private String vehicalNumber;
  @SerializedName("description")
  @Expose
  private String description;
  @SerializedName("currentPlace")
  @Expose
  private String currentPlace;
  @SerializedName("dieselRemain")
  @Expose
  private String dieselRemain;
  @SerializedName("imagePath")
  @Expose
  private String imagePath;
  
  private boolean isAvailable;
  
  @SerializedName("tasks")
  @Expose
  public RealmList<Task> tasks;
  
  public InventoryItem() {
    
  }
  
  public InventoryItem(int id) {
    this.id = id;
  }
  
  public String getImagePath() {
    return imagePath;
  }
  
  public InventoryItem setImagePath(String imagePath) {
    this.imagePath = imagePath;
    return this;
  }
  
  public String getDieselRemain() {
    return dieselRemain;
  }
  
  public InventoryItem setDieselRemain(String dieselRemain) {
    this.dieselRemain = dieselRemain;
    return this;
  }
  
  public int getId() {
    return id;
  }
  
  public InventoryItem setId(int id) {
    this.id = id;
    return this;
  }
  
  public String getName() {
    return name;
  }
  
  public InventoryItem setName(String name) {
    this.name = name;
    return this;
  }
  
  public String getImage() {
    return image;
  }
  
  public InventoryItem setImage(String image) {
    this.image = image;
    return this;
  }
  
  public String getVehicalNumber() {
    return vehicalNumber;
  }
  
  public InventoryItem setVehicalNumber(String vehicalNumber) {
    this.vehicalNumber = vehicalNumber;
    return this;
  }
  
  public String getDescription() {
    return description;
  }
  
  public InventoryItem setDescription(String description) {
    this.description = description;
    return this;
  }
  
  public String getCurrentPlace() {
    return currentPlace;
  }
  
  public InventoryItem setCurrentPlace(String currentPlace) {
    this.currentPlace = currentPlace;
    return this;
  }
  
  
  public boolean isAvailable() {
    return isAvailable;
  }
  
  public InventoryItem setAvailable(boolean available) {
    isAvailable = available;
    return this;
  }
  
  public RealmList<Task> getTasks() {
    return tasks;
  }
  
  public InventoryItem setTasks(RealmList<Task> tasks) {
    this.tasks = tasks;
    return this;
  }
}

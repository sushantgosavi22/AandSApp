
package com.aandssoftware.aandsinventory.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class CarouselMenuModel extends RealmObject {
  
  
  @SerializedName("id")
  @Expose
  @PrimaryKey
  private int id;
  
  @SerializedName("aliceName")
  @Expose
  private String aliceName;
  
  @SerializedName("imageId")
  @Expose
  private String imageId;
  
  @SerializedName("expression")
  @Expose
  private String expression;
  
  @SerializedName("tag")
  @Expose
  private String tag;
  
  @SerializedName("description")
  @Expose
  private String description;
  
  @SerializedName("dateCreated")
  @Expose
  private long dateCreated;
  
  @SerializedName("CarouselId")
  @Expose
  private String CarouselId;
  
  public CarouselMenuModel() {
  
  }
  
  public CarouselMenuModel(CarouselMenuModel item) {
    this.id = item.getId();
    this.aliceName = item.getAliceName();
    this.imageId = item.getImageId();
    this.aliceName = item.getAliceName();
    this.expression = item.getExpression();
    this.tag = item.getTag();
    this.description = item.getDescription();
    this.dateCreated = item.getDateCreated();
    this.CarouselId = item.getCarouselId();
  }
  
  public int getId() {
    return id;
  }
  
  public CarouselMenuModel setId(int id) {
    this.id = id;
    return this;
  }
  
  public String getAliceName() {
    return aliceName;
  }
  
  public CarouselMenuModel setAliceName(String aliceName) {
    this.aliceName = aliceName;
    return this;
  }
  
  public String getImageId() {
    return imageId;
  }
  
  public CarouselMenuModel setImageId(String imageId) {
    this.imageId = imageId;
    return this;
  }
  
  public String getExpression() {
    return expression;
  }
  
  public CarouselMenuModel setExpression(String expression) {
    this.expression = expression;
    return this;
  }
  
  public String getTag() {
    return tag;
  }
  
  public CarouselMenuModel setTag(String tag) {
    this.tag = tag;
    return this;
  }
  
  public String getDescription() {
    return description;
  }
  
  public CarouselMenuModel setDescription(String description) {
    this.description = description;
    return this;
  }
  
  public long getDateCreated() {
    return dateCreated;
  }
  
  public CarouselMenuModel setDateCreated(long dateCreated) {
    this.dateCreated = dateCreated;
    return this;
  }
  
  public String getCarouselId() {
    return CarouselId;
  }
  
  public CarouselMenuModel setCarouselId(String carouselId) {
    CarouselId = carouselId;
    return this;
  }
}

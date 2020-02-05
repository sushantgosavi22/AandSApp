package com.aandssoftware.aandsinventory.firebase;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class CarouselItem implements Serializable {
  
  public static final String TABLE_CAROUSEL = "carousel";
  public static final String ID = "id";
  public static final String ALICE_NAME = "aliceName";
  public static final String IMAGE_ID = "imageId";
  public static final String EXPRESSION = "expression";
  public static final String TAG = "tag";
  public static final String DESCRIPTION = "description";
  public static final String DATE_CREATED = "dateCreated";
  public static final String CAROUSEL_ID = "carouselId";
  public static final String DEFAULT_IMAGE_ID = "defaultImageId";
  
  
  @SerializedName(ID)
  @Expose
  private int id;
  
  @SerializedName(ALICE_NAME)
  @Expose
  private String aliceName;
  
  @SerializedName(IMAGE_ID)
  @Expose
  private String imageId;
  
  @SerializedName(EXPRESSION)
  @Expose
  private String expression;
  
  @SerializedName(TAG)
  @Expose
  private String tag;
  
  @SerializedName(DESCRIPTION)
  @Expose
  private String description;
  
  @SerializedName(DATE_CREATED)
  @Expose
  private long dateCreated;
  
  @SerializedName(CAROUSEL_ID)
  @Expose
  private String carouselId;
  
  
  @SerializedName(DEFAULT_IMAGE_ID)
  @Expose
  private int defaultImageId;
  
  public int getId() {
    return id;
  }
  
  public void setId(int id) {
    this.id = id;
  }
  
  public String getAliceName() {
    return aliceName;
  }
  
  public void setAliceName(String aliceName) {
    this.aliceName = aliceName;
  }
  
  public String getImageId() {
    return imageId;
  }
  
  public void setImageId(String imageId) {
    this.imageId = imageId;
  }
  
  public String getExpression() {
    return expression;
  }
  
  public void setExpression(String expression) {
    this.expression = expression;
  }
  
  public String getTag() {
    return tag;
  }
  
  public void setTag(String tag) {
    this.tag = tag;
  }
  
  public String getDescription() {
    return description;
  }
  
  public void setDescription(String description) {
    this.description = description;
  }
  
  public long getDateCreated() {
    return dateCreated;
  }
  
  public void setDateCreated(long dateCreated) {
    this.dateCreated = dateCreated;
  }
  
  public String getCarouselId() {
    return carouselId;
  }
  
  public void setCarouselId(String carouselId) {
    this.carouselId = carouselId;
  }
  
  public int getDefaultImageId() {
    return defaultImageId;
  }
  
  public void setDefaultImageId(int defaultImageId) {
    this.defaultImageId = defaultImageId;
  }
}

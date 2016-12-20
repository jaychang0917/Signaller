package com.jaychang.signaller.core.model;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Sender extends RealmObject {

  @PrimaryKey
  @SerializedName("id")
  private String userId;
  @SerializedName("is_brand")
  private boolean isBrand;
  @SerializedName("image_url")
  private String imageUrl;
  @SerializedName("name")
  private String name;

  //region getters & setters
  public String getUserId() {
    return userId;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }

  public boolean isBrand() {
    return isBrand;
  }

  public void setBrand(boolean brand) {
    isBrand = brand;
  }

  public String getImageUrl() {
    return imageUrl;
  }

  public void setImageUrl(String imageUrl) {
    this.imageUrl = imageUrl;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
  //endregion

}

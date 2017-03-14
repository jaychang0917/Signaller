package com.redso.signaller.core.model;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class ChatSender extends RealmObject {

  @PrimaryKey
  @SerializedName("id")
  private String userId;
  @SerializedName("type")
  private String type;
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

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }
  //endregion

}

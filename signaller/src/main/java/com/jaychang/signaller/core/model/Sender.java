package com.jaychang.signaller.core.model;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;

public class Sender extends RealmObject {

  @SerializedName("is_brand")
  public boolean isBrand;
  @SerializedName("image_url")
  public String imageUrl;
  @SerializedName("id")
  public String userId;
  @SerializedName("name")
  public String name;

}

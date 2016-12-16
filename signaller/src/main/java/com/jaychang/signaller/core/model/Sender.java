package com.jaychang.signaller.core.model;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Sender extends RealmObject {

  @PrimaryKey
  @SerializedName("id")
  public String userId;
  @SerializedName("is_brand")
  public boolean isBrand;
  @SerializedName("image_url")
  public String imageUrl;
  @SerializedName("name")
  public String name;

}

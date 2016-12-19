package com.jaychang.signaller.core.model;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;

public class ImageAttribute extends RealmObject {

  @SerializedName("width")
  public int width;
  @SerializedName("height")
  public int height;

}

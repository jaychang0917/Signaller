package com.redso.signaller.core.model;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;

public class SignallerImageAttribute extends RealmObject {

  @SerializedName("width")
  private int width;
  @SerializedName("height")
  private int height;

  public SignallerImageAttribute() {
  }

  public SignallerImageAttribute(int width, int height) {
    this.width = width;
    this.height = height;
  }

  public int getWidth() {
    return width;
  }

  public void setWidth(int width) {
    this.width = width;
  }

  public int getHeight() {
    return height;
  }

  public void setHeight(int height) {
    this.height = height;
  }

}

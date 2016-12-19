package com.jaychang.signaller.core.model;

import com.google.gson.annotations.SerializedName;
import com.jaychang.signaller.util.GsonUtils;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Image extends RealmObject {

  @PrimaryKey
  @SerializedName("resource_id")
  public String resourceId;
  @SerializedName("ctime")
  public long ctime;
  @SerializedName("format")
  public String format;
  @SerializedName("url")
  public String url;
  @SerializedName("download_url")
  public String downloadUrl;
  @SerializedName("filename")
  public String filename;
  @SerializedName("mtime")
  public long mtime;
  @SerializedName("type")
  public String type;
  @SerializedName("name")
  public String name;
  @SerializedName("attributes")
  public ImageAttribute attributes;

  public static Image from(String json) {
    return GsonUtils.getGson().fromJson(json, Image.class);
  }

  public float getRatio() {
    return (float)attributes.width / (float) attributes.height;
  }
}

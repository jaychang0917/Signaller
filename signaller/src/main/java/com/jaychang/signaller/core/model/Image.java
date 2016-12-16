package com.jaychang.signaller.core.model;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

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

  public static Image from(String json) {
    return new Gson().fromJson(json, Image.class);
  }
}

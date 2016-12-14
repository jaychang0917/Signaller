package com.jaychang.signaller.core.model;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import io.realm.annotations.Ignore;

public class Image {

  @SerializedName("ctime")
  public long ctime;
  @SerializedName("format")
  public String format;
  @SerializedName("url")
  public String url;
  @SerializedName("resource_id")
  public String resourceId;
  @SerializedName("download_url")
  public String downloadUrl;
  @SerializedName("filename")
  public String filename;
  @SerializedName("mtime")
  public long mtime;
  @Ignore
  @SerializedName("attributes")
  public Attributes attributes;
  @SerializedName("type")
  public String type;
  @SerializedName("name")
  public Object name;

  public static class Attributes {
    @SerializedName("width")
    public int width;
    @SerializedName("height")
    public int height;
  }

  public static Image from(String json) {
    return new Gson().fromJson(json, Image.class);
  }
}

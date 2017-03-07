package com.redso.signaller.core.model;

import com.google.gson.annotations.SerializedName;
import com.redso.signaller.util.GsonUtils;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class SignallerImage extends RealmObject {

  @PrimaryKey
  @SerializedName("resource_id")
  private String resourceId;
  @SerializedName("ctime")
  private long ctime;
  @SerializedName("format")
  private String format;
  @SerializedName("url")
  private String url;
  @SerializedName("download_url")
  private String downloadUrl;
  @SerializedName("filename")
  private String filename;
  @SerializedName("mtime")
  private long mtime;
  @SerializedName("type")
  private String type;
  @SerializedName("name")
  private String name;
  @SerializedName("attributes")
  private SignallerImageAttribute attributes;

  public static SignallerImage from(String json) {
    return GsonUtils.getGson().fromJson(json, SignallerImage.class);
  }

  public float getRatio() {
    return (float)attributes.getWidth() / (float) attributes.getHeight();
  }

  //region getters & setters
  public String getResourceId() {
    return resourceId;
  }

  public void setResourceId(String resourceId) {
    this.resourceId = resourceId;
  }

  public long getCtime() {
    return ctime;
  }

  public void setCtime(long ctime) {
    this.ctime = ctime;
  }

  public String getFormat() {
    return format;
  }

  public void setFormat(String format) {
    this.format = format;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public String getDownloadUrl() {
    return downloadUrl;
  }

  public void setDownloadUrl(String downloadUrl) {
    this.downloadUrl = downloadUrl;
  }

  public String getFilename() {
    return filename;
  }

  public void setFilename(String filename) {
    this.filename = filename;
  }

  public long getMtime() {
    return mtime;
  }

  public void setMtime(long mtime) {
    this.mtime = mtime;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public SignallerImageAttribute getAttributes() {
    return attributes;
  }

  public void setAttributes(SignallerImageAttribute attributes) {
    this.attributes = attributes;
  }
  //endregion
}

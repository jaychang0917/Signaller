package com.jaychang.signaller.core.model;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Receiver extends RealmObject {

  @PrimaryKey
  @SerializedName("user_id")
  private String userId;
  @SerializedName("ctime")
  private long ctime;
  @SerializedName("user_type")
  private String userType;
  @SerializedName("profile_photo")
  private ProfilePhoto profilePhoto;
  @SerializedName("mtime")
  private long mtime;
  @SerializedName("name")
  private String name;

  //region getters & setters
  public String getUserId() {
    return userId;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }

  public long getCtime() {
    return ctime;
  }

  public void setCtime(long ctime) {
    this.ctime = ctime;
  }

  public String getUserType() {
    return userType;
  }

  public void setUserType(String userType) {
    this.userType = userType;
  }

  public String getProfilePhotoUrl() {
    return profilePhoto == null ? "" : profilePhoto.getUrl();
  }

  public void setProfilePhotoUrl(String url) {
    if (profilePhoto == null) {
      profilePhoto = new ProfilePhoto();
    }
    profilePhoto.setUrl(url);
  }

  public long getMtime() {
    return mtime;
  }

  public void setMtime(long mtime) {
    this.mtime = mtime;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
  //endregion

}

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
  @SerializedName("sns_facebook")
  private String facebookId;
  @SerializedName("sns_youtube")
  private String youtubeId;
  @SerializedName("sns_instagram")
  private String instagramId;

  //region getters & setters
  public String getUserId() {
    return userId;
  }

  public long getCtime() {
    return ctime;
  }

  public String getUserType() {
    return userType;
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

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getFacebookId() {
    return facebookId;
  }

  public String getYoutubeId() {
    return youtubeId;
  }

  public String getInstagramId() {
    return instagramId;
  }

  //endregion

}

package com.jaychang.signaller.core.model;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;

public class Receiver extends RealmObject {

  @SerializedName("user_id")
  public String userId;
  @SerializedName("ctime")
  public long ctime;
  @SerializedName("user_type")
  public String userType;
  @SerializedName("profile_pic_url")
  public String profilePicUrl;
  @SerializedName("mtime")
  public long mtime;
  @SerializedName("name")
  public String name;

}

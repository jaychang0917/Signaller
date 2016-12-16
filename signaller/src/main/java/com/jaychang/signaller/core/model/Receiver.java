package com.jaychang.signaller.core.model;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Receiver extends RealmObject {

  @PrimaryKey
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

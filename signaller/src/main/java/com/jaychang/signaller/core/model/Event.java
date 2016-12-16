package com.jaychang.signaller.core.model;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Event extends RealmObject {

  @PrimaryKey
  @SerializedName("event_id")
  public String eventId;
  @SerializedName("ctime")
  public long ctime;
  @SerializedName("title")
  public String title;
  @SerializedName("content")
  public String content;
  @SerializedName("image_url")
  public String imageUrl;
  @SerializedName("mtime")
  public long mtime;
  @SerializedName("hashtags")
  public RealmList<RealmString> hashtags;
  @SerializedName("options")
  public RealmList<RealmString> options;

}

package com.jaychang.signaller.core.model;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Event extends RealmObject {

  @PrimaryKey
  @SerializedName("event_id")
  private String eventId;
  @SerializedName("ctime")
  private long ctime;
  @SerializedName("title")
  private String title;
  @SerializedName("content")
  private String content;
  @SerializedName("image_url")
  private String imageUrl;
  @SerializedName("mtime")
  private long mtime;
  @SerializedName("hashtags")
  private RealmList<RealmString> hashtags;
  @SerializedName("options")
  private RealmList<RealmString> options;

  //region getters & setters
  public String getEventId() {
    return eventId;
  }

  public void setEventId(String eventId) {
    this.eventId = eventId;
  }

  public long getCtime() {
    return ctime;
  }

  public void setCtime(long ctime) {
    this.ctime = ctime;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public String getImageUrl() {
    return imageUrl;
  }

  public void setImageUrl(String imageUrl) {
    this.imageUrl = imageUrl;
  }

  public long getMtime() {
    return mtime;
  }

  public void setMtime(long mtime) {
    this.mtime = mtime;
  }

  public RealmList<RealmString> getHashtags() {
    return hashtags;
  }

  public void setHashtags(RealmList<RealmString> hashtags) {
    this.hashtags = hashtags;
  }

  public RealmList<RealmString> getOptions() {
    return options;
  }

  public void setOptions(RealmList<RealmString> options) {
    this.options = options;
  }
  //endregion

}

package com.redso.signaller.core.model;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class SignallerChatRoomInfo extends RealmObject {

  @PrimaryKey
  @SerializedName("id")
  private String chatRoomId;
  @SerializedName("last_update_time")
  private long lastUpdateTime;
  @SerializedName("total_messages")
  private int totalMessages;
  @SerializedName("ctime")
  private long ctime;
  @SerializedName("mtime")
  private long mtime;

  //region getters & setters
  public String getChatRoomId() {
    return chatRoomId;
  }

  public void setChatRoomId(String chatRoomId) {
    this.chatRoomId = chatRoomId;
  }

  public long getLastUpdateTime() {
    return lastUpdateTime;
  }

  public void setLastUpdateTime(long lastUpdateTime) {
    this.lastUpdateTime = lastUpdateTime;
  }

  public int getTotalMessages() {
    return totalMessages;
  }

  public void setTotalMessages(int totalMessages) {
    this.totalMessages = totalMessages;
  }

  public long getCtime() {
    return ctime;
  }

  public void setCtime(long ctime) {
    this.ctime = ctime;
  }

  public long getMtime() {
    return mtime;
  }

  public void setMtime(long mtime) {
    this.mtime = mtime;
  }
  //endregion

}

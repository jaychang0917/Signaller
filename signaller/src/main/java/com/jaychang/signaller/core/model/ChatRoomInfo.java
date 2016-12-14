package com.jaychang.signaller.core.model;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;

public class ChatRoomInfo extends RealmObject {

  @SerializedName("last_update_time")
  public long lastUpdateTime;
  @SerializedName("id")
  public String chatroomId;
  @SerializedName("total_messages")
  public int totalMessages;
  @SerializedName("ctime")
  public long ctime;
  @SerializedName("mtime")
  public long mtime;

}

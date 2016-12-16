package com.jaychang.signaller.core.model;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class ChatRoomInfo extends RealmObject {

  @PrimaryKey
  @SerializedName("id")
  public String chatRoomId;
  @SerializedName("last_update_time")
  public long lastUpdateTime;
  @SerializedName("total_messages")
  public int totalMessages;
  @SerializedName("ctime")
  public long ctime;
  @SerializedName("mtime")
  public long mtime;

}

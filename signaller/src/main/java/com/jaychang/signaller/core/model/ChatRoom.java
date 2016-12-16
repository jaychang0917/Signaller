package com.jaychang.signaller.core.model;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class ChatRoom extends RealmObject {

  @PrimaryKey
  @SerializedName("parent_id")
  public String chatRoomId;
  @SerializedName("last_update_time")
  public long lastUpdateTime;
  @SerializedName("info")
  public ChatRoomInfo info;
  @SerializedName("ctime")
  public long ctime;
  @SerializedName("last_message")
  public ChatMessage lastMessage;
  @SerializedName("mtime")
  public long mtime;
  @SerializedName("unread_count")
  public int unreadCount;
  @SerializedName("receiver")
  public Receiver receiver;

}

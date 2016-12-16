package com.jaychang.signaller.core.model;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;

public class ChatRoom extends RealmObject {

  public String userId;
  @SerializedName("last_update_time")
  public long lastUpdateTime;
  @SerializedName("info")
  public ChatRoomInfo info;
  @SerializedName("ctime")
  public long ctime;
  @SerializedName("parent_id")
  public String chatRoomId;
  @SerializedName("last_message")
  public ChatMessage lastMessage;
  @SerializedName("mtime")
  public long mtime;
  @SerializedName("unread_count")
  public int unreadCount;
  @SerializedName("receiver")
  public Receiver receiver;

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder("ChatRoom{");
    sb.append("chatRoomId='").append(chatRoomId).append('\'');
    sb.append('}');
    return sb.toString();
  }

}

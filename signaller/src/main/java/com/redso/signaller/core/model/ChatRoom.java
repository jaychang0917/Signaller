package com.redso.signaller.core.model;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class ChatRoom extends RealmObject {

  @PrimaryKey
  @SerializedName("parent_id")
  private String chatRoomId;
  @SerializedName("last_update_time")
  private long lastUpdateTime;
  @SerializedName("last_message_time")
  private Long lastMessageTime;
  @SerializedName("info")
  private ChatRoomInfo info;
  @SerializedName("ctime")
  private long ctime;
  @SerializedName("last_message")
  private ChatMessage lastMessage;
  @SerializedName("mtime")
  private long mtime;
  @SerializedName("unread_count")
  private int unreadCount;
  @SerializedName("receiver")
  private ChatReceiver receiver;

  public void increaseUnreadCount() {
    unreadCount++;
  }

  //region getters & setters
  public String getChatRoomId() {
    return chatRoomId;
  }

  public void setChatRoomId(String chatRoomId) {
    this.chatRoomId = chatRoomId;
  }

  public ChatRoomInfo getInfo() {
    return info;
  }

  public void setInfo(ChatRoomInfo info) {
    this.info = info;
  }

  public ChatMessage getLastMessage() {
    return lastMessage;
  }

  public void setLastMessage(ChatMessage lastMessage) {
    this.lastMessage = lastMessage;
  }

  public long getLastUpdateTime() {
    return lastUpdateTime;
  }

  public void setLastUpdateTime(long lastUpdateTime) {
    this.lastUpdateTime = lastUpdateTime;
  }

  public Long getLastMessageTime() {
    return lastMessage != null ? lastMessage.getMsgTime() : 0;
  }

  public void setLastMessageTime(long lastMessageTime) {
    this.lastMessageTime = lastMessageTime;
  }

  public int getUnreadCount() {
    return unreadCount;
  }

  public void setUnreadCount(int unreadCount) {
    this.unreadCount = unreadCount;
  }

  public ChatReceiver getReceiver() {
    return receiver;
  }

  public void setReceiver(ChatReceiver receiver) {
    this.receiver = receiver;
  }
  //endregion

}

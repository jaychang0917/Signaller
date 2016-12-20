package com.jaychang.signaller.core.model;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class ChatRoom extends RealmObject {

  @PrimaryKey
  @SerializedName("parent_id")
  private String chatRoomId;
  @SerializedName("last_update_time")
  private long lastUpdateTime;
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
  private Receiver receiver;

  public static ChatRoom from(String chatRoomId, ChatMessage message) {
    ChatRoom chatRoom = new ChatRoom();
    chatRoom.setChatRoomId(chatRoomId);
    Receiver receiver = new Receiver();
    receiver.setName(message.getSender().getName());
    receiver.setProfilePhotoUrl(message.getSender().getImageUrl());
    chatRoom.setReceiver(receiver);
    chatRoom.setLastMessage(message);
    chatRoom.setUnreadCount(1);
    return chatRoom;
  }

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

  public long getLastUpdateTime() {
    return lastUpdateTime;
  }

  public void setLastUpdateTime(long lastUpdateTime) {
    this.lastUpdateTime = lastUpdateTime;
  }

  public ChatRoomInfo getInfo() {
    return info;
  }

  public void setInfo(ChatRoomInfo info) {
    this.info = info;
  }

  public long getCtime() {
    return ctime;
  }

  public void setCtime(long ctime) {
    this.ctime = ctime;
  }

  public ChatMessage getLastMessage() {
    return lastMessage;
  }

  public void setLastMessage(ChatMessage lastMessage) {
    this.lastMessage = lastMessage;
  }

  public long getMtime() {
    return mtime;
  }

  public void setMtime(long mtime) {
    this.mtime = mtime;
  }

  public int getUnreadCount() {
    return unreadCount;
  }

  public void setUnreadCount(int unreadCount) {
    this.unreadCount = unreadCount;
  }

  public Receiver getReceiver() {
    return receiver;
  }

  public void setReceiver(Receiver receiver) {
    this.receiver = receiver;
  }
  //endregion

}

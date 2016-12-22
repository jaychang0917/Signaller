package com.jaychang.signaller.core.model;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class SignallerChatRoom extends RealmObject {

  @PrimaryKey
  @SerializedName("parent_id")
  private String chatRoomId;
  @SerializedName("last_update_time")
  private long lastUpdateTime;
  @SerializedName("last_message_time")
  private long lastMessageTime;
  @SerializedName("info")
  private SignallerChatRoomInfo info;
  @SerializedName("ctime")
  private long ctime;
  @SerializedName("last_message")
  private SignallerChatMessage lastMessage;
  @SerializedName("mtime")
  private long mtime;
  @SerializedName("unread_count")
  private int unreadCount;
  @SerializedName("receiver")
  private SignallerReceiver receiver;

  public static SignallerChatRoom from(String chatRoomId, SignallerChatMessage message) {
    SignallerChatRoom chatRoom = new SignallerChatRoom();
    chatRoom.setChatRoomId(chatRoomId);
    SignallerReceiver receiver = new SignallerReceiver();
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

  public SignallerChatRoomInfo getInfo() {
    return info;
  }

  public void setInfo(SignallerChatRoomInfo info) {
    this.info = info;
  }

  public SignallerChatMessage getLastMessage() {
    return lastMessage;
  }

  public void setLastMessage(SignallerChatMessage lastMessage) {
    this.lastMessage = lastMessage;
  }

  public long getLastUpdateTime() {
    return lastUpdateTime;
  }

  public void setLastUpdateTime(long lastUpdateTime) {
    this.lastUpdateTime = lastUpdateTime;
  }

  public long getLastMessageTime() {
    return lastMessageTime;
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

  public SignallerReceiver getReceiver() {
    return receiver;
  }

  public void setReceiver(SignallerReceiver receiver) {
    this.receiver = receiver;
  }
  //endregion

}

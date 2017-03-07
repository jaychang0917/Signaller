package com.redso.signaller.core.model;

import android.os.Bundle;

import com.redso.signaller.core.UserData;

import java.io.Serializable;

public class PushNotification implements Serializable {

  private String message;
  private String chatRoomId;
  private String msgId;
  private String chatId;
  private String roomTitle;
  private String msgType;

  public PushNotification(String message, String chatRoomId, String chatId, String roomTitle, String msgType, String msgId) {
    this.message = message;
    this.chatRoomId = chatRoomId;
    this.chatId = chatId;
    this.roomTitle = roomTitle;
    this.msgType = msgType;
    this.msgId = msgId;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public String getMessage() {
    return message;
  }

  public String getChatRoomId() {
    return chatRoomId;
  }

  public String getChatId() {
    return chatId;
  }

  public String getRoomTitle() {
    return roomTitle;
  }

  public String getMsgType() {
    return msgType;
  }

  public String getMsgId() {
    return msgId;
  }

  public static PushNotification from(Bundle data) {
    String message = data.getString("content");
    String msgId = data.getString("msg_id");
    String msgType = data.getString("msg_type");
    String senderId = data.getString("user_id");
    String roomTitle = data.getString("room_title");

    String ownUserId = UserData.getInstance().getUserId();
    String chatRoomId = ownUserId.compareTo(senderId) < 0 ?
      ownUserId + "_" + senderId :
      senderId + "_" + ownUserId;

    return new PushNotification(message, chatRoomId, senderId, roomTitle, msgType, msgId);
  }

}

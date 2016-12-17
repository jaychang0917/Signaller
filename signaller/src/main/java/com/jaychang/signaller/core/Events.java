package com.jaychang.signaller.core;

public class Events {

  public static class OnMsgReceivedEvent {
    public String msgId;

    public OnMsgReceivedEvent(String msgId) {
      this.msgId = msgId;
    }
  }

  public static class ShowPushNotificationEvent {
    public String msgId;

    public ShowPushNotificationEvent(String msgId) {
      this.msgId = msgId;
    }
  }

  public static class UpdateChatRoomListEvent {
    public String chatRoomId;

    public UpdateChatRoomListEvent(String chatRoomId) {
      this.chatRoomId = chatRoomId;
    }
  }
}

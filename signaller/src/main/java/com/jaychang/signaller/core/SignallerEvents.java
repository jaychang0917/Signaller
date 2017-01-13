package com.jaychang.signaller.core;

public class SignallerEvents {

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

  public static class OnSocketConnectEvent {
  }

  public static class OnSocketConnectingEvent {
  }

  public static class OnSocketConnectedEvent {
  }

  public static class OnSocketDisconnectedEvent {
  }
}

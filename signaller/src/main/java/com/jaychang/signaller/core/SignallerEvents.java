package com.jaychang.signaller.core;

import com.jaychang.signaller.core.model.PushNotification;

public class SignallerEvents {

  public static class OnMsgReceivedEvent {
    public String chatRoomId;
    public String msgId;

    public OnMsgReceivedEvent(String chatRoomId, String msgId) {
      this.chatRoomId = chatRoomId;
      this.msgId = msgId;
    }
  }

  public static class ShowPushNotificationEvent {
    public PushNotification notification;

    public ShowPushNotificationEvent(PushNotification notification) {
      this.notification = notification;
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

package com.redso.signaller.core;

import com.redso.signaller.core.model.ChatMessage;
import com.redso.signaller.core.model.PushNotification;

public class Events {

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
    public boolean updateUnreadCount;

    public UpdateChatRoomListEvent(String chatRoomId, boolean updateUnreadCount) {
      this.chatRoomId = chatRoomId;
      this.updateUnreadCount = updateUnreadCount;
    }

    public UpdateChatRoomListEvent(String chatRoomId) {
      this(chatRoomId, true);
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

  public static class ClearUnreadCountEvent {
    public String chatRoomId;

    public ClearUnreadCountEvent(String chatRoomId) {
      this.chatRoomId = chatRoomId;
    }
  }

  public static class OnMsgSentEvent {
    public ChatMessage chatMessage;
    public int messageCellIndex;

    public OnMsgSentEvent(ChatMessage chatMessage, int messageCellIndex) {
      this.chatMessage = chatMessage;
      this.messageCellIndex = messageCellIndex;
    }
  }

}

package com.jaychang.signaller.core;

import com.jaychang.signaller.core.model.ChatMessage;

public class Events {

  public static class OnMsgReceivedEvent {
    public ChatMessage chatMessage;

    public OnMsgReceivedEvent(ChatMessage chatMessage) {
      this.chatMessage = chatMessage;
    }
  }

  public static class ShowPushNotificationEvent {
    public ChatMessage chatMessage;

    public ShowPushNotificationEvent(ChatMessage chatMessage) {
      this.chatMessage = chatMessage;
    }
  }

  public static class UpdateChatRoomListEvent {
    public ChatMessage chatMessage;

    public UpdateChatRoomListEvent(ChatMessage chatMessage) {
      this.chatMessage = chatMessage;
    }
  }
}

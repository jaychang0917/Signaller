package com.jaychang.signaller.core;

public interface ChatRoomJoinCallback {
  void onChatRoomJoined(String chatRoomId, String userId);
}

package com.redso.signaller.util;

public class ChatUtils {

  private ChatUtils() {
  }

  public static String createChatRoomId(String user1Id, String user2Id) {
    String chatRoomId = user1Id.compareTo(user2Id) < 0 ?
      user1Id + "_" + user2Id :
      user2Id + "_" + user1Id;
    return chatRoomId;
  }

}

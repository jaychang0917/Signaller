package com.jaychang.signaller.core;

public final class ChatRoomMeta {

  public static String cursor;
  public static boolean hasMoreData;

  public static void clear() {
    cursor = null;
    hasMoreData = false;
  }

}

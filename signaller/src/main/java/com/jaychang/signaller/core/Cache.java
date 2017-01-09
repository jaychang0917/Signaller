package com.jaychang.signaller.core;

import com.jaychang.signaller.core.model.SignallerChatRoom;

import java.util.HashMap;

public final class Cache {

  public static HashMap<String, SignallerChatRoom> chatRoomCache = new HashMap<>();

  public static void clear() {
    chatRoomCache.clear();
  }

}

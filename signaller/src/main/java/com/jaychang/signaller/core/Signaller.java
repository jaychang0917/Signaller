package com.jaychang.signaller.core;

import android.content.Context;

public final class Signaller {

  private static final Signaller INSTANCE = new Signaller();


  private Signaller() {
  }

  public static void init(Context appContext,
                          String serverDomain,
                          String socketUrl) {
    UserData.getInstance().setServerDomain(serverDomain);
    UserData.getInstance().setSocketUrl(socketUrl);
    DatabaseManager.getInstance().init(appContext);
  }

  public static Signaller getInstance() {
    return INSTANCE;
  }

  public void connect(String accessToken, String userId) {
    UserData.getInstance().setAccessToken(accessToken);
    UserData.getInstance().setUserId(userId);

    SocketManager.getInstance().initSocket(accessToken);
    SocketManager.getInstance().connect();
  }

  public void chatWith(String userId, ChatRoomJoinCallback callback) {
    SocketManager.getInstance().join(userId, makeChatRoomId(userId), callback);
  }

  public void leaveChatRoom(String chatRoomId, ChatRoomLeaveCallback callback) {
    SocketManager.getInstance().leave(chatRoomId, callback);
  }

  private String makeChatRoomId(String userId) {
    String ownUserId = UserData.getInstance().getUserId();
    return ownUserId.compareTo(userId) < 0 ?
      ownUserId + "_" + userId :
      userId + "_" + ownUserId;
  }

}

package com.jaychang.signaller.core;

import android.content.Context;

public final class Signaller {

  private static final Signaller INSTANCE = new Signaller();
  private static String SERVER_DOMAIN;
  private static String SOCKET_URL;


  private Signaller() {
  }

  public static void init(Context appContext, String serverDomain, String socketUrl) {
    Signaller.SERVER_DOMAIN = serverDomain;
    Signaller.SOCKET_URL = socketUrl;
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

  String getServerDomain() {
    return SERVER_DOMAIN;
  }

  String getSocketUrl() {
    return SOCKET_URL;
  }

}

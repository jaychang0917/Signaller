package com.jaychang.signaller.core;

import android.content.Context;

public final class Signaller {

  private static final Signaller INSTANCE = new Signaller();
  private String serverDomain;
  private String socketUrl;
  private String accessToken;
  private String userId;

  private Signaller() {
  }

  public static void init(Context appContext) {
    DatabaseManager.init(appContext);
  }

  public static Signaller getInstance() {
    return INSTANCE;
  }

  public void setServerDomain(String serverHost) {
    this.serverDomain = serverHost;
  }

  public void setSocketUrl(String socketUrl) {
    this.socketUrl = socketUrl;
  }

  public void connect(String accessToken, String userId) {
    this.accessToken = accessToken;
    this.userId = userId;

    SocketManager.getInstance().initSocket(accessToken);
    SocketManager.getInstance().connect();
  }

  public String getServerDomain() {
    return serverDomain;
  }

  public String getSocketUrl() {
    return socketUrl;
  }

  public String getAccessToken() {
    return accessToken;
  }

  public String getUserId() {
    return userId;
  }

}

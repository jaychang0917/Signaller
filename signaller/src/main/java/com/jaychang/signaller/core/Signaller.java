package com.jaychang.signaller.core;

public final class Signaller {

  private static final Signaller INSTANCE = new Signaller();
  private String serverDomain;
  private String socketUrl;
  private String accessToken;
  private String userId;
  private boolean isInChatRoomListPage;
  private boolean isInChatRoomPage;
  private String currentChatRoomId;

  private Signaller() {
    DatabaseManager.init();
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

  public boolean isInChatRoomListPage() {
    return isInChatRoomListPage;
  }

  public void setInChatRoomListPage(boolean inChatRoomListPage) {
    isInChatRoomListPage = inChatRoomListPage;
  }

  public boolean isInChatRoomPage() {
    return isInChatRoomPage;
  }

  public void setInChatRoomPage(boolean inChatRoomPage) {
    isInChatRoomPage = inChatRoomPage;
  }

  public String getCurrentChatRoomId() {
    return currentChatRoomId;
  }

  public void setCurrentChatRoomId(String currentChatRoomId) {
    this.currentChatRoomId = currentChatRoomId;
  }

}

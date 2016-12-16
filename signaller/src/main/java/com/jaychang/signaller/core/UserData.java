package com.jaychang.signaller.core;

public class UserData {

  private static final UserData INSTANCE = new UserData();
  private static String serverDomain;
  private static String socketUrl;
  private static boolean isInChatRoomPage;
  private static String currentChatRoomId;
  private static String accessToken;
  private static String userId;

  private UserData() {
  }

  public static UserData getInstance() {
    return INSTANCE;
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

  public String getAccessToken() {
    return accessToken;
  }

  public void setAccessToken(String accessToken) {
    this.accessToken = accessToken;
  }

  public String getUserId() {
    return userId;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }

  public String getSocketUrl() {
    return socketUrl;
  }

  public void setSocketUrl(String socketUrl) {
    this.socketUrl = socketUrl;
  }

  public String getServerDomain() {
    return serverDomain;
  }

  public void setServerDomain(String serverDomain) {
    this.serverDomain = serverDomain;
  }
}

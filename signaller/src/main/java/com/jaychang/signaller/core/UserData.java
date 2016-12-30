package com.jaychang.signaller.core;

public class UserData {

  private static final UserData INSTANCE = new UserData();
  private boolean isInChatRoomPage;
  private String currentChatRoomId;
  private String accessToken;
  private String userId;

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

}

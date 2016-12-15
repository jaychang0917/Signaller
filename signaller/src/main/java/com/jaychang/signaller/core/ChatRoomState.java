package com.jaychang.signaller.core;

class ChatRoomState {

  private static final ChatRoomState INSTANCE = new ChatRoomState();
  private boolean isInChatRoomListPage;
  private boolean isInChatRoomPage;
  private String currentChatRoomId;

  private ChatRoomState() {
  }

  public static ChatRoomState getInstance() {
    return INSTANCE;
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

package com.jaychang.signaller.core;

public class ChatRoomMeta {

  private static final ChatRoomMeta INSTANCE = new ChatRoomMeta();

  private ChatRoomMeta() {
  }

  private String cursor;
  private boolean hasMoreData;
  private int totalUnreadCount;

  public static ChatRoomMeta getInstance() {
    return INSTANCE;
  }

  public void clear() {
    cursor = null;
    hasMoreData = false;
    totalUnreadCount = -1;
  }

  public String getCursor() {
    return cursor;
  }

  public void setCursor(String cursor) {
    this.cursor = cursor;
  }

  public boolean hasMoreData() {
    return hasMoreData;
  }

  public void setHasMoreData(boolean hasMoreData) {
    this.hasMoreData = hasMoreData;
  }

  public int getTotalUnreadCount() {
    return totalUnreadCount;
  }

  public void setTotalUnreadCount(int totalUnreadCount) {
    this.totalUnreadCount = totalUnreadCount;
  }

}

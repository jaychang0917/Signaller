package com.redso.signaller.core.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ChatRoomResponse {

  @SerializedName("num_results")
  public int numResults;
  @SerializedName("cursor")
  public String cursor;
  @SerializedName("total_unread_count")
  public int totalUnreadCount;
  @SerializedName("num_returned")
  public int numReturned;
  @SerializedName("more")
  public boolean hasMore;
  @SerializedName("results")
  public List<ChatRoom> chatRooms;

}

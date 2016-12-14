package com.jaychang.signaller.core.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import io.realm.RealmList;

public class ChatMessageResponse {

  @SerializedName("cursor")
  public String cursor;
  @SerializedName("num_results")
  public int numResults;
  @SerializedName("num_returned")
  public int numReturned;
  @SerializedName("more")
  public boolean hasMore;
  @SerializedName("results")
  public List<ChatMessage> chatMessages;

}

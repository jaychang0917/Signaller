package com.jaychang.signaller.core.model;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;

public class SocketChatMessage extends RealmObject {

  @SerializedName("room_id")
  public String roomId;
  @SerializedName("message")
  public ChatMessage message;
  @SerializedName("payload")
  public Payload payload;

}

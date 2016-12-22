package com.jaychang.signaller.core.model;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class SignallerSocketChatMessage extends RealmObject {

  @PrimaryKey
  private long timestamp;
  @SerializedName("room_id")
  private String roomId;
  @SerializedName("message")
  private SignallerChatMessage message;
  @SerializedName("payload")
  private SignallerPayload payload;

  public long getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(long timestamp) {
    this.timestamp = timestamp;
  }

  public String getRoomId() {
    return roomId;
  }

  public void setRoomId(String roomId) {
    this.roomId = roomId;
  }

  public SignallerChatMessage getMessage() {
    return message;
  }

  public void setMessage(SignallerChatMessage message) {
    this.message = message;
  }

  public SignallerPayload getPayload() {
    return payload;
  }

  public void setPayload(SignallerPayload payload) {
    this.payload = payload;
  }

}

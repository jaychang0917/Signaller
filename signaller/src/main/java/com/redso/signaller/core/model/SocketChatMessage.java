package com.redso.signaller.core.model;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class SocketChatMessage extends RealmObject {

  @PrimaryKey
  private long timestamp;
  @SerializedName("room_id")
  private String roomId;
  @SerializedName("message")
  private ChatMessage message;
  @SerializedName("payload")
  private String payloadJson;
  private Payload payloadModel;

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

  public ChatMessage getMessage() {
    return message;
  }

  public void setMessage(ChatMessage message) {
    this.message = message;
  }

  public String getPayloadJson() {
    return payloadJson;
  }

  public void setPayloadJson(String payloadJson) {
    this.payloadJson = payloadJson;
  }

  public Payload getPayloadModel() {
    return payloadModel;
  }

  public void setPayloadModel(Payload payloadModel) {
    this.payloadModel = payloadModel;
  }

}

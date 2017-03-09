package com.redso.signaller.core.model;

import com.google.gson.annotations.SerializedName;
import com.redso.signaller.core.UserData;

import java.util.Calendar;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class SignallerChatMessage extends RealmObject {

  @PrimaryKey
  @SerializedName("id")
  private String msgId;
  @SerializedName("chatroom_id")
  private String chatroomId;
  @SerializedName("ctime")
  private long ctime;
  @SerializedName("content")
  private String content;
  @SerializedName("image")
  private SignallerImage image;
  @SerializedName("mtime")
  private long mtime;
  @SerializedName("type")
  private String type;
  @SerializedName("sender")
  private SignallerSender sender;
  private boolean isSent = true;
  private long timestamp;

  public boolean isSameSender(SignallerChatMessage message) {
    return sender.getUserId().equals(message.sender.getUserId());
  }

  public boolean isOwnMessage() {
    return sender.getUserId().equals(UserData.getInstance().getUserId());
  }

  public boolean isSameDate(SignallerChatMessage message) {
    Calendar cal1 = Calendar.getInstance();
    Calendar cal2 = Calendar.getInstance();
    cal1.setTimeInMillis(mtime == 0L ? timestamp : mtime);
    cal2.setTimeInMillis(message.mtime == 0L ? message.timestamp : message.mtime);
    return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
      cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
  }

  public boolean isText() {
    return type.equals("text");
  }

  public boolean isImage() {
    return type.equals("image");
  }

  public boolean isCustomType() {
    return !isText() && !isImage();
  }

  //region getters & setters
  public String getMsgId() {
    return msgId;
  }

  public void setMsgId(String msgId) {
    this.msgId = msgId;
  }

  public String getChatroomId() {
    return chatroomId;
  }

  public void setChatroomId(String chatroomId) {
    this.chatroomId = chatroomId;
  }

  public long getCtime() {
    return ctime;
  }

  public void setCtime(long ctime) {
    this.ctime = ctime;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public SignallerImage getImage() {
    return image;
  }

  public void setImage(SignallerImage image) {
    this.image = image;
  }

  public long getMsgTime() {
    return mtime;
  }

  public void setMsgTime(long time) {
    this.mtime = time;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public SignallerSender getSender() {
    return sender;
  }

  public void setSender(SignallerSender sender) {
    this.sender = sender;
  }

  public boolean isSent() {
    return isSent;
  }

  public void setSent(boolean sent) {
    isSent = sent;
  }

  public long getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(long timestamp) {
    this.timestamp = timestamp;
  }
  //endregion


  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder("SignallerChatMessage{");
    sb.append("msgId='").append(msgId).append('\'');
    sb.append(", content='").append(content).append('\'');
    sb.append('}');
    return sb.toString();
  }

}


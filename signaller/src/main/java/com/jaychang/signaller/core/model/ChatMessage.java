package com.jaychang.signaller.core.model;

import android.media.Image;

import com.google.gson.annotations.SerializedName;
import com.jaychang.signaller.core.Signaller;

import java.util.Calendar;

import io.realm.RealmObject;

public class ChatMessage extends RealmObject {

  public boolean isSent;
  public String msgUUID;
  public String userId;
  @SerializedName("chatroom_id")
  public String chatroomId;
  @SerializedName("ctime")
  public long ctime;
  @SerializedName("content")
  public String content;
  @SerializedName("image")
  public Image image;
  @SerializedName("event")
  public Event event;
  @SerializedName("mtime")
  public long mtime;
  @SerializedName("type")
  private String type;
  @SerializedName("id")
  public String msgId;
  @SerializedName("sender")
  public Sender sender;

  public boolean isSameSender(ChatMessage message) {
    return sender.userId.equals(message.sender.userId);
  }

  public boolean isOwnMessage() {
    return sender.userId.equals(Signaller.getInstance().getUserId());
  }

  public boolean isSameDate(ChatMessage message) {
    Calendar cal1 = Calendar.getInstance();
    Calendar cal2 = Calendar.getInstance();
    cal1.setTimeInMillis(mtime);
    cal2.setTimeInMillis(message.mtime);
    return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
      cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
  }

  public boolean isImage() {
    return type.equals("image");
  }

  public boolean isEvent() {
    return type.equals("event");
  }

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder("ChatMessage{");
    sb.append("msgId='").append(msgId).append('\'');
    sb.append('}');
    return sb.toString();
  }

}


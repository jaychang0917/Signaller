package com.redso.signaller.core.model;

import java.io.Serializable;

import io.realm.RealmObject;

public class SignallerPayload extends RealmObject implements Serializable{

  private long timestamp;
  private int messageCellIndex;

  public long getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(long timestamp) {
    this.timestamp = timestamp;
  }

  public int getMessageCellIndex() {
    return messageCellIndex;
  }

  public void setMessageCellIndex(int messageCellIndex) {
    this.messageCellIndex = messageCellIndex;
  }

}

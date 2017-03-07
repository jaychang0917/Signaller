package com.redso.signaller.core.model;

import java.io.Serializable;

import io.realm.RealmObject;

public class SignallerPayload extends RealmObject implements Serializable{

  private long timestamp;

  public long getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(long timestamp) {
    this.timestamp = timestamp;
  }

}

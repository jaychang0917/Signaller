package com.jaychang.signaller.core.model;

import io.realm.RealmObject;

public class SignallerPayload extends RealmObject {

  private long timestamp;

  public long getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(long timestamp) {
    this.timestamp = timestamp;
  }

}

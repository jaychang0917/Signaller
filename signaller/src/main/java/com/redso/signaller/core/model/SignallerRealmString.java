package com.redso.signaller.core.model;

import io.realm.RealmObject;

public class SignallerRealmString extends RealmObject {
  private String val;

  public SignallerRealmString() {
  }

  public SignallerRealmString(String val) {
    this.val = val;
  }

  public String getVal() {
    return val;
  }

  public void setVal(String val) {
    this.val = val;
  }
}

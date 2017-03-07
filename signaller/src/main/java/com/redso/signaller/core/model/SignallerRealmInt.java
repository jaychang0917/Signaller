package com.redso.signaller.core.model;

import io.realm.RealmObject;

public class SignallerRealmInt extends RealmObject {

  private int value;

  public SignallerRealmInt() {
  }

  public SignallerRealmInt(int value) {
    this.value = value;
  }

  public int getValue() {
    return this.value;
  }

  public void setValue(int value) {
    this.value = value;
  }

}
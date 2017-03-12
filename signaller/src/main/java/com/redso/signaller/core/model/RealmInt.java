package com.redso.signaller.core.model;

import io.realm.RealmObject;

public class RealmInt extends RealmObject {

  private int value;

  public RealmInt() {
  }

  public RealmInt(int value) {
    this.value = value;
  }

  public int getValue() {
    return this.value;
  }

  public void setValue(int value) {
    this.value = value;
  }

}
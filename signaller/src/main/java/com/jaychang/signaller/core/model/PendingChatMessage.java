package com.jaychang.signaller.core.model;

import io.realm.RealmObject;

public class PendingChatMessage extends RealmObject {
  public SocketChatMessage socketChatMessage;
}

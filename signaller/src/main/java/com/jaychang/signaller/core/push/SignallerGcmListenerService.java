package com.jaychang.signaller.core.push;

import android.os.Bundle;

import com.google.android.gms.gcm.GcmListenerService;
import com.jaychang.signaller.util.LogUtils;

public class SignallerGcmListenerService extends GcmListenerService {

  /**
   * Called when message is received.
   *
   * @param from SenderID of the sender.
   * @param data Data bundle containing message data as key/value pairs.
   *             For Set of keys use data.keySet().
   */
  @Override
  public void onMessageReceived(String from, Bundle data) {
    LogUtils.d("onMessageReceived");

    String message = data.getString("content");

    for (String key : data.keySet()) {
      LogUtils.d("GCM:data->key:" + key + " value:" + data.get(key));
    }

    String chatRoomId = "123";

    SignallerNotificationManager.showNotification(message, chatRoomId);
    LogUtils.d("show push notification:" + message);
  }

}
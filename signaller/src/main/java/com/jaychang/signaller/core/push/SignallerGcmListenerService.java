package com.jaychang.signaller.core.push;

import android.os.Bundle;

import com.google.android.gms.gcm.GcmListenerService;
import com.jaychang.signaller.core.UserData;
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
    LogUtils.d("GCM:onMessageReceived");

    for (String key : data.keySet()) {
      LogUtils.d("GCM:data->key:" + key + " value:" + data.get(key));
    }

    String message = data.getString("content");
    // todo
    String userId = "5745865499082752";
    String title = "todo";

    String ownUserId = UserData.getInstance().getUserId();
    String chatRoomId = ownUserId.compareTo(userId) < 0 ?
      ownUserId + "_" + userId :
      userId + "_" + ownUserId;

    SignallerNotificationManager.showNotification(message, userId, chatRoomId, title);
    LogUtils.d("[GCM]show push notification:" + message);
  }

}
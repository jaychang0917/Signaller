package com.wiser.kol;

import android.os.Bundle;

import com.jaychang.signaller.core.push.SignallerPushNotificationService;

public class PushNotificationService extends SignallerPushNotificationService {

  @Override
  public void onMessageReceived(String from, Bundle data) {
    super.onMessageReceived(from, data);

    // app push notification handling
  }

}

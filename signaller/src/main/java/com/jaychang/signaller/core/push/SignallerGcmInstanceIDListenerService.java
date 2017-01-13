package com.jaychang.signaller.core.push;

import android.content.Intent;

import com.google.android.gms.iid.InstanceIDListenerService;

public class SignallerGcmInstanceIDListenerService extends InstanceIDListenerService {

  @Override
  public void onTokenRefresh() {
    Intent intent = new Intent(this, SignallerGcmRegistrationService.class);
    startService(intent);
  }

}
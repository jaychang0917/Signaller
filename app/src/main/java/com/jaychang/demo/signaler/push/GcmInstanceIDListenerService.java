package com.jaychang.demo.signaler.push;

import android.content.Intent;

import com.google.android.gms.iid.InstanceIDListenerService;

public class GcmInstanceIDListenerService extends InstanceIDListenerService {

  @Override
  public void onTokenRefresh() {
    Intent intent = new Intent(this, GcmRegistrationService.class);
    startService(intent);
  }
}
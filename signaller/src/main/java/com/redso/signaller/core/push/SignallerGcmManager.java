package com.redso.signaller.core.push;

import android.content.Context;
import android.content.Intent;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.jaychang.utils.PreferenceUtils;
import com.redso.signaller.util.LogUtils;

public class SignallerGcmManager {

  public static final String GCM_TOKEN = SignallerGcmManager.class.getPackage().getName() + "GCM_TOKEN";

  public static void register(Context context) {
    if (checkPlayServices(context)) {
      Intent intent = new Intent(context, SignallerGcmRegistrationService.class);
      intent.setAction(SignallerGcmRegistrationService.ACTION_REGISTER);
      context.startService(intent);
    }
  }

  public static void unregister(Context context) {
    String token = PreferenceUtils.getString(context, GCM_TOKEN);
    if (!token.isEmpty()) {
      Intent intent = new Intent(context, SignallerGcmRegistrationService.class);
      intent.setAction(SignallerGcmRegistrationService.ACTION_UNREGISTER);
      intent.putExtra(SignallerGcmRegistrationService.EXTRA_TOKEN, token);
      context.startService(intent);
    }
  }

  private static boolean checkPlayServices(Context context) {
    GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
    int resultCode = apiAvailability.isGooglePlayServicesAvailable(context);
    if (resultCode != ConnectionResult.SUCCESS) {
      LogUtils.d("GCM:Google play service is not installed.");
      return false;
    }
    return true;
  }

}
package com.wiser.kol.push;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

public class GcmManager {

  public static final String IS_GCM_REGISTERED = "IS_GCM_REGISTERED";

  public static void init(Context context) {
    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    boolean isRegistered = sharedPreferences.getBoolean(IS_GCM_REGISTERED, false);
    if (!isRegistered) {
      if (checkPlayServices(context)) {
        Intent intent = new Intent(context, GcmRegistrationService.class);
        context.startService(intent);
      }
    }
  }

  private static boolean checkPlayServices(Context context) {
    GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
    int resultCode = apiAvailability.isGooglePlayServicesAvailable(context);
    if (resultCode != ConnectionResult.SUCCESS) {
      Log.d("GcmManager", "GCM:Google play service is not installed.");
      return false;
    }
    return true;
  }
}
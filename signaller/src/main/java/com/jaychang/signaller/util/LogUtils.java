package com.jaychang.signaller.util;

import android.util.Log;

import com.jaychang.signaller.BuildConfig;

public class LogUtils {

  private static final String TAG = "signaller";

  public static void d(String message) {
    if (!BuildConfig.DEBUG) {
      return;
    }
    Log.d(TAG, message);
  }

  public static void e(String message) {
    if (!BuildConfig.DEBUG) {
      return;
    }
    Log.e(TAG, message);
  }

}

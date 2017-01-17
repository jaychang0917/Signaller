package com.jaychang.signaller.util;

import android.util.Log;

public class LogUtils {

  private static final String TAG = "signaller";
  private static boolean enable;

  public static void enable() {
    enable = true;
  }

  public static void d(String message) {
    if (!enable) {
      return;
    }
    Log.d(TAG, message);
  }

  public static void e(String message) {
    if (!enable) {
      return;
    }
    Log.e(TAG, message);
  }

}

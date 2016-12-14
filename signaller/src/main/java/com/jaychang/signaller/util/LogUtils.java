package com.jaychang.signaller.util;

import android.util.Log;

public class LogUtils {

  public static boolean enable = true;
  private static final String TAG = "signaller";

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

  public static void setEnable(boolean enable) {
    LogUtils.enable = enable;
  }
}

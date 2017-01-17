package com.jaychang.signaller.util;

import android.util.Log;

public final class LogUtils {

  private static final String TAG = "signaller";
  private static boolean enable = false;

  private LogUtils() {
  }

  public static boolean isEnable() {
    return enable;
  }

  public static void setEnable(boolean enable) {
    LogUtils.enable = enable;
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

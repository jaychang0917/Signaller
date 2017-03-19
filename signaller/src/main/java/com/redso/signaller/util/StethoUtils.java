package com.redso.signaller.util;

import android.content.Context;

import com.facebook.stetho.Stetho;

public final class StethoUtils {

  private StethoUtils() {
  }

  private static boolean enable = true;

  public static void init(Context context) {
    Stetho.initializeWithDefaults(context);
  }

  public static boolean isEnable() {
    return enable;
  }

  public static void setEnable(boolean enable) {
    StethoUtils.enable = enable;
  }

}

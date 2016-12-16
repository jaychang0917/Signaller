package com.jaychang.signaller.util;

import android.content.Context;

import com.facebook.stetho.Stetho;

public class StethoUtils {

  public static boolean debug = true;

  public static void init(Context appContext) {
    if (StethoUtils.debug) {
      Stetho.initializeWithDefaults(appContext);
    }
  }

}

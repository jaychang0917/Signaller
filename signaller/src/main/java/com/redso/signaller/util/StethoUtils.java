package com.redso.signaller.util;

public final class StethoUtils {

  private StethoUtils() {
  }

  private static boolean enable = true;

  public static boolean isEnable() {
    return enable;
  }

  public static void setEnable(boolean enable) {
    StethoUtils.enable = enable;
  }

}

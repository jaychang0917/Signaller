package com.redso.signaller.demo;

import android.content.Context;
import android.widget.Toast;

import com.facebook.stetho.Stetho;

public class Utils {

  public static boolean debug = true;

  public static void init(Context appContext) {
    if (debug) {
      Stetho.initializeWithDefaults(appContext);
    }
  }

  public static void showToast(Context context, String msg) {
    Toast.makeText(context.getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
  }

}

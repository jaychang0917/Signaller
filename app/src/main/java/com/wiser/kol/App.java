package com.wiser.kol;

import android.support.multidex.MultiDexApplication;

import com.jaychang.signaller.core.Signaller;

public class App extends MultiDexApplication {

  public static String currentUserId;

  @Override
  public void onCreate() {
    super.onCreate();

    Utils.init(this);

    Signaller.init(this, Constant.SERVER_DOMAIN, Constant.SOCKETE_URL);
  }
}

package com.jaychang.demo.signaler;

import android.app.Application;

import com.jaychang.signaller.core.Signaller;

public class App extends Application {

  public static String currentUserId;

  @Override
  public void onCreate() {
    super.onCreate();

    Utils.init(this);

    Signaller.init(this, Constant.SERVER_DOMAIN, Constant.SOCKETE_URL);
  }
}

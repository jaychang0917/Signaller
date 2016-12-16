package com.jaychang.demo.signaler;

import android.app.Application;

import com.jaychang.signaller.core.Signaller;

public class App extends Application {

  @Override
  public void onCreate() {
    super.onCreate();
    Signaller.init(getApplicationContext(), Constant.SERVER_DOMAIN, Constant.SOCKETE_URL);
  }
}

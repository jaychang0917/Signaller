package com.jaychang.signaller.core;

import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;

public final class AppConfig {

  private int appName;
  private int appIcon;
  private String serverDomain;
  private String socketUrl;
  private String pushSenderId;

  public AppConfig(@StringRes int appName,
                   @DrawableRes int appIcon,
                   String serverDomain,
                   String socketUrl,
                   String pushSenderId) {
    this.appName = appName;
    this.appIcon = appIcon;
    this.serverDomain = serverDomain;
    this.socketUrl = socketUrl;
    this.pushSenderId = pushSenderId;
  }

  public int getAppName() {
    return appName;
  }

  public int getAppIcon() {
    return appIcon;
  }

  public String getServerDomain() {
    return serverDomain;
  }

  public String getSocketUrl() {
    return socketUrl;
  }

  public String getPushSenderId() {
    return pushSenderId;
  }

}

package com.jaychang.signaller.core;

import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;

public final class AppConfig {

  private int appName;
  private int appIcon;
  private String serverDomain;
  private String socketUrl;
  private String pushNotificationSenderId;
  private Class<?> pushNotificationParentStack;

  public AppConfig(@StringRes int appName,
                   @DrawableRes int appIcon,
                   String serverDomain,
                   String socketUrl,
                   String pushNotificationSenderId,
                   Class<?> pushNotificationParentStack) {
    this.appName = appName;
    this.appIcon = appIcon;
    this.serverDomain = serverDomain;
    this.socketUrl = socketUrl;
    this.pushNotificationSenderId = pushNotificationSenderId;
    this.pushNotificationParentStack = pushNotificationParentStack;
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

  public String getPushNotificationSenderId() {
    return pushNotificationSenderId;
  }

  public Class<?> getPushNotificationParentStack() {
    return pushNotificationParentStack;
  }

}

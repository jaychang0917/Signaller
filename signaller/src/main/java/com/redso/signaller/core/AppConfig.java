package com.redso.signaller.core;

import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;

public final class AppConfig {

  private String socketUrl;
  private String serverDomain;
  private int appName;
  private int appIcon;
  private String pushNotificationSenderId;
  private Class<?> pushNotificationParentActivity;

  private AppConfig(Builder builder) {
    socketUrl = builder.socketUrl;
    serverDomain = builder.serverDomain;
    appName = builder.appName;
    appIcon = builder.appIcon;
    pushNotificationSenderId = builder.senderId;
    pushNotificationParentActivity = builder.parentActivity;
  }

  public static Builder newBuilder(String socketUrl, String serverDomain) {
    return new Builder(socketUrl, serverDomain);
  }


  public static final class Builder {
    private final String socketUrl;
    private final String serverDomain;
    private int appName;
    private int appIcon;
    private String senderId;
    private Class<?> parentActivity;

    private Builder(String socketUrl, String serverDomain) {
      this.socketUrl = socketUrl;
      this.serverDomain = serverDomain;
    }

    public Builder enablePushNotification(@StringRes int appName,
                                          @DrawableRes int appIcon,
                                          String senderId,
                                          Class<?> parentActivity) {
      this.appName = appName;
      this.appIcon = appIcon;
      this.senderId = senderId;
      this.parentActivity = parentActivity;
      return this;
    }

    public AppConfig build() {
      return new AppConfig(this);
    }
  }

  public String getSocketUrl() {
    return socketUrl;
  }

  public String getServerDomain() {
    return serverDomain;
  }

  public int getAppName() {
    return appName;
  }

  public int getAppIcon() {
    return appIcon;
  }

  public String getPushNotificationSenderId() {
    return pushNotificationSenderId;
  }

  public Class<?> getPushNotificationParentActivity() {
    return pushNotificationParentActivity;
  }

}

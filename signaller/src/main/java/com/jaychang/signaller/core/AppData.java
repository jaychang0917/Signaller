package com.jaychang.signaller.core;

import android.content.Context;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;

public final class AppData {

  private static final AppData INSTANCE = new AppData();
  private int appName;
  private int appIcon;
  private String serverDomain;
  private String socketUrl;
  private Context appContext;

  private AppData() {
  }

  public static AppData getInstance() {
    return INSTANCE;
  }

  public int getAppIcon() {
    return appIcon;
  }

  public void setAppIcon(@DrawableRes int appIcon) {
    this.appIcon = appIcon;
  }

  public int getAppName() {
    return appName;
  }

  public void setAppName(@StringRes int appName) {
    this.appName = appName;
  }

  public String getSocketUrl() {
    return socketUrl;
  }

  public void setSocketUrl(String socketUrl) {
    this.socketUrl = socketUrl;
  }

  public String getServerDomain() {
    return serverDomain;
  }

  public void setServerDomain(String serverDomain) {
    this.serverDomain = serverDomain;
  }

  public Context getAppContext() {
    return appContext;
  }

  public void setAppContext(Context appContext) {
    this.appContext = appContext;
  }

}

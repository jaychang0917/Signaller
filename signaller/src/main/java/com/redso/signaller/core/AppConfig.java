package com.redso.signaller.core;

import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;

import com.redso.signaller.ui.ChatRoomActivity;

public final class AppConfig {

  private String socketUrl;
  private String serverDomain;
  private int appName;
  private int appIcon;
  private String pushNotificationSenderId;
  private Class<?> chatRoomActivity;
  private Class<?> chatRoomParentActivity;

  private AppConfig(Builder builder) {
    socketUrl = builder.socketUrl;
    serverDomain = builder.serverDomain;
    appName = builder.appName;
    appIcon = builder.appIcon;
    pushNotificationSenderId = builder.senderId;
    chatRoomActivity = builder.chatRoomActivity;
    chatRoomParentActivity = builder.chatRoomParentActivity;
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
    private Class<?> chatRoomParentActivity;
    private Class<?> chatRoomActivity;

    private Builder(String socketUrl, String serverDomain) {
      this.socketUrl = socketUrl;
      this.serverDomain = serverDomain;
    }

    public Builder enablePushNotification(@StringRes int appName,
                                          @DrawableRes int appIcon,
                                          String senderId,
                                          Class<?> chatRoomActivity,
                                          Class<?> chatRoomParentActivity) {
      this.appName = appName;
      this.appIcon = appIcon;
      this.senderId = senderId;
      this.chatRoomActivity = chatRoomActivity;
      this.chatRoomParentActivity = chatRoomParentActivity;
      return this;
    }

    public Builder enablePushNotification(@StringRes int appName,
                                          @DrawableRes int appIcon,
                                          String senderId,
                                          Class<?> chatRoomParentActivity) {
      return enablePushNotification(appName, appIcon, senderId, null, chatRoomParentActivity);
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

  public Class<?> getChatRoomParentActivity() {
    return chatRoomParentActivity;
  }

  public Class<?> getChatRoomActivity() {
    if (chatRoomActivity != null) {
      return chatRoomActivity;
    }
    return ChatRoomActivity.class;
  }

}

package com.jaychang.signaller.core;

import android.app.Application;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;

import com.jaychang.signaller.core.push.SignallerGcmManager;
import com.jaychang.signaller.ui.config.UIConfig;
import com.jaychang.signaller.util.LogUtils;
import com.jaychang.utils.AppStatusUtils;

public final class Signaller {

  private static final Signaller INSTANCE = new Signaller();
  private UIConfig uiConfig;

  private Signaller() {
  }

  public static void init(Application app,
                          String serverDomain,
                          String socketUrl,
                          @StringRes int appName,
                          @DrawableRes int appIcon) {
    AppData.getInstance().setServerDomain(serverDomain);
    AppData.getInstance().setSocketUrl(socketUrl);
    AppData.getInstance().setAppName(appName);
    AppData.getInstance().setAppIcon(appIcon);

    registerAppCallback(app);
    SignallerDbManager.getInstance().init(app.getApplicationContext());
    SignallerGcmManager.init(app.getApplicationContext());
  }

  public static Signaller getInstance() {
    return INSTANCE;
  }

  private static void registerAppCallback(Application app) {
    AppStatusUtils.registerAppStatusCallback(app, new AppStatusUtils.Callback() {
      @Override
      public void onAppEnterBackground() {
        SocketManager.getInstance().disconnect();
        LogUtils.d("onAppEnterBackground, disconnect socket");
      }

      @Override
      public void onAppEnterForeground() {
        SocketManager.getInstance().connect();
        LogUtils.d("onAppEnterForeground, connect socket");
      }
    });
  }

  public void connect(String accessToken, String userId) {
    UserData.getInstance().setAccessToken(accessToken);
    UserData.getInstance().setUserId(userId);

    SocketManager.getInstance().initSocket(accessToken);
    SocketManager.getInstance().connect();
  }

  public void disconnect() {
    SocketManager.getInstance().disconnect();
  }

  public void chatWith(String userId, ChatRoomJoinCallback callback) {
    SocketManager.getInstance().join(userId, makeChatRoomId(userId), callback);
  }

  public void leaveChatRoom(String chatRoomId, ChatRoomLeaveCallback callback) {
    SocketManager.getInstance().leave(chatRoomId, callback);
  }

  private String makeChatRoomId(String userId) {
    String ownUserId = UserData.getInstance().getUserId();
    return ownUserId.compareTo(userId) < 0 ?
      ownUserId + "_" + userId :
      userId + "_" + ownUserId;
  }

  public void setUIConfig(UIConfig config) {
    this.uiConfig = config;
  }

  public UIConfig getUiConfig() {
    if (uiConfig == null) {
      return UIConfig.newBuilder().build();
    }
    return uiConfig;
  }

  public void clearChatCache() {
    SignallerDbManager.getInstance().clear();
  }

}

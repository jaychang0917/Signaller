package com.jaychang.signaller.core;

import android.app.Application;
import android.content.Context;

import com.jaychang.signaller.core.push.SignallerGcmManager;
import com.jaychang.signaller.ui.ChatRoomActivity;
import com.jaychang.signaller.util.LogUtils;
import com.jaychang.signaller.util.StethoUtils;
import com.jaychang.utils.AppStatusUtils;

public final class Signaller {

  private static final Signaller INSTANCE = new Signaller();
  private Context appContext;
  private AppConfig appConfig;
  private UIConfig uiConfig;

  private Signaller() {
  }

  public static void init(Application app, AppConfig appConfig, UIConfig uiConfig) {
    INSTANCE.appContext = app.getApplicationContext();
    INSTANCE.appConfig = appConfig;
    INSTANCE.uiConfig = uiConfig;

    registerAppCallback(app);

    SignallerDbManager.getInstance().init(app.getApplicationContext());
  }

  public static void init(Application app, AppConfig appConfig) {
    init(app, appConfig, null);
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

    SignallerGcmManager.register(appContext);
  }

  public void disconnect() {
    SocketManager.getInstance().disconnect();
  }

  public void chatWith(Context context, String userId, String toolbarTitle) {
    if (!SocketManager.getInstance().isConnected()) {
      SocketManager.getInstance().connect(new SocketConnectionCallbacks() {
        @Override
        void onConnected() {
          if (context == null) {
            return;
          }
          chatWithInternal(context, userId, toolbarTitle);
        }
      });
    } else {
      chatWithInternal(context, userId, toolbarTitle);
    }
  }

  private void chatWithInternal(Context context, String userId, String toolbarTitle) {
    String ownUserId = UserData.getInstance().getUserId();
    String chatRoomId = ownUserId.compareTo(userId) < 0 ?
      ownUserId + "_" + userId :
      userId + "_" + ownUserId;

    SocketManager.getInstance().join(userId, chatRoomId, new ChatRoomJoinCallback() {
      @Override
      public void onChatRoomJoined(String chatRoomId, String userId) {
        ChatRoomActivity.start(context, chatRoomId, userId, toolbarTitle);
      }
    });
  }

  public void leaveChatRoom(String chatRoomId, ChatRoomLeaveCallback callback) {
    SocketManager.getInstance().leave(chatRoomId, callback);
  }

  public Context getAppContext() {
    return appContext;
  }

  public UIConfig getUiConfig() {
    if (uiConfig == null) {
      return UIConfig.newBuilder().build();
    }
    return uiConfig;
  }

  public AppConfig getAppConfig() {
    return appConfig;
  }

  public void clearChatCache() {
    SignallerDbManager.getInstance().clear();
    SignallerGcmManager.unregister(appContext);
    UserData.getInstance().clear();
    ChatRoomMeta.clear();
  }

  public void enableDebug(boolean enable) {
    LogUtils.setEnable(enable);
    StethoUtils.setEnable(enable);
  }

}

package com.redso.signaller.core;

import android.app.Application;
import android.content.Context;
import android.net.Uri;

import com.jaychang.utils.AppStatusUtils;
import com.redso.signaller.core.push.SignallerGcmManager;
import com.redso.signaller.ui.ChatRoomActivity;
import com.redso.signaller.ui.ChatRoomFragmentProxy;
import com.redso.signaller.ui.UIConfig;
import com.redso.signaller.util.LogUtils;
import com.redso.signaller.util.StethoUtils;

public final class Signaller {

  public interface ChatRoomMetaCallback {
    void onChatRoomMetaReady(int count);
  }

  private static final Signaller INSTANCE = new Signaller();
  private Context appContext;
  private AppConfig appConfig;
  private UIConfig uiConfig;
  private Application app;

  private Signaller() {
  }

  public static void init(Application app, AppConfig appConfig, UIConfig uiConfig) {
    INSTANCE.app = app;
    INSTANCE.appContext = app.getApplicationContext();
    INSTANCE.appConfig = appConfig;
    INSTANCE.uiConfig = uiConfig;

    DatabaseManager.getInstance().init(app.getApplicationContext());
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

  private static void unregisterAppCallback(Application app) {
    AppStatusUtils.unregisterAppStatusCallback(app);
  }

  public boolean isPushNotificationEnabled() {
    String pushNotificationSenderId = appConfig.getPushNotificationSenderId();
    return pushNotificationSenderId != null && !pushNotificationSenderId.isEmpty();
  }

  public void connect(String accessToken, String userId, SocketConnectionCallbacks callbacks) {
    UserData.getInstance().setAccessToken(accessToken);
    UserData.getInstance().setUserId(userId);
    SocketManager.getInstance().initSocket(accessToken);
    SocketManager.getInstance().connect(callbacks);
    registerAppCallback(app);
    if (isPushNotificationEnabled()) {
      SignallerGcmManager.register(appContext);
    }
  }

  public void connect(String accessToken, String userId) {
    connect(accessToken, userId, null);
  }

  public void disconnect() {
    SocketManager.getInstance().disconnect();
    SocketManager.getInstance().invalidate();
    DatabaseManager.getInstance().clear();
    if (isPushNotificationEnabled()) {
      SignallerGcmManager.unregister(appContext);
    }
    unregisterAppCallback(app);
    UserData.getInstance().clear();
    ChatRoomMeta.getInstance().clear();
  }

  public void chatWith(Context context, String receiverId, String toolbarTitle) {
    if (!SocketManager.getInstance().isConnected()) {
      SocketManager.getInstance().connect(new SocketConnectionCallbacks() {
        @Override
        void onConnected() {
          if (context == null) {
            return;
          }
          chatWithInternal(context, receiverId, toolbarTitle);
        }
      });
    } else {
      chatWithInternal(context, receiverId, toolbarTitle);
    }
  }

  private void chatWithInternal(Context context, String receiverId, String toolbarTitle) {
    String ownUserId = UserData.getInstance().getUserId();
    String chatRoomId = ownUserId.compareTo(receiverId) < 0 ?
      ownUserId + "_" + receiverId :
      receiverId + "_" + ownUserId;

    SocketManager.getInstance().join(receiverId, chatRoomId, new ChatRoomJoinCallback() {
      @Override
      public void onChatRoomJoined(String chatRoomId, String userId) {
        ChatRoomActivity.start(context, chatRoomId, userId, toolbarTitle);
      }
    });
  }

  public void joinChatRoom(Context context, String chatRoomId, String toolbarTitle) {
    SocketManager.getInstance().join(chatRoomId, chatRoomId, null);
  }

  public void sendPhotoMessage(Uri uri) throws IllegalStateException {
    ChatRoomFragmentProxy proxy = ProxyManager.getInstance().getChatRoomFragmentProxy();
    if (proxy == null) {
      throw new IllegalStateException("You must call this method when you are in chat room page.");
    }

    proxy.sendPhotoMessage(uri);
  }

  public void sendTextMessage(String message) throws IllegalStateException {
    ChatRoomFragmentProxy proxy = ProxyManager.getInstance().getChatRoomFragmentProxy();
    if (proxy == null) {
      throw new IllegalStateException("You must call this method when you are in chat room page.");
    }

    proxy.sendTextMessage(message);
  }

  public void leaveChatRoom(String chatRoomId, ChatRoomLeaveCallback callback) {
    SocketManager.getInstance().leave(chatRoomId, callback);
  }

  public void getUnreadMessageCount(ChatRoomMetaCallback callback) {
    DataManager.getInstance().getChatRoomsFromNetwork(null)
      .subscribe(
        rooms -> {
          int totalUnreadCount = ChatRoomMeta.getInstance().getTotalUnreadCount();
          callback.onChatRoomMetaReady(totalUnreadCount);
          LogUtils.d("Unread message count: " + totalUnreadCount);
        },
        error -> {
          LogUtils.e("Fail to get unread message count: " + error.getMessage());
        });
  }

  public Context getAppContext() {
    return appContext;
  }

  public UIConfig getUiConfig() {
    return uiConfig;
  }

  public AppConfig getAppConfig() {
    return appConfig;
  }

  public void setDebugEnabled(boolean enable) {
    LogUtils.setEnable(enable);
    StethoUtils.setEnable(enable);
  }

}

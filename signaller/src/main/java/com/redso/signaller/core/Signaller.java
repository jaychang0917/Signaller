package com.redso.signaller.core;

import android.app.Application;
import android.content.Context;
import android.net.Uri;

import com.jaychang.utils.AppStatusUtils;
import com.redso.signaller.core.push.SignallerGcmManager;
import com.redso.signaller.ui.ChatRoomActivity;
import com.redso.signaller.ui.ChatRoomFragmentProxy;
import com.redso.signaller.ui.UIConfig;
import com.redso.signaller.util.ChatUtils;
import com.redso.signaller.util.LogUtils;
import com.redso.signaller.util.StethoUtils;

public final class Signaller {

  public interface UnreadMessageCountCallback {
    void onUnreadMessageCountReady(int count);
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
        LogUtils.d("App did enter background, disconnect socket");
      }

      @Override
      public void onAppEnterForeground() {
        SocketManager.getInstance().connect();
        LogUtils.d("App did enter foreground, connect socket");
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

  public void connect(String accessToken, String userId) {
    UserData.getInstance().setAccessToken(accessToken);
    UserData.getInstance().setUserId(userId);
    SocketManager.getInstance().initSocket(accessToken);
    SocketManager.getInstance().connect();
    registerAppCallback(app);
    if (isPushNotificationEnabled()) {
      SignallerGcmManager.register(appContext);
    }
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

  public void connectSocket(String accessToken) {
    SocketManager.getInstance().initSocket(accessToken);
    SocketManager.getInstance().connect();
  }

  public void connectSocket(String accessToken, ConnectSocketCallback callback) {
    SocketManager.getInstance().initSocket(accessToken);
    SocketManager.getInstance().connect(new SocketConnectionCallback() {
      @Override
      public void onConnect() {
        callback.onConnect();
      }

      @Override
      public void onConnecting() {
        callback.onConnecting();
      }

      @Override
      public void onConnected() {
        callback.onConnected();
      }
    });
  }

  public void disconnectSocket() {
    SocketManager.getInstance().disconnect();
  }

  public void disconnectSocket(DisconnectSocketCallback callback) {
    SocketManager.getInstance().disconnect(new SocketConnectionCallback() {
      @Override
      protected void onDisconnected() {
        callback.onDisconnected();
      }
    });
  }

  public void goToIndividualChatRoomPage(Context context, String targetUserId, String toolbarTitle) {
    if (!SocketManager.getInstance().isConnected()) {
      SocketManager.getInstance().connect(new SocketConnectionCallback() {
        @Override
        protected void onConnected() {
          if (context == null) {
            return;
          }
          goToIndividualChatRoomPageInternal(context, targetUserId, toolbarTitle);
        }
      });
    } else {
      goToIndividualChatRoomPageInternal(context, targetUserId, toolbarTitle);
    }
  }

  private void goToIndividualChatRoomPageInternal(Context context, String targetUserId, String toolbarTitle) {
    String chatRoomId = ChatUtils.createChatRoomId(UserData.getInstance().getUserId(), targetUserId);

    SocketManager.getInstance().join(targetUserId, chatRoomId, new ChatRoomJoinCallback() {
      @Override
      public void onChatRoomJoined(String chatRoomId) {
        ChatRoomActivity.start(context, chatRoomId, targetUserId, toolbarTitle);
      }
    });
  }

  public void goToGroupChatRoomPage(Context context, String groupChatId, String toolbarTitle) {
    if (!SocketManager.getInstance().isConnected()) {
      SocketManager.getInstance().connect(new SocketConnectionCallback() {
        @Override
        protected void onConnected() {
          if (context == null) {
            return;
          }
          goToIndividualChatRoomPageInternal(context, groupChatId, toolbarTitle);
        }
      });
    } else {
      goToIndividualChatRoomPageInternal(context, groupChatId, toolbarTitle);
    }
  }

  private void goToGroupChatRoomPageInternal(Context context, String groupChatId, String toolbarTitle) {
    SocketManager.getInstance().join(null, groupChatId, new ChatRoomJoinCallback() {
      @Override
      public void onChatRoomJoined(String chatRoomId) {
        ChatRoomActivity.start(context, chatRoomId, groupChatId, toolbarTitle);
      }
    });
  }

  public void sendImageMessage(Uri uri) throws IllegalStateException {
    ChatRoomFragmentProxy proxy = ProxyManager.getInstance().getChatRoomFragmentProxy();
    if (proxy == null) {
      throw new IllegalStateException("You must call this method when you are in chat room page.");
    }

    proxy.sendImageMessage(uri);
  }

  public void sendTextMessage(String message) throws IllegalStateException {
    ChatRoomFragmentProxy proxy = ProxyManager.getInstance().getChatRoomFragmentProxy();
    if (proxy == null) {
      throw new IllegalStateException("You must call this method when you are in chat room page.");
    }

    proxy.sendTextMessage(message);
  }

  public void joinChatRoom(String chatRoomId) {
    joinChatRoom(chatRoomId, null);
  }

  public void joinChatRoom(String chatRoomId, ChatRoomLeaveCallback callback) {
    SocketManager.getInstance().leave(chatRoomId, callback);
  }

  public void leaveChatRoom(String chatRoomId) {
    leaveChatRoom(chatRoomId, null);
  }

  public void leaveChatRoom(String chatRoomId, ChatRoomLeaveCallback callback) {
    SocketManager.getInstance().leave(chatRoomId, callback);
  }

  public void getUnreadMessageCount(UnreadMessageCountCallback callback) {
    DataManager.getInstance().getChatRoomsFromNetwork(null)
      .subscribe(
        rooms -> {
          int totalUnreadCount = ChatRoomMeta.getInstance().getTotalUnreadCount();
          callback.onUnreadMessageCountReady(totalUnreadCount);
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
    StethoUtils.init(appContext);
    StethoUtils.setEnable(enable);
  }

}

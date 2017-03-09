package com.redso.signaller.core;

import android.os.Handler;
import android.os.Looper;

import com.redso.signaller.core.model.PushNotification;
import com.redso.signaller.core.model.SignallerChatMessage;
import com.redso.signaller.core.model.SignallerPayload;
import com.redso.signaller.core.model.SignallerSocketChatMessage;
import com.redso.signaller.core.push.SignallerPushNotificationManager;
import com.redso.signaller.util.GsonUtils;
import com.redso.signaller.util.LogUtils;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

import io.socket.client.Ack;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import rx.subjects.PublishSubject;

public class SocketManager {

  private static final SocketManager INSTANCE = new SocketManager();

  private static final String CONNECT = "connect";
  private static final String CONNECTING = Socket.EVENT_CONNECTING;
  private static final String CONNECTED = "connected";
  private static final String DISCONNECTED = Socket.EVENT_DISCONNECT;
  private static final String JOIN = "join";
  private static final String LEAVE = "leave";
  private static final String SEND_MESSAGE = "send_message";
  private static final String RECEIVE_MESSAGE = "receive_message";

  private Socket socket;
  private Handler mainThreadHandler;

  private PublishSubject<String> connectionEmitter;

  private SocketManager() {
    mainThreadHandler = new Handler(Looper.getMainLooper());
    connectionEmitter = PublishSubject.create();
  }

  public static SocketManager getInstance() {
    return INSTANCE;
  }

  void initSocket(String accessToken) {
    if (socket != null) {
      return;
    }

    try {
      IO.Options opts = new IO.Options();
      opts.query = "access_token=" + accessToken;
      opts.secure = true;
      socket = IO.socket(Signaller.getInstance().getAppConfig().getSocketUrl(), opts);
    } catch (URISyntaxException e) {
      throw new RuntimeException(e);
    }
  }

  void initSocketIfNeed() {
    String accessToken = UserData.getInstance().getAccessToken();
    if (socket == null && accessToken != null && !accessToken.isEmpty()) {
      initSocket(accessToken);
    }
  }

  public boolean isConnected() {
    return socket != null && socket.connected();
  }

  public void connect(SocketConnectionCallbacks callback) {
    initSocketIfNeed();

    if (!isConnected()) {
      onEvents();
      socket.connect();
      LogUtils.d("connect");
      registerConnectionCallbacks(callback);
    }
  }

  private void registerConnectionCallbacks(SocketConnectionCallbacks callback) {
    if (callback == null) {
      return;
    }

    connectionEmitter.subscribe(type -> {
      if (type.equals(CONNECT)) {
        callback.onConnect();
      } else if (type.equals(CONNECTING)) {
        callback.onConnecting();
      } else if (type.equals(CONNECTED)) {
        callback.onConnected();
      } else if (type.equals(DISCONNECTED)) {
        callback.onDisconnected();
      }
    });
  }

  public void connect() {
    connect(null);
  }

  public void disconnect() {
    if (isConnected()) {
      offEvents();
      socket.disconnect();
      LogUtils.d("disconnect");
    }
  }

  public void invalidate() {
    socket = null;
  }

  private void onEvents() {
    socket.on(CONNECT, onConnect);
    socket.on(CONNECTING, onConnecting);
    socket.on(CONNECTED, onConnected);
    socket.on(DISCONNECTED, onDisconnected);
    socket.on(RECEIVE_MESSAGE, onMsgReceived);
  }

  private void offEvents() {
    socket.off(CONNECT);
    socket.off(CONNECTING);
    socket.off(CONNECTED);
    socket.off(DISCONNECTED);
    socket.off(RECEIVE_MESSAGE);
  }

  public void send(SignallerSocketChatMessage message) {
    try {
      JSONObject chatMsgObj = new JSONObject();
      chatMsgObj.put("room_id", message.getRoomId());

      JSONObject msgObj = new JSONObject();
      msgObj.put("type", message.getMessage().getType());
      msgObj.put("content", message.getMessage().getContent());
      chatMsgObj.put("message", msgObj);

      chatMsgObj.put("payload", message.getPayloadJson());

      socket.emit(SEND_MESSAGE, chatMsgObj);
    } catch (JSONException e) {
      e.printStackTrace();
    }
  }

  void join(String userId, String chatRoomId, ChatRoomJoinCallback callback) {
    try {
      JSONObject object = new JSONObject();
      object.put("user_id", userId);
      object.put("room_id", chatRoomId);
      socket.emit(JOIN, object, (Ack) args -> {
        LogUtils.d("onJoined");
        mainThreadHandler.post(() -> {
          callback.onChatRoomJoined(chatRoomId, userId);
        });
      });
    } catch (JSONException e) {
      e.printStackTrace();
    }
  }

  void leave(String chatRoomId, ChatRoomLeaveCallback callback) {
    try {
      JSONObject object = new JSONObject();
      object.put("user_id", UserData.getInstance().getUserId());
      object.put("room_id", chatRoomId);
      socket.emit(LEAVE, object, (Ack) args -> {
        LogUtils.d("onLeft");
        mainThreadHandler.post(() -> {
          callback.onChatRoomLeft(chatRoomId);
        });
      });
    } catch (JSONException e) {
      e.printStackTrace();
    }
  }

  private Emitter.Listener onConnect = args -> {
    LogUtils.d("onConnect");
    connectionEmitter.onNext(CONNECT);
    EventBus.getDefault().postSticky(new SignallerEvents.OnSocketConnectEvent());
  };

  private Emitter.Listener onConnecting = args -> {
    LogUtils.d("onConnecting");
    connectionEmitter.onNext(CONNECTING);
    EventBus.getDefault().postSticky(new SignallerEvents.OnSocketConnectingEvent());
  };

  private Emitter.Listener onConnected = args -> {
    LogUtils.d("onConnected");
    connectionEmitter.onNext(CONNECTED);
    EventBus.getDefault().postSticky(new SignallerEvents.OnSocketConnectedEvent());
    sendPendingChatMsg();
  };

  private Emitter.Listener onDisconnected = args -> {
    // todo SocketIO BUG, no callback received
    LogUtils.d("onDisconnected");
    connectionEmitter.onNext(DISCONNECTED);
    EventBus.getDefault().postSticky(new SignallerEvents.OnSocketDisconnectedEvent());
  };

  private Emitter.Listener onMsgReceived = args -> {
    SignallerSocketChatMessage socketChatMessage = GsonUtils.getGson().fromJson(args[0].toString(), SignallerSocketChatMessage.class);
    LogUtils.d("Message is sent / received: " + socketChatMessage.getMessage());
    insertOrUpdateChatMsgInDb(socketChatMessage);
    updateChatRoomInDb(socketChatMessage);
    dispatchMsgEvents(socketChatMessage);
  };

  private void insertOrUpdateChatMsgInDb(SignallerSocketChatMessage socketChatMessage) {
    // update own just sent msg
    if (socketChatMessage.getPayloadJson() != null && socketChatMessage.getPayloadJson().length() > 0 && socketChatMessage.getMessage().isOwnMessage()) {
      SignallerPayload payload = GsonUtils.getGson().fromJson(socketChatMessage.getPayloadJson(), SignallerPayload.class);
      long timestamp = payload.getTimestamp();
      // remove temp msg
      SignallerDbManager.getInstance().removeTempChatMessage(timestamp);
      // save real msg with local timestamp
      socketChatMessage.getMessage().setSent(true);
      socketChatMessage.getMessage().setMsgTime(timestamp);
      SignallerDbManager.getInstance().saveChatMessage(socketChatMessage.getMessage());
      // remove msg from pending queue
      SignallerDbManager.getInstance().removePendingChatMsg(timestamp);
      // notify msg is sent if in the same chat room
      boolean isInSameChatRoom = UserData.getInstance().isInChatRoomPage() &&
        UserData.getInstance().getCurrentChatRoomId().equals(socketChatMessage.getRoomId());
      int messageCellIndex = payload.getMessageCellIndex();
      if (isInSameChatRoom) {
        LogUtils.d(String.format("Notify message cell (%1$s) to update.", socketChatMessage.getMessage()));
        EventBus.getDefault().postSticky(new SignallerEvents.OnMsgSentEvent(socketChatMessage.getMessage(), messageCellIndex));
      }
    }
    // save another received msg
    else {
      SignallerDbManager.getInstance().saveChatMessage(socketChatMessage.getMessage());
    }
  }

  private void updateChatRoomInDb(SignallerSocketChatMessage socketChatMessage) {
    // if in the same chat room now, don't increase unread count
    boolean dontIncreaseUnreadCount = UserData.getInstance().isInChatRoomPage() &&
      UserData.getInstance().getCurrentChatRoomId().equals(socketChatMessage.getRoomId());

    SignallerDbManager.getInstance().updateChatRoom(socketChatMessage.getRoomId(), socketChatMessage.getMessage(), dontIncreaseUnreadCount);
  }

  private void dispatchMsgEvents(SignallerSocketChatMessage socketChatMessage) {
    SignallerChatMessage chatMessage = socketChatMessage.getMessage();
    String chatRoomId = socketChatMessage.getRoomId();
    String senderId = socketChatMessage.getMessage().getSender().getUserId();
    String senderName = socketChatMessage.getMessage().getSender().getName();
    String msgId = chatMessage.getMsgId();
    String message = chatMessage.getContent();
    String msgType = chatMessage.getType();
    PushNotification pushNotification = new PushNotification(message, chatRoomId, senderId, senderName, msgType, msgId);

    if (UserData.getInstance().isInChatRoomPage()) {
      boolean isInSameChatRoom = UserData.getInstance().getCurrentChatRoomId().equals(chatRoomId);
      if (isInSameChatRoom) {
        EventBus.getDefault().postSticky(new SignallerEvents.OnMsgReceivedEvent(chatRoomId, msgId));
        EventBus.getDefault().postSticky(new SignallerEvents.UpdateChatRoomListEvent(chatRoomId, false));
      } else {
        SignallerPushNotificationManager.showNotification(pushNotification);
        EventBus.getDefault().postSticky(new SignallerEvents.UpdateChatRoomListEvent(chatRoomId));
      }
    } else {
      EventBus.getDefault().postSticky(new SignallerEvents.UpdateChatRoomListEvent(chatRoomId));
      SignallerPushNotificationManager.showNotification(pushNotification);
    }
  }

  // todo send multiple times
  private void sendPendingChatMsg() {
//    SignallerDbManager.getInstance().getPendingChatMessages()
//      .subscribe(
//        pendingChatMessages -> {
//          for (SignallerSocketChatMessage pendingChatMessage : pendingChatMessages) {
//            send(pendingChatMessage);
//            LogUtils.d("sent pending chat message:" + pendingChatMessage.getMessage());
//          }
//        },
//        error -> {
//          LogUtils.e("sendPendingChatMsg:" + error.getMessage());
//        }
//      );
  }

}

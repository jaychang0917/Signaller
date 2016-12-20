package com.jaychang.signaller.core;

import android.os.Handler;
import android.os.Looper;

import com.jaychang.signaller.core.model.SocketChatMessage;
import com.jaychang.signaller.util.GsonUtils;
import com.jaychang.signaller.util.LogUtils;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

import io.socket.client.Ack;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class SocketManager {

  private static final SocketManager INSTANCE = new SocketManager();

  private static final String CONNECT = "connect";
  private static final String CONNECTED = "connected";
  private static final String DISCONNECTED = Socket.EVENT_DISCONNECT;
  private static final String JOIN = "join";
  private static final String LEAVE = "leave";
  private static final String SEND_MESSAGE = "send_message";
  private static final String RECEIVE_MESSAGE = "receive_message";

  private Socket socket;
  private boolean isSocketInitialized;
  private Handler mainThreadHandler;

  private SocketManager() {
    mainThreadHandler = new Handler(Looper.getMainLooper());
  }

  public static SocketManager getInstance() {
    return INSTANCE;
  }

  void initSocket(String accessToken) {
    if (isSocketInitialized) {
      return;
    }

    try {
      IO.Options opts = new IO.Options();
      opts.query = "access_token=" + accessToken;
      opts.secure = true;
      socket = IO.socket(UserData.getInstance().getSocketUrl(), opts);
    } catch (URISyntaxException e) {
      throw new RuntimeException(e);
    }

    socket.on(CONNECT, onConnect);
    socket.on(CONNECTED, onConnected);
    socket.on(DISCONNECTED, onDisconnected);
    socket.on(RECEIVE_MESSAGE, onMsgReceived);

    isSocketInitialized = true;
  }

  public boolean isConnected() {
    return socket != null && socket.connected();
  }

  void connect() {
    if (!isConnected()) {
      socket.connect();
    }
  }

  void disconnect() {
    if (isConnected()) {
      offEvents();
      socket.disconnect();
    }
  }

  private void offEvents() {
    if (isConnected()) {
      socket.off(CONNECT);
      socket.off(CONNECTED);
      socket.off(DISCONNECTED);
      socket.off(RECEIVE_MESSAGE);
    }
  }

  public void send(SocketChatMessage message) {
    try {
      JSONObject chatMsgObj = new JSONObject();
      chatMsgObj.put("room_id", message.getRoomId());

      JSONObject msgObj = new JSONObject();
      msgObj.put("type", message.getMessage().getType());
      msgObj.put("content", message.getMessage().getContent());
      chatMsgObj.put("message", msgObj);

      JSONObject payload = new JSONObject();
      payload.put("timestamp", message.getPayload().getTimestamp());
      chatMsgObj.put("payload", payload);

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
          callback.onChatRoomJoined(chatRoomId);
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
  };

  private Emitter.Listener onDisconnected = args -> {
    LogUtils.d("onDisconnected");
    EventBus.getDefault().postSticky(new SignallerEvents.OnSocketDisconnectedEvent());
  };

  private Emitter.Listener onConnected = args -> {
    LogUtils.d("onConnected");
    EventBus.getDefault().postSticky(new SignallerEvents.OnSocketConnectedEvent());
    sendPendingChatMsg();
  };

  private Emitter.Listener onMsgReceived = args -> {
    SocketChatMessage socketChatMessage = GsonUtils.getGson().fromJson(args[0].toString(), SocketChatMessage.class);
    LogUtils.d("message received:" + socketChatMessage.getMessage().getContent());
    insertOrUpdateChatMsgInDb(socketChatMessage);
    insertOrUpdateChatRoomInDb(socketChatMessage);
    dispatchMsgEvents(socketChatMessage.getRoomId(), socketChatMessage.getMessage().getMsgId());
  };

  private void insertOrUpdateChatMsgInDb(SocketChatMessage socketChatMessage) {
    // update own just sent msg
    if (socketChatMessage.getPayload() != null && socketChatMessage.getPayload().getTimestamp() != 0L) {
      // remove temp msg
      SignallerDbManager.getInstance().removeTempChatMessage(socketChatMessage.getPayload().getTimestamp());
      // save real msg with local timestamp
      socketChatMessage.getMessage().setSent(true);
      socketChatMessage.getMessage().setMtime(socketChatMessage.getPayload().getTimestamp());
      SignallerDbManager.getInstance().saveChatMessage(socketChatMessage.getMessage());
      // remove msg from pending queue
      SignallerDbManager.getInstance().removePendingChatMsg(socketChatMessage.getPayload().getTimestamp());
    }
    // save another received msg
    else {
      SignallerDbManager.getInstance().saveChatMessage(socketChatMessage.getMessage());
    }
  }

  private void insertOrUpdateChatRoomInDb(SocketChatMessage socketChatMessage) {
    SignallerDbManager.getInstance().insertOrUpdateChatRoom(socketChatMessage.getRoomId(), socketChatMessage.getMessage());
  }

  // todo how to handle push??
  private void dispatchMsgEvents(String chatRoomId, String msgId) {
    boolean isInChatRoomPage = UserData.getInstance().isInChatRoomPage();

    if (isInChatRoomPage) {
      boolean isInSameChatRoom = UserData.getInstance().getCurrentChatRoomId().equals(chatRoomId);
      if (isInSameChatRoom) {
        EventBus.getDefault().postSticky(new SignallerEvents.OnMsgReceivedEvent(msgId));
      } else {
        EventBus.getDefault().postSticky(new SignallerEvents.ShowPushNotificationEvent(msgId));
      }
      EventBus.getDefault().postSticky(new SignallerEvents.UpdateChatRoomListEvent(chatRoomId));
    } else {
      EventBus.getDefault().postSticky(new SignallerEvents.UpdateChatRoomListEvent(chatRoomId));
      EventBus.getDefault().postSticky(new SignallerEvents.ShowPushNotificationEvent(msgId));
    }
  }

  private void sendPendingChatMsg() {
    SignallerDbManager.getInstance().getPendingChatMessages()
      .subscribe(
        pendingChatMessages -> {
          for (SocketChatMessage pendingChatMessage : pendingChatMessages) {
            send(pendingChatMessage);
            LogUtils.d("sent pending chat message:" + pendingChatMessage.getMessage());
          }
        },
        error -> {
          LogUtils.e("sendPendingChatMsg:" + error.getMessage());
        }
      );
  }

}

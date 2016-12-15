package com.jaychang.signaller.core;

import android.util.Log;

import com.google.gson.Gson;
import com.jaychang.signaller.core.model.Payload;
import com.jaychang.signaller.core.model.PendingChatMessage;
import com.jaychang.signaller.core.model.SocketChatMessage;
import com.jaychang.signaller.util.LogUtils;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

import io.socket.client.Ack;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

class SocketManager {

  private static final SocketManager INSTANCE = new SocketManager();

  private static final String CONNECT = "connect";
  private static final String CONNECTED = "connected";
  private static final String JOIN = "join";
  private static final String LEAVE = "leave";
  private static final String SEND_MESSAGE = "send_message";
  private static final String RECEIVE_MESSAGE = "receive_message";

  private Socket socket;
  private boolean isSocketInitialized;

  private SocketManager() {
  }

  static SocketManager getInstance() {
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
      socket = IO.socket(Signaller.getInstance().getAccessToken(), opts);
    } catch (URISyntaxException e) {
      throw new RuntimeException(e);
    }

    socket.on(CONNECT, onConnect);
    socket.on(CONNECTED, onConnected);
    socket.on(RECEIVE_MESSAGE, onMsgReceived);

    isSocketInitialized = true;
  }

  boolean isConnected() {
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
      socket.off(RECEIVE_MESSAGE);
    }
  }

  void send(SocketChatMessage message) {
    try {
      Payload payload = new Payload();
      payload.timestamp = System.currentTimeMillis();
      message.payload = payload;
      JSONObject object = new JSONObject(new Gson().toJson(message));
      socket.emit(SEND_MESSAGE, object);
    } catch (JSONException e) {
      e.printStackTrace();
    }
  }

  void join(String userId, String roomId) {
    try {
      JSONObject object = new JSONObject();
      object.put("user_id", userId);
      object.put("room_id", roomId);
      socket.emit(JOIN, object, (Ack) args -> {
        Log.d("", "onJoined");
      });
    } catch (JSONException e) {
      e.printStackTrace();
    }
  }

  void leave(String userId, String roomId) {
    try {
      JSONObject object = new JSONObject();
      object.put("user_id", userId);
      object.put("room_id", roomId);
      socket.emit(LEAVE, object, (Ack) args -> {
        Log.d("", "onLeft");
      });
    } catch (JSONException e) {
      e.printStackTrace();
    }
  }

  private Emitter.Listener onConnect = args -> {
    LogUtils.d("onConnect");
  };

  private Emitter.Listener onConnected = args -> {
    LogUtils.d("onConnected");
    sendPendingChatMsg();
  };

  private Emitter.Listener onMsgReceived = args -> {
    SocketChatMessage socketChatMessage = new Gson().fromJson(args[0].toString(), SocketChatMessage.class);
    updateChatMsgInDb(socketChatMessage);
  };

  private void updateChatMsgInDb(SocketChatMessage socketChatMessage) {
    // update own just sent msg
    if (socketChatMessage.payload != null && socketChatMessage.payload.timestamp != 0L) {
      // remove temp msg
      DatabaseManager.getInstance().removeChatMessage(socketChatMessage.payload.timestamp);
      // save real msg with local timestamp
      socketChatMessage.message.isSent = true;
      socketChatMessage.message.mtime = socketChatMessage.payload.timestamp;
      DatabaseManager.getInstance().saveChatMessage(socketChatMessage.message);
      // remove msg from pending queue
      removePendingChatMsg(socketChatMessage);
    }
    // save another received msg
    else {
      DatabaseManager.getInstance().saveChatMessage(socketChatMessage.message);
    }

    dispatchMsg(socketChatMessage);
  }

  private void removePendingChatMsg(SocketChatMessage msg) {
    DatabaseManager.getInstance().removePendingChatMsg(msg.payload.timestamp);
  }

  private void sendPendingChatMsg() {
    DatabaseManager.getInstance().getPendingChatMessages()
      .subscribe(
        pendingChatMessages -> {
          for (PendingChatMessage pendingChatMessage : pendingChatMessages) {
            send(pendingChatMessage.socketChatMessage);
            LogUtils.d("sent pending chat message:" + pendingChatMessage.socketChatMessage.message);
          }
        },
        error -> {
          LogUtils.e("sendPendingChatMsg:" + error.getMessage());
        }
      );
  }

  private void dispatchMsg(SocketChatMessage socketChatMessage) {
    boolean isInChatRoomPage = ChatRoomState.getInstance().isInChatRoomPage();
    boolean isInChatRoomListPage = ChatRoomState.getInstance().isInChatRoomListPage();

    if (isInChatRoomPage) {
      boolean isInSameChatRoom = ChatRoomState.getInstance().getCurrentChatRoomId().equals(socketChatMessage.roomId);
      if (isInSameChatRoom) {
        EventBus.getDefault().postSticky(new Events.OnMsgReceivedEvent(socketChatMessage.message));
      } else {
        EventBus.getDefault().postSticky(new Events.ShowPushNotificationEvent(socketChatMessage.message));
      }
    } else if (isInChatRoomListPage) {
      EventBus.getDefault().postSticky(new Events.UpdateChatRoomListEvent());
    } else {
      EventBus.getDefault().postSticky(new Events.ShowPushNotificationEvent(socketChatMessage.message));
    }
  }

}

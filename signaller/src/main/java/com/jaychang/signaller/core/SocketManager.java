package com.jaychang.signaller.core;

import android.os.AsyncTask;
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
      socket.off(RECEIVE_MESSAGE);
    }
  }

  public void send(SocketChatMessage message) {
    try {
      JSONObject chatMsgObj = new JSONObject();
      chatMsgObj.put("room_id", message.roomId);
      JSONObject msgObj = new JSONObject();
      msgObj.put("type", message.message.type);
      msgObj.put("content", message.message.content);
      chatMsgObj.put("message", msgObj);
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
          callback.onChatRoomJoined(userId, chatRoomId);
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

  private Emitter.Listener onConnected = args -> {
    LogUtils.d("onConnected");
    mainThreadHandler.post(() -> {
      sendPendingChatMsg();
    });
  };

  private Emitter.Listener onMsgReceived = args -> {
    SocketChatMessage socketChatMessage = GsonUtils.getGson().fromJson(args[0].toString(), SocketChatMessage.class);
    LogUtils.d("message sent:" + socketChatMessage.message.content);
    mainThreadHandler.post(() -> {
      AsyncTask.execute(() -> {
        insertOrUpdateChatMsgInDb(socketChatMessage);
        insertOrUpdateChatRoomInDb(socketChatMessage);
        dispatchMsgEvents(socketChatMessage.roomId, socketChatMessage.message.msgId);
      });
    });
  };

  private void insertOrUpdateChatMsgInDb(SocketChatMessage socketChatMessage) {
    // update own just sent msg
    if (socketChatMessage.payload != null && socketChatMessage.payload.timestamp != 0L) {
      // remove temp msg
      DatabaseManager.getInstance().removeTempChatMessage(socketChatMessage.payload.timestamp);
      // save real msg with local timestamp
      socketChatMessage.message.isSent = true;
      socketChatMessage.message.mtime = socketChatMessage.payload.timestamp;
      DatabaseManager.getInstance().saveChatMessage(socketChatMessage.message);
      // remove msg from pending queue
      DatabaseManager.getInstance().removePendingChatMsg(socketChatMessage.payload.timestamp);
    }
    // save another received msg
    else {
      DatabaseManager.getInstance().saveChatMessage(socketChatMessage.message);
    }
  }

  private void insertOrUpdateChatRoomInDb(SocketChatMessage socketChatMessage) {
    DatabaseManager.getInstance().insertOrUpdateChatRoom(socketChatMessage.roomId, socketChatMessage.message);
  }

  private void dispatchMsgEvents(String chatRoomId, String msgId) {
    boolean isInChatRoomPage = UserData.getInstance().isInChatRoomPage();

    if (isInChatRoomPage) {
      boolean isInSameChatRoom = UserData.getInstance().getCurrentChatRoomId().equals(chatRoomId);
      if (isInSameChatRoom) {
        EventBus.getDefault().postSticky(new Events.OnMsgReceivedEvent(msgId));
      } else {
        EventBus.getDefault().postSticky(new Events.ShowPushNotificationEvent(msgId));
      }
      EventBus.getDefault().postSticky(new Events.UpdateChatRoomListEvent(chatRoomId));
    } else {
      EventBus.getDefault().postSticky(new Events.UpdateChatRoomListEvent(chatRoomId));
      EventBus.getDefault().postSticky(new Events.ShowPushNotificationEvent(msgId));
    }
  }

  private void sendPendingChatMsg() {
    DatabaseManager.getInstance().getPendingChatMessages()
      .subscribe(
        pendingChatMessages -> {
          for (SocketChatMessage pendingChatMessage : pendingChatMessages) {
            send(pendingChatMessage);
            LogUtils.d("sent pending chat message:" + pendingChatMessage.message);
          }
        },
        error -> {
          LogUtils.e("sendPendingChatMsg:" + error.getMessage());
        }
      );
  }

}

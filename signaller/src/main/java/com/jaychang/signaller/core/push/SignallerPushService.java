package com.jaychang.signaller.core.push;

import android.os.Bundle;

import com.google.android.gms.gcm.GcmListenerService;
import com.jaychang.signaller.core.SignallerEvents;
import com.jaychang.signaller.core.UserData;
import com.jaychang.signaller.util.LogUtils;

import org.greenrobot.eventbus.EventBus;

public class SignallerPushService extends GcmListenerService {

  @Override
  public void onMessageReceived(String from, Bundle data) {
    LogUtils.d("GCM:onMessageReceived");

    for (String key : data.keySet()) {
      LogUtils.d("GCM:data->key:" + key + " value:" + data.get(key));
    }

    String message = data.getString("content");
    String msgType = data.getString("msg_type");
    String msgId = data.getString("msg_id");
    String userId = data.getString("user_id");
    String roomTitle = data.getString("room_title");

    String ownUserId = UserData.getInstance().getUserId();
    String chatRoomId = ownUserId.compareTo(userId) < 0 ?
      ownUserId + "_" + userId :
      userId + "_" + ownUserId;

    SignallerNotificationManager.showNotification(message, chatRoomId, userId, roomTitle, msgType);
    LogUtils.d("[GCM]show push notification:" + message);

    boolean isInSameChatRoom = chatRoomId.equals(UserData.getInstance().getCurrentChatRoomId());
    if (isInSameChatRoom) {
      // todo msgId is unque in chatroom only
      EventBus.getDefault().postSticky(new SignallerEvents.OnMsgReceivedEvent(msgId));
    }

    EventBus.getDefault().postSticky(new SignallerEvents.UpdateChatRoomListEvent(chatRoomId));
  }

}
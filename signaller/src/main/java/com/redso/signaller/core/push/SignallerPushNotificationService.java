package com.redso.signaller.core.push;

import android.os.Bundle;
import android.support.annotation.CallSuper;

import com.google.android.gms.gcm.GcmListenerService;
import com.redso.signaller.core.SignallerEvents;
import com.redso.signaller.core.UserData;
import com.redso.signaller.core.model.PushNotification;
import com.redso.signaller.util.LogUtils;

import org.greenrobot.eventbus.EventBus;

public class SignallerPushNotificationService extends GcmListenerService {

  @CallSuper
  @Override
  public void onMessageReceived(String from, Bundle data) {
    LogUtils.d("GCM:onMessageReceived");

    for (String key : data.keySet()) {
      LogUtils.d("GCM:data->key:" + key + " value:" + data.get(key));
    }

    PushNotification pushNotification = PushNotification.from(data);

    SignallerPushNotificationManager.showNotification(pushNotification);

    LogUtils.d("GCM:show push notification:" + pushNotification.getMessage());

    String chatRoomId = pushNotification.getChatRoomId();
    boolean isInSameChatRoom = chatRoomId.equals(UserData.getInstance().getCurrentChatRoomId());
    if (isInSameChatRoom) {
      EventBus.getDefault().postSticky(new SignallerEvents.OnMsgReceivedEvent(chatRoomId, pushNotification.getMsgId()));
    }

    EventBus.getDefault().postSticky(new SignallerEvents.UpdateChatRoomListEvent(chatRoomId));
  }

}
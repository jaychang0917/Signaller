package com.jaychang.signaller.core.push;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import com.jaychang.signaller.R;
import com.jaychang.signaller.core.Signaller;
import com.jaychang.signaller.core.model.PushNotification;
import com.jaychang.signaller.ui.SignallerChatRoomActivity;

import java.util.HashMap;

public class SignallerPushNotificationManager {

  private static HashMap<String, Integer> notificationMap = new HashMap<>();

  private SignallerPushNotificationManager() {
  }

  public static void showNotification(Context context, PushNotification pushNotification, Class<?> parentStack) {
    Intent intent = new Intent(context, SignallerChatRoomActivity.class);
    intent.putExtra(SignallerChatRoomActivity.EXTRA_USER_ID, pushNotification.getSenderId());
    intent.putExtra(SignallerChatRoomActivity.EXTRA_TITLE, pushNotification.getRoomTitle());
    intent.putExtra(SignallerChatRoomActivity.EXTRA_CHAT_ROOM_ID, pushNotification.getChatRoomId());

//    TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
//    stackBuilder.addParentStack(parentStack);
//    stackBuilder.addNextIntent(intent);
//    PendingIntent pendingIntent =
//      stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
    PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

    if ("image".equals(pushNotification.getMsgType())) {
      pushNotification.setMessage(context.getString(R.string.sig_image));
    }

    NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
      .setSmallIcon(Signaller.getInstance().getAppConfig().getAppIcon())
      .setContentTitle(pushNotification.getRoomTitle())
      .setContentText(pushNotification.getMessage())
      .setAutoCancel(true)
      .setContentIntent(pendingIntent);

    Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
    notificationBuilder.setSound(defaultSoundUri);

    NotificationManager notificationManager =
      (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

    int notificationId = notificationMap.size();
    if (!notificationMap.containsKey(pushNotification.getSenderId())) {
      notificationMap.put(pushNotification.getSenderId(), notificationId);
    } else {
      notificationId = notificationMap.get(pushNotification.getSenderId());
    }

    notificationManager.notify(notificationId, notificationBuilder.build());
  }

  public static void cancelNotification(String userId) {
    if (!notificationMap.containsKey(userId)) {
      return;
    }

    NotificationManager notificationManager =
      (NotificationManager) Signaller.getInstance().getAppContext().getSystemService(Context.NOTIFICATION_SERVICE);

    notificationManager.cancel(notificationMap.get(userId));

    notificationMap.remove(userId);
  }

}

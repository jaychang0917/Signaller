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
import com.jaychang.signaller.ui.ChatRoomActivity;

import java.util.HashMap;

public class SignallerPushNotificationManager {

  private static HashMap<String, Integer> notificationIdMap = new HashMap<>();

  private SignallerPushNotificationManager() {
  }

  public static void showNotification(PushNotification pushNotification) {
    Context context = Signaller.getInstance().getAppContext();

    Intent intent = new Intent(context, ChatRoomActivity.class);
    intent.putExtra(ChatRoomActivity.EXTRA_USER_ID, pushNotification.getSenderId());
    intent.putExtra(ChatRoomActivity.EXTRA_TITLE, pushNotification.getRoomTitle());
    intent.putExtra(ChatRoomActivity.EXTRA_CHAT_ROOM_ID, pushNotification.getChatRoomId());

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

    int notificationId = notificationIdMap.size();
    if (!notificationIdMap.containsKey(pushNotification.getSenderId())) {
      notificationIdMap.put(pushNotification.getSenderId(), notificationId);
    } else {
      notificationId = notificationIdMap.get(pushNotification.getSenderId());
    }

    notificationManager.notify(notificationId, notificationBuilder.build());
  }

  public static void cancelNotification(String userId) {
    if (!notificationIdMap.containsKey(userId)) {
      return;
    }

    NotificationManager notificationManager =
      (NotificationManager) Signaller.getInstance().getAppContext().getSystemService(Context.NOTIFICATION_SERVICE);

    notificationManager.cancel(notificationIdMap.get(userId));

    notificationIdMap.remove(userId);
  }

}

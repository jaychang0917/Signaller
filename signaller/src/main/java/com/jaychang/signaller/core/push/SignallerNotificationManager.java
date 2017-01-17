package com.jaychang.signaller.core.push;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import com.jaychang.signaller.R;
import com.jaychang.signaller.core.Signaller;
import com.jaychang.signaller.ui.ChatRoomActivity;

import java.util.HashMap;

public class SignallerNotificationManager {

  private static HashMap<String, Integer> notificationMap = new HashMap<>();

  private SignallerNotificationManager() {
  }

  public static void showNotification(String message, String chatRoomId, String userId, String roomTitle, String msgType) {
    Context context = Signaller.getInstance().getAppContext();

    Intent intent = new Intent(context, ChatRoomActivity.class);
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    intent.putExtra(ChatRoomActivity.EXTRA_USER_ID, userId);
    intent.putExtra(ChatRoomActivity.EXTRA_TITLE, roomTitle);
    intent.putExtra(ChatRoomActivity.EXTRA_CHAT_ROOM_ID, chatRoomId);

    TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
//    stackBuilder.addParentStack(MainActivity.class);
    stackBuilder.addNextIntent(intent);
    PendingIntent pendingIntent =
      stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

    if ("image".equals(msgType)) {
      message = Signaller.getInstance().getAppContext().getString(R.string.sig_image);
    }

    NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
      .setSmallIcon(Signaller.getInstance().getAppConfig().getAppIcon())
      .setContentTitle(roomTitle)
      .setContentText(message)
      .setAutoCancel(true)
      .setContentIntent(pendingIntent);

    Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
    notificationBuilder.setSound(defaultSoundUri);

    NotificationManager notificationManager =
      (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

    int notificationId = notificationMap.size();
    if (!notificationMap.containsKey(userId)) {
      notificationMap.put(userId, notificationId);
    } else {
      notificationId = notificationMap.get(userId);
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

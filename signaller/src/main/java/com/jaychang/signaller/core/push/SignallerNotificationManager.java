package com.jaychang.signaller.core.push;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import com.jaychang.signaller.core.AppData;
import com.jaychang.signaller.ui.ChatRoomActivity;
import com.jaychang.signaller.util.LogUtils;

public class SignallerNotificationManager {

  private SignallerNotificationManager() {
  }

  public static void showNotification(String message, String chatRoomId) {
    Context context = AppData.getInstance().getAppContext();

    Intent intent = new Intent(context, ChatRoomActivity.class);
    intent.putExtra(ChatRoomActivity.EXTRA_CHATROOM_ID, chatRoomId);

    TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
//    stackBuilder.addParentStack(MainActivity.class);
    stackBuilder.addNextIntent(intent);
    PendingIntent pendingIntent =
      stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

    NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
      .setSmallIcon(AppData.getInstance().getAppIcon())
      .setContentTitle(context.getString(AppData.getInstance().getAppName()))
      .setContentText(message)
      .setAutoCancel(true)
      .setContentIntent(pendingIntent);

    Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
    notificationBuilder.setSound(defaultSoundUri);

    NotificationManager notificationManager =
      (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

    notificationManager.notify(0, notificationBuilder.build());
  }

  public static void cancelAllNofications() {
    NotificationManager notificationManager =
      (NotificationManager) AppData.getInstance().getAppContext().getSystemService(Context.NOTIFICATION_SERVICE);

    notificationManager.cancelAll();
  }
}

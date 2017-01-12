package com.jaychang.signaller.core.push;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import com.jaychang.signaller.core.Signaller;
import com.jaychang.signaller.ui.ChatRoomActivity;

public class SignallerNotificationManager {

  private SignallerNotificationManager() {
  }

  public static void showNotification(String message, String chatRoomId, String userId, String title) {
    Context context = Signaller.getInstance().getAppContext();

    Intent intent = new Intent(context, ChatRoomActivity.class);
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    intent.putExtra(ChatRoomActivity.EXTRA_USER_ID, userId);
    intent.putExtra(ChatRoomActivity.EXTRA_TITLE, title);
    intent.putExtra(ChatRoomActivity.EXTRA_CHAT_ROOM_ID, chatRoomId);

    TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
//    stackBuilder.addParentStack(MainActivity.class);
    stackBuilder.addNextIntent(intent);
    PendingIntent pendingIntent =
      stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

    NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
      .setSmallIcon(Signaller.getInstance().getAppConfig().getAppIcon())
      .setContentTitle(context.getString(Signaller.getInstance().getAppConfig().getAppName()))
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
      (NotificationManager) Signaller.getInstance().getAppContext().getSystemService(Context.NOTIFICATION_SERVICE);

    notificationManager.cancelAll();
  }
}

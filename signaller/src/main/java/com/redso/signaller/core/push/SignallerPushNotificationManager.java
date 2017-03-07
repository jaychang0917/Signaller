package com.redso.signaller.core.push;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import com.redso.signaller.R;
import com.redso.signaller.core.Signaller;
import com.redso.signaller.core.model.PushNotification;
import com.redso.signaller.ui.ChatRoomActivity;

import java.util.HashMap;

public class SignallerPushNotificationManager {

  private static HashMap<String, Integer> notificationIdMap = new HashMap<>();

  private SignallerPushNotificationManager() {
  }

  public static void showNotification(PushNotification pushNotification) {
    if (!Signaller.getInstance().isPushNotificationEnabled()) {
      return;
    }

    Context context = Signaller.getInstance().getAppContext();

    Intent intent = new Intent(context, ChatRoomActivity.class);
    intent.putExtra(ChatRoomActivity.EXTRA_CHAT_ID, pushNotification.getChatId());
    intent.putExtra(ChatRoomActivity.EXTRA_CHAT_ROOM_ID, pushNotification.getChatRoomId());
    intent.putExtra(ChatRoomActivity.EXTRA_TITLE, pushNotification.getRoomTitle());

    PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

    if ("image".equals(pushNotification.getMsgType())) {
      pushNotification.setMessage(context.getString(R.string.sig_photo));
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
    if (!notificationIdMap.containsKey(pushNotification.getChatId())) {
      notificationIdMap.put(pushNotification.getChatId(), notificationId);
    } else {
      notificationId = notificationIdMap.get(pushNotification.getChatId());
    }

    notificationManager.notify(notificationId, notificationBuilder.build());
  }

  public static void cancelNotification(String chatId) {
    if (!notificationIdMap.containsKey(chatId)) {
      return;
    }

    NotificationManager notificationManager =
      (NotificationManager) Signaller.getInstance().getAppContext().getSystemService(Context.NOTIFICATION_SERVICE);

    notificationManager.cancel(notificationIdMap.get(chatId));

    notificationIdMap.remove(chatId);
  }

}

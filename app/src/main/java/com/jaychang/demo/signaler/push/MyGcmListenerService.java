package com.jaychang.demo.signaler.push;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import com.google.android.gms.gcm.GcmListenerService;
import com.jaychang.demo.signaler.MainActivity;
import com.jaychang.demo.signaler.R;
import com.jaychang.signaller.ui.ChatRoomActivity;

public class MyGcmListenerService extends GcmListenerService {

  /**
   * Called when message is received.
   *
   * @param from SenderID of the sender.
   * @param data Data bundle containing message data as key/value pairs.
   *             For Set of keys use data.keySet().
   */
  @Override
  public void onMessageReceived(String from, Bundle data) {
    String message = data.getString("content");
    for (String key : data.keySet()) {
      System.out.println("GCM:data->key:" + key + " value:" + data.get(key));
    }
    showNotification(message);
  }

  private void showNotification(String message) {
    String title = "KOL";
    boolean hasSound = true;

    Intent intent = new Intent(this, ChatRoomActivity.class);

    TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
    stackBuilder.addParentStack(MainActivity.class);
    stackBuilder.addNextIntent(intent);
    PendingIntent pendingIntent =
      stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

    NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
      .setSmallIcon(R.mipmap.ic_launcher)
      .setContentTitle(title)
      .setContentText(message)
      .setAutoCancel(true)
      .setContentIntent(pendingIntent);

    if (hasSound) {
      Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
      notificationBuilder.setSound(defaultSoundUri);
    }

    NotificationManager notificationManager =
      (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

    notificationManager.notify(0, notificationBuilder.build());
  }
}
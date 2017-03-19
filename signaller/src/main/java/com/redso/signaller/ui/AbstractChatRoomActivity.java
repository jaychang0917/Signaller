package com.redso.signaller.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AppCompatActivity;

import com.redso.signaller.core.Signaller;

public abstract class AbstractChatRoomActivity extends AppCompatActivity {

  public static final String EXTRA_FROM_PUSH_NOTIFICATION = "EXTRA_FROM_PUSH_NOTIFICATION";
  public static final String EXTRA_CHAT_ID = "EXTRA_CHAT_ID";
  public static final String EXTRA_CHAT_ROOM_ID = "EXTRA_CHAT_ROOM_ID";
  public static final String EXTRA_TOOLBAR_TITLE = "EXTRA_TOOLBAR_TITLE";

  private boolean isFromPushNotification;

  protected abstract void onCreateFromPushNotification(String chatRoomId, String chatId, String toolbarTitle);

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    isFromPushNotification = getIntent().getBooleanExtra(EXTRA_FROM_PUSH_NOTIFICATION, false);
  }

  @Override
  protected void onStart() {
    super.onStart();
    if (isFromPushNotification) {
      handleIntentFromPushNotification(getIntent());
    }
  }

  public boolean isFromPushNotification() {
    return isFromPushNotification;
  }

  @Override
  public void onNewIntent(Intent intent) {
    super.onNewIntent(intent);
    handleIntentFromPushNotification(intent);
  }

  private void handleIntentFromPushNotification(Intent intent) {
    String chatRoomId = intent.getStringExtra(EXTRA_CHAT_ROOM_ID);
    String chatId = intent.getStringExtra(EXTRA_CHAT_ID);
    String toolbarTitle = intent.getStringExtra(EXTRA_TOOLBAR_TITLE);
    onCreateFromPushNotification(chatRoomId, chatId, toolbarTitle);
  }

  @Override
  public void onBackPressed() {
    super.onBackPressed();
    if (isTaskRoot()) {
      TaskStackBuilder.create(this)
        .addNextIntentWithParentStack(new Intent(this, Signaller.getInstance().getAppConfig().getChatRoomParentActivity()))
        .startActivities();
    }
  }

}

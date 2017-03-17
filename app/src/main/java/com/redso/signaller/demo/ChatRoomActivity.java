package com.redso.signaller.demo;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.jaychang.npp.NPhotoPicker;
import com.redso.signaller.core.Signaller;
import com.redso.signaller.ui.ChatRoomFragment;
import com.redso.signaller.util.LogUtils;

public class ChatRoomActivity extends AppCompatActivity {

  public static final String EXTRA_CHAT_ROOM_ID = "EXTRA_CHAT_ROOM_ID";
  public static final String EXTRA_CHAT_ID = "EXTRA_CHAT_ID";
  public static final String EXTRA_TITLE = "EXTRA_TITLE";

  public static void start(Context context, String chatRoomId, String chatId, String toolbarTitle) {
    Intent intent = new Intent(context, ChatRoomActivity.class);
    intent.putExtra(EXTRA_CHAT_ROOM_ID, chatRoomId);
    intent.putExtra(EXTRA_CHAT_ID, chatId);
    intent.putExtra(EXTRA_TITLE, toolbarTitle);
    context.startActivity(intent);
  }

  @Override
  protected void onNewIntent(Intent intent) {
    super.onNewIntent(intent);
    initToolbar(intent);
    initMessageFragment(intent);
  }

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_chatroom);
    init();
  }

  private void init() {
    initToolbar(getIntent());
    initMessageFragment(getIntent());
  }

  private void initToolbar(Intent intent) {
    String title = intent.getStringExtra(EXTRA_TITLE);
    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    toolbar.setTitle(title);
  }

  private void initMessageFragment(Intent intent) {
    String chatRoomId = intent.getStringExtra(EXTRA_CHAT_ROOM_ID);
    String chatId = intent.getStringExtra(EXTRA_CHAT_ID);

    ChatRoomFragment chatRoomFragment = ChatRoomFragment.newInstance(chatId, chatRoomId);
    // Set your custom action when photo icon is clicked
    chatRoomFragment.setPickPhotoCallback(this::showCustomPhotoPicker);

    getSupportFragmentManager().beginTransaction()
      .replace(R.id.messageFragmentContainer, chatRoomFragment)
      .commitNow();
  }

  private void showCustomPhotoPicker() {
    NPhotoPicker.with(this)
      .pickSinglePhoto()
      .subscribe(uri -> {
        // Send the picked photo
        Signaller.getInstance().sendPhotoMessage(uri);
      }, error -> {
        LogUtils.e("Fail to show photo picker:" + error.getMessage());
      });
  }

  @Override
  public void onBackPressed() {
    super.onBackPressed();
    if (isTaskRoot()) {
      TaskStackBuilder.create(this)
        .addNextIntentWithParentStack(new Intent(this, Signaller.getInstance().getAppConfig().getPushNotificationParentActivity()))
        .startActivities();
    }
  }

}

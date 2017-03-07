package com.redso.signaller.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.TaskStackBuilder;
import android.view.View;
import android.widget.FrameLayout;

import com.redso.signaller.R;
import com.jaychang.utils.AppUtils;
import com.redso.signaller.core.Signaller;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;

public class ChatRoomActivity extends RxAppCompatActivity {

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
    setContentView(R.layout.sig_activity_chatroom);
    init();
  }

  private void init() {
    setStatusBarColor();
    initToolbar(getIntent());
    initMessageFragment(getIntent());
  }

  private void initMessageFragment(Intent intent) {
    String chatRoomId = intent.getStringExtra(EXTRA_CHAT_ROOM_ID);
    String chatId = intent.getStringExtra(EXTRA_CHAT_ID);

    getSupportFragmentManager().beginTransaction()
      .replace(R.id.messageFragmentContainer, ChatRoomFragment.newInstance(chatId, chatRoomId))
      .commitNow();
  }

  private void initToolbar(Intent intent) {
    String toolbarTitle = intent.getStringExtra(EXTRA_TITLE);
    FrameLayout placeholder = (FrameLayout) findViewById(R.id.toolbarPlaceholder);
    placeholder.removeAllViews();
    View toolbar = Signaller.getInstance().getUiConfig().getChatRoomToolbarProvider().getToolbar(this, toolbarTitle);
    if (toolbar != null) {
      placeholder.addView(toolbar);
    }
  }

  private void setStatusBarColor() {
    int statusBarColor = Signaller.getInstance().getUiConfig().getChatRoomThemeProvider().getStatusBarColor();
    if (statusBarColor != 0) {
      AppUtils.setStatusBarColor(this, statusBarColor);
    }
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

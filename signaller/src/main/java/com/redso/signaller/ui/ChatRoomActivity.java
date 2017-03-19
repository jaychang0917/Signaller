package com.redso.signaller.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.FrameLayout;

import com.jaychang.utils.AppUtils;
import com.redso.signaller.R;
import com.redso.signaller.core.Signaller;

public class ChatRoomActivity extends AbstractChatRoomActivity {

  public static void start(Context context, String chatRoomId, String chatId, String toolbarTitle) {
    Intent intent = new Intent(context, ChatRoomActivity.class);
    intent.putExtra(EXTRA_CHAT_ID, chatId);
    intent.putExtra(EXTRA_CHAT_ROOM_ID, chatRoomId);
    intent.putExtra(EXTRA_TOOLBAR_TITLE, toolbarTitle);
    context.startActivity(intent);
  }

  @Override
  protected void onCreateFromPushNotification(String chatRoomId, String chatId, String toolbarTitle) {
    setStatusBarColor();
    initToolbar(toolbarTitle);
    initMessageFragment(chatRoomId, chatId);
  }

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.sig_activity_chatroom);
    if (!isFromPushNotification()) {
      init();
    }
  }

  private void init() {
    setStatusBarColor();
    initToolbar(getIntent().getStringExtra(EXTRA_TOOLBAR_TITLE));
    initMessageFragment(getIntent().getStringExtra(EXTRA_CHAT_ROOM_ID), getIntent().getStringExtra(EXTRA_CHAT_ID));
  }

  private void initMessageFragment(String chatRoomId, String chatId) {
    getSupportFragmentManager().beginTransaction()
      .replace(R.id.messageFragmentContainer, ChatRoomFragment.newInstance(chatRoomId, chatId))
      .commitNow();
  }

  private void initToolbar(String toolbarTitle) {
    ChatRoomToolbarProvider chatRoomToolbarProvider = Signaller.getInstance().getUiConfig().getChatRoomToolbarProvider();

    if (chatRoomToolbarProvider == null) {
      return;
    }

    FrameLayout placeholder = (FrameLayout) findViewById(R.id.toolbarPlaceholder);
    placeholder.removeAllViews();
    View toolbar = chatRoomToolbarProvider.getToolbar(this, toolbarTitle);
    if (toolbar != null) {
      placeholder.addView(toolbar);
    }
  }

  private void setStatusBarColor() {
    int themeColor = Signaller.getInstance().getUiConfig().getChatRoomPhotoPickerThemeColor();

    if (themeColor != 0) {
      AppUtils.setStatusBarColor(this, themeColor);
    }
  }

}

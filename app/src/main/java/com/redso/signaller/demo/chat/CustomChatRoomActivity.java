package com.redso.signaller.demo.chat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;

import com.jaychang.npp.NPhotoPicker;
import com.redso.signaller.core.Signaller;
import com.redso.signaller.demo.R;
import com.redso.signaller.ui.AbstractChatRoomActivity;
import com.redso.signaller.ui.ChatRoomFragment;
import com.redso.signaller.util.LogUtils;

public class CustomChatRoomActivity extends AbstractChatRoomActivity {

  public static final String EXTRA_USER_ID = "EXTRA_USER_ID";
  public static final String EXTRA_TOOLBAR_TITLE = "EXTRA_TOOLBAR_TITLE";

  public static void start(Context context, String userId, String toolbarTitle) {
    Intent intent = new Intent(context, CustomChatRoomActivity.class);
    intent.putExtra(EXTRA_USER_ID, userId);
    intent.putExtra(EXTRA_TOOLBAR_TITLE, toolbarTitle);
    context.startActivity(intent);
  }

  @Override
  protected void onCreateFromPushNotification(String chatRoomId, String chatId, String toolbarTitle) {
    initToolbar(toolbarTitle);
    initMessageFragment(chatId);
  }

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_chatroom);

    // if it is created from push notification, onCreateFromPushNotification() will be called.
    if (!isFromPushNotification()) {
      init();
    }
  }

  private void init() {
    initToolbar(getIntent().getStringExtra(EXTRA_TOOLBAR_TITLE));
    initMessageFragment(getIntent().getStringExtra(EXTRA_USER_ID));
  }

  private void initToolbar(String toolbarTitle) {
    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    toolbar.setTitle(toolbarTitle);
  }

  private void initMessageFragment(String userId) {
    ChatRoomFragment chatRoomFragment = ChatRoomFragment.fromUserId(userId);
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
        Signaller.getInstance().sendImageMessage(uri);
      }, error -> {
        LogUtils.e("Fail to show photo picker:" + error.getMessage());
      });
  }

}

package com.jaychang.signaller.ui.config;

import android.support.annotation.NonNull;

import com.jaychang.signaller.R;

public class DefaultChatRoomControlViewProvider implements ChatRoomControlViewProvider {

  @NonNull
  @Override
  public int getLayoutRes() {
    return R.layout.view_chatroom_control;
  }

  @NonNull
  @Override
  public int getInputEditTextId() {
    return R.id.inputEditText;
  }

  @NonNull
  @Override
  public int getEmojiIconViewId() {
    return R.id.emojiIconView;
  }

  @NonNull
  @Override
  public int getPhotoIconViewId() {
    return R.id.photoIconView;
  }

  @NonNull
  @Override
  public int getSendViewId() {
    return R.id.sendView;
  }
}

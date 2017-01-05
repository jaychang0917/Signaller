package com.jaychang.signaller.ui.config;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.View;

import com.jaychang.signaller.core.model.SignallerChatRoom;
import com.jaychang.signaller.ui.part.DefaultChatRoomToolbar;

class DefaultChatRoomToolbarProvider implements ChatRoomToolbarProvider {

  @NonNull
  @Override
  public View getToolbar(Activity activity, String username) {
    return DefaultChatRoomToolbar.create(activity, username);
  }
}

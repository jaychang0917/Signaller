package com.jaychang.signaller.ui.config;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.View;

import com.jaychang.signaller.core.model.ChatRoom;
import com.jaychang.signaller.ui.part.DefaultChatRoomToolbar;

public class DefaultChatRoomToolbarProvider implements ChatRoomToolbarProvider {

  @NonNull
  @Override
  public View getToolbar(Activity activity, ChatRoom chatRoom) {
    return DefaultChatRoomToolbar.create(activity, chatRoom);
  }
}

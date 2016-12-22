package com.jaychang.signaller.ui.config;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.View;

import com.jaychang.signaller.core.model.SignallerChatRoom;

public interface ChatRoomToolbarProvider {
  @NonNull View getToolbar(Activity activity, SignallerChatRoom chatRoom);
}

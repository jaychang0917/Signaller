package com.jaychang.signaller.ui.config;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.View;

public interface ChatRoomToolbarProvider {
  @NonNull View getToolbar(Activity activity, String username);
}

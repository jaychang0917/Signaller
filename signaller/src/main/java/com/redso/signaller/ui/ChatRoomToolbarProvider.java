package com.redso.signaller.ui;

import android.app.Activity;
import android.support.annotation.Nullable;
import android.view.View;

public interface ChatRoomToolbarProvider {

  @Nullable View getToolbar(Activity activity, String roomTitle);

}

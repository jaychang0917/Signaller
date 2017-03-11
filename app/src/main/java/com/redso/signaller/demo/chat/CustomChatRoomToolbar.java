package com.redso.signaller.demo.chat;

import android.app.Activity;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;

import com.redso.signaller.demo.R;

public class CustomChatRoomToolbar extends Toolbar {

  private Activity activity;

  private CustomChatRoomToolbar(Context context) {
    super(context);
    activity = (Activity) context;
  }

  public static CustomChatRoomToolbar create(Activity activity, String username) {
    CustomChatRoomToolbar toolbar = new CustomChatRoomToolbar(activity);
    toolbar.setupWithChatRoom(username);
    return toolbar;
  }

  private void setupWithChatRoom(String username) {
    setTitle(username);
    setTitleTextColor(ContextCompat.getColor(getContext(), android.R.color.white));
    setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
    setNavigationIcon(R.drawable.ic_toolbar_back);
    setNavigationOnClickListener(v -> {
      activity.finish();
    });
  }

}

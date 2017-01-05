package com.wiser.kol.ui;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;

import com.wiser.kol.R;

public class KolChatRoomToolbar extends Toolbar {

  private Activity activity;

  private KolChatRoomToolbar(Context context) {
    super(context);
    activity = (Activity) context;
  }

  public static KolChatRoomToolbar create(Activity activity, String username) {
    KolChatRoomToolbar toolbar = new KolChatRoomToolbar(activity);
    toolbar.setupWithChatRoom(username);
    return toolbar;
  }

  private void setupWithChatRoom(String username) {
    setTitle(username);
    setBackgroundColor(ContextCompat.getColor(getContext(), R.color.white));
    setNavigationIcon(R.drawable.btn_back);
    setTitleTextColor(Color.BLACK);
    setNavigationOnClickListener(v -> {
      activity.finish();
    });
  }

}

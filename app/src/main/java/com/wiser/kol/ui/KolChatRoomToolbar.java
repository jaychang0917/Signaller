package com.wiser.kol.ui;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;

import com.jaychang.signaller.core.model.ChatRoom;
import com.wiser.kol.R;

public class KolChatRoomToolbar extends Toolbar {

  private Activity activity;

  private KolChatRoomToolbar(Context context) {
    super(context);
    activity = (Activity) context;
  }

  public static KolChatRoomToolbar create(Activity activity, ChatRoom chatRoom) {
    KolChatRoomToolbar toolbar = new KolChatRoomToolbar(activity);
    toolbar.setupWithChatRoom(chatRoom);
    return toolbar;
  }

  private void setupWithChatRoom(ChatRoom chatRoom) {
    setTitle(chatRoom.getReceiver().getName());
    setBackgroundColor(ContextCompat.getColor(getContext(), R.color.white));
    setNavigationIcon(R.drawable.ic_toolbar_back);
    setTitleTextColor(Color.BLACK);
    setNavigationOnClickListener(v -> {
      activity.finish();
    });
  }

}

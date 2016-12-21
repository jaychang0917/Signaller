package com.jaychang.signaller.ui.part;

import android.app.Activity;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;

import com.jaychang.signaller.R;
import com.jaychang.signaller.core.Signaller;
import com.jaychang.signaller.core.model.ChatRoom;
import com.jaychang.utils.AppUtils;

public class DefaultChatRoomToolbar extends Toolbar {

  private Activity activity;

  private DefaultChatRoomToolbar(Context context) {
    super(context);
    activity = (Activity) context;
  }

  public static DefaultChatRoomToolbar create(Activity activity, ChatRoom chatRoom) {
    DefaultChatRoomToolbar toolbar = new DefaultChatRoomToolbar(activity);
    toolbar.setupWithChatRoom(chatRoom);
    return toolbar;
  }

  private void setupWithChatRoom(ChatRoom chatRoom) {
    setTitle(chatRoom.getReceiver().getName());
    setBackgroundColor(ContextCompat.getColor(getContext(), Signaller.getInstance().getUiConfig().getChatRoomToolbarBackgroundColor()));
    setNavigationIcon(R.drawable.ic_toolbar_back);
    setMinimumHeight(AppUtils.dp2px(getContext(), 48));
    setNavigationOnClickListener(v -> {
      activity.finish();
    });
  }

}

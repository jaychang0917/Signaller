package com.redso.signaller.ui;

import android.support.annotation.NonNull;

import com.redso.signaller.core.model.ChatRoom;

public interface ChatRoomCellProvider {

  @NonNull ChatRoomCell getChatRoomCell(ChatRoom chatRoom);

}

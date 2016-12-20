package com.jaychang.signaller.ui.config;

import android.support.annotation.NonNull;

import com.jaychang.signaller.core.model.ChatRoom;
import com.jaychang.signaller.ui.part.ChatRoomCell;

public interface ChatRoomCellProvider {
  @NonNull ChatRoomCell getChatRoomCell(ChatRoom chatRoom);
}

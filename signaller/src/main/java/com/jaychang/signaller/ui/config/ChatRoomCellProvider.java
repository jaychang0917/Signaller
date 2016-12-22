package com.jaychang.signaller.ui.config;

import android.support.annotation.NonNull;

import com.jaychang.signaller.core.model.SignallerChatRoom;
import com.jaychang.signaller.ui.part.ChatRoomCell;

public interface ChatRoomCellProvider {
  @NonNull ChatRoomCell getChatRoomCell(SignallerChatRoom chatRoom);
}

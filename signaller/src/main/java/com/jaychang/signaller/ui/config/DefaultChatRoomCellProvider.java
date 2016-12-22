package com.jaychang.signaller.ui.config;

import com.jaychang.signaller.core.model.SignallerChatRoom;
import com.jaychang.signaller.ui.part.ChatRoomCell;
import com.jaychang.signaller.ui.part.DefaultChatRoomCell;

class DefaultChatRoomCellProvider implements ChatRoomCellProvider {

  @Override
  public ChatRoomCell getChatRoomCell(SignallerChatRoom chatRoom) {
    return new DefaultChatRoomCell(chatRoom);
  }

}

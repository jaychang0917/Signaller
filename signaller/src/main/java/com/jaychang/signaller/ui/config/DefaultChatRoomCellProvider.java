package com.jaychang.signaller.ui.config;

import com.jaychang.signaller.core.model.ChatRoom;
import com.jaychang.signaller.ui.part.ChatRoomCell;
import com.jaychang.signaller.ui.part.DefaultChatRoomCell;

public class DefaultChatRoomCellProvider implements ChatRoomCellProvider {

  @Override
  public ChatRoomCell createChatRoomCell(ChatRoom chatRoom) {
    return new DefaultChatRoomCell(chatRoom);
  }

}

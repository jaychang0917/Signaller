package com.jaychang.signaller.ui.config;

import com.jaychang.signaller.core.model.ChatRoom;
import com.jaychang.signaller.ui.cell.ChatRoomCell;
import com.jaychang.signaller.ui.cell.DefaultChatRoomCell;

public class DefaultChatRoomCellProvider implements ChatRoomCellProvider {

  @Override
  public ChatRoomCell createChatRoomCell(ChatRoom chatRoom) {
    return new DefaultChatRoomCell(chatRoom);
  }

}

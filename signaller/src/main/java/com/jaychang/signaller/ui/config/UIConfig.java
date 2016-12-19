package com.jaychang.signaller.ui.config;

public interface UIConfig {
  ChatRoomCellProvider getChatRoomCellProvider();
  ChatMessageCellProvider getChatMessageCellProvider();
  ChatMessageDateSeparatorCellProvider getChatMessageDateSeparatorCellProvider();
}

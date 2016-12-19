package com.jaychang.signaller.ui.config;

public class SimpleUIConfig implements UIConfig{

  @Override
  public ChatRoomCellProvider getChatRoomCellProvider() {
    return new DefaultChatRoomCellProvider();
  }

  @Override
  public ChatMessageCellProvider getChatMessageCellProvider() {
    return new DefaultChatMessageCellProvider();
  }

  @Override
  public ChatMessageDateSeparatorCellProvider getChatMessageDateSeparatorCellProvider() {
    return new DefaultChatMessageDateSeparatorCellProvider();
  }

}

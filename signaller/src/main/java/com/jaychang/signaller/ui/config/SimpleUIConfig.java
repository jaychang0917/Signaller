package com.jaychang.signaller.ui.config;

public class SimpleUIConfig extends UIConfig {

  public SimpleUIConfig() {
    setChatRoomCellProvider(new DefaultChatRoomCellProvider());
    setChatMessageCellProvider(new DefaultChatMessageCellProvider());
    setChatMessageDateSeparatorCellProvider(new DefaultChatMessageDateSeparatorCellProvider());
    setShowChatMessageDateSeparator(true);
  }

}

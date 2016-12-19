package com.jaychang.signaller.ui.config;

public class UIConfig {

  private ChatRoomCellProvider chatRoomCellProvider;
  private ChatMessageCellProvider chatMessageCellProvider;
  private ChatMessageDateSeparatorCellProvider chatMsgDateSeparatorCellProvider;
  private boolean showChatMsgDateSeparator = true;

  public ChatRoomCellProvider getChatRoomCellProvider() {
    return chatRoomCellProvider;
  }

  public void setChatRoomCellProvider(ChatRoomCellProvider chatRoomCellProvider) {
    this.chatRoomCellProvider = chatRoomCellProvider;
  }

  public ChatMessageCellProvider getChatMessageCellProvider() {
    return chatMessageCellProvider;
  }

  public void setChatMessageCellProvider(ChatMessageCellProvider chatMessageCellProvider) {
    this.chatMessageCellProvider = chatMessageCellProvider;
  }

  public ChatMessageDateSeparatorCellProvider getChatMsgDateSeparatorCellProvider() {
    return chatMsgDateSeparatorCellProvider;
  }

  public void setChatMessageDateSeparatorCellProvider(ChatMessageDateSeparatorCellProvider chatMsgDateSeparatorCellProvider) {
    this.chatMsgDateSeparatorCellProvider = chatMsgDateSeparatorCellProvider;
  }

  public boolean isShowChatMessageDateSeparator() {
    return showChatMsgDateSeparator;
  }

  public void setShowChatMessageDateSeparator(boolean showChatMsgDateSeparator) {
    this.showChatMsgDateSeparator = showChatMsgDateSeparator;
  }
}

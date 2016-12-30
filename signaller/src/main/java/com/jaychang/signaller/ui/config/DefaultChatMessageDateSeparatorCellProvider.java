package com.jaychang.signaller.ui.config;

import com.jaychang.signaller.ui.part.ChatMessageDateSeparatorCell;
import com.jaychang.signaller.ui.part.DefaultChatMessageDateSeparatorCell;

class DefaultChatMessageDateSeparatorCellProvider implements ChatMessageDateSeparatorCellProvider {

  @Override
  public ChatMessageDateSeparatorCell getChatMessageDateSeparatorCell(long timestampMillis) {
    return new DefaultChatMessageDateSeparatorCell(timestampMillis);
  }

}
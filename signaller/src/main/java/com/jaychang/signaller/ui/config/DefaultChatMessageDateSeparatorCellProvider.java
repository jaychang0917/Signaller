package com.jaychang.signaller.ui.config;

import com.jaychang.signaller.ui.part.ChatMessageDateSeparatorCell;
import com.jaychang.signaller.ui.part.DefaultChatMessageDateSeparatorCell;

public class DefaultChatMessageDateSeparatorCellProvider implements ChatMessageDateSeparatorCellProvider {

  @Override
  public ChatMessageDateSeparatorCell createChatMessageDateSeparatorCell(long timestampMillis) {
    return new DefaultChatMessageDateSeparatorCell(timestampMillis);
  }

}

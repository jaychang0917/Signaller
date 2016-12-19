package com.jaychang.signaller.ui.config;

import com.jaychang.signaller.ui.cell.ChatMessageDateSeparatorCell;
import com.jaychang.signaller.ui.cell.DefaultChatMessageDateSeparatorCell;

public class DefaultChatMessageDateSeparatorCellProvider implements ChatMessageDateSeparatorCellProvider {

  @Override
  public ChatMessageDateSeparatorCell createChatMessageDateSeparatorCell(long timestampMillis) {
    return new DefaultChatMessageDateSeparatorCell(timestampMillis);
  }

}

package com.jaychang.signaller.ui.config;

import com.jaychang.signaller.core.model.ChatMessage;
import com.jaychang.signaller.ui.cell.ChatMessageCell;
import com.jaychang.signaller.ui.cell.DefaultOtherImageMessageCell;
import com.jaychang.signaller.ui.cell.DefaultOtherTextMessageCell;
import com.jaychang.signaller.ui.cell.DefaultOwnImageMessageCell;
import com.jaychang.signaller.ui.cell.DefaultOwnTextMessageCell;

import static com.jaychang.signaller.ui.config.ChatMessageType.IMAGE;
import static com.jaychang.signaller.ui.config.ChatMessageType.TEXT;

public class DefaultChatMessageCellProvider implements ChatMessageCellProvider {

  @Override
  public ChatMessageCell createOwnChatMessageCell(ChatMessageType type, ChatMessage message) {
    if (type.equals(TEXT)) {
      return new DefaultOwnTextMessageCell(message);
    } else if (type.equals(IMAGE)) {
      return new DefaultOwnImageMessageCell(message);
    }

    throw new RuntimeException(type + " is not support.");
  }

  @Override
  public ChatMessageCell createOtherChatMessageCell(ChatMessageType type, ChatMessage message) {
    if (type.equals(TEXT)) {
      return new DefaultOtherTextMessageCell(message);
    } else if (type.equals(IMAGE)) {
      return new DefaultOtherImageMessageCell(message);
    }

    throw new RuntimeException(type + " is not support.");
  }
}

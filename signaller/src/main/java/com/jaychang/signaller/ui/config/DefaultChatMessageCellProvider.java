package com.jaychang.signaller.ui.config;

import com.jaychang.signaller.core.model.SignallerChatMessage;
import com.jaychang.signaller.ui.part.ChatMessageCell;
import com.jaychang.signaller.ui.part.DefaultOtherImageMessageCell;
import com.jaychang.signaller.ui.part.DefaultOtherTextMessageCell;
import com.jaychang.signaller.ui.part.DefaultOwnImageMessageCell;
import com.jaychang.signaller.ui.part.DefaultOwnTextMessageCell;

import static com.jaychang.signaller.ui.config.ChatMessageType.IMAGE;
import static com.jaychang.signaller.ui.config.ChatMessageType.TEXT;

class DefaultChatMessageCellProvider implements ChatMessageCellProvider {

  @Override
  public ChatMessageCell getOwnChatMessageCell(ChatMessageType type, SignallerChatMessage message) {
    if (type.equals(TEXT)) {
      return new DefaultOwnTextMessageCell(message);
    } else if (type.equals(IMAGE)) {
      return new DefaultOwnImageMessageCell(message);
    }

    throw new RuntimeException(type + " is not support.");
  }

  @Override
  public ChatMessageCell getOtherChatMessageCell(ChatMessageType type, SignallerChatMessage message) {
    if (type.equals(TEXT)) {
      return new DefaultOtherTextMessageCell(message);
    } else if (type.equals(IMAGE)) {
      return new DefaultOtherImageMessageCell(message);
    }

    throw new RuntimeException(type + " is not support.");
  }
}

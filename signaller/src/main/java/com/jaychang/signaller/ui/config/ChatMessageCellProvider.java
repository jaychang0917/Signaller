package com.jaychang.signaller.ui.config;

import android.support.annotation.NonNull;

import com.jaychang.signaller.core.model.ChatMessage;
import com.jaychang.signaller.ui.part.ChatMessageCell;

public interface ChatMessageCellProvider {
  @NonNull ChatMessageCell getOwnChatMessageCell(ChatMessageType type, ChatMessage message);
  @NonNull ChatMessageCell getOtherChatMessageCell(ChatMessageType type, ChatMessage message);
}

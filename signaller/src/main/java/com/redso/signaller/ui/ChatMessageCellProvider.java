package com.redso.signaller.ui;

import android.support.annotation.NonNull;

import com.redso.signaller.core.model.ChatMessage;

public interface ChatMessageCellProvider {
  @NonNull ChatMessageCell getOwnChatMessageCell(ChatMessageType type, ChatMessage message);
  @NonNull ChatMessageCell getOtherChatMessageCell(ChatMessageType type, ChatMessage message);
}

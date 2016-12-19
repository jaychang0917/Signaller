package com.jaychang.signaller.ui.config;

import android.support.annotation.NonNull;

import com.jaychang.signaller.core.model.ChatMessage;
import com.jaychang.signaller.ui.cell.ChatMessageCell;

public interface ChatMessageCellProvider {
  @NonNull ChatMessageCell createOwnChatMessageCell(ChatMessageType type, ChatMessage message);
  @NonNull ChatMessageCell createOtherChatMessageCell(ChatMessageType type, ChatMessage message);
}

package com.jaychang.signaller.ui.config;

import android.support.annotation.NonNull;

import com.jaychang.signaller.core.model.ChatMessage;
import com.jaychang.signaller.ui.part.ChatMessageCell;

public interface CustomChatMessageCellProvider {
  @NonNull ChatMessageCell getCustomChatMessageCells(ChatMessage message);
}

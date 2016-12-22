package com.jaychang.signaller.ui.config;

import android.support.annotation.NonNull;

import com.jaychang.signaller.core.model.SignallerChatMessage;
import com.jaychang.signaller.ui.part.ChatMessageCell;

public interface CustomChatMessageCellProvider {
  @NonNull ChatMessageCell getCustomChatMessageCells(SignallerChatMessage message);
}

package com.redso.signaller.ui;

import android.support.annotation.NonNull;
import com.redso.signaller.core.model.SignallerChatMessage;

public interface ChatMessageCellProvider {
  @NonNull ChatMessageCell getOwnChatMessageCell(ChatMessageType type, SignallerChatMessage message);
  @NonNull ChatMessageCell getOtherChatMessageCell(ChatMessageType type, SignallerChatMessage message);
}

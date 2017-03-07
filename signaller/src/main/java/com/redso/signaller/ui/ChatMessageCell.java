package com.redso.signaller.ui;

import com.jaychang.srv.SimpleCell;
import com.jaychang.srv.SimpleViewHolder;
import com.redso.signaller.core.model.SignallerChatMessage;

public abstract class ChatMessageCell<VH extends SimpleViewHolder> extends SimpleCell<SignallerChatMessage, VH> {

  public ChatMessageCell(SignallerChatMessage chatMessage) {
    super(chatMessage);
  }

  public SignallerChatMessage geChatMessage() {
    return getItem();
  }

  public void setChatMessage(SignallerChatMessage message) {
    setItem(message);
  }

}

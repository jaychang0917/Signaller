package com.redso.signaller.ui;

import android.content.Context;

import com.jaychang.srv.SimpleCell;
import com.jaychang.srv.SimpleViewHolder;
import com.redso.signaller.core.model.SignallerChatMessage;

public abstract class ChatMessageCell<VH extends SimpleViewHolder> extends SimpleCell<SignallerChatMessage, VH> {

  public ChatMessageCell(SignallerChatMessage chatMessage) {
    super(chatMessage);
  }

  public SignallerChatMessage getChatMessage() {
    return getItem();
  }

  public void setChatMessage(SignallerChatMessage message) {
    setItem(message);
  }

  @Override
  protected void onBindViewHolder(VH vh, int position, Context context, Object payload) {
    onBindViewHolder(getChatMessage(), vh, position, context);
  }

  protected abstract void onBindViewHolder(SignallerChatMessage chatMessage, VH viewHolder, int position, Context context);

  @Override
  protected long getItemId() {
    if (getChatMessage().getMsgId() == null) {
      return getChatMessage().getTimestamp();
    } else {
      return getChatMessage().getMsgId().hashCode();
    }
  }

}

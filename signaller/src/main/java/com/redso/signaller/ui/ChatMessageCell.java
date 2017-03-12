package com.redso.signaller.ui;

import android.content.Context;

import com.jaychang.srv.SimpleCell;
import com.jaychang.srv.SimpleViewHolder;
import com.redso.signaller.core.model.ChatMessage;

public abstract class ChatMessageCell<VH extends SimpleViewHolder> extends SimpleCell<ChatMessage, VH> {

  public ChatMessageCell(ChatMessage chatMessage) {
    super(chatMessage);
  }

  public ChatMessage getChatMessage() {
    return getItem();
  }

  public void setChatMessage(ChatMessage message) {
    setItem(message);
  }

  @Override
  protected void onBindViewHolder(VH vh, int position, Context context, Object payload) {
    onBindViewHolder(getChatMessage(), vh, position, context);
  }

  protected abstract void onBindViewHolder(ChatMessage chatMessage, VH viewHolder, int position, Context context);

  @Override
  protected long getItemId() {
    if (getChatMessage().getMsgId() == null) {
      return getChatMessage().getTimestamp();
    } else {
      return getChatMessage().getMsgId().hashCode();
    }
  }

}

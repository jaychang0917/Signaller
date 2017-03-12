package com.redso.signaller.ui;

import android.content.Context;

import com.jaychang.srv.SimpleCell;
import com.jaychang.srv.SimpleViewHolder;
import com.redso.signaller.core.model.ChatMessage;
import com.redso.signaller.core.model.ChatRoom;

public abstract class ChatRoomCell<VH extends SimpleViewHolder> extends SimpleCell<ChatRoom, VH> {

  public ChatRoomCell(ChatRoom chatRoom) {
    super(chatRoom);
  }

  public ChatRoom getChatRoom() {
    return getItem();
  }

  public void setChatRoom(ChatRoom chatRoom) {
    setItem(chatRoom);
  }

  void updateLastMessage(ChatMessage lastMsg) {
    getChatRoom().setLastMessage(lastMsg);
  }

  void increaseUnreadCount() {
    getChatRoom().increaseUnreadCount();
  }

  void clearUnreadCount() {
    getChatRoom().setUnreadCount(0);
  }

  public void onCellClicked() {
    getChatRoom().setUnreadCount(0);
    getOnCellClickListener().onCellClicked(getChatRoom());
  }

  @Override
  protected void onBindViewHolder(VH vh, int position, Context context, Object payload) {
    onBindViewHolder(getChatRoom(), vh, position, context);
  }

  protected abstract void onBindViewHolder(ChatRoom chatRoom, VH vh, int position, Context context);

  @Override
  protected long getItemId() {
    return getChatRoom().getChatRoomId().hashCode();
  }

}

package com.redso.signaller.ui;

import com.jaychang.srv.SimpleCell;
import com.jaychang.srv.SimpleViewHolder;
import com.redso.signaller.core.SignallerDbManager;
import com.redso.signaller.core.model.SignallerChatMessage;
import com.redso.signaller.core.model.SignallerChatRoom;

public abstract class ChatRoomCell<VH extends SimpleViewHolder> extends SimpleCell<SignallerChatRoom, VH> {

  public ChatRoomCell(SignallerChatRoom chatRoom) {
    super(chatRoom);
  }

  public SignallerChatRoom getChatRoom() {
    return getItem();
  }

  public void setChatRoom(SignallerChatRoom chatRoom) {
    setItem(chatRoom);
  }

  public void updateLastMessage(SignallerChatMessage lastMsg) {
    getChatRoom().setLastMessage(lastMsg);
  }

  public void increaseUnreadCount() {
    getChatRoom().increaseUnreadCount();
  }

  public void onCellClicked() {
    getChatRoom().setUnreadCount(0);
    SignallerDbManager.getInstance().clearUnreadMessageCount(getChatRoom().getChatRoomId());
    getOnCellClickListener().onCellClicked(getChatRoom());
  }

}

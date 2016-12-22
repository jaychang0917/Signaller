package com.jaychang.signaller.ui.part;

import com.jaychang.nrv.BaseCell;
import com.jaychang.signaller.core.model.SignallerChatMessage;
import com.jaychang.signaller.core.model.SignallerChatRoom;

public abstract class ChatRoomCell extends BaseCell{

  public interface Callback {
    void onCellClicked(SignallerChatRoom chatroom);
  }

  protected SignallerChatRoom chatRoom;
  protected Callback callback;

  public ChatRoomCell(SignallerChatRoom chatRoom) {
    this.chatRoom = chatRoom;
  }

  public SignallerChatRoom getChatRoom() {
    return chatRoom;
  }

  public void setChatRoom(SignallerChatRoom chatRoom) {
    this.chatRoom = chatRoom;
  }

  public void updateLastMessage(SignallerChatMessage lastMsg) {
    chatRoom.setLastMessage(lastMsg);
  }

  public void increaseUnreadCount() {
    chatRoom.increaseUnreadCount();
  }

  public void setCallback(Callback callback) {
    this.callback = callback;
  }

}

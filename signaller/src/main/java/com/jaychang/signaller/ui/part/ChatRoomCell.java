package com.jaychang.signaller.ui.part;

import com.jaychang.nrv.BaseCell;
import com.jaychang.signaller.core.model.ChatMessage;
import com.jaychang.signaller.core.model.ChatRoom;

public abstract class ChatRoomCell extends BaseCell{

  public interface Callback {
    void onCellClicked(ChatRoom chatroom);
  }

  protected ChatRoom chatRoom;
  protected Callback callback;

  public ChatRoomCell(ChatRoom chatRoom) {
    this.chatRoom = chatRoom;
  }

  public ChatRoom getChatRoom() {
    return chatRoom;
  }

  public void setChatRoom(ChatRoom chatRoom) {
    this.chatRoom = chatRoom;
  }

  public void updateLastMessage(ChatMessage lastMsg) {
    chatRoom.setLastMessage(lastMsg);
  }

  public void increaseUnreadCount() {
    chatRoom.increaseUnreadCount();
  }

  public void setCallback(Callback callback) {
    this.callback = callback;
  }

}

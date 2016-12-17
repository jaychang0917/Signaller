package com.jaychang.signaller.ui;

import com.jaychang.nrv.BaseCell;
import com.jaychang.signaller.core.model.ChatMessage;
import com.jaychang.signaller.core.model.ChatRoom;

public abstract class ChatRoomCell extends BaseCell{

  private ChatRoom chatRoom;

  public ChatRoomCell(ChatRoom chatRoom) {
    this.chatRoom = chatRoom;
  }

  public ChatRoom getChatRoom() {
    return chatRoom;
  }

  public void updateLastMessage(ChatMessage lastMsg) {
    chatRoom.lastMessage = lastMsg;
  }

  public void increaseUnreadCount() {
    chatRoom.unreadCount++;
  }

  // todo default click cell to invisible unread count
}

package com.redso.signaller.ui;

import android.support.annotation.NonNull;

import com.redso.signaller.core.model.SignallerChatRoom;

public interface ChatRoomCellProvider {

  @NonNull ChatRoomCell getChatRoomCell(SignallerChatRoom chatRoom);

}

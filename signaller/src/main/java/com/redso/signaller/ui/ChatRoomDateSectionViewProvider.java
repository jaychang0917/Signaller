package com.redso.signaller.ui;

import android.support.annotation.NonNull;
import android.view.View;

import com.redso.signaller.core.model.SignallerChatMessage;

public interface ChatRoomDateSectionViewProvider {

  @NonNull View getChatRoomDateSectionView(SignallerChatMessage item);

  boolean isSameSection(SignallerChatMessage item, SignallerChatMessage nextItem);

}

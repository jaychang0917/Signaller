package com.redso.signaller.ui;

import android.support.annotation.NonNull;
import android.view.View;

import com.redso.signaller.core.model.ChatMessage;

public interface ChatRoomDateSectionViewProvider {

  @NonNull View getChatRoomDateSectionView(ChatMessage item);

  boolean isSameSection(ChatMessage item, ChatMessage nextItem);

}

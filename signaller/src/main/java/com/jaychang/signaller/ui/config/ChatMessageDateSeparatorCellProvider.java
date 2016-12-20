package com.jaychang.signaller.ui.config;

import android.support.annotation.NonNull;

import com.jaychang.signaller.ui.part.ChatMessageDateSeparatorCell;

public interface ChatMessageDateSeparatorCellProvider {
  @NonNull ChatMessageDateSeparatorCell getChatMessageDateSeparatorCell(long timestampMillis);
}

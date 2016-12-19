package com.jaychang.signaller.ui.config;

import android.support.annotation.NonNull;

import com.jaychang.signaller.ui.cell.ChatMessageDateSeparatorCell;

public interface ChatMessageDateSeparatorCellProvider {
  @NonNull ChatMessageDateSeparatorCell createChatMessageDateSeparatorCell(long timestampMillis);
}

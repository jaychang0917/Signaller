package com.jaychang.signaller.ui.config;

import android.support.annotation.NonNull;
import android.view.View;

import com.jaychang.signaller.core.model.SignallerChatMessage;

public interface DateSeparatorViewProvider {
  @NonNull View getSeparatorView(SignallerChatMessage item);
  boolean isSameDate(SignallerChatMessage item, SignallerChatMessage nextItem);
}

package com.jaychang.signaller.ui.config;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;

import com.jaychang.signaller.core.Signaller;
import com.jaychang.signaller.core.model.SignallerChatMessage;
import com.jaychang.signaller.ui.part.DefaultDateSeparatorView;

public class DefaultDateSeparatorViewProvider implements DateSeparatorViewProvider {

  @NonNull
  @Override
  public View getSeparatorView(SignallerChatMessage item) {
    Context context = Signaller.getInstance().getAppContext();
    DefaultDateSeparatorView view = new DefaultDateSeparatorView(context);
    view.bind(item);
    return view;
  }

  @Override
  public boolean isSameDate(SignallerChatMessage item, SignallerChatMessage nextItem) {
    return item.isSameDate(nextItem);
  }

}

package com.jaychang.signaller.ui.config;

import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;

public interface ChatRoomControlViewProvider {

  @LayoutRes int getLayoutRes();
  @IdRes int getInputEditTextId();
  @IdRes int getEmojiIconViewId();
  @IdRes int getPhotoIconViewId();
  @IdRes int getSendViewId();

}

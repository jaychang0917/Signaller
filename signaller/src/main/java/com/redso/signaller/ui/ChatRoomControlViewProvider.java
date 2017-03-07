package com.redso.signaller.ui;

import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;

public interface ChatRoomControlViewProvider {

  @LayoutRes int getLayoutRes();

  @IdRes int getInputEditTextId();

  EmojiKeyboardViewInfo getEmojiKeyboardViewInfo();

  @IdRes int getPhotoIconViewId();

  @IdRes int getSendMessageViewId();

}

package com.redso.signaller.ui;

import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;

public interface ChatRoomMessageInputViewProvider {

  @LayoutRes int getLayoutRes();

  @IdRes int getInputEditTextId();

  EmojiKeyboardViewInfo getEmojiKeyboardViewInfo();

  @IdRes int getPhotoPickerIconViewId();

  @IdRes int getSendMessageViewId();

}

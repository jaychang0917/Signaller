package com.redso.signaller.ui;

import android.support.annotation.DrawableRes;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;

public interface ChatRoomControlViewProvider {

  @LayoutRes int getLayoutRes();

  @IdRes int getInputEditTextId();

  @IdRes int getEmojiIconViewId();

  @DrawableRes int getEmojiIconResId();

  @DrawableRes int getKeyboardIconResId();

  @IdRes int getPhotoIconViewId();

  @IdRes int getSendMessageViewId();

}

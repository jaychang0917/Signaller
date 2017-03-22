package com.redso.signaller.ui;

import android.support.annotation.DrawableRes;
import android.support.annotation.IdRes;

public interface EmojiKeyboardViewInfo {

  @IdRes int getEmojiIconImageViewId();

  @DrawableRes int getEmojiIconResId();

  @DrawableRes int getKeyboardIconResId();

}

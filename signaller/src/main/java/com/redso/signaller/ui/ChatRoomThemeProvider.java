package com.redso.signaller.ui;

import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;

public interface ChatRoomThemeProvider {

  @NonNull @ColorRes int getStatusBarColor();

  @NonNull @ColorRes int getPhotoPickerToolbarBackgroundColor();

}

package com.redso.signaller.ui;

import android.net.Uri;

public interface ChatRoomOperations {

  void sendImageMessage(Uri uri);

  void sendTextMessage(String msg);

}

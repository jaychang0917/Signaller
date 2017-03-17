package com.redso.signaller.ui;

import android.net.Uri;

public class ChatRoomFragmentProxy implements ChatRoomOperations {

   private ChatRoomFragment real;

   public ChatRoomFragmentProxy(ChatRoomFragment real) {
      this.real = real;
   }

   @Override
   public void sendPhotoMessage(Uri uri) {
     real.sendPhotoMessage(uri);
   }

   @Override
   public void sendTextMessage(String msg) {
      real.sendTextMessage(msg);
   }

}

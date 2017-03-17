package com.redso.signaller.core;

import com.redso.signaller.ui.ChatRoomFragmentProxy;

public class ProxyManager {

  private static ProxyManager INSTANCE = new ProxyManager();
  private ChatRoomFragmentProxy chatRoomFragmentProxy;

  private ProxyManager() {
  }

  public static ProxyManager getInstance() {
    return INSTANCE;
  }

  public ChatRoomFragmentProxy getChatRoomFragmentProxy() {
    return chatRoomFragmentProxy;
  }

  public void setChatRoomFragmentProxy(ChatRoomFragmentProxy chatRoomFragmentProxy) {
    this.chatRoomFragmentProxy = chatRoomFragmentProxy;
  }

}

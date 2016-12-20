package com.jaychang.signaller.ui.config;

import android.support.annotation.ColorRes;

import com.jaychang.signaller.R;

public class UIConfig {

  private ChatRoomCellProvider chatRoomCellProvider;
  private ChatRoomToolbarProvider chatRoomToolbarProvider;
  private ChatRoomControlViewProvider chatRoomControlViewProvider;
  private ChatMessageCellProvider chatMessageCellProvider;
  private CustomChatMessageCellProvider customChatMessageCellProvider;
  private ChatMessageDateSeparatorCellProvider chatMsgDateSeparatorCellProvider;
  private boolean showChatMsgDateSeparator = true;
  private int toolbarBackgroundColor = R.color.toolbar_background;

  private UIConfig() {
    chatRoomCellProvider(new DefaultChatRoomCellProvider());
    chatRoomToolbarProvider(new DefaultChatRoomToolbarProvider());
    chatRoomControlViewProvider(new DefaultChatRoomControlViewProvider());
    chatMessageCellProvider(new DefaultChatMessageCellProvider());
    chatMessageDateSeparatorCellProvider(new DefaultChatMessageDateSeparatorCellProvider());
    showChatMessageDateSeparator(true);
    chatRoomToolbarProvider(new DefaultChatRoomToolbarProvider());
  }

  public static UIConfig create() {
    return new UIConfig();
  }

  public ChatRoomCellProvider getChatRoomCellProvider() {
    return chatRoomCellProvider;
  }

  public ChatMessageCellProvider getChatMessageCellProvider() {
    return chatMessageCellProvider;
  }

  public ChatMessageDateSeparatorCellProvider getChatMsgDateSeparatorCellProvider() {
    return chatMsgDateSeparatorCellProvider;
  }

  public boolean isShowChatMessageDateSeparator() {
    return showChatMsgDateSeparator;
  }

  public ChatRoomToolbarProvider getChatRoomToolbarProvider() {
    return chatRoomToolbarProvider;
  }

  public UIConfig chatRoomCellProvider(ChatRoomCellProvider provider) {
    chatRoomCellProvider = provider;
    return this;
  }

  public UIConfig chatRoomToolbarProvider(ChatRoomToolbarProvider provider) {
    chatRoomToolbarProvider = provider;
    return this;
  }

  public UIConfig chatMessageCellProvider(ChatMessageCellProvider provider) {
    chatMessageCellProvider = provider;
    return this;
  }

  public UIConfig chatMessageDateSeparatorCellProvider(ChatMessageDateSeparatorCellProvider provider) {
    chatMsgDateSeparatorCellProvider = provider;
    return this;
  }

  public UIConfig showChatMessageDateSeparator(boolean showChatMsgDateSeparator) {
    this.showChatMsgDateSeparator = showChatMsgDateSeparator;
    return this;
  }

  public UIConfig customChatMessageCellProvider(CustomChatMessageCellProvider provider) {
    customChatMessageCellProvider = provider;
    return this;
  }

  public CustomChatMessageCellProvider getCustomChatMessageCellProvider() {
    return customChatMessageCellProvider;
  }

  public UIConfig chatRoomControlViewProvider(ChatRoomControlViewProvider provider) {
    chatRoomControlViewProvider = provider;
    return this;
  }

  public ChatRoomControlViewProvider getChatRoomControlViewProvider() {
    return chatRoomControlViewProvider;
  }

  public UIConfig toolbarBackgroundColor(@ColorRes int toolbarBackgroundColor) {
    this.toolbarBackgroundColor = toolbarBackgroundColor;
    return this;
  }

  public @ColorRes int getToolbarBackgroundColor() {
    return toolbarBackgroundColor;
  }
}

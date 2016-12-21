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
  private boolean showChatMsgDateSeparator;
  private @ColorRes int toolbarBackgroundColor;

  private UIConfig(Builder builder) {
    chatRoomCellProvider = builder.chatRoomCellProvider;
    chatRoomToolbarProvider = builder.chatRoomToolbarProvider;
    chatRoomControlViewProvider = builder.chatRoomControlViewProvider;
    chatMessageCellProvider = builder.chatMessageCellProvider;
    customChatMessageCellProvider = builder.customChatMessageCellProvider;
    chatMsgDateSeparatorCellProvider = builder.chatMsgDateSeparatorCellProvider;
    showChatMsgDateSeparator = builder.showChatMsgDateSeparator;
    toolbarBackgroundColor = builder.toolbarBackgroundColor;
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  public ChatRoomCellProvider getChatRoomCellProvider() {
    return chatRoomCellProvider;
  }

  public ChatRoomToolbarProvider getChatRoomToolbarProvider() {
    return chatRoomToolbarProvider;
  }

  public ChatRoomControlViewProvider getChatRoomControlViewProvider() {
    return chatRoomControlViewProvider;
  }

  public ChatMessageCellProvider getChatMessageCellProvider() {
    return chatMessageCellProvider;
  }

  public CustomChatMessageCellProvider getCustomChatMessageCellProvider() {
    return customChatMessageCellProvider;
  }

  public ChatMessageDateSeparatorCellProvider getChatMsgDateSeparatorCellProvider() {
    return chatMsgDateSeparatorCellProvider;
  }

  public boolean isShowChatMessageDateSeparator() {
    return showChatMsgDateSeparator;
  }

  public int getToolbarBackgroundColor() {
    return toolbarBackgroundColor;
  }

  public static final class Builder {
    private ChatRoomCellProvider chatRoomCellProvider;
    private ChatRoomToolbarProvider chatRoomToolbarProvider;
    private ChatRoomControlViewProvider chatRoomControlViewProvider;
    private ChatMessageCellProvider chatMessageCellProvider;
    private CustomChatMessageCellProvider customChatMessageCellProvider;
    private ChatMessageDateSeparatorCellProvider chatMsgDateSeparatorCellProvider;
    private boolean showChatMsgDateSeparator = true;
    private int toolbarBackgroundColor = R.color.toolbar_background;

    private Builder() {
    }

    public Builder chatRoomCellProvider(ChatRoomCellProvider provider) {
      chatRoomCellProvider = provider;
      return this;
    }

    public Builder chatRoomToolbarProvider(ChatRoomToolbarProvider provider) {
      chatRoomToolbarProvider = provider;
      return this;
    }

    public Builder chatRoomControlViewProvider(ChatRoomControlViewProvider provider) {
      chatRoomControlViewProvider = provider;
      return this;
    }

    public Builder chatMessageCellProvider(ChatMessageCellProvider provider) {
      chatMessageCellProvider = provider;
      return this;
    }

    public Builder customChatMessageCellProvider(CustomChatMessageCellProvider provider) {
      customChatMessageCellProvider = provider;
      return this;
    }

    public Builder chatMessageDateSeparatorCellProvider(ChatMessageDateSeparatorCellProvider provider) {
      chatMsgDateSeparatorCellProvider = provider;
      return this;
    }

    public Builder showChatMessageDateSeparator(boolean showChatMsgDateSeparator) {
      this.showChatMsgDateSeparator = showChatMsgDateSeparator;
      return this;
    }

    public Builder toolbarBackgroundColor(@ColorRes int colorRes) {
      toolbarBackgroundColor = colorRes;
      return this;
    }

    public UIConfig build() {
      setDefaultProviders();
      return new UIConfig(this);
    }

    private void setDefaultProviders() {
      if (chatRoomCellProvider == null) {
        chatRoomCellProvider = new DefaultChatRoomCellProvider();
      }
      if (chatRoomToolbarProvider == null) {
        chatRoomToolbarProvider = new DefaultChatRoomToolbarProvider();
      }
      if (chatRoomControlViewProvider == null) {
        chatRoomControlViewProvider = new DefaultChatRoomControlViewProvider();
      }
      if (chatMessageCellProvider == null) {
        chatMessageCellProvider = new DefaultChatMessageCellProvider();
      }
      if (chatMsgDateSeparatorCellProvider == null) {
        chatMsgDateSeparatorCellProvider = new DefaultChatMessageDateSeparatorCellProvider();
      }
    }
  }

}

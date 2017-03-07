package com.redso.signaller.ui;

public class UIConfig {

  private ChatRoomCellProvider chatRoomCellProvider;
  private ChatMessageCellProvider chatMessageCellProvider;
  private ChatRoomDateSectionViewProvider chatRoomDateSectionViewProvider;
  private ChatRoomToolbarProvider chatRoomToolbarProvider;
  private ChatRoomControlViewProvider chatRoomControlViewProvider;
  private ChatRoomThemeProvider chatRoomThemeProvider;

  private UIConfig(Builder builder) {
    chatRoomCellProvider = builder.chatRoomCellProvider;
    chatMessageCellProvider = builder.chatMessageCellProvider;
    chatRoomDateSectionViewProvider = builder.chatRoomDateSectionViewProvider;
    chatRoomToolbarProvider = builder.chatRoomToolbarProvider;
    chatRoomControlViewProvider = builder.chatRoomControlViewProvider;
    chatRoomThemeProvider = builder.chatRoomThemeProvider;
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  public static final class Builder {
    private ChatRoomCellProvider chatRoomCellProvider;
    private ChatMessageCellProvider chatMessageCellProvider;
    private ChatRoomDateSectionViewProvider chatRoomDateSectionViewProvider;
    private ChatRoomToolbarProvider chatRoomToolbarProvider;
    private ChatRoomControlViewProvider chatRoomControlViewProvider;
    private ChatRoomThemeProvider chatRoomThemeProvider;

    private Builder() {
    }

    public Builder setChatRoomCellProvider(ChatRoomCellProvider val) {
      chatRoomCellProvider = val;
      return this;
    }

    public Builder setChatMessageCellProvider(ChatMessageCellProvider val) {
      chatMessageCellProvider = val;
      return this;
    }

    public Builder setChatRoomDateSectionViewProvider(ChatRoomDateSectionViewProvider val) {
      chatRoomDateSectionViewProvider = val;
      return this;
    }

    public Builder setChatRoomToolbarProvider(ChatRoomToolbarProvider val) {
      chatRoomToolbarProvider = val;
      return this;
    }

    public Builder setChatRoomControlViewProvider(ChatRoomControlViewProvider val) {
      chatRoomControlViewProvider = val;
      return this;
    }

    public Builder setChatRoomThemeProvider(ChatRoomThemeProvider val) {
      chatRoomThemeProvider = val;
      return this;
    }

    public UIConfig build() {
      return new UIConfig(this);
    }
  }

  ChatRoomCellProvider getChatRoomCellProvider() {
    return chatRoomCellProvider;
  }

  ChatMessageCellProvider getChatMessageCellProvider() {
    return chatMessageCellProvider;
  }

  ChatRoomDateSectionViewProvider getChatRoomDateSectionViewProvider() {
    return chatRoomDateSectionViewProvider;
  }

  ChatRoomToolbarProvider getChatRoomToolbarProvider() {
    return chatRoomToolbarProvider;
  }

  ChatRoomControlViewProvider getChatRoomControlViewProvider() {
    return chatRoomControlViewProvider;
  }

  ChatRoomThemeProvider getChatRoomThemeProvider() {
    return chatRoomThemeProvider;
  }

}

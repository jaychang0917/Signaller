package com.redso.signaller.ui;

import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.LayoutRes;
import android.view.View;

public class UIConfig {

  private ChatRoomCellProvider chatRoomCellProvider;
  private ChatMessageCellProvider chatMessageCellProvider;
  private ChatRoomDateSectionViewProvider chatRoomDateSectionViewProvider;
  private ChatRoomToolbarProvider chatRoomToolbarProvider;
  private ChatRoomControlViewProvider chatRoomControlViewProvider;
  private int chatRoomPhotoPickerThemeColor;
  private int chatRoomEmptyStateViewRes;
  private View chatRoomEmptyStateView;
  private int chatRoomListEmptyStateViewRes;
  private View chatRoomListEmptyStateView;
  private int chatRoomBackgroundRes;

  private UIConfig(Builder builder) {
    chatRoomCellProvider = builder.chatRoomCellProvider;
    chatMessageCellProvider = builder.chatMessageCellProvider;
    chatRoomDateSectionViewProvider = builder.chatRoomDateSectionViewProvider;
    chatRoomToolbarProvider = builder.chatRoomToolbarProvider;
    chatRoomControlViewProvider = builder.chatRoomControlViewProvider;
    chatRoomPhotoPickerThemeColor = builder.chatRoomPhotoPickerThemeColor;
    chatRoomEmptyStateViewRes = builder.chatRoomEmptyStateViewRes;
    chatRoomEmptyStateView = builder.chatRoomEmptyStateView;
    chatRoomListEmptyStateViewRes = builder.chatRoomListEmptyStateViewRes;
    chatRoomListEmptyStateView = builder.chatRoomListEmptyStateView;
    chatRoomBackgroundRes = builder.chatRoomBackgroundRes;
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
    private int chatRoomPhotoPickerThemeColor;
    private int chatRoomEmptyStateViewRes;
    private View chatRoomEmptyStateView;
    private int chatRoomListEmptyStateViewRes;
    private View chatRoomListEmptyStateView;
    private int chatRoomBackgroundRes;

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

    public Builder setChatRoomPhotoPickerThemeColor(@ColorRes int val) {
      chatRoomPhotoPickerThemeColor = val;
      return this;
    }

    public Builder setChatRoomEmptyStateViewRes(@LayoutRes int val) {
      chatRoomEmptyStateViewRes = val;
      return this;
    }

    public Builder setChatRoomEmptyStateView(View val) {
      chatRoomEmptyStateView = val;
      return this;
    }

    public Builder setChatRoomListEmptyStateViewRes(@LayoutRes int val) {
      chatRoomListEmptyStateViewRes = val;
      return this;
    }

    public Builder setChatRoomListEmptyStateView(View val) {
      chatRoomListEmptyStateView = val;
      return this;
    }

    public Builder setChatRoomBackgroundRes(@ColorRes @DrawableRes int val) {
      chatRoomBackgroundRes = val;
      return this;
    }

    public UIConfig build() {
      return new UIConfig(this);
    }

  }

  public ChatRoomCellProvider getChatRoomCellProvider() {
    return chatRoomCellProvider;
  }

  public ChatMessageCellProvider getChatMessageCellProvider() {
    return chatMessageCellProvider;
  }

  public ChatRoomDateSectionViewProvider getChatRoomDateSectionViewProvider() {
    return chatRoomDateSectionViewProvider;
  }

  public ChatRoomToolbarProvider getChatRoomToolbarProvider() {
    return chatRoomToolbarProvider;
  }

  public ChatRoomControlViewProvider getChatRoomControlViewProvider() {
    return chatRoomControlViewProvider;
  }

  public int getChatRoomPhotoPickerThemeColor() {
    return chatRoomPhotoPickerThemeColor;
  }

  public int getChatRoomEmptyStateViewRes() {
    return chatRoomEmptyStateViewRes;
  }

  public View getChatRoomEmptyStateView() {
    return chatRoomEmptyStateView;
  }

  public int getChatRoomListEmptyStateViewRes() {
    return chatRoomListEmptyStateViewRes;
  }

  public View getChatRoomListEmptyStateView() {
    return chatRoomListEmptyStateView;
  }

  public int getChatRoomBackgroundRes() {
    return chatRoomBackgroundRes;
  }

}

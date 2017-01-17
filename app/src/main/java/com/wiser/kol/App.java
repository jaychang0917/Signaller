package com.wiser.kol;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.multidex.MultiDexApplication;
import android.view.View;

import com.jaychang.signaller.core.AppConfig;
import com.jaychang.signaller.core.Signaller;
import com.jaychang.signaller.core.model.SignallerChatMessage;
import com.jaychang.signaller.core.model.SignallerChatRoom;
import com.jaychang.signaller.ui.config.ChatMessageCellProvider;
import com.jaychang.signaller.ui.config.ChatMessageType;
import com.jaychang.signaller.ui.config.ChatRoomCellProvider;
import com.jaychang.signaller.ui.config.ChatRoomControlViewProvider;
import com.jaychang.signaller.ui.config.ChatRoomToolbarProvider;
import com.jaychang.signaller.ui.config.CustomChatMessageCellProvider;
import com.jaychang.signaller.ui.config.DateSeparatorViewProvider;
import com.jaychang.signaller.core.UIConfig;
import com.jaychang.signaller.ui.part.ChatMessageCell;
import com.jaychang.signaller.ui.part.ChatRoomCell;
import com.wiser.kol.ui.KolChatRoomCell;
import com.wiser.kol.ui.KolChatRoomToolbar;
import com.wiser.kol.ui.KolDateSeparatorView;
import com.wiser.kol.ui.KolEventMessageCell;
import com.wiser.kol.ui.KolOtherImageMessageCell;
import com.wiser.kol.ui.KolOtherTextMessageCell;
import com.wiser.kol.ui.KolOwnImageMessageCell;
import com.wiser.kol.ui.KolOwnTextMessageCell;

public class App extends MultiDexApplication {

  public static String currentUserId;

  @Override
  public void onCreate() {
    super.onCreate();

    Utils.init(this);

    initSignaller();
  }

  private void initSignaller() {
    UIConfig uiConfig = UIConfig.newBuilder()
      .chatRoomCellProvider(new ChatRoomCellProvider() {
        @NonNull
        @Override
        public ChatRoomCell getChatRoomCell(SignallerChatRoom chatRoom) {
          return new KolChatRoomCell(chatRoom);
        }
      })
      .chatMessageCellProvider(new ChatMessageCellProvider() {
        @NonNull
        @Override
        public ChatMessageCell getOwnChatMessageCell(ChatMessageType type, SignallerChatMessage message) {
          if (type.equals(ChatMessageType.TEXT)) {
            return new KolOwnTextMessageCell(message);
          } else {
            return new KolOwnImageMessageCell(message);
          }
        }

        @NonNull
        @Override
        public ChatMessageCell getOtherChatMessageCell(ChatMessageType type, SignallerChatMessage message) {
          if (type.equals(ChatMessageType.TEXT)) {
            return new KolOtherTextMessageCell(message);
          } else {
            return new KolOtherImageMessageCell(message);
          }
        }
      })
      .customChatMessageCellProvider(new CustomChatMessageCellProvider() {
        @NonNull
        @Override
        public ChatMessageCell getCustomChatMessageCells(SignallerChatMessage message) {
          return new KolEventMessageCell(message);
        }
      })
      .dateSeparatorViewProvider(new DateSeparatorViewProvider() {
        @NonNull
        @Override
        public View getSeparatorView(SignallerChatMessage item) {
          KolDateSeparatorView view = new KolDateSeparatorView(getApplicationContext());
          view.bind(item);
          return view;
        }

        @Override
        public boolean isSameDate(SignallerChatMessage item, SignallerChatMessage nextItem) {
          return item.isSameDate(nextItem);
        }
      })
      .chatRoomToolbarProvider(new ChatRoomToolbarProvider() {
        @NonNull
        @Override
        public View getToolbar(Activity activity, String username) {
          return KolChatRoomToolbar.create(activity, username);
        }
      })
      .chatRoomControlViewProvider(new ChatRoomControlViewProvider() {
        @Override
        public int getLayoutRes() {
          return R.layout.sig_view_chatroom_control;
        }

        @Override
        public int getInputEditTextId() {
          return R.id.inputEditText;
        }

        @Override
        public int getEmojiIconViewId() {
          return R.id.emojiIconView;
        }

        @Override
        public int getPhotoIconViewId() {
          return R.id.photoIconView;
        }

        @Override
        public int getSendViewId() {
          return R.id.sendView;
        }
      })
      .chatRoomToolbarBackgroundColor(R.color.white)
      .chatRoomStatusBarBackgroundColor(R.color.colorPrimary)
      .build();

    AppConfig appConfig = new AppConfig(
      R.string.app_name,
      R.mipmap.ic_launcher,
      Constant.SERVER_DOMAIN,
      Constant.SOCKET_URL,
      Constant.PUSH_SENDER_ID,
      MainActivity.class);

    Signaller.init(this, appConfig, uiConfig);

    Signaller.getInstance().enableDebug();
  }

}

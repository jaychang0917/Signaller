package com.wiser.kol;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.multidex.MultiDexApplication;
import android.view.View;

import com.jaychang.signaller.core.Signaller;
import com.jaychang.signaller.core.model.ChatMessage;
import com.jaychang.signaller.core.model.ChatRoom;
import com.jaychang.signaller.ui.config.ChatMessageCellProvider;
import com.jaychang.signaller.ui.config.ChatMessageDateSeparatorCellProvider;
import com.jaychang.signaller.ui.config.ChatMessageType;
import com.jaychang.signaller.ui.config.ChatRoomCellProvider;
import com.jaychang.signaller.ui.config.ChatRoomControlViewProvider;
import com.jaychang.signaller.ui.config.ChatRoomToolbarProvider;
import com.jaychang.signaller.ui.config.CustomChatMessageCellProvider;
import com.jaychang.signaller.ui.config.UIConfig;
import com.jaychang.signaller.ui.part.ChatMessageCell;
import com.jaychang.signaller.ui.part.ChatMessageDateSeparatorCell;
import com.jaychang.signaller.ui.part.ChatRoomCell;
import com.wiser.kol.ui.KolChatMessageDateSeparatorCell;
import com.wiser.kol.ui.KolChatRoomCell;
import com.wiser.kol.ui.KolChatRoomToolbar;
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

    Signaller.init(this, Constant.SERVER_DOMAIN, Constant.SOCKETE_URL);

    UIConfig uiConfig = UIConfig.create()
      .chatRoomCellProvider(new ChatRoomCellProvider() {
        @NonNull
        @Override
        public ChatRoomCell getChatRoomCell(ChatRoom chatRoom) {
          return new KolChatRoomCell(chatRoom);
        }
      })
      .chatMessageCellProvider(new ChatMessageCellProvider() {
        @NonNull
        @Override
        public ChatMessageCell getOwnChatMessageCell(ChatMessageType type, ChatMessage message) {
          if (type.equals(ChatMessageType.TEXT)) {
            return new KolOwnTextMessageCell(message);
          } else {
            return new KolOwnImageMessageCell(message);
          }
        }

        @NonNull
        @Override
        public ChatMessageCell getOtherChatMessageCell(ChatMessageType type, ChatMessage message) {
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
        public ChatMessageCell getCustomChatMessageCells(ChatMessage message) {
          return new KolEventMessageCell(message);
        }
      })
      .chatMessageDateSeparatorCellProvider(new ChatMessageDateSeparatorCellProvider() {
        @NonNull
        @Override
        public ChatMessageDateSeparatorCell getChatMessageDateSeparatorCell(long timestampMillis) {
          return new KolChatMessageDateSeparatorCell(timestampMillis);
        }
      })
      .chatRoomToolbarProvider(new ChatRoomToolbarProvider() {
        @NonNull
        @Override
        public View getToolbar(Activity activity, ChatRoom chatRoom) {
          return KolChatRoomToolbar.create(activity, chatRoom);
        }
      })
      .chatRoomControlViewProvider(new ChatRoomControlViewProvider() {
        @Override
        public int getLayoutRes() {
          return R.layout.view_chatroom_control;
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
      .toolbarBackgroundColor(R.color.colorPrimary);

    Signaller.getInstance().setUIConfig(uiConfig);
  }
}

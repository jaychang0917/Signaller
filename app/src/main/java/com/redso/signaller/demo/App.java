package com.redso.signaller.demo;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.multidex.MultiDexApplication;
import android.view.View;

import com.redso.signaller.core.AppConfig;
import com.redso.signaller.core.Signaller;
import com.redso.signaller.core.model.SignallerChatMessage;
import com.redso.signaller.core.model.SignallerChatRoom;
import com.redso.signaller.demo.widget.CustomChatRoomCell;
import com.redso.signaller.demo.widget.CustomChatRoomDateSectionView;
import com.redso.signaller.demo.widget.CustomChatRoomToolbar;
import com.redso.signaller.demo.widget.CustomOtherImageMessageCell;
import com.redso.signaller.demo.widget.CustomOtherTextMessageCell;
import com.redso.signaller.demo.widget.CustomOwnImageMessageCell;
import com.redso.signaller.demo.widget.CustomOwnTextMessageCell;
import com.redso.signaller.ui.ChatMessageCell;
import com.redso.signaller.ui.ChatMessageCellProvider;
import com.redso.signaller.ui.ChatMessageType;
import com.redso.signaller.ui.ChatRoomCell;
import com.redso.signaller.ui.ChatRoomCellProvider;
import com.redso.signaller.ui.ChatRoomControlViewProvider;
import com.redso.signaller.ui.ChatRoomDateSectionViewProvider;
import com.redso.signaller.ui.ChatRoomThemeProvider;
import com.redso.signaller.ui.ChatRoomToolbarProvider;
import com.redso.signaller.ui.UIConfig;

public class App extends MultiDexApplication {

  public static String currentUserId;

  @Override
  public void onCreate() {
    super.onCreate();

    Utils.init(this);

    initSignaller();
  }

  private void initSignaller() {
    AppConfig appConfig = AppConfig.newBuilder(Constant.SOCKET_URL, Constant.SERVER_DOMAIN)
      .enablePushNotification(R.string.app_name, R.mipmap.ic_launcher, Constant.PUSH_SENDER_ID, MainActivity.class)
      .build();

    UIConfig uiConfig = UIConfig.newBuilder()
      .setChatRoomCellProvider(new ChatRoomCellProvider() {
        @NonNull
        @Override
        public ChatRoomCell getChatRoomCell(SignallerChatRoom chatRoom) {
          return new CustomChatRoomCell(chatRoom);
        }
      })
      .setChatMessageCellProvider(new ChatMessageCellProvider() {
        @NonNull
        @Override
        public ChatMessageCell getOwnChatMessageCell(ChatMessageType type, SignallerChatMessage message) {
          if (type.equals(ChatMessageType.TEXT)) {
            return new CustomOwnTextMessageCell(message);
          } else if (type.equals(ChatMessageType.IMAGE)) {
            return new CustomOwnImageMessageCell(message);
          }
          throw new RuntimeException("Unsupported chat message type");
        }

        @NonNull
        @Override
        public ChatMessageCell getOtherChatMessageCell(ChatMessageType type, SignallerChatMessage message) {
          if (type.equals(ChatMessageType.TEXT)) {
            return new CustomOtherTextMessageCell(message);
          } else if (type.equals(ChatMessageType.IMAGE)) {
            return new CustomOtherImageMessageCell(message);
          }
          throw new RuntimeException("Unsupported chat message type");
        }
      })
      .setChatRoomControlViewProvider(new ChatRoomControlViewProvider() {
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
        public int getEmojiIconResId() {
          return R.drawable.ic_emoji;
        }

        @Override
        public int getKeyboardIconResId() {
          return R.drawable.ic_keyboard;
        }

        @Override
        public int getPhotoIconViewId() {
          return R.id.photoIconView;
        }

        @Override
        public int getSendMessageViewId() {
          return R.id.sendMessageView;
        }
      })
      // optional
      .setChatRoomDateSectionViewProvider(new ChatRoomDateSectionViewProvider() {
        @NonNull
        @Override
        public View getChatRoomDateSectionView(SignallerChatMessage item) {
          CustomChatRoomDateSectionView view = new CustomChatRoomDateSectionView(getApplicationContext());
          view.bind(item);
          return view;
        }

        @Override
        public boolean isSameSection(SignallerChatMessage item, SignallerChatMessage nextItem) {
          return item.isSameDate(nextItem);
        }
      })
      // optional
      .setChatRoomToolbarProvider(new ChatRoomToolbarProvider() {
        @NonNull
        @Override
        public View getToolbar(Activity activity, String username) {
          return CustomChatRoomToolbar.create(activity, username);
        }
      })
      // optional
      .setChatRoomThemeProvider(new ChatRoomThemeProvider() {
        @NonNull
        @Override
        public int getStatusBarColor() {
          return R.color.colorPrimaryDark;
        }

        @NonNull
        @Override
        public int getPhotoPickerToolbarBackgroundColor() {
          return R.color.colorPrimary;
        }
      })
      .build();

    Signaller.init(this, appConfig, uiConfig);

    Signaller.getInstance().setDebugEnabled(true);
  }

}

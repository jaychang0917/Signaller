package com.redso.signaller.demo.app;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.multidex.MultiDexApplication;
import android.view.View;

import com.redso.signaller.core.AppConfig;
import com.redso.signaller.core.Signaller;
import com.redso.signaller.core.model.ChatMessage;
import com.redso.signaller.core.model.ChatRoom;
import com.redso.signaller.demo.Constant;
import com.redso.signaller.demo.MainActivity;
import com.redso.signaller.demo.R;
import com.redso.signaller.demo.chat.CustomChatRoomActivity;
import com.redso.signaller.demo.chat.CustomChatRoomCell;
import com.redso.signaller.demo.chat.CustomChatRoomDateSectionView;
import com.redso.signaller.demo.chat.CustomChatRoomToolbar;
import com.redso.signaller.demo.chat.CustomOtherImageMessageCell;
import com.redso.signaller.demo.chat.CustomOtherTextMessageCell;
import com.redso.signaller.demo.chat.CustomOwnImageMessageCell;
import com.redso.signaller.demo.chat.CustomOwnTextMessageCell;
import com.redso.signaller.ui.ChatMessageCell;
import com.redso.signaller.ui.ChatMessageCellProvider;
import com.redso.signaller.ui.ChatMessageType;
import com.redso.signaller.ui.ChatRoomCell;
import com.redso.signaller.ui.ChatRoomCellProvider;
import com.redso.signaller.ui.ChatRoomDateSectionViewProvider;
import com.redso.signaller.ui.ChatRoomToolbarProvider;
import com.redso.signaller.ui.EmojiKeyboardViewInfo;
import com.redso.signaller.ui.SimpleChatRoomMessageInputViewProvider;
import com.redso.signaller.ui.UIConfig;

public class App extends MultiDexApplication {

  public static String currentUserId;

  @Override
  public void onCreate() {
    super.onCreate();

    initSignaller();
  }

  private void initSignaller() {
    AppConfig appConfig = AppConfig.newBuilder(Constant.SOCKET_URL, Constant.SERVER_DOMAIN)
      .enablePushNotification(
        R.string.app_name_kol,
        R.mipmap.ic_launcher,
        Constant.PUSH_SENDER_ID,
        // optional, only need if you use your own chat room activity
        CustomChatRoomActivity.class,
        // parent activity of CustomChatRoomActivity
        MainActivity.class)
      .build();

    UIConfig uiConfig = UIConfig.newBuilder()
      .setChatRoomCellProvider(new ChatRoomCellProvider() {
        @NonNull
        @Override
        public ChatRoomCell getChatRoomCell(ChatRoom chatRoom) {
          return new CustomChatRoomCell(chatRoom);
        }
      })
      .setChatMessageCellProvider(new ChatMessageCellProvider() {
        @NonNull
        @Override
        public ChatMessageCell getOwnChatMessageCell(ChatMessageType type, ChatMessage message) {
          if (type.equals(ChatMessageType.TEXT)) {
            return new CustomOwnTextMessageCell(message);
          } else if (type.equals(ChatMessageType.IMAGE)) {
            return new CustomOwnImageMessageCell(message);
          }
          throw new RuntimeException("Unsupported chat message type");
        }

        @NonNull
        @Override
        public ChatMessageCell getOtherChatMessageCell(ChatMessageType type, ChatMessage message) {
          if (type.equals(ChatMessageType.TEXT)) {
            return new CustomOtherTextMessageCell(message);
          } else if (type.equals(ChatMessageType.IMAGE)) {
            return new CustomOtherImageMessageCell(message);
          }
          throw new RuntimeException("Unsupported chat message type");
        }
      })
      .setChatRoomMessageInputViewProvider(new SimpleChatRoomMessageInputViewProvider() {
        // if you enable the default emoji keyboard, your MUST use SignallerEditText as the input view
        @Override
        public int getLayoutRes() {
          return R.layout.view_message_input;
        }

        @Override
        public int getInputEditTextId() {
          return R.id.inputEditText;
        }

        // optional, set it if you want an emoji keyboard
        @Override
        public EmojiKeyboardViewInfo getEmojiKeyboardViewInfo() {
          return new EmojiKeyboardViewInfo() {
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
          };
        }

        @Override
        public int getPhotoPickerIconViewId() {
          return R.id.photoPickerIconView;
        }

        @Override
        public int getSendMessageViewId() {
          return R.id.sendMessageView;
        }
      })
      // the following options are optional
      .setChatRoomDateSectionViewProvider(new ChatRoomDateSectionViewProvider() {
        @NonNull
        @Override
        public View getChatRoomDateSectionView(ChatMessage item) {
          CustomChatRoomDateSectionView view = new CustomChatRoomDateSectionView(getApplicationContext());
          view.bind(item);
          return view;
        }

        @Override
        public boolean isSameSection(ChatMessage item, ChatMessage nextItem) {
          return item.isSameDate(nextItem);
        }
      })
      .setChatRoomToolbarProvider(new ChatRoomToolbarProvider() {
        @NonNull
        @Override
        public View getToolbar(Activity activity, String username) {
          return CustomChatRoomToolbar.create(activity, username);
        }
      })
      .setChatRoomPhotoPickerThemeColor(R.color.colorPrimaryDark)
      .setChatRoomEmptyStateViewRes(R.layout.view_empty_state)
      .setChatRoomBackgroundRes(R.color.colorBackground)
      .setChatRoomListEmptyStateViewRes(R.layout.view_empty_state)
      .setChatRoomListDividerColorRes(R.color.colorGrey)
      .setChatRoomListDividerPaddingDp(64, 0, 0, 0)
      .build();

    // init Signaller with provided configs
    Signaller.init(this, appConfig, uiConfig);

    // Enable debug log and Stetho
    Signaller.getInstance().setDebugEnabled(true);
  }

}

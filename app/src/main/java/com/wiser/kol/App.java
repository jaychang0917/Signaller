package com.wiser.kol;

import android.support.annotation.NonNull;
import android.support.multidex.MultiDexApplication;

import com.jaychang.signaller.core.Signaller;
import com.jaychang.signaller.core.model.ChatMessage;
import com.jaychang.signaller.core.model.ChatRoom;
import com.jaychang.signaller.ui.cell.ChatMessageCell;
import com.jaychang.signaller.ui.cell.ChatMessageDateSeparatorCell;
import com.jaychang.signaller.ui.cell.ChatRoomCell;
import com.jaychang.signaller.ui.cell.DefaultOtherImageMessageCell;
import com.jaychang.signaller.ui.cell.DefaultOtherTextMessageCell;
import com.jaychang.signaller.ui.cell.DefaultOwnImageMessageCell;
import com.jaychang.signaller.ui.cell.DefaultOwnTextMessageCell;
import com.jaychang.signaller.ui.config.ChatMessageCellProvider;
import com.jaychang.signaller.ui.config.ChatMessageDateSeparatorCellProvider;
import com.jaychang.signaller.ui.config.ChatMessageType;
import com.jaychang.signaller.ui.config.ChatRoomCellProvider;
import com.jaychang.signaller.ui.config.UIConfig;
import com.wiser.kol.cell.KolChatMessageDateSeparatorCell;
import com.wiser.kol.cell.KolChatRoomCell;

public class App extends MultiDexApplication {

  public static String currentUserId;

  @Override
  public void onCreate() {
    super.onCreate();

    Utils.init(this);

    Signaller.init(this, Constant.SERVER_DOMAIN, Constant.SOCKETE_URL);

    UIConfig uiConfig = new UIConfig();

    uiConfig.setChatRoomCellProvider(new ChatRoomCellProvider() {
      @NonNull
      @Override
      public ChatRoomCell createChatRoomCell(ChatRoom chatRoom) {
        return new KolChatRoomCell(chatRoom);
      }
    });

    uiConfig.setChatMessageCellProvider(new ChatMessageCellProvider() {
      @NonNull
      @Override
      public ChatMessageCell createOwnChatMessageCell(ChatMessageType type, ChatMessage message) {
        if (type.equals(ChatMessageType.TEXT)) {
          return new DefaultOwnTextMessageCell(message);
        } else {
          return new DefaultOwnImageMessageCell(message);
        }
      }

      @NonNull
      @Override
      public ChatMessageCell createOtherChatMessageCell(ChatMessageType type, ChatMessage message) {
        if (type.equals(ChatMessageType.TEXT)) {
          return new DefaultOtherTextMessageCell(message);
        } else {
          return new DefaultOtherImageMessageCell(message);
        }
      }
    });

    uiConfig.setChatMessageDateSeparatorCellProvider(new ChatMessageDateSeparatorCellProvider() {
      @NonNull
      @Override
      public ChatMessageDateSeparatorCell createChatMessageDateSeparatorCell(long timestampMillis) {
        return new KolChatMessageDateSeparatorCell(timestampMillis);
      }
    });

    uiConfig.setShowChatMessageDateSeparator(false);

    Signaller.getInstance().setUIConfig(uiConfig);
  }
}

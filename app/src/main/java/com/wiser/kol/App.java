package com.wiser.kol;

import android.support.annotation.NonNull;
import android.support.multidex.MultiDexApplication;

import com.jaychang.signaller.core.Signaller;
import com.jaychang.signaller.core.model.ChatMessage;
import com.jaychang.signaller.core.model.ChatRoom;
import com.jaychang.signaller.ui.cell.ChatMessageCell;
import com.jaychang.signaller.ui.cell.ChatRoomCell;
import com.jaychang.signaller.ui.config.ChatMessageCellProvider;
import com.jaychang.signaller.ui.config.ChatMessageType;
import com.jaychang.signaller.ui.config.ChatRoomCellProvider;
import com.jaychang.signaller.ui.config.SimpleUIConfig;
import com.jaychang.signaller.ui.config.UIConfig;
import com.wiser.kol.cell.KolChatRoomCell;
import com.wiser.kol.cell.KolOtherImageMessageCell;
import com.wiser.kol.cell.KolOtherTextMessageCell;
import com.wiser.kol.cell.KolOwnImageMessageCell;
import com.wiser.kol.cell.KolOwnTextMessageCell;

public class App extends MultiDexApplication {

  public static String currentUserId;

  @Override
  public void onCreate() {
    super.onCreate();

    Utils.init(this);

    Signaller.init(this, Constant.SERVER_DOMAIN, Constant.SOCKETE_URL);

    UIConfig uiConfig = new SimpleUIConfig() {
      @Override
      public ChatRoomCellProvider getChatRoomCellProvider() {
        return new ChatRoomCellProvider() {
          @Override
          public ChatRoomCell createChatRoomCell(ChatRoom chatRoom) {
            return new KolChatRoomCell(chatRoom);
          }
        };
      }

      @Override
      public ChatMessageCellProvider getChatMessageCellProvider() {
        return new ChatMessageCellProvider() {
          @NonNull
          @Override
          public ChatMessageCell createOwnChatMessageCell(ChatMessageType type, ChatMessage message) {
            if (type.equals(ChatMessageType.TEXT)) {
              return new KolOwnTextMessageCell(message);
            } else {
              return new KolOwnImageMessageCell(message);
            }
          }

          @NonNull
          @Override
          public ChatMessageCell createOtherChatMessageCell(ChatMessageType type, ChatMessage message) {
            if (type.equals(ChatMessageType.TEXT)) {
              return new KolOtherTextMessageCell(message);
            } else {
              return new KolOtherImageMessageCell(message);
            }
          }
        };
      }
    };

    Signaller.getInstance().setUIConfig(uiConfig);
  }
}

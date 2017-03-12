package com.redso.signaller.core;

import android.content.Context;

import com.redso.signaller.core.model.ChatMessage;
import com.redso.signaller.core.model.ChatRoom;
import com.redso.signaller.core.model.ChatRoomInfo;
import com.redso.signaller.core.model.Image;
import com.redso.signaller.core.model.ImageAttribute;
import com.redso.signaller.core.model.Payload;
import com.redso.signaller.core.model.ProfilePhoto;
import com.redso.signaller.core.model.ChatReceiver;
import com.redso.signaller.core.model.ChatSender;
import com.redso.signaller.core.model.SocketChatMessage;
import com.redso.signaller.util.LogUtils;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import io.realm.Sort;
import rx.Observable;

public class DatabaseManager {

  private static final DatabaseManager INSTANCE = new DatabaseManager();
  private static final int DB_VERSION = 1;

  private RealmConfiguration realmConfig;

  private DatabaseManager() {
  }

  public static DatabaseManager getInstance() {
    return INSTANCE;
  }

  void init(Context appContext) {
    Realm.init(appContext);

    realmConfig = new RealmConfiguration.Builder()
      .schemaVersion(DB_VERSION)
      .migration(new DatabaseMigration())
      .name("signaller.realm")
      .modules(new SignallerModule())
      .build();
  }

  private Realm getRealm() {
    return Realm.getInstance(realmConfig);
  }

  void clear() {
    getRealm().executeTransactionAsync(realm -> {
      realm.delete(Image.class);
      realm.delete(ImageAttribute.class);
      realm.delete(Payload.class);
      realm.delete(ProfilePhoto.class);
      realm.delete(ChatReceiver.class);
      realm.delete(ChatSender.class);
      realm.delete(ChatMessage.class);
      realm.delete(ChatRoomInfo.class);
      realm.delete(ChatRoom.class);
    });
  }

  /**
   * chat message
   */
  public Observable<RealmResults<SocketChatMessage>> getPendingChatMessages() {
    Realm realm = getRealm();

    return Observable.just(
      realm
        .where(SocketChatMessage.class)
        .findAllSorted("timestamp", Sort.ASCENDING)
    ).doOnCompleted(realm::close);
  }

  public void addPendingChatMessageAsync(SocketChatMessage msg, Realm.Transaction.OnSuccess callback) {
    getRealm().executeTransactionAsync(realm -> {
      msg.setTimestamp(msg.getPayloadModel().getTimestamp());
      realm.insertOrUpdate(msg);
    }, callback);
  }

  public void removePendingChatMsg(long timestamp) {
    getRealm().executeTransaction(realm -> {
      SocketChatMessage msg = realm.where(SocketChatMessage.class)
        .equalTo("payloadModel.timestamp", timestamp).findFirst();
      if (msg != null) {
        msg.deleteFromRealm();
        LogUtils.d("Removed pending msg:" + timestamp);
      }
    });
  }

  public Observable<RealmResults<ChatMessage>> getChatMessages() {
    Realm realm = getRealm();

    return realm
      .where(ChatMessage.class)
      .findAllSorted("mtime", Sort.DESCENDING)
      .asObservable()
      .doOnCompleted(realm::close);
  }

  public ChatMessage getChatMessage(String chatRoomId, String msgId) {
    return getRealm().where(ChatMessage.class)
      .equalTo("chatroomId", chatRoomId)
      .equalTo("msgId", msgId)
      .findFirst();
  }

  public void saveChatMessages(final List<ChatMessage> chatMessages) {
    getRealm().executeTransaction(realm -> {
      for (ChatMessage chatMessage : chatMessages) {
        chatMessage.setSent(true);
      }
      realm.insertOrUpdate(chatMessages);
    });
  }

  public void saveChatMessage(ChatMessage msg) {
    getRealm().executeTransaction(realm -> {
      realm.insertOrUpdate(msg);
    });
  }

  public void saveChatMessageAsync(ChatMessage msg) {
    getRealm().executeTransactionAsync(realm -> {
      realm.insertOrUpdate(msg);
    });
  }

  public void removeTempChatMessage(long timestamp) {
    getRealm().executeTransaction(realm -> {
      ChatMessage msg = realm.where(ChatMessage.class)
        .equalTo("timestamp", timestamp).findFirst();
      LogUtils.d("Try to remove temp chat msg:" + timestamp + " msg:" + msg);
      if (msg != null) {
        msg.deleteFromRealm();
        LogUtils.d("Remove temp chat msg:" + timestamp);
      }
    });
  }

  /**
   * chat room
   */
  public void saveChatRooms(final List<ChatRoom> chatRooms) {
    getRealm().executeTransaction(realm -> {
      realm.insertOrUpdate(chatRooms);
    });
  }

  public void updateChatRoom(String roomId, ChatMessage lastMsg, boolean dontIncreaseUnreadCount) {
    getRealm().executeTransaction(realm -> {
      ChatRoom chatRoom = realm.where(ChatRoom.class)
        .equalTo("chatRoomId", roomId).findFirst();
      if (chatRoom != null) {
        if (!lastMsg.isOwnMessage() && !dontIncreaseUnreadCount) {
          chatRoom.increaseUnreadCount();
        }
        chatRoom.setLastMessage(realm.copyToRealmOrUpdate(lastMsg));
      }
    });
  }

  public Observable<List<ChatRoom>> getChatRooms() {
    Realm realm = getRealm();

    return realm
      .where(ChatRoom.class)
      .findAllSortedAsync("lastUpdateTime", Sort.DESCENDING)
      .asObservable()
      .filter(RealmResults::isLoaded)
      .map(realm::copyFromRealm)
      .first();
  }

  public ChatRoom getChatRoom(String chatRoomId) {
    return getRealm().where(ChatRoom.class).equalTo("chatRoomId", chatRoomId).findFirst();
  }

  public ChatReceiver getReceiver(String userId) {
    return getRealm().where(ChatReceiver.class).equalTo("userId", userId).findFirst();
  }

  public void clearUnreadMessageCount(String chatRoomId) {
    getRealm().executeTransactionAsync(realm -> {
      ChatRoom chatRoom = realm.where(ChatRoom.class).equalTo("chatRoomId", chatRoomId).findFirst();
      if (chatRoom != null) {
        chatRoom.setUnreadCount(0);
      }
    });
  }

}

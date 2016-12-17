package com.jaychang.signaller.core;

import android.content.Context;

import com.jaychang.signaller.core.model.ChatMessage;
import com.jaychang.signaller.core.model.ChatRoom;
import com.jaychang.signaller.core.model.SocketChatMessage;

import java.util.List;

import io.realm.DynamicRealm;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmMigration;
import io.realm.RealmResults;
import io.realm.Sort;
import rx.Observable;

public class DatabaseManager {

  private static final int DB_VERSION = 1;
  private static final DatabaseManager INSTANCE = new DatabaseManager();

  private RealmConfiguration realmConfig;

  private DatabaseManager() {
  }

  public static DatabaseManager getInstance() {
    return INSTANCE;
  }

  void init(Context appContext) {
    Realm.init(appContext);

    RealmMigration migration = new RealmMigration() {
      @Override
      public void migrate(DynamicRealm realm, long oldVersion, long newVersion) {

      }
    };

    realmConfig = new RealmConfiguration.Builder()
      .schemaVersion(DB_VERSION)
      .migration(migration)
//      .name("signaller.realm")   // todo uncomment
      .modules(new SignallerModule())
      .build();
  }

  private Realm getRealm() {
    return Realm.getInstance(realmConfig);
  }

  Observable<RealmResults<SocketChatMessage>> getPendingChatMessages() {
    Realm realm = getRealm();

    return realm
      .where(SocketChatMessage.class)
      .findAllSorted("timestamp", Sort.ASCENDING)
      .asObservable()
      .doOnCompleted(realm::close);
  }

  public void addPendingChatMessageAsync(SocketChatMessage msg) {
    getRealm().executeTransactionAsync(realm -> {
      msg.timestamp = msg.payload.timestamp;
      realm.insertOrUpdate(msg);
    });
  }

  public Observable<RealmResults<ChatRoom>> getChatRooms() {
    Realm realm = getRealm();

    return realm
      .where(ChatRoom.class)
      .findAllSorted("mtime", Sort.DESCENDING)
      .asObservable()
      .doOnCompleted(realm::close);
  }

  // todo paging
  public Observable<RealmResults<ChatMessage>> getChatMessages() {
    Realm realm = getRealm();

    return realm
      .where(ChatMessage.class)
      .findAllSorted("mtime", Sort.DESCENDING)
      .asObservable()
      .doOnCompleted(realm::close);
  }

  public void saveChatRooms(final List<ChatRoom> chatRooms) {
    getRealm().executeTransaction(realm -> {
      realm.insertOrUpdate(chatRooms);
    });
  }

  public void insertOrUpdateChatRoom(String roomId, ChatMessage lastMsg) {
    getRealm().executeTransaction(realm -> {
      ChatRoom chatRoom = realm.where(ChatRoom.class)
        .equalTo("chatRoomId", roomId).findFirst();
      if (chatRoom != null) {
        if (!lastMsg.isOwnMessage()) {
          chatRoom.unreadCount++;
        }
        chatRoom.lastMessage = realm.copyToRealmOrUpdate(lastMsg);
      } else {
        chatRoom = ChatRoom.from(roomId, lastMsg);
        realm.copyToRealmOrUpdate(chatRoom);
      }
    });
  }

  public ChatRoom getChatRoom(String chatRoomId) {
    return getRealm().where(ChatRoom.class).equalTo("chatRoomId", chatRoomId).findFirst();
  }

  public void saveChatMessages(final List<ChatMessage> chatMessages) {
    getRealm().executeTransaction(realm -> {
      realm.insertOrUpdate(chatMessages);
    });
  }

  public void saveChatMessage(ChatMessage msg) {
    getRealm().executeTransaction(realm -> {
      realm.insertOrUpdate(msg);
    });
  }

  public void removeTempChatMessage(long timestamp) {
    getRealm().executeTransaction(realm -> {
      ChatMessage msg = realm.where(ChatMessage.class)
        .equalTo("timestamp", timestamp).findFirst();
      if (msg != null) {
        msg.deleteFromRealm();
      }
    });
  }

  public void removePendingChatMsg(long timestamp) {
    getRealm().executeTransaction(realm -> {
      SocketChatMessage msg = realm.where(SocketChatMessage.class)
        .equalTo("payload.timestamp", timestamp).findFirst();
      if (msg != null) {
        msg.deleteFromRealm();
      }
    });
  }

}

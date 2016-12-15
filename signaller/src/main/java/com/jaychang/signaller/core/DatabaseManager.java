package com.jaychang.signaller.core;

import android.content.Context;

import com.jaychang.signaller.core.model.ChatMessage;
import com.jaychang.signaller.core.model.ChatRoom;
import com.jaychang.signaller.core.model.PendingChatMessage;

import java.util.List;

import io.realm.DynamicRealm;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmMigration;
import io.realm.RealmResults;
import io.realm.Sort;
import rx.Observable;

class DatabaseManager {

  private static final int DB_VERSION = 1;
  private static final DatabaseManager INSTANCE = new DatabaseManager();
  private Realm realm;

  private static RealmMigration migration = new RealmMigration() {
    @Override
    public void migrate(DynamicRealm realm, long oldVersion, long newVersion) {

    }
  };

  private DatabaseManager() {
  }

  public static DatabaseManager getInstance() {
    return INSTANCE;
  }

  static void init(Context appContext) {
    Realm.init(appContext);

    RealmConfiguration realmConfig = new RealmConfiguration.Builder()
      .schemaVersion(DB_VERSION)
      .migration(migration)
      .name("signaller.realm")
      .modules(new SignallerModule())
      .build();

    INSTANCE.realm = Realm.getInstance(realmConfig);
  }

  public Observable<List<PendingChatMessage>> getPendingChatMessages() {
    return realm
      .where(PendingChatMessage.class)
      .findAllAsync()
      .asObservable()
      .filter(RealmResults::isLoaded)
      .map(realm::copyFromRealm)
      .first()
      .doOnCompleted(realm::close);
  }

  public Observable<List<ChatRoom>> getChatRooms(String userId) {
    return realm
      .where(ChatRoom.class)
      .equalTo("userId", userId)
      .findAllSortedAsync("mtime", Sort.DESCENDING)
      .asObservable()
      .filter(RealmResults::isLoaded)
      .map(realm::copyFromRealm)
      .first()
      .doOnCompleted(realm::close);
  }

  // todo paging
  public Observable<List<ChatMessage>> getChatMessages() {
    return realm
      .where(ChatMessage.class)
      .findAllSortedAsync("mtime", Sort.DESCENDING)
      .asObservable()
      .filter(RealmResults::isLoaded)
      .map(realm::copyFromRealm)
      .first()
      .doOnCompleted(realm::close);
  }

  public void saveChatRooms(final List<ChatRoom> chatRooms) {
    Realm.getDefaultInstance()
      .executeTransactionAsync(realm -> {
        for (ChatRoom chatRoom : chatRooms) {
          chatRoom.userId = Signaller.getInstance().getUserId();
        }
        realm.insertOrUpdate(chatRooms);
      });
  }

  public void saveChatMessages(final List<ChatMessage> chatMessages) {
    Realm.getDefaultInstance()
      .executeTransactionAsync(realm -> {
        for (ChatMessage chatMessage : chatMessages) {
          chatMessage.userId = Signaller.getInstance().getUserId();
        }
        realm.insertOrUpdate(chatMessages);
      });
  }

  public void saveChatMessage(ChatMessage msg) {
    Realm.getDefaultInstance()
      .executeTransactionAsync(realm -> {
        realm.insertOrUpdate(msg);
      });
  }

  public void removeChatMessage(long timestamp) {
    Realm.getDefaultInstance()
      .executeTransactionAsync(realm -> {
        ChatMessage msg = realm.where(ChatMessage.class)
          .equalTo("timestamp", timestamp).findFirst();
        if (msg != null) {
          msg.deleteFromRealm();
        }
      });
  }

  public void removePendingChatMsg(long timestamp) {
    Realm.getDefaultInstance()
      .executeTransactionAsync(realm -> {
        PendingChatMessage msg = realm.where(PendingChatMessage.class)
          .equalTo("socketChatMessage.payload.timestamp", timestamp).findFirst();
        if (msg != null) {
          msg.deleteFromRealm();
        }
      });
  }

}

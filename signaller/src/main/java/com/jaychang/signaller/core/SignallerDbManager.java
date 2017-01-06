package com.jaychang.signaller.core;

import android.content.Context;

import com.jaychang.signaller.core.model.SignallerChatMessage;
import com.jaychang.signaller.core.model.SignallerChatRoom;
import com.jaychang.signaller.core.model.SignallerChatRoomInfo;
import com.jaychang.signaller.core.model.SignallerEvent;
import com.jaychang.signaller.core.model.SignallerImage;
import com.jaychang.signaller.core.model.SignallerImageAttribute;
import com.jaychang.signaller.core.model.SignallerPayload;
import com.jaychang.signaller.core.model.SignallerProfilePhoto;
import com.jaychang.signaller.core.model.SignallerReceiver;
import com.jaychang.signaller.core.model.SignallerSender;
import com.jaychang.signaller.core.model.SignallerSocketChatMessage;
import com.jaychang.signaller.util.LogUtils;

import java.util.List;

import io.realm.DynamicRealm;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmMigration;
import io.realm.RealmResults;
import io.realm.Sort;
import rx.Observable;

public class SignallerDbManager {

  private static final int DB_VERSION = 1;
  private static final SignallerDbManager INSTANCE = new SignallerDbManager();

  private RealmConfiguration realmConfig;

  private SignallerDbManager() {
  }

  public static SignallerDbManager getInstance() {
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
      .name("signaller.realm")
      .modules(new SignallerModule())
      .build();
  }

  private Realm getRealm() {
    return Realm.getInstance(realmConfig);
  }

  void clear() {
    getRealm().executeTransactionAsync(realm -> {
      realm.delete(SignallerEvent.class);
      realm.delete(SignallerImage.class);
      realm.delete(SignallerImageAttribute.class);
      realm.delete(SignallerPayload.class);
      realm.delete(SignallerProfilePhoto.class);
      realm.delete(SignallerReceiver.class);
      realm.delete(SignallerSender.class);
      realm.delete(SignallerChatMessage.class);
      realm.delete(SignallerChatRoomInfo.class);
      realm.delete(SignallerChatRoom.class);
    });
  }

  /**
   * chat message
   */
  public Observable<RealmResults<SignallerSocketChatMessage>> getPendingChatMessages() {
    Realm realm = getRealm();

    return Observable.just(
      realm
        .where(SignallerSocketChatMessage.class)
        .findAllSorted("timestamp", Sort.ASCENDING)
    ).doOnCompleted(realm::close);
  }

  public void addPendingChatMessageAsync(SignallerSocketChatMessage msg, Realm.Transaction.OnSuccess callback) {
    getRealm().executeTransactionAsync(realm -> {
      msg.setTimestamp(msg.getPayloadModel().getTimestamp());
      realm.insertOrUpdate(msg);
    }, callback);
  }

  public void removePendingChatMsg(long timestamp) {
    getRealm().executeTransaction(realm -> {
      SignallerSocketChatMessage msg = realm.where(SignallerSocketChatMessage.class)
        .equalTo("payloadModel.timestamp", timestamp).findFirst();
      if (msg != null) {
        msg.deleteFromRealm();
        LogUtils.d("removed pending msg:" + timestamp);
      }
    });
  }

  public Observable<RealmResults<SignallerChatMessage>> getChatMessages() {
    Realm realm = getRealm();

    return realm
      .where(SignallerChatMessage.class)
      .findAllSorted("mtime", Sort.DESCENDING)
      .asObservable()
      .doOnCompleted(realm::close);
  }

  public SignallerChatMessage getChatMessage(String msgId) {
    return getRealm().where(SignallerChatMessage.class).equalTo("msgId", msgId).findFirst();
  }

  public void saveChatMessages(final List<SignallerChatMessage> chatMessages) {
    getRealm().executeTransaction(realm -> {
      for (SignallerChatMessage chatMessage : chatMessages) {
        chatMessage.setSent(true);
      }
      realm.insertOrUpdate(chatMessages);
    });
  }

  public void saveChatMessage(SignallerChatMessage msg) {
    getRealm().executeTransaction(realm -> {
      realm.insertOrUpdate(msg);
    });
  }

  public void saveChatMessageAsync(SignallerChatMessage msg) {
    getRealm().executeTransactionAsync(realm -> {
      realm.insertOrUpdate(msg);
    });
  }

  public void removeTempChatMessage(long timestamp) {
    getRealm().executeTransaction(realm -> {
      SignallerChatMessage msg = realm.where(SignallerChatMessage.class)
        .equalTo("timestamp", timestamp).findFirst();
      LogUtils.d("try to remove temp chat msg:" + timestamp + " msg:" + msg);
      if (msg != null) {
        msg.deleteFromRealm();
        LogUtils.d("remove temp chat msg:" + timestamp);
      }
    });
  }

  /**
   * chat room
   */
  public void saveChatRooms(final List<SignallerChatRoom> chatRooms) {
    getRealm().executeTransaction(realm -> {
      realm.insertOrUpdate(chatRooms);
    });
  }

  public void updateChatRoom(String roomId, SignallerChatMessage lastMsg) {
    getRealm().executeTransaction(realm -> {
      SignallerChatRoom chatRoom = realm.where(SignallerChatRoom.class)
        .equalTo("chatRoomId", roomId).findFirst();
      if (chatRoom != null) {
        if (!lastMsg.isOwnMessage()) {
          chatRoom.increaseUnreadCount();
        }
        chatRoom.setLastMessage(realm.copyToRealmOrUpdate(lastMsg));
      }
    });
  }

  public Observable<RealmResults<SignallerChatRoom>> getChatRooms() {
    Realm realm = getRealm();

    return realm
      .where(SignallerChatRoom.class)
      .findAllSorted("lastUpdateTime", Sort.DESCENDING)
      .asObservable()
      .doOnCompleted(realm::close);
  }

  public SignallerChatRoom getChatRoom(String chatRoomId) {
    return getRealm().where(SignallerChatRoom.class).equalTo("chatRoomId", chatRoomId).findFirst();
  }

  public SignallerReceiver getReceiver(String userId) {
    return getRealm().where(SignallerReceiver.class).equalTo("userId", userId).findFirst();
  }
}

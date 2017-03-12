package com.redso.signaller.core;

import android.net.Uri;

import com.redso.signaller.core.model.ChatMessageResponse;
import com.redso.signaller.core.model.ChatRoom;
import com.redso.signaller.core.model.Image;

import java.io.File;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import rx.Observable;

public class DataManager {

  private static final DataManager INSTANCE = new DataManager();
  private DatabaseManager databaseManager;
  private Api api;

  private DataManager() {
    databaseManager = DatabaseManager.getInstance();
    api = ApiManager.getApi();
  }

  public static DataManager getInstance() {
    return INSTANCE;
  }

  public Observable<List<ChatRoom>> getChatRooms() {
    return Observable.concat(databaseManager.getChatRooms(), getChatRoomsFromNetwork(null));
  }

  public Observable<List<ChatRoom>> getChatRoomsFromNetwork(String cursor) {
    return api.getChatRooms(cursor, 24)
      .doOnNext(response -> {
        ChatRoomMeta.getInstance().setCursor(response.cursor);
        ChatRoomMeta.getInstance().setHasMoreData(response.hasMore);
        ChatRoomMeta.getInstance().setTotalUnreadCount(response.totalUnreadCount);
        databaseManager.saveChatRooms(response.chatRooms);
      })
      .map(response -> response.chatRooms)
      .compose(new SchedulerTransformer<>());
  }

  public Observable<ChatMessageResponse> getChatMessages(String userId, String cursor) {
    return api.getChatMessages(userId, cursor, 24)
      .compose(new SchedulerTransformer<>())
      .doOnNext(response -> {
        databaseManager.saveChatMessages(response.chatMessages);
      });
  }

  public Observable<Image> uploadPhoto(Uri photoUri) {
    File file = new File(photoUri.getPath());
    MultipartBody.Part filePart = MultipartBody.Part.createFormData("file", file.getName(), RequestBody.create(MediaType.parse("image/*"), file));
    return api.uploadPhoto(filePart)
      .compose(new SchedulerTransformer<>());
  }

  public void clearUnreadCount(String chatRoomId) {
    api.clearUnreadCount(chatRoomId, 0)
      .compose(new SchedulerTransformer<>())
      .subscribe();
  }

}

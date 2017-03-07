package com.redso.signaller.core;

import android.net.Uri;

import com.redso.signaller.core.model.ChatMessageResponse;
import com.redso.signaller.core.model.SignallerChatRoom;
import com.redso.signaller.core.model.SignallerImage;

import java.io.File;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import rx.Observable;

public class SignallerDataManager {

  private static final SignallerDataManager INSTANCE = new SignallerDataManager();
  private SignallerDbManager databaseManager;
  private SignallerApi api;

  private SignallerDataManager() {
    databaseManager = SignallerDbManager.getInstance();
    api = SignallerApiManager.getApi();
  }

  public static SignallerDataManager getInstance() {
    return INSTANCE;
  }

  public Observable<List<SignallerChatRoom>> getChatRooms() {
    return Observable.concat(databaseManager.getChatRooms(), getChatRoomsFromNetwork(null));
  }

  public Observable<List<SignallerChatRoom>> getChatRoomsFromNetwork(String cursor) {
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

  public Observable<SignallerImage> uploadPhoto(Uri photoUri) {
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

package com.jaychang.signaller.core;

import android.net.Uri;

import com.jaychang.signaller.core.model.ChatMessageResponse;
import com.jaychang.signaller.core.model.ChatRoomResponse;
import com.jaychang.signaller.core.model.Image;

import java.io.File;

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

  public Observable<ChatRoomResponse> getChatRooms(String cursor) {
    return api.getChatRooms(cursor)
      .compose(new SchedulerTransformer<>())
      .doOnNext(response -> {
        databaseManager.saveChatRooms(response.chatRooms);
      });
  }

  public Observable<ChatMessageResponse> getChatMessages(String userId, String cursor) {
    return api.getChatMessages(userId, cursor)
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

}

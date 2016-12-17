package com.jaychang.signaller.core;

import com.jaychang.signaller.core.model.ChatMessageResponse;
import com.jaychang.signaller.core.model.ChatRoomResponse;
import com.jaychang.signaller.core.model.Image;

import okhttp3.MultipartBody;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

public interface Api {

  @GET("api/chats")
  Observable<ChatRoomResponse> getChatRooms(@Query("cursor") String cursor);

  @GET("api/chats/{user_id}/messages")
  Observable<ChatMessageResponse> getChatMessages(@Path("user_id") String userId, @Query("cursor") String cursor);

  @POST("createImage")
  @Multipart
  Observable<Image> uploadPhoto(@Part MultipartBody.Part file);

  @PUT("api/chatrooms/{room_id}/count")
  Observable<Void> resetUnreadCount(@Path("room_id") String roomId, @Query("unread_count") int count);
}

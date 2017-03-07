package com.redso.signaller.core;


import com.redso.signaller.core.model.ChatMessageResponse;
import com.redso.signaller.core.model.ChatRoomResponse;
import com.redso.signaller.core.model.SignallerImage;

import okhttp3.MultipartBody;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

public interface SignallerApi {

  @GET("api/chats")
  Observable<ChatRoomResponse> getChatRooms(@Query("cursor") String cursor,
                                            @Query("hits") int hits);

  @GET("api/chats/{chat_id}/messages")
  Observable<ChatMessageResponse> getChatMessages(@Path("chat_id") String chatId,
                                                  @Query("cursor") String cursor,
                                                  @Query("hits") int hits);

  @POST("/api/resources/images")
  @Multipart
  Observable<SignallerImage> uploadPhoto(@Part MultipartBody.Part file);

  @PUT("api/chatrooms/{room_id}/count")
  Observable<Void> clearUnreadCount(@Path("room_id") String roomId,
                                                @Query("unread_count") int count);

}

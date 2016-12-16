package com.jaychang.signaller.core;

import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.jaychang.signaller.core.model.RealmInt;
import com.jaychang.signaller.core.model.RealmString;
import com.jaychang.signaller.util.DebugUtils;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import io.realm.RealmList;
import io.realm.RealmObject;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiManager {

  private static final String BASE_URL;
  private static Api api;

  static {
    BASE_URL = UserData.getInstance().getServerDomain();
    setup();
  }

  private static void setup() {
    api = createRetrofit(createOkHttpClient(), createGson()).create(Api.class);
  }

  private static OkHttpClient createOkHttpClient() {
    OkHttpClient.Builder okHttpClientBuilder = new OkHttpClient.Builder();
    if (DebugUtils.debug) {
      okHttpClientBuilder.addNetworkInterceptor(new StethoInterceptor());
    }

    okHttpClientBuilder.addInterceptor(new Interceptor() {
      @Override
      public Response intercept(Chain chain) throws IOException {
        HashMap<String, String> defaultHeaders = new HashMap<>();
        defaultHeaders.putAll(ServerHeader.getDefaultHeaders());
        Request request = chain.request();
        Request.Builder requestBuilder = request.newBuilder();
        for (Map.Entry<String, String> headers : defaultHeaders.entrySet()) {
          requestBuilder.addHeader(headers.getKey(), headers.getValue());
        }
        return chain.proceed(requestBuilder.build());
      }
    });

    return okHttpClientBuilder.build();
  }

  private static Gson createGson() {
    GsonBuilder gsonBuilder = new GsonBuilder();
    gsonBuilder.setPrettyPrinting();

    gsonBuilder.setExclusionStrategies(new ExclusionStrategy() {
      @Override
      public boolean shouldSkipField(FieldAttributes f) {
        return f.getDeclaringClass().equals(RealmObject.class);
      }

      @Override
      public boolean shouldSkipClass(Class<?> clazz) {
        return false;
      }
    });

    Type stringToken = new TypeToken<RealmList<RealmString>>() {}.getType();
    gsonBuilder.registerTypeAdapter(stringToken, new TypeAdapter<RealmList<RealmString>>() {
      @Override
      public void write(JsonWriter out, RealmList<RealmString> value) throws IOException {
      }

      @Override
      public RealmList<RealmString> read(JsonReader in) throws IOException {
        RealmList<RealmString> list = new RealmList<>();
        in.beginArray();
        while (in.hasNext()) {
          list.add(new RealmString(in.nextString()));
        }
        in.endArray();
        return list;
      }
    });
    Type intToken = new TypeToken<RealmList<RealmInt>>() {}.getType();
    gsonBuilder.registerTypeAdapter(intToken, new TypeAdapter<RealmList<RealmInt>>() {
      @Override
      public void write(JsonWriter out, RealmList<RealmInt> value) throws IOException {
      }

      @Override
      public RealmList<RealmInt> read(JsonReader in) throws IOException {
        RealmList<RealmInt> list = new RealmList<>();
        in.beginArray();
        while (in.hasNext()) {
          list.add(new RealmInt(in.nextInt()));
        }
        in.endArray();
        return list;
      }
    });
    return gsonBuilder.create();
  }

  private static Retrofit createRetrofit(OkHttpClient client, Gson gson) {
    Retrofit.Builder retrofitBuilder = new Retrofit.Builder();
    retrofitBuilder.baseUrl(BASE_URL).client(client);
    retrofitBuilder.addConverterFactory(GsonConverterFactory.create(gson));
    retrofitBuilder.addCallAdapterFactory(RxJavaCallAdapterFactory.create());
    return retrofitBuilder.build();
  }

  public static Api getApi() {
    return api;
  }

}

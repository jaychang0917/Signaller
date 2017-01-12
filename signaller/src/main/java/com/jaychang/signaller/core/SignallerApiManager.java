package com.jaychang.signaller.core;

import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.google.gson.Gson;
import com.jaychang.signaller.util.DebugUtils;
import com.jaychang.signaller.util.GsonUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class SignallerApiManager {

  private static final String BASE_URL;
  private static SignallerApi api;

  static {
    BASE_URL = Signaller.getInstance().getAppConfig().getServerDomain();
    setup();
  }

  private static void setup() {
    api = createRetrofit(createOkHttpClient(), GsonUtils.getGson()).create(SignallerApi.class);
  }

  private static OkHttpClient createOkHttpClient() {
    OkHttpClient.Builder okHttpClientBuilder = new OkHttpClient.Builder();
    if (DebugUtils.debug) {
      okHttpClientBuilder.addNetworkInterceptor(new StethoInterceptor());
    }

    okHttpClientBuilder.connectTimeout(1, TimeUnit.MINUTES);
    okHttpClientBuilder.readTimeout(1, TimeUnit.MINUTES);
    okHttpClientBuilder.writeTimeout(1, TimeUnit.MINUTES);

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

  private static Retrofit createRetrofit(OkHttpClient client, Gson gson) {
    Retrofit.Builder retrofitBuilder = new Retrofit.Builder();
    retrofitBuilder.baseUrl(BASE_URL).client(client);
    retrofitBuilder.addConverterFactory(GsonConverterFactory.create(gson));
    retrofitBuilder.addCallAdapterFactory(RxJavaCallAdapterFactory.create());
    return retrofitBuilder.build();
  }

  public static SignallerApi getApi() {
    return api;
  }

}

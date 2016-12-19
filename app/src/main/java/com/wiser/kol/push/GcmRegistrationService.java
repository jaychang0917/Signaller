package com.wiser.kol.push;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.wiser.kol.Constant;
import com.jaychang.signaller.util.LogUtils;
import com.jaychang.utils.AppUtils;
import com.jaychang.utils.DeviceUtils;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class GcmRegistrationService extends IntentService {
  // **change the sender id**
  private static final String SENDER_ID = "1051552062396";

  public GcmRegistrationService() {
    super("GcmRegistrationService");
  }

  @Override
  protected void onHandleIntent(Intent intent) {

    try {
      InstanceID instanceID = InstanceID.getInstance(this);
      String token = instanceID.getToken(SENDER_ID,
        GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
      sendRegistrationToServer(token);
    } catch (Exception e) {
      LogUtils.d("GCM: error to get token from gcm server.");
    }
  }

  private void sendRegistrationToServer(final String token) {
    final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

    // custom implementation for server to send push to specific devices
    HttpUrl url = HttpUrl.parse("https://redsopushserver.appspot.com/apis/reg_device")
      .newBuilder()
      .addQueryParameter("device_token", token)
      .addQueryParameter("app_name", "kol")
      .addQueryParameter("is_dev", "0")
      .addQueryParameter("client_version", AppUtils.getVersionCode(getApplicationContext())+"")
      .addQueryParameter("os_version", Build.VERSION.RELEASE)
      .addQueryParameter("device_model", DeviceUtils.getDeviceModel())
      .addQueryParameter("deviceid", DeviceUtils.getDeviceId(getApplicationContext()))
      .addQueryParameter("client_os", "android")
      .addQueryParameter("enable_push", "1")
      .addQueryParameter("userid", Constant.USER_ID_JAY10)
      .build();

    Request request = new Request.Builder().get()
      .url(url)
      .build();

    new OkHttpClient().newCall(request).enqueue(new Callback() {
      @Override
      public void onFailure(Call call, IOException e) {
        sharedPreferences.edit().putBoolean(GcmManager.IS_GCM_REGISTERED, false).apply();
        LogUtils.d("GCM:fail to send gcm token to server.");
      }

      @Override
      public void onResponse(Call call, Response response) throws IOException {
        sharedPreferences.edit().putBoolean(GcmManager.IS_GCM_REGISTERED, true).apply();
        LogUtils.d("GCM: sent gcm token(" + token +  ") to server successfully.");
      }
    });
  }

}
package com.jaychang.signaller.core.push;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.jaychang.signaller.core.Signaller;
import com.jaychang.signaller.core.UserData;
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

public class SignallerGcmRegistrationService extends IntentService {

  public SignallerGcmRegistrationService() {
    super("GcmRegistrationService");
  }

  @Override
  protected void onHandleIntent(Intent intent) {

    try {
      InstanceID instanceID = InstanceID.getInstance(this);
      String token = instanceID.getToken(
        Signaller.getInstance().getAppConfig().getPushSenderId(),
        GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
      sendRegistrationToServer(token);
    } catch (Exception e) {
      LogUtils.d("GCM: error to get token from gcm server.");
    }
  }

  private void sendRegistrationToServer(final String token) {
    final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

    HttpUrl url = HttpUrl.parse("https://redsopushserver.appspot.com/apis/reg_device")
      .newBuilder()
      .addQueryParameter("device_token", token)
      .addQueryParameter("app_name", getString(Signaller.getInstance().getAppConfig().getAppName()).toLowerCase())
      .addQueryParameter("is_dev", "0")
      .addQueryParameter("client_version", AppUtils.getVersionCode(getApplicationContext()) + "")
      .addQueryParameter("os_version", Build.VERSION.RELEASE)
      .addQueryParameter("device_model", DeviceUtils.getDeviceModel())
      .addQueryParameter("deviceid", DeviceUtils.getDeviceId(getApplicationContext()))
      .addQueryParameter("client_os", "android")
      .addQueryParameter("enable_push", "1")
      .addQueryParameter("userid", UserData.getInstance().getUserId())
      .build();

    Request request = new Request.Builder().get()
      .url(url)
      .build();

    new OkHttpClient().newCall(request).enqueue(new Callback() {
      @Override
      public void onFailure(Call call, IOException e) {
        sharedPreferences.edit().putBoolean(SignallerGcmManager.IS_GCM_REGISTERED, false).apply();
        LogUtils.d("GCM:fail to send gcm token to server.");
      }

      @Override
      public void onResponse(Call call, Response response) throws IOException {
        sharedPreferences.edit().putBoolean(SignallerGcmManager.IS_GCM_REGISTERED, true).apply();
        LogUtils.d("GCM: sent gcm token(" + token + ") to server successfully.");
      }
    });
  }

}
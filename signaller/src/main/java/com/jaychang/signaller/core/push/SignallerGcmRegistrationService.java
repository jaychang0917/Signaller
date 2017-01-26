package com.jaychang.signaller.core.push;

import android.app.IntentService;
import android.content.Intent;
import android.os.Build;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.jaychang.signaller.core.Signaller;
import com.jaychang.signaller.core.UserData;
import com.jaychang.signaller.util.LogUtils;
import com.jaychang.utils.AppUtils;
import com.jaychang.utils.DeviceUtils;
import com.jaychang.utils.PreferenceUtils;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.jaychang.signaller.core.push.SignallerGcmManager.GCM_TOKEN;

public class SignallerGcmRegistrationService extends IntentService {

  public static final String ACTION_REGISTER = "EXTRA_ACTION_REGISTER";
  public static final String ACTION_UNREGISTER = "ACTION_UNREGISTER";
  public static final String EXTRA_TOKEN = "EXTRA_TOKEN";

  public SignallerGcmRegistrationService() {
    super("GcmRegistrationService");
  }

  @Override
  protected void onHandleIntent(Intent intent) {
    String action = intent.getAction();

    if (ACTION_REGISTER.equals(action)) {
      try {
        InstanceID instanceID = InstanceID.getInstance(this);
        String token = instanceID.getToken(
          Signaller.getInstance().getAppConfig().getPushNotificationSenderId(),
          GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
        sendTokenToServer(token, true);
      } catch (Exception e) {
        LogUtils.d("GCM: error to get token from gcm server.");
      }
    } else {
      String token = intent.getStringExtra(EXTRA_TOKEN);
      sendTokenToServer(token, false);
    }
  }

  private void sendTokenToServer(final String token, boolean isReg) {
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
      .addQueryParameter("enable_push", isReg ? "1" : "0")
      .addQueryParameter("userid", UserData.getInstance().getUserId())
      .build();

    Request request = new Request.Builder().get()
      .url(url)
      .build();

    new OkHttpClient().newCall(request).enqueue(new Callback() {
      @Override
      public void onFailure(Call call, IOException e) {
        LogUtils.d("GCM:fail to send gcm token to server.");
      }

      @Override
      public void onResponse(Call call, Response response) throws IOException {
        if (isReg) {
          PreferenceUtils.saveString(SignallerGcmRegistrationService.this, GCM_TOKEN, token);
          LogUtils.d("GCM: register gcm token(" + token + ") successfully.");
        } else {
          PreferenceUtils.remove(SignallerGcmRegistrationService.this, GCM_TOKEN);
          LogUtils.d("GCM: unregister gcm token(" + token + ") successfully.");
        }
      }
    });
  }

}
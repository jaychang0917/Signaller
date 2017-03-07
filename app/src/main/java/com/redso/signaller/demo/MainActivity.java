package com.redso.signaller.demo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.jaychang.utils.PreferenceUtils;
import com.redso.signaller.core.Signaller;

public class MainActivity extends AppCompatActivity {

  public static final String EXTRA_ACCESS_TOKEN = "EXTRA_ACCESS_TOKEN";
  public static final String EXTRA_USER_ID = "EXTRA_USER_ID";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    init();
  }

  public void init() {
    BottomTabManager.init(this);

    String accessToken = getIntent().getStringExtra(EXTRA_ACCESS_TOKEN);
    String userId = getIntent().getStringExtra(EXTRA_USER_ID);
    if (accessToken != null) {
      PreferenceUtils.saveString(this, "accessToken", accessToken);
    } else {
      accessToken = PreferenceUtils.getString(this, "accessToken");
    }
    if (userId != null) {
      PreferenceUtils.saveString(this, "userId", userId);
    } else {
      userId = PreferenceUtils.getString(this, "userId");
    }

    Signaller.getInstance().connect(accessToken, userId);
  }

}

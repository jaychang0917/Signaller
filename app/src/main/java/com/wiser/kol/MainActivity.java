package com.wiser.kol;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.jaychang.signaller.core.Signaller;
import com.wiser.kol.push.GcmManager;

public class MainActivity extends AppCompatActivity {

  public static final String EXTRA_ACCESS_TOEKN = "EXTRA_ACCESS_TOEKN";
  public static final String EXTRA_USER_ID = "EXTRA_USER_ID";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    init();
  }

  public void init() {
    BottomTabManager.init(this);

    String accessToken = getIntent().getStringExtra(EXTRA_ACCESS_TOEKN);
    String userId = getIntent().getStringExtra(EXTRA_USER_ID);

    Signaller.getInstance().connect(accessToken, userId);

    GcmManager.init(this);

  }
}

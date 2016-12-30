package com.wiser.kol;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.jaychang.signaller.core.Signaller;

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

    Signaller.getInstance().connect(accessToken, userId);
  }

}

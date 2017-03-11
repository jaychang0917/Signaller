package com.redso.signaller.demo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.redso.signaller.core.Signaller;
import com.redso.signaller.demo.app.App;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class ChooseUserActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_choose_user);
    ButterKnife.bind(this);
  }

  @OnClick(R.id.jay10Button)
  void loginAsJay10() {
    Signaller.getInstance().disconnect();

    Intent intent = new Intent(this, MainActivity.class);
    intent.putExtra(MainActivity.EXTRA_ACCESS_TOKEN, Constant.ACCESS_TOKEN_JAY10);
    intent.putExtra(MainActivity.EXTRA_USER_ID, Constant.USER_ID_JAY10);
    startActivity(intent);
    App.currentUserId = Constant.USER_ID_JAY10;
  }

  @OnClick(R.id.jay11Button)
  void loginAsJay11() {
    Signaller.getInstance().disconnect();

    Intent intent = new Intent(this, MainActivity.class);
    intent.putExtra(MainActivity.EXTRA_ACCESS_TOKEN, Constant.ACCESS_TOKEN_JAY11);
    intent.putExtra(MainActivity.EXTRA_USER_ID, Constant.USER_ID_JAY11);
    startActivity(intent);
    App.currentUserId = Constant.USER_ID_JAY11;
  }

  @OnClick(R.id.jay12Button)
  void loginAsJay12() {
    Signaller.getInstance().disconnect();

    Intent intent = new Intent(this, MainActivity.class);
    intent.putExtra(MainActivity.EXTRA_ACCESS_TOKEN, Constant.ACCESS_TOKEN_JAY12);
    intent.putExtra(MainActivity.EXTRA_USER_ID, Constant.USER_ID_JAY12);
    startActivity(intent);
    App.currentUserId = Constant.USER_ID_JAY12;
  }

  @OnClick(R.id.jay13Button)
  void loginAsJay13() {
    Signaller.getInstance().disconnect();

    Intent intent = new Intent(this, MainActivity.class);
    intent.putExtra(MainActivity.EXTRA_ACCESS_TOKEN, Constant.ACCESS_TOKEN_JAY13);
    intent.putExtra(MainActivity.EXTRA_USER_ID, Constant.USER_ID_JAY13);
    startActivity(intent);
    App.currentUserId = Constant.USER_ID_JAY13;
  }

  @OnClick(R.id.jay14Button)
  void loginAsJay14() {
    Signaller.getInstance().disconnect();

    Intent intent = new Intent(this, MainActivity.class);
    intent.putExtra(MainActivity.EXTRA_ACCESS_TOKEN, Constant.ACCESS_TOKEN_JAY14);
    intent.putExtra(MainActivity.EXTRA_USER_ID, Constant.USER_ID_JAY14);
    startActivity(intent);
    App.currentUserId = Constant.USER_ID_JAY14;
  }

}

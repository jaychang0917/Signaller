package com.jaychang.demo.signaler;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ChooseUserActivity extends AppCompatActivity {

  @BindView(R.id.jay10Button)
  Button jay10Button;
  @BindView(R.id.jay11Button)
  Button jay11Button;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_choose_user);
    ButterKnife.bind(this);
  }

  @OnClick(R.id.jay10Button)
  void openWithJay10() {
    Intent intent = new Intent(this, MainActivity.class);
    intent.putExtra(MainActivity.EXTRA_ACCESS_TOEKN, Constant.ACCESS_TOKEN_JAY10);
    intent.putExtra(MainActivity.EXTRA_USER_ID, Constant.USER_ID_JAY10);
    startActivity(intent);
  }

  @OnClick(R.id.jay11Button)
  void openWithJay11() {
    Intent intent = new Intent(this, MainActivity.class);
    intent.putExtra(MainActivity.EXTRA_ACCESS_TOEKN, Constant.ACCESS_TOKEN_JAY11);
    intent.putExtra(MainActivity.EXTRA_USER_ID, Constant.USER_ID_JAY11);
    startActivity(intent);
  }

}

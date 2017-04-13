package com.jaychang.signaller.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.jaychang.signaller.R;
import com.jaychang.signaller.R2;
import com.jaychang.utils.AppUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

public class PhotoViewerActivity extends AppCompatActivity {

  @BindView(R2.id.photoView)
  PhotoView photoView;
  @BindView(R2.id.closeView)
  ImageView closeView;

  public static final String EXTRA_IMAGE_URL = "EXTRA_IMAGE_URL";
  private String imageUrl;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.sig_activity_photo_view);
    ButterKnife.bind(this);
    init();
  }

  private void init() {
    AppUtils.setStatusBarColor(this, android.R.color.black);

    imageUrl = getIntent().getStringExtra(EXTRA_IMAGE_URL);

    Glide.with(this)
      .load(imageUrl)
      .thumbnail(0.1f)
      .into(photoView);

    PhotoViewAttacher attacher = new PhotoViewAttacher(photoView);
    attacher.update();

  }

  @OnClick(R2.id.closeView)
  void close() {
    finish();
  }

}
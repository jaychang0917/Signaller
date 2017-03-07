package com.redso.signaller.demo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.jaychang.utils.AppUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

public class PhotoViewerActivity extends AppCompatActivity {

  @BindView(R.id.photoView)
  PhotoView photoView;

  public static final String EXTRA_IMAGE_URL = "EXTRA_IMAGE_URL";
  private String imageUrl;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_photo_view);
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

}
package com.redso.signaller.demo.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.redso.signaller.core.model.SignallerChatMessage;
import com.redso.signaller.ui.ChatMessageCell;
import com.jaychang.srv.SimpleViewHolder;
import com.jaychang.utils.ViewUtils;
import com.redso.signaller.demo.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CustomOtherImageMessageCell extends ChatMessageCell<CustomOtherImageMessageCell.ViewHolder> {

  public CustomOtherImageMessageCell(SignallerChatMessage message) {
    super(message);
  }

  @Override
  protected int getLayoutRes() {
    return R.layout.cell_other_image_message;
  }

  @NonNull
  @Override
  protected ViewHolder onCreateViewHolder(ViewGroup viewGroup, View view) {
    return new ViewHolder(view);
  }

  @Override
  protected void onBindViewHolder(ViewHolder viewHolder, int i, Context context, Object o) {
    ViewUtils.setViewWidthHeight(viewHolder.imageView, 150, (int) (150 / getChatMessage().getImage().getRatio()));

    Glide.with(context)
      .load(getChatMessage().getImage().getUrl())
      .into(viewHolder.imageView);
  }

  static class ViewHolder extends SimpleViewHolder {
    @BindView(R.id.imageView)
    ImageView imageView;

    ViewHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
    }
  }

}

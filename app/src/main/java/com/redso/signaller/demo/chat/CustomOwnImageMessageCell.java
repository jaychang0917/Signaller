package com.redso.signaller.demo.chat;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.jaychang.srv.SimpleViewHolder;
import com.jaychang.utils.ViewUtils;
import com.redso.signaller.demo.R;
import com.redso.signaller.core.model.SignallerChatMessage;
import com.redso.signaller.ui.ChatMessageCell;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CustomOwnImageMessageCell extends ChatMessageCell<CustomOwnImageMessageCell.ViewHolder> {

  public CustomOwnImageMessageCell(SignallerChatMessage message) {
    super(message);
  }

  @Override
  protected int getLayoutRes() {
    return R.layout.cell_own_image_message;
  }

  @NonNull
  @Override
  protected ViewHolder onCreateViewHolder(ViewGroup viewGroup, View view) {
    return new ViewHolder(view);
  }

  @Override
  protected void onBindViewHolder(SignallerChatMessage chatMessage, ViewHolder viewHolder, int position, Context context) {
    viewHolder.imageView.setAlpha(chatMessage.isSent() ? 1f : 0.3f);

    ViewUtils.setViewWidthHeight(viewHolder.imageView, 150, (int) (150 / chatMessage.getImage().getRatio()));

    Glide.with(context)
      .load(chatMessage.getImage().getUrl())
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

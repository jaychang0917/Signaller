package com.jaychang.signaller.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.inthecheesefactory.thecheeselibrary.widget.AdjustableImageView;
import com.jaychang.nrv.BaseViewHolder;
import com.jaychang.signaller.R;
import com.jaychang.signaller.R2;
import com.jaychang.signaller.core.model.ChatMessage;

import butterknife.BindView;
import butterknife.ButterKnife;

class AnotherImageMessageCell extends ChatMessageCell {

  private Callback callback;

  AnotherImageMessageCell(ChatMessage message) {
    super(message);
  }

  @Override
  public BaseViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
    View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cell_another_image_message, viewGroup, false);
    return new ViewHolder(view);
  }

  @Override
  public void onBindViewHolder(BaseViewHolder viewHolder, int position, View.OnTouchListener handleTouchListener) {
    ViewHolder holder = (ViewHolder) viewHolder;
    Context context = holder.itemView.getContext();

    if (callback != null) {
      holder.itemView.setOnClickListener(view -> callback.onCellClicked(message));
    }

    Glide.with(context)
      .load(message.image.url)
      .thumbnail(0.1f)
      .into(holder.imageView);
  }

  void setCallback(Callback callback) {
    this.callback = callback;
  }

  static class ViewHolder extends BaseViewHolder {
    @BindView(R2.id.imageView)
    AdjustableImageView imageView;

    ViewHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
    }
  }

  interface Callback {
    void onCellClicked(ChatMessage message);
  }
}

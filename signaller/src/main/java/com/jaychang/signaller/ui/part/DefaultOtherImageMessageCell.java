package com.jaychang.signaller.ui.part;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.jaychang.nrv.BaseViewHolder;
import com.jaychang.signaller.R;
import com.jaychang.signaller.R2;
import com.jaychang.signaller.core.model.SignallerChatMessage;
import com.jaychang.utils.ViewUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DefaultOtherImageMessageCell extends ChatMessageCell {

  public DefaultOtherImageMessageCell(SignallerChatMessage message) {
    super(message);
  }

  @Override
  public BaseViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
    View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.sig_cell_other_image_message, viewGroup, false);
    return new ViewHolder(view);
  }

  @Override
  public void onBindViewHolder(BaseViewHolder viewHolder, int position, View.OnTouchListener handleTouchListener) {
    ViewHolder holder = (ViewHolder) viewHolder;
    Context context = holder.itemView.getContext();

    if (callback != null) {
      holder.itemView.setOnClickListener(view -> callback.onCellClicked(message));
    }

    ViewUtils.setViewWidthHeight(holder.imageView, 150, (int) (150 / message.getImage().getRatio()));

    Glide.with(context)
      .load(message.getImage().getUrl())
      .into(holder.imageView);
  }

  static class ViewHolder extends BaseViewHolder {
    @BindView(R2.id.imageView)
    ImageView imageView;

    ViewHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
    }
  }

}

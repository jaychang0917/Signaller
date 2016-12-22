package com.wiser.kol.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.jaychang.nrv.BaseViewHolder;
import com.jaychang.signaller.core.model.SignallerChatMessage;
import com.jaychang.signaller.ui.part.ChatMessageCell;
import com.jaychang.utils.AppUtils;
import com.jaychang.utils.ViewUtils;
import com.wiser.kol.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class KolOwnImageMessageCell extends ChatMessageCell {

  public KolOwnImageMessageCell(SignallerChatMessage message) {
    super(message);
  }

  @Override
  public BaseViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
    View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cell_own_image_message, viewGroup, false);
    return new ViewHolder(view);
  }

  @Override
  public void onBindViewHolder(BaseViewHolder viewHolder, int position, View.OnTouchListener handleTouchListener) {
    ViewHolder holder = (ViewHolder) viewHolder;
    Context context = holder.itemView.getContext();

    if (callback != null) {
      holder.itemView.setOnClickListener(view -> callback.onCellClicked(message));
    }

    int photoWidthDp = 150;
    int photoHeightDp = (int) (150 / message.getImage().getRatio());
    ViewUtils.setViewWidthHeight(holder.imageView, photoWidthDp, photoHeightDp);

    Glide.with(context)
      .load(message.getImage().getUrl())
      .override(AppUtils.dp2px(context, photoWidthDp), AppUtils.dp2px(context, photoHeightDp))
      .into(holder.imageView);

  }

  static class ViewHolder extends BaseViewHolder {
    @BindView(R.id.imageView)
    ImageView imageView;

    ViewHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
    }
  }

}

package com.wiser.kol.ui;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.jaychang.nrv.BaseViewHolder;
import com.jaychang.signaller.core.model.SignallerChatRoom;
import com.jaychang.signaller.core.model.SignallerReceiver;
import com.jaychang.signaller.ui.part.ChatRoomCell;
import com.wiser.kol.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

public class KolChatRoomCell extends ChatRoomCell {

  public KolChatRoomCell(SignallerChatRoom chatroom) {
    super(chatroom);
  }

  @Override
  public BaseViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
    View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cell_chatroom, viewGroup, false);
    ViewHolder viewHolder = new ViewHolder(view);

    if (callback != null) {
      viewHolder.itemView.setOnClickListener(v -> {
        callback.onCellClicked(chatRoom);
      });
    }

    return viewHolder;
  }

  @Override
  public void onBindViewHolder(BaseViewHolder viewHolder, int position, View.OnTouchListener handleTouchListener) {
    ViewHolder holder = (ViewHolder) viewHolder;
    Context context = holder.itemView.getContext();

    if (callback != null) {
      holder.itemView.setOnClickListener(v -> callback.onCellClicked(chatRoom));
    }

    SignallerReceiver receiver = chatRoom.getReceiver();
    String userLogoUrl = receiver.getProfilePhotoUrl();
    if (!TextUtils.isEmpty(userLogoUrl)) {
      holder.logoImageView.setVisibility(View.VISIBLE);
      holder.logoTextView.setVisibility(View.GONE);
      Glide.with(context)
        .load(userLogoUrl)
        .bitmapTransform(new CropCircleTransformation(context))
        .into(holder.logoImageView);
    } else {
      holder.logoImageView.setVisibility(View.GONE);
      holder.logoTextView.setVisibility(View.VISIBLE);
      holder.logoTextView.setText(receiver.getName().substring(0, 1).toUpperCase());
    }

    holder.nameView.setText(receiver.getName());

    if ("brand".equals(receiver.getUserType())) {
      holder.brandLabelView.setVisibility(View.VISIBLE);
      holder.nameView.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));
    } else {
      holder.brandLabelView.setVisibility(View.GONE);
      holder.nameView.setTextColor(ContextCompat.getColor(context, R.color.text_color));
    }

    if (!TextUtils.isEmpty(receiver.getFacebookId()) ||
      !TextUtils.isEmpty(receiver.getYoutubeId()) ||
      !TextUtils.isEmpty(receiver.getInstagramId())) {
      holder.chatBubbleView.setImageResource(R.drawable.btn_msg_on);
    } else {
      holder.chatBubbleView.setImageResource(R.drawable.btn_msg_off);
    }
  }

  static class ViewHolder extends BaseViewHolder {
    @BindView(R.id.logoImageView)
    ImageView logoImageView;
    @BindView(R.id.logoTextView)
    TextView logoTextView;
    @BindView(R.id.logoView)
    FrameLayout logoView;
    @BindView(R.id.nameView)
    TextView nameView;
    @BindView(R.id.brandLabelView)
    TextView brandLabelView;
    @BindView(R.id.chatBubbleView)
    ImageView chatBubbleView;

    ViewHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
    }
  }

}

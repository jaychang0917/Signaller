package com.jaychang.signaller.ui.part;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.jaychang.nrv.BaseViewHolder;
import com.jaychang.signaller.R;
import com.jaychang.signaller.R2;
import com.jaychang.signaller.core.model.SignallerChatMessage;
import com.jaychang.signaller.core.model.SignallerChatRoom;
import com.jaychang.signaller.core.model.SignallerReceiver;
import com.jaychang.utils.DateTimeFormatUtils;
import com.vanniktech.emoji.EmojiTextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

public class DefaultChatRoomCell extends ChatRoomCell {

  public DefaultChatRoomCell(SignallerChatRoom chatroom) {
    super(chatroom);
  }

  @Override
  public BaseViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
    View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.sig_cell_chatroom, viewGroup, false);
    ViewHolder viewHolder = new ViewHolder(view);

    if (callback != null) {
      viewHolder.itemView.setOnClickListener(v -> {
        callback.onCellClicked(chatRoom);
        chatRoom.setUnreadCount(0);
        viewHolder.unreadCountView.setVisibility(View.GONE);
      });
      viewHolder.logoView.setOnClickListener(v -> {
        callback.onReceiverLogoClicked(chatRoom.getReceiver());
      });
    }

    return viewHolder;
  }

  @Override
  public void onBindViewHolder(BaseViewHolder viewHolder, int position, View.OnTouchListener handleTouchListener) {
    ViewHolder holder = (ViewHolder) viewHolder;
    Context context = holder.itemView.getContext().getApplicationContext();

    SignallerReceiver receiver = chatRoom.getReceiver();
    boolean hasLogo = !TextUtils.isEmpty(receiver.getProfilePhotoUrl());
    Object logo = hasLogo ? receiver.getProfilePhotoUrl() : R.drawable.ic_default_profile_logo;
    Glide.with(context)
      .load(logo)
      .bitmapTransform(new CropCircleTransformation(context))
      .into(holder.logoView);

    holder.nameView.setText(receiver.getName().trim());

    if (chatRoom.getUnreadCount() > 0) {
      holder.unreadCountView.setText(String.valueOf(chatRoom.getUnreadCount()));
      holder.unreadCountView.setVisibility(View.VISIBLE);
    } else {
      holder.unreadCountView.setVisibility(View.INVISIBLE);
    }

    SignallerChatMessage lastMessage = chatRoom.getLastMessage();
    if (lastMessage != null) {
      if (lastMessage.isText()) {
        holder.lastMsgView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        holder.lastMsgView.setText(lastMessage.getContent());
      } else if (lastMessage.isImage()) {
        holder.lastMsgView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_small_camera, 0, 0, 0);
        holder.lastMsgView.setText(R.string.sig_image);
      } else {
        holder.lastMsgView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
      }
    }

    String yesterday = "'" + context.getString(R.string.sig_yesterday) + "'";
    String date = DateTimeFormatUtils.translate(
      String.valueOf(chatRoom.getLastMessageTime()),
      "hh:mm a",
      yesterday,
      "dd/MM/yyyy");
    holder.dateView.setText(date);
  }

  static class ViewHolder extends BaseViewHolder {
    @BindView(R2.id.logoView)
    ImageView logoView;
    @BindView(R2.id.nameView)
    EmojiTextView nameView;
    @BindView(R2.id.lastMsgView)
    EmojiTextView lastMsgView;
    @BindView(R2.id.dateView)
    TextView dateView;
    @BindView(R2.id.unreadCountView)
    TextView unreadCountView;

    ViewHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
    }
  }

}
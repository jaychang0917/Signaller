package com.wiser.kol.ui;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.jaychang.nrv.BaseViewHolder;
import com.jaychang.signaller.R2;
import com.jaychang.signaller.core.model.ChatMessage;
import com.jaychang.signaller.core.model.ChatRoom;
import com.jaychang.signaller.core.model.Receiver;
import com.jaychang.signaller.ui.part.ChatRoomCell;
import com.jaychang.utils.DateTimeFormatUtils;
import com.vanniktech.emoji.EmojiTextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

public class KolChatRoomCell extends ChatRoomCell {

  public KolChatRoomCell(ChatRoom chatroom) {
    super(chatroom);
  }

  @Override
  public BaseViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
    View view = LayoutInflater.from(viewGroup.getContext()).inflate(com.jaychang.signaller.R.layout.cell_chatroom, viewGroup, false);
    ViewHolder viewHolder = new ViewHolder(view);

    if (callback != null) {
      viewHolder.itemView.setOnClickListener(v -> {
        callback.onCellClicked(chatRoom);
        chatRoom.setUnreadCount(0);
        viewHolder.unreadCountView.setVisibility(View.GONE);
      });
    }

    return viewHolder;
  }

  @Override
  public void onBindViewHolder(BaseViewHolder viewHolder, int position, View.OnTouchListener handleTouchListener) {
    ViewHolder holder = (ViewHolder) viewHolder;
    Context context = holder.itemView.getContext().getApplicationContext();

    Receiver receiver = chatRoom.getReceiver();
    boolean hasLogo = !TextUtils.isEmpty(receiver.getProfilePhotoUrl());
    Object logo = hasLogo ? receiver.getProfilePhotoUrl() : com.jaychang.signaller.R.drawable.ic_default_profile_logo;
    Glide.with(context)
      .load(logo)
      .bitmapTransform(new CropCircleTransformation(context))
      .into(holder.logoView);

    holder.nameView.setText(receiver.getName());

    if (chatRoom.getUnreadCount() > 0) {
      holder.unreadCountView.setText(String.valueOf(chatRoom.getUnreadCount()));
      holder.unreadCountView.setVisibility(View.VISIBLE);
    } else {
      holder.unreadCountView.setVisibility(View.INVISIBLE);
    }

    ChatMessage lastMessage = chatRoom.getLastMessage();
    if (lastMessage != null) {
      if (lastMessage.isText()) {
        holder.lastMsgView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        holder.lastMsgView.setText(lastMessage.getContent());
      } else if (lastMessage.isImage()) {
        holder.lastMsgView.setCompoundDrawablesWithIntrinsicBounds(com.jaychang.signaller.R.drawable.ic_small_camera, 0, 0, 0);
        holder.lastMsgView.setText(com.jaychang.signaller.R.string.image);
      } else {
        holder.lastMsgView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
      }

      String yesterday = "'" + context.getString(com.jaychang.signaller.R.string.yesterday) + "'";
      String date = DateTimeFormatUtils.translate(
        String.valueOf(chatRoom.getMtime()),
        "hh:mm a",
        yesterday,
        "dd/MM/yyyy");
      holder.dateView.setText(date);
    }
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

package com.redso.signaller.demo.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.redso.signaller.core.model.SignallerChatMessage;
import com.redso.signaller.core.model.SignallerChatRoom;
import com.redso.signaller.core.model.SignallerReceiver;
import com.redso.signaller.ui.ChatRoomCell;
import com.jaychang.srv.SimpleViewHolder;
import com.jaychang.utils.DateTimeFormatUtils;
import com.redso.signaller.demo.R;
import com.vanniktech.emoji.EmojiTextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

public class CustomChatRoomCell extends ChatRoomCell<CustomChatRoomCell.ViewHolder> {

  public CustomChatRoomCell(SignallerChatRoom chatroom) {
    super(chatroom);
  }

  @Override
  protected int getLayoutRes() {
    return R.layout.cell_chat_room;
  }

  @NonNull
  @Override
  protected ViewHolder onCreateViewHolder(ViewGroup viewGroup, View view) {
    return new ViewHolder(view);
  }

  @Override
  protected void onBindViewHolder(ViewHolder holder, int position, Context context, Object payload) {
    holder.itemView.setOnClickListener(v -> {
      onCellClicked();
      holder.unreadCountView.setVisibility(View.GONE);
    });

    SignallerReceiver receiver = getChatRoom().getReceiver();
    boolean hasLogo = !TextUtils.isEmpty(receiver.getProfilePhotoUrl());
    Object logo = hasLogo ? receiver.getProfilePhotoUrl() : R.drawable.ic_default_profile_logo;
    Glide.with(context)
      .load(logo)
      .bitmapTransform(new CropCircleTransformation(context))
      .into(holder.logoView);

    holder.nameView.setText(receiver.getName().trim());

    if (getChatRoom().getUnreadCount() > 0) {
      holder.unreadCountView.setText(String.valueOf(getChatRoom().getUnreadCount()));
      holder.unreadCountView.setVisibility(View.VISIBLE);
    } else {
      holder.unreadCountView.setVisibility(View.INVISIBLE);
    }

    SignallerChatMessage lastMessage = getChatRoom().getLastMessage();
    if (lastMessage != null) {
      if (lastMessage.isText()) {
        holder.lastMsgView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        holder.lastMsgView.setText(lastMessage.getContent());
      } else if (lastMessage.isImage()) {
        holder.lastMsgView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_small_camera, 0, 0, 0);
        holder.lastMsgView.setText(R.string.photo);
      } else {
        holder.lastMsgView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
      }
    }

    String yesterday = "'" + context.getString(R.string.yesterday) + "'";
    String date = DateTimeFormatUtils.translate(
      String.valueOf(getChatRoom().getLastMessageTime()),
      "hh:mm a",
      yesterday,
      "dd/MM/yyyy");
    holder.dateView.setText(date);
  }

  @Override
  protected long getItemId() {
    return getChatRoom().getChatRoomId().hashCode();
  }

  static class ViewHolder extends SimpleViewHolder {
    @BindView(R.id.logoView)
    ImageView logoView;
    @BindView(R.id.nameView)
    EmojiTextView nameView;
    @BindView(R.id.lastMsgView)
    EmojiTextView lastMsgView;
    @BindView(R.id.dateView)
    TextView dateView;
    @BindView(R.id.unreadCountView)
    TextView unreadCountView;

    ViewHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
    }
  }

}

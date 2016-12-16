package com.jaychang.signaller.ui;

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
import com.jaychang.signaller.core.model.ChatMessage;
import com.jaychang.signaller.core.model.ChatRoom;
import com.jaychang.signaller.core.model.Receiver;
import com.jaychang.utils.DateTimeFormatUtils;
import com.vanniktech.emoji.EmojiTextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

class DefaultChatRoomCell extends ChatRoomCell {

  private ChatRoom chatroom;
  private Callback callback;

  public DefaultChatRoomCell(ChatRoom chatroom) {
    super(chatroom);
    this.chatroom = chatroom;
  }

  @Override
  public BaseViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
    View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cell_chatroom, viewGroup, false);
    ViewHolder viewHolder = new ViewHolder(view);

    if (callback != null) {
      viewHolder.itemView.setOnClickListener(v -> callback.onCellClicked(chatroom));
    }

    return viewHolder;
  }

  @Override
  public void onBindViewHolder(BaseViewHolder viewHolder, int position, View.OnTouchListener handleTouchListener) {
    ViewHolder holder = (ViewHolder) viewHolder;
    Context context = holder.itemView.getContext().getApplicationContext();

    Receiver receiver = chatroom.receiver;
    boolean hasLogo = !TextUtils.isEmpty(receiver.profilePicUrl);
    Object logo = hasLogo ? receiver.profilePicUrl : R.drawable.ic_default_profile_logo;
    Glide.with(context)
      .load(logo)
      .bitmapTransform(new CropCircleTransformation(context))
      .into(holder.logoView);

    holder.nameView.setText(receiver.name);

    ChatMessage lastMessage = chatroom.lastMessage;
    if (lastMessage != null) {
      if (lastMessage.isText()) {
        holder.lastMsgView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        holder.lastMsgView.setText(lastMessage.content);
      } else if (lastMessage.isImage()) {
        holder.lastMsgView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_camera, 0, 0, 0);
        holder.lastMsgView.setText(R.string.image);
      } else if (lastMessage.isEvent()) {
        holder.lastMsgView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
      }

      String yesterday = "'" + context.getString(R.string.yesterday) + "'";
      String date = DateTimeFormatUtils.translate(
        String.valueOf(lastMessage.mtime),
        "hh:mm a",
        yesterday,
        "dd/MM/yyyy");
      holder.dateView.setText(date);
    }
  }

  public void setCallback(Callback callback) {
    this.callback = callback;
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

    ViewHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
    }
  }

  public interface Callback {
    void onCellClicked(ChatRoom chatroom);
  }

}

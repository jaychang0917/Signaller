package com.redso.signaller.demo.chat;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;

import com.jaychang.srv.SimpleViewHolder;
import com.redso.signaller.demo.R;
import com.redso.signaller.core.model.SignallerChatMessage;
import com.redso.signaller.ui.ChatMessageCell;
import com.vanniktech.emoji.EmojiTextView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CustomOwnTextMessageCell extends ChatMessageCell<CustomOwnTextMessageCell.ViewHolder> {

  public CustomOwnTextMessageCell(SignallerChatMessage message) {
    super(message);
  }

  @Override
  protected int getLayoutRes() {
    return R.layout.cell_own_text_message;
  }

  @NonNull
  @Override
  protected ViewHolder onCreateViewHolder(ViewGroup viewGroup, View view) {
    return new ViewHolder(view);
  }

  @Override
  protected void onBindViewHolder(SignallerChatMessage chatMessage, ViewHolder viewHolder, int position, Context context) {
    viewHolder.messageView.setAlpha(chatMessage.isSent() ? 1f : 0.3f);

    viewHolder.messageView.setText(chatMessage.getContent());
  }

  static class ViewHolder extends SimpleViewHolder {
    @BindView(R.id.messageView)
    EmojiTextView messageView;

    ViewHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
    }
  }

}

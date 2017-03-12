package com.redso.signaller.demo.chat;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;

import com.jaychang.srv.SimpleViewHolder;
import com.redso.signaller.core.model.ChatMessage;
import com.redso.signaller.demo.R;
import com.redso.signaller.ui.ChatMessageCell;
import com.vanniktech.emoji.EmojiTextView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CustomOtherTextMessageCell extends ChatMessageCell<CustomOtherTextMessageCell.ViewHolder> {

  public CustomOtherTextMessageCell(ChatMessage message) {
    super(message);
  }

  @Override
  protected int getLayoutRes() {
    return R.layout.cell_other_text_message;
  }

  @NonNull
  @Override
  protected ViewHolder onCreateViewHolder(ViewGroup viewGroup, View view) {
    return new ViewHolder(view);
  }

  @Override
  protected void onBindViewHolder(ChatMessage chatMessage, ViewHolder viewHolder, int position, Context context) {
    viewHolder.otherMessageView.setAlpha(chatMessage.isSent() ? 1f : 0.3f);

    viewHolder.otherMessageView.setText(chatMessage.getContent());
  }

  static class ViewHolder extends SimpleViewHolder {
    @BindView(R.id.otherMessageView)
    EmojiTextView otherMessageView;

    ViewHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
    }
  }

}

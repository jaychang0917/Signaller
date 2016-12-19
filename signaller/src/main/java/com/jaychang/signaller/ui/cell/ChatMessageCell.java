package com.jaychang.signaller.ui.cell;

import com.jaychang.nrv.BaseCell;
import com.jaychang.signaller.core.model.ChatMessage;

public abstract class ChatMessageCell extends BaseCell {

  public interface Callback {
    void onCellClicked(ChatMessage message);
  }

  protected ChatMessage message;
  protected Callback callback;
  protected boolean isSent = true;

  public ChatMessageCell(ChatMessage message) {
    this.message = message;
  }

  public ChatMessage getMessage() {
    return message;
  }

  public void setMessage(ChatMessage message) {
    this.message = message;
  }

  public void setSent(boolean isSent) {
     this.isSent = isSent;
  }

  public boolean isSent() {
    return isSent;
  }

  public void setCallback(Callback callback) {
    this.callback = callback;
  }
}

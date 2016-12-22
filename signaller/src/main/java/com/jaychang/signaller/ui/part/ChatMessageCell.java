package com.jaychang.signaller.ui.part;

import com.jaychang.nrv.BaseCell;
import com.jaychang.signaller.core.model.SignallerChatMessage;

public abstract class ChatMessageCell extends BaseCell {

  public interface Callback {
    void onCellClicked(SignallerChatMessage message);
  }

  protected SignallerChatMessage message;
  protected Callback callback;
  protected boolean isSent = true;

  public ChatMessageCell(SignallerChatMessage message) {
    this.message = message;
  }

  public SignallerChatMessage getMessage() {
    return message;
  }

  public void setMessage(SignallerChatMessage message) {
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

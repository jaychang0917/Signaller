package com.jaychang.signaller.ui;

import com.jaychang.nrv.BaseCell;
import com.jaychang.signaller.core.model.ChatMessage;

abstract class ChatMessageCell extends BaseCell {

  protected ChatMessage message;
  protected boolean isSent = true;

  ChatMessageCell(ChatMessage message) {
    this.message = message;
  }

  ChatMessage getMessage() {
    return message;
  }

  void setMessage(ChatMessage message) {
    this.message = message;
  }

  void setSent(boolean isSent) {
     this.isSent = isSent;
  }

  boolean isSent() {
    return isSent;
  }

}

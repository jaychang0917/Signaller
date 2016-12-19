package com.jaychang.signaller.ui.cell;

import com.jaychang.nrv.BaseCell;

public abstract class ChatMessageDateSeparatorCell extends BaseCell {

  protected long timestamp;
  protected String format = "dd/MM/yy,h:mm a";

  public ChatMessageDateSeparatorCell(long timestamp) {
    this.timestamp = timestamp;
  }

  public ChatMessageDateSeparatorCell(long timestamp, String format) {
    this.timestamp = timestamp;
    this.format = format;
  }

  public long getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(long timestamp) {
    this.timestamp = timestamp;
  }

  public void setFormat(String format) {
    this.format = format;
  }

  public String getFormat() {
    return format;
  }
}

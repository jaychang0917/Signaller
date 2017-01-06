package com.jaychang.signaller.ui.part;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.jaychang.signaller.R;
import com.jaychang.signaller.core.model.SignallerChatMessage;
import com.jaychang.utils.DateTimeFormatUtils;

public class DefaultDateSeparatorView extends FrameLayout {

  private TextView dateView;

  public DefaultDateSeparatorView(Context context) {
    super(context);
    View view = LayoutInflater.from(context).inflate(R.layout.sig_view_date_separator, this, true);
    dateView = (TextView) view.findViewById(R.id.dateView);
  }

  public void bind(SignallerChatMessage message) {
    String yesterday = "'" + getContext().getString(R.string.sig_yesterday) + "', h:mm a";
    String today = "'" + getContext().getString(R.string.sig_today) + "', h:mm a";

    String date = DateTimeFormatUtils.translate(
      String.valueOf(message.getMsgTime() != 0L ? message.getMsgTime() : message.getTimestamp()),
      today,
      yesterday,
      "dd/MM/yy,h:mm a"
    );
    dateView.setText(date);
  }

}

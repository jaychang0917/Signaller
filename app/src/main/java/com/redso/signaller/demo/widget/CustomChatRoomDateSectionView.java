package com.redso.signaller.demo.widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.jaychang.utils.DateTimeFormatUtils;
import com.redso.signaller.demo.R;
import com.redso.signaller.core.model.SignallerChatMessage;

public class CustomChatRoomDateSectionView extends FrameLayout {

  private TextView dateView;

  public CustomChatRoomDateSectionView(Context context) {
    super(context);
    View view = LayoutInflater.from(context).inflate(R.layout.view_date_section, this);
    dateView = (TextView) view.findViewById(R.id.dateView);
  }

  public void bind(SignallerChatMessage message) {
    String yesterday = "'" + getContext().getString(R.string.yesterday) + "', h:mm a";
    String today = "'" + getContext().getString(R.string.today) + "', h:mm a";

    String date = DateTimeFormatUtils.translate(
      String.valueOf(message.getMsgTime() != 0L ? message.getMsgTime() : message.getTimestamp()),
      today,
      yesterday,
      "dd/MM/yy,h:mm a"
    );
    dateView.setText(date);
  }

}

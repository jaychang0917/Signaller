package com.jaychang.signaller.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jaychang.nrv.BaseCell;
import com.jaychang.nrv.BaseViewHolder;
import com.jaychang.signaller.R;
import com.jaychang.signaller.R2;
import com.jaychang.utils.DateTimeFormatUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

class ChatMessageDateCell extends BaseCell {

  private long date;

  ChatMessageDateCell(long date) {
    this.date = date;
  }

  @Override
  public BaseViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
    View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cell_chat_message_date, viewGroup, false);
    return new ViewHolder(view);
  }

  @Override
  public void onBindViewHolder(BaseViewHolder viewHolder, int position, View.OnTouchListener handleTouchListener) {
    ViewHolder holder = (ViewHolder) viewHolder;
    Context context = holder.itemView.getContext();

    String text = DateTimeFormatUtils.format(String.valueOf(date), "dd/MM/yy,h:mm a");
    holder.dateView.setText(text);
  }

  static class ViewHolder extends BaseViewHolder {
    @BindView(R2.id.dateView)
    TextView dateView;

    ViewHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
    }
  }
}

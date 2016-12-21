package com.wiser.kol.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.jaychang.nrv.BaseViewHolder;
import com.jaychang.signaller.core.model.ChatMessage;
import com.jaychang.signaller.core.model.Event;
import com.jaychang.signaller.ui.part.ChatMessageCell;
import com.jaychang.toolbox.widget.NButton;
import com.wiser.kol.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class KolEventMessageCell extends ChatMessageCell {

  public KolEventMessageCell(ChatMessage message) {
    super(message);
  }

  @Override
  public BaseViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
    View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cell_other_event_message, viewGroup, false);
    return new ViewHolder(view);
  }

  @Override
  public void onBindViewHolder(BaseViewHolder viewHolder, int i, View.OnTouchListener onTouchListener) {
    ViewHolder holder = (ViewHolder) viewHolder;
    Context context = holder.itemView.getContext().getApplicationContext();

    Event event = message.getEvent();

    Glide.with(context)
      .load(event.getImageUrl())
      .into(holder.imageView);

    holder.contentView.setText(event.getTitle());

    holder.leftButton.setText(event.getOptions().get(0).getVal());
    holder.middleButton.setText(event.getOptions().get(1).getVal());
    holder.rightButton.setText(event.getOptions().get(2).getVal());

    holder.leftButton.setOnClickListener(view -> {
      handleLeftButtonClick();
    });
    holder.middleButton.setOnClickListener(view -> {
      handleMiddleButtonClick();
    });
    holder.rightButton.setOnClickListener(view -> {
      handleRightButtonClick();
    });
  }

  private void handleRightButtonClick() {

  }

  private void handleMiddleButtonClick() {

  }

  private void handleLeftButtonClick() {

  }

  static class ViewHolder extends BaseViewHolder {
    @BindView(R.id.imageView)
    ImageView imageView;
    @BindView(R.id.contentView)
    TextView contentView;
    @BindView(R.id.leftButton)
    NButton leftButton;
    @BindView(R.id.middleButton)
    NButton middleButton;
    @BindView(R.id.rightButton)
    NButton rightButton;

    ViewHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
    }
  }

}

package com.wiser.kol.ui;

import android.content.Context;
import android.content.res.ColorStateList;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.jaychang.nrv.BaseViewHolder;
import com.jaychang.signaller.core.model.SignallerChatMessage;
import com.jaychang.signaller.core.model.SignallerEvent;
import com.jaychang.signaller.ui.part.ChatMessageCell;
import com.jaychang.toolbox.widget.NButton;
import com.jaychang.utils.StringUtils;
import com.wiser.kol.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class KolEventMessageCell extends ChatMessageCell {

  public KolEventMessageCell(SignallerChatMessage message) {
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

    SignallerEvent event = message.getEvent();

    Glide.with(context)
      .load(event.getImageUrl())
      .into(holder.imageView);

    holder.contentView.setText(event.getTitle());

    String leftOption = event.getOptions().get(0).getVal();
    String middleOption = event.getOptions().get(1).getVal();
    String rightOption = event.getOptions().get(2).getVal();
    holder.leftButton.setText(StringUtils.capitalize(leftOption));
    holder.middleButton.setText(StringUtils.capitalize(middleOption));
    holder.rightButton.setText(StringUtils.capitalize(rightOption));

    holder.leftButton.setOnClickListener(view -> {
      selectLeftOption(holder);
      updateEventMsg(leftOption);
    });
    holder.middleButton.setOnClickListener(view -> {
      selectMiddleOption(holder);
      updateEventMsg(middleOption);
    });
    holder.rightButton.setOnClickListener(view -> {
      selectRightOption(holder);
      updateEventMsg(rightOption);
    });

    String result = event.getResult();
    if (result.equalsIgnoreCase(leftOption)) {
      selectLeftOption(holder);
    } else if (result.equalsIgnoreCase(middleOption)) {
      selectMiddleOption(holder);
    } else if (result.equalsIgnoreCase(rightOption)) {
      selectRightOption(holder);
    }
  }

  private void selectRightOption(ViewHolder holder) {
    int onColor = ContextCompat.getColor(holder.itemView.getContext(), R.color.event_option_on);
    int offColor = ContextCompat.getColor(holder.itemView.getContext(), R.color.event_option_off);
    holder.leftButton.setSelected(false);
    holder.leftButton.getButton().setTextColor(offColor);
    holder.middleButton.setSelected(false);
    holder.middleButton.getButton().setTextColor(offColor);
    holder.rightButton.setSelected(true);
    holder.rightButton.getButton().setTextColor(onColor);
  }

  private void selectMiddleOption(ViewHolder holder) {
    int onColor = ContextCompat.getColor(holder.itemView.getContext(), R.color.event_option_on);
    int offColor = ContextCompat.getColor(holder.itemView.getContext(), R.color.event_option_off);
    holder.leftButton.setSelected(false);
    holder.leftButton.getButton().setTextColor(offColor);
    holder.middleButton.setSelected(true);
    holder.middleButton.getButton().setTextColor(onColor);
    holder.rightButton.setSelected(false);
    holder.rightButton.getButton().setTextColor(offColor);
  }

  private void selectLeftOption(ViewHolder holder) {
    int onColor = ContextCompat.getColor(holder.itemView.getContext(), R.color.event_option_on);
    int offColor = ContextCompat.getColor(holder.itemView.getContext(), R.color.event_option_off);
    holder.leftButton.setSelected(true);
    holder.leftButton.getButton().setTextColor(onColor);
    holder.middleButton.setSelected(false);
    holder.middleButton.getButton().setTextColor(offColor);
    holder.rightButton.setSelected(false);
    holder.rightButton.getButton().setTextColor(offColor);
  }

  private void updateEventMsg(String result) {
    // todo
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

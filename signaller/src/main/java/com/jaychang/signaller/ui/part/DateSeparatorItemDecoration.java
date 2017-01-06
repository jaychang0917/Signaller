package com.jaychang.signaller.ui.part;

import android.annotation.SuppressLint;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.jaychang.nrv.NRecyclerView;
import com.jaychang.signaller.core.model.SignallerChatMessage;
import com.jaychang.signaller.ui.config.DateSeparatorViewProvider;

public class DateSeparatorItemDecoration extends RecyclerView.ItemDecoration {

  private DateSeparatorViewProvider provider;
  private NRecyclerView recyclerView;
  private int sectionHeight;

  public DateSeparatorItemDecoration(DateSeparatorViewProvider provider) {
    this.provider = provider;
  }

  @Override
  public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
    if (recyclerView == null) {
      recyclerView = ((NRecyclerView) parent);
    }

    int position = parent.getChildAdapterPosition(view);

    if (sectionHeight == 0) {
      View sectionView = getAndMeasureSectionView(parent, position);
      sectionHeight = sectionView.getMeasuredHeight();
    }

    if (!isSameSection(position)) {
      outRect.top = sectionHeight;
    } else {
      outRect.top = 0;
    }
  }

  private View getAndMeasureSectionView(RecyclerView parent, int position) {
    View sectionView = provider.getSeparatorView(getMessage(position));
    int widthSpec = View.MeasureSpec.makeMeasureSpec(parent.getWidth(), View.MeasureSpec.EXACTLY);
    int heightSpec = View.MeasureSpec.makeMeasureSpec(parent.getHeight(), View.MeasureSpec.AT_MOST);
    sectionView.measure(widthSpec, heightSpec);
    return sectionView;
  }

  @SuppressLint("NewApi")
  @Override
  public void onDraw(Canvas canvas, RecyclerView parent, RecyclerView.State state) {
    int left = parent.getPaddingLeft();
    int right = parent.getWidth() - parent.getPaddingRight();

    for (int i = 0; i < parent.getChildCount(); i++) {
      View view = parent.getChildAt(i);
      int position = parent.getChildAdapterPosition(view);
      if (!isSameSection(position)) {
        View sectionView = getAndMeasureSectionView(parent, position);
        int top = view.getTop() - sectionHeight;
        int bottom = view.getTop();
        sectionView.layout(left, top, right, bottom);
        canvas.save();
        canvas.translate(left, top);
        sectionView.draw(canvas);
        canvas.restore();
      }
    }
  }

  private SignallerChatMessage getMessage(int position) {
    return ((ChatMessageCell) recyclerView.getCell(position)).getMessage();
  }

  private boolean isSameSection(int position) {
    if (position == 0) {
      return false;
    }

    return provider.isSameDate(getMessage(position), getMessage(position - 1));
  }

}
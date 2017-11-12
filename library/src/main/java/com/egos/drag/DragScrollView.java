package com.egos.drag;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;

/**
 * Created by Egos on 2017/11/7.
 * </p>
 * {@link DragView}中的item 比较多时需要滚动。
 */
public class DragScrollView extends ScrollView {
  public DragScrollView(Context context) {
    super(context);
  }

  public DragScrollView(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public DragScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
  public DragScrollView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
    super(context, attrs, defStyleAttr, defStyleRes);
  }

  @Override public boolean onInterceptTouchEvent(MotionEvent ev) {
    return super.onInterceptTouchEvent(ev);
  }
}

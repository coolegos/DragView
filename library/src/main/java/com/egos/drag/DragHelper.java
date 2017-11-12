package com.egos.drag;

import android.content.Context;

/**
 * Created by Egos on 2017/11/7.
 * </p>
 * 工具类
 */
public class DragHelper {
  private DragHelper() {
    throw new IllegalStateException("DragHelper is a util");
  }

  public static int getScreenWidth(Context context) {
    return context.getResources().getDisplayMetrics().widthPixels;
  }
}

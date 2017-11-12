package com.egos.drag;

import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Egos on 2017/11/7.
 *
 * 适配{@link DragView}里面的内容。
 * </p>
 * 1.需要知道select 和unselect 的数量。
 */
public abstract class DragAdapter {

  public abstract View getSelectView(int position, ViewGroup parent);

  public abstract View getUnselectView(int position, ViewGroup parent);

  public abstract View getUndragView(int position, ViewGroup parent);

  public abstract int getSelectCount();

  public abstract int getUnselectCount();

  public abstract int getUndragCount();

  public abstract void removeSelect(int position);

  public abstract void removeUnselect(int position);

  public abstract void reAdd(int srcPosition, int dstPosition);
}

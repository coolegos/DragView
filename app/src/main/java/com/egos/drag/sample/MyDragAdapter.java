package com.egos.drag.sample;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.egos.drag.DragAdapter;
import java.util.List;

/**
 * Created by Egos on 2017/11/7.
 */

public class MyDragAdapter extends DragAdapter {

  private Context mContext;

  private List<String> undragLst;
  private List<String> selectLst;
  private List<String> unselectLst;

  public MyDragAdapter(Context context, List<String> undragLst, List<String> selectLst,
      List<String> unselectLst) {
    mContext = context;
    this.undragLst = undragLst;
    this.selectLst = selectLst;
    this.unselectLst = unselectLst;
  }

  @Override public View getSelectView(int position, ViewGroup parent) {
    View view = LayoutInflater.from(mContext).inflate(R.layout.item, parent, false);
    ((TextView) view.findViewById(R.id.text)).setText(selectLst.get(position));
    return view;
  }

  @Override public View getUnselectView(int position, ViewGroup parent) {
    View view = LayoutInflater.from(mContext).inflate(R.layout.item, parent, false);
    ((TextView) view.findViewById(R.id.text)).setText(unselectLst.get(position));
    return view;
  }

  @Override public View getUndragView(int position, ViewGroup parent) {
    View view = LayoutInflater.from(mContext).inflate(R.layout.item, parent, false);
    ((TextView) view.findViewById(R.id.text)).setText(undragLst.get(position));
    return view;
  }

  @Override public int getSelectCount() {
    return selectLst == null ? 0 : selectLst.size();
  }

  @Override public int getUnselectCount() {
    return unselectLst == null ? 0 : unselectLst.size();
  }

  @Override public int getUndragCount() {
    return undragLst == null ? 0 : undragLst.size();
  }

  @Override public void removeSelect(int position) {
    String item = selectLst.remove(position);
    unselectLst.add(0, item);
  }

  @Override public void removeUnselect(int position) {
    String item = unselectLst.remove(position);
    selectLst.add(item);
  }

  @Override public void reAdd(int srcPosition, int dstPosition) {
    String item = selectLst.remove(srcPosition);
    selectLst.add(dstPosition, item);
  }

}

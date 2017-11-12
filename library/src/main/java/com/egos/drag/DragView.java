package com.egos.drag;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Egos on 2017/11/7.
 * </p>
 * 封装的View，功能如下：
 * 1.长按事件
 * 2.切换动画
 * 3.联动父布局
 * 4.数据来源：最好是可以任意布局的ItemView。
 * </p>
 * 实现：
 * 1.分为两部分：选中的item 和未选中的item。需要对应两个View 分别代表两个Title
 * 2.measure
 * 3.layout
 * 4.从某个位置回来。动画选择
 * 5.与某个Item 交换位置：动画+数据交换
 * </p>
 */
public class DragView extends ViewGroup {

  private final static String TAG = "DragView";

  private final static int MIN_COLUMNS = 3;
  private final static float DEFAULT_RATIO = 1.0f;

  private Vibrator mVibrator;

  private int mItemWidth;
  private int mItemHeight;

  private int mColumns;
  private float mRatio; // 子ItemView 的宽高比
  private int mVerticalPadding; // px
  private int mHorizontalPadding; // px

  private View mSelectTitleView; // 选择列表的Title
  private View mUnselectTitleView; // 未选择列表的Title

  private List<View> selectList;
  private List<View> unselectList;
  private List<View> undragList;

  private float mLastX;
  private float mLastY;

  private DragAdapter mAdapter;

  private View mDraggingView;
  private int mDraggingPosition = -1;

  private Rect mVisibleRect;

  private OnLongClickListener mDragLongClick = new OnLongClickListener() {
    @Override public boolean onLongClick(View view) {
      mVibrator.vibrate(30);
      mDraggingPosition = selectList.indexOf(view);
      mDraggingView = view;
      mDraggingView.bringToFront();
      return true;
    }
  };

  private OnClickListener mDragClick = new OnClickListener() {
    @Override public void onClick(View v) {
      if (selectList != null && selectList.contains(v)) {
        int position = selectList.indexOf(v);
        selectList.remove(v);
        if (unselectList == null) {
          unselectList = new ArrayList<>();
        }
        unselectList.add(0, v);
        requestLayout();

        v.setOnLongClickListener(null);
        mAdapter.removeSelect(position);
      } else if (unselectList != null && unselectList.contains(v)) {
        int position = unselectList.indexOf(v);
        unselectList.remove(v);
        if (selectList == null) {
          selectList = new ArrayList<>();
        }
        selectList.add(v);
        requestLayout();

        v.setOnLongClickListener(mDragLongClick);

        mAdapter.removeUnselect(position);
      }
    }
  };

  public DragView(Context context) {
    this(context, null);
  }

  public DragView(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public DragView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init(context, attrs, defStyleAttr);
  }

  private void init(Context context, AttributeSet attrs, int defStyleAttr) {
    TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.DragView, defStyleAttr, 0);
    mColumns = Math.max(a.getInt(R.styleable.DragView_column, MIN_COLUMNS), MIN_COLUMNS);
    mRatio = a.getFloat(R.styleable.DragView_ratio, DEFAULT_RATIO);
    mRatio = mRatio <= 0.0f ? DEFAULT_RATIO : mRatio;
    mVerticalPadding = a.getDimensionPixelSize(R.styleable.DragView_vertical_padding, 0);
    mHorizontalPadding = a.getDimensionPixelSize(R.styleable.DragView_horizontal_padding, 0);
    a.recycle();

    mVibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
  }

  public void setAdapter(DragAdapter adapter) {
    if (adapter == null) {
      throw new IllegalArgumentException("adapter can not be null");
    }
    this.mAdapter = adapter;

    for (int i = 0; i < mAdapter.getUndragCount(); i++) {
      View view = mAdapter.getUndragView(i, this);

      if (undragList == null) {
        undragList = new ArrayList<>();
      }
      undragList.add(view);

      if (selectList == null) {
        selectList = new ArrayList<>();
      }
      selectList.add(view);

      addView(view, new LayoutParams());
    }

    for (int i = mAdapter.getUndragCount(); i < mAdapter.getSelectCount(); i++) {
      View view = mAdapter.getSelectView(i, this);

      if (selectList == null) {
        selectList = new ArrayList<>();
      }
      selectList.add(view);

      addView(view, new LayoutParams());
      view.setOnLongClickListener(mDragLongClick);
      view.setOnClickListener(mDragClick);
    }

    for (int i = 0; i < mAdapter.getUnselectCount(); i++) {
      if (unselectList == null) {
        unselectList = new ArrayList<>();
      }
      View view = mAdapter.getUnselectView(i, this);
      unselectList.add(view);
      addView(view, new LayoutParams());
      view.setOnClickListener(mDragClick);
    }

    requestLayout();
  }

  /**
   * 计算手指滚到的位置，需要考虑padding 值
   */
  private int computeDragPosition(int x, int y) {
    if (mDraggingView != null) {
      int height = y - mSelectTitleView.getHeight() - getPaddingTop();
      int row = height / mItemHeight + (height % mItemHeight > 0 ? 1 : 0);
      if (row > 0) {
        int width = x - getPaddingLeft();
        int column = width / mItemWidth + (width % mItemWidth > 0 ? 1 : 0);

        int index = (row - 1) * mColumns + column - 1;
        if (index < selectList.size() && checkCanDrag(index)) {
          return index;
        }
      }
    }
    return mDraggingPosition;
  }

  private boolean checkCanDrag(int index) {
    return undragList == null || !undragList.contains(selectList.get(index));
  }

  @Override protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    int width = MeasureSpec.getSize(widthMeasureSpec);

    mItemWidth =
        (width - getPaddingLeft() - getPaddingRight() - (mColumns - 1) * mHorizontalPadding)
            / mColumns;
    mItemHeight = (int) (mItemWidth / mRatio);

    int height = 0;

    for (int i = 0; i < getChildCount(); i++) {
      View child = getChildAt(i);
      if (child == mSelectTitleView || child == mUnselectTitleView) {
        measureChild(child, widthMeasureSpec, heightMeasureSpec);
        height += child.getMeasuredHeight();
      } else {
        child.measure(MeasureSpec.makeMeasureSpec(mItemWidth, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(mItemHeight, MeasureSpec.EXACTLY));
      }
    }

    if (selectList != null && selectList.size() > 0) {
      int selectRowCount = (selectList.size() + mColumns - 1) / mColumns;
      height += mItemHeight * selectRowCount + (selectRowCount - 1) * mVerticalPadding;
    }

    if (unselectList != null && unselectList.size() > 0) {
      int unselectRowCount = (unselectList.size() + mColumns - 1) / mColumns;
      height += mItemHeight * unselectRowCount + (unselectRowCount - 1) * mVerticalPadding;
    }

    setMeasuredDimension(width, height + getPaddingTop() + getPaddingBottom());
  }

  private void performItemAnimation() {
    int titleHeight = mSelectTitleView.getMeasuredHeight();
    int column;
    int row;
    for (int i = 0; i < selectList.size(); i++) {
      if (mDraggingPosition == i) {
        continue;
      }
      row = i / mColumns;
      column = i - row * mColumns;
      View child = selectList.get(i);
      LayoutParams layoutParams = (LayoutParams) child.getLayoutParams();

      itemAnimation(child, layoutParams.leftMargin, layoutParams.topMargin,
          column * mItemWidth + column * mHorizontalPadding + getPaddingLeft(),
          row * mItemHeight + titleHeight + row * mVerticalPadding + getPaddingTop());

      layoutParams.leftMargin =
          column * mItemWidth + column * mHorizontalPadding + getPaddingLeft();
      layoutParams.topMargin =
          row * mItemHeight + titleHeight + row * mVerticalPadding + getPaddingTop();
    }
  }

  /**
   * Item 展示动画，从(startX, startY)到(endX, endY)
   */
  private void itemAnimation(View view, int startX, int startY, int endX, int endY) {
    TranslateAnimation translateAnimation =
        new TranslateAnimation(startX - endX, 0, startY - endY, 0);
    translateAnimation.setDuration(200);
    view.startAnimation(translateAnimation);
  }

  /**
   * 1.mSelectTitleView + Select Item
   * 2.mUnselectTitleView + Unselect Item
   */
  @Override protected void onLayout(boolean changed, int l, int t, int r, int b) {
    int leftStart = l + getPaddingLeft();
    int childLeft = leftStart;
    int childTop = t + getPaddingTop();
    // 1. layout select item
    mSelectTitleView.layout(childLeft, childTop, childLeft + mSelectTitleView.getMeasuredWidth(),
        childTop + mSelectTitleView.getMeasuredHeight());
    childTop += mSelectTitleView.getMeasuredHeight();
    if (selectList != null) {
      int selectCount = selectList.size();
      if (selectCount > 0) {
        int selectRowCount = (selectCount + mColumns - 1) / mColumns;
        int index;
        for (int i = 0; i < selectRowCount; i++) {
          childLeft = leftStart;
          for (int j = 0; j < mColumns; j++) {
            index = i * mColumns + j;
            if (index >= selectCount) {
              break;
            }
            if (j > 0) {
              childLeft += mHorizontalPadding;
            }
            View child = selectList.get(index);
            LayoutParams layoutParams = (LayoutParams) child.getLayoutParams();
            if (mDraggingView == null) {
              itemAnimation(child, layoutParams.leftMargin, layoutParams.topMargin, childLeft,
                  childTop);
              layoutParams.leftMargin = childLeft;
              layoutParams.topMargin = childTop;
            }
            child.layout(layoutParams.leftMargin, layoutParams.topMargin,
                layoutParams.leftMargin + mItemWidth, layoutParams.topMargin + mItemHeight);
            childLeft += mItemWidth;
          }
          childTop += mItemHeight;
          if (i < selectRowCount - 1) {
            childTop += mVerticalPadding;
          }
        }
      }
    }

    // 2. layout unselect item
    childLeft = leftStart;
    mUnselectTitleView.layout(childLeft, childTop, l + mUnselectTitleView.getMeasuredWidth(),
        childTop + mUnselectTitleView.getMeasuredHeight());
    childTop += mUnselectTitleView.getMeasuredHeight();
    if (unselectList != null) {
      int unselectCount = unselectList.size();
      if (unselectCount > 0) {
        int unselectRowCount = (unselectCount + mColumns - 1) / mColumns;
        int index;
        for (int i = 0; i < unselectRowCount; i++) {
          childLeft = leftStart;
          for (int j = 0; j < mColumns; j++) {
            index = i * mColumns + j;
            if (index >= unselectCount) {
              break;
            }
            if (j > 0) {
              childLeft += mHorizontalPadding;
            }
            View child = unselectList.get(index);
            LayoutParams layoutParams = (LayoutParams) child.getLayoutParams();
            if (mDraggingView == null) {
              itemAnimation(child, layoutParams.leftMargin, layoutParams.topMargin, childLeft,
                  childTop);
              layoutParams.leftMargin = childLeft;
              layoutParams.topMargin = childTop;
            }
            child.layout(childLeft, childTop, childLeft + mItemWidth, childTop + mItemHeight);
            childLeft += mItemWidth;
          }
          childTop += mItemHeight;
          if (i < unselectRowCount - 1) {
            childTop += mVerticalPadding;
          }
        }
      }
    }
  }

  @Override public boolean dispatchTouchEvent(MotionEvent event) {
    switch (event.getAction()) {
      case MotionEvent.ACTION_DOWN:
        mLastX = event.getX();
        mLastY = event.getY();
        break;
      case MotionEvent.ACTION_MOVE:
        if (mDraggingView != null) {
          getParent().requestDisallowInterceptTouchEvent(true);
          updateDraggingViewPosition((int) (event.getX() - mLastX), (int) (event.getY() - mLastY));

          checkNeedRequestDisallowInterceptTouchEvent();
          mLastX = event.getX();
          mLastY = event.getY();
          int position = computeDragPosition((int) mLastX, (int) mLastY);
          if (mDraggingPosition != position) {
            selectList.remove(mDraggingView);
            selectList.add(position, mDraggingView);
            mAdapter.reAdd(mDraggingPosition, position);
            mDraggingPosition = position;
            performItemAnimation();
          }
        }
        break;
      case MotionEvent.ACTION_UP:
      default:
        if (mDraggingView != null) {
          getParent().requestDisallowInterceptTouchEvent(false);
          mDraggingView.requestLayout();
          mDraggingView = null;
        }
        break;
    }
    return super.dispatchTouchEvent(event);
  }

  private void checkNeedRequestDisallowInterceptTouchEvent() {
    if (mDraggingView != null) {
      LayoutParams layoutParams = (LayoutParams) mDraggingView.getLayoutParams();
      if (mVisibleRect == null) {
        mVisibleRect = new Rect();
      }
      getLocalVisibleRect(mVisibleRect);
      if (layoutParams.topMargin == mVisibleRect.top) {
        ((DragScrollView) getParent()).scrollBy(0, -30);
      } else if (layoutParams.topMargin == mVisibleRect.bottom - mItemHeight) {
        ((DragScrollView) getParent()).scrollBy(0, 30);
      }
    }
  }

  private void updateDraggingViewPosition(int deltaX, int dletaY) {
    int leftMargin = ((LayoutParams) mDraggingView.getLayoutParams()).leftMargin + deltaX;
    leftMargin = Math.max(getPaddingLeft(), leftMargin);
    leftMargin = Math.min(getWidth() - mItemWidth - getPaddingRight(), leftMargin);

    if (mVisibleRect == null) {
      mVisibleRect = new Rect();
    }

    getLocalVisibleRect(mVisibleRect);
    int topMargin = ((LayoutParams) mDraggingView.getLayoutParams()).topMargin + dletaY;
    topMargin = Math.max(mVisibleRect.top + getPaddingTop(), topMargin);
    topMargin = Math.min(mVisibleRect.bottom - mItemHeight - getPaddingBottom(), topMargin);

    ((LayoutParams) mDraggingView.getLayoutParams()).leftMargin = leftMargin;
    ((LayoutParams) mDraggingView.getLayoutParams()).topMargin = topMargin;
    mDraggingView.requestLayout();
  }

  @Override protected void onFinishInflate() {
    super.onFinishInflate();
    if (getChildCount() != 2) {
      throw new IllegalArgumentException("must have two title child");
    }
    mSelectTitleView = getChildAt(0);
    mUnselectTitleView = getChildAt(1);
  }

  public static class LayoutParams extends MarginLayoutParams {

    public LayoutParams() {
      super(MATCH_PARENT, MATCH_PARENT);
    }

    public LayoutParams(Context c, AttributeSet attrs) {
      super(c, attrs);
    }

    public LayoutParams(int width, int height) {
      super(width, height);
    }

    public LayoutParams(MarginLayoutParams source) {
      super(source);
    }

    public LayoutParams(ViewGroup.LayoutParams source) {
      super(source);
    }
  }
}
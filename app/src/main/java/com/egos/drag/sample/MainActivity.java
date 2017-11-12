package com.egos.drag.sample;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import com.astuetz.PagerSlidingTabStrip;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Egos on 2017/11/7.
 */

public class MainActivity extends AppCompatActivity {

  private final static String TAG = "MainActivity";

  private final static int REQUEST_CHANGE = 1;

  private ViewPager container;
  private PagerSlidingTabStrip pagerSlidingTabStrip;

  private List<String> undragLst;
  private List<String> selectLst;
  private List<String> unselectLst;

  private MyAdapter mAdapter;

  private int currentPosition;
  private String currentItem;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    container = (ViewPager) findViewById(R.id.container);
    pagerSlidingTabStrip = (PagerSlidingTabStrip) findViewById(R.id.pagerSlidingTabStrip);
    FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

    initData(savedInstanceState);

    mAdapter = new MyAdapter(getSupportFragmentManager());
    container.setAdapter(mAdapter);

    pagerSlidingTabStrip.setViewPager(container);

    fab.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        Intent intent = new Intent(MainActivity.this, DragActivity.class);
        intent.putExtra(DragActivity.EXTRA_SELECT_LIST, (Serializable) selectLst);
        intent.putExtra(DragActivity.EXTRA_UNSELECT_LIST, (Serializable) unselectLst);
        intent.putExtra(DragActivity.EXTRA_UNDRAG_LIST, (Serializable) undragLst);
        startActivityForResult(intent, REQUEST_CHANGE);

        currentPosition = container.getCurrentItem();
        currentItem = selectLst.get(currentPosition);
      }
    });
  }

  @Override protected void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    outState.putSerializable(DragActivity.EXTRA_UNDRAG_LIST, (Serializable) undragLst);
    outState.putSerializable(DragActivity.EXTRA_SELECT_LIST, (Serializable) selectLst);
    outState.putSerializable(DragActivity.EXTRA_UNSELECT_LIST, (Serializable) unselectLst);
  }

  @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

    if (resultCode == RESULT_OK && requestCode == REQUEST_CHANGE) {
      undragLst = (List<String>) data.getSerializableExtra(DragActivity.EXTRA_UNDRAG_LIST);
      selectLst = (List<String>) data.getSerializableExtra(DragActivity.EXTRA_SELECT_LIST);
      unselectLst = (List<String>) data.getSerializableExtra(DragActivity.EXTRA_UNSELECT_LIST);

      mAdapter.notifyDataSetChanged();

      if (selectLst.contains(currentItem)) {
        container.setCurrentItem(selectLst.indexOf(currentItem));
      } else if (selectLst.size() > currentPosition) {
        container.setCurrentItem(currentPosition);
      } else {
        container.setCurrentItem(selectLst.size() - 1);
      }

      pagerSlidingTabStrip.notifyDataSetChanged();
    }
  }

  private void initData(Bundle savedInstanceState) {
    if (savedInstanceState != null) {
      undragLst = (List<String>) savedInstanceState.getSerializable(DragActivity.EXTRA_UNDRAG_LIST);
      selectLst = (List<String>) savedInstanceState.getSerializable(DragActivity.EXTRA_SELECT_LIST);
      unselectLst =
          (List<String>) savedInstanceState.getSerializable(DragActivity.EXTRA_UNSELECT_LIST);
      return;
    }

    undragLst = new ArrayList<>();
    undragLst.add("推荐");
    undragLst.add("热门");

    selectLst = new ArrayList<>();
    selectLst.add("头条");
    selectLst.add("社会");
    selectLst.add("军事");
    selectLst.add("历史");
    selectLst.add("影视");
    selectLst.add("独家");
    selectLst.add("航空");
    selectLst.add("要闻");
    selectLst.add("娱乐");
    selectLst.add("音乐");

    unselectLst = new ArrayList<>();
    unselectLst.add("贴吧");
    unselectLst.add("美女");
    unselectLst.add("薄荷");
    unselectLst.add("萌宠");
    unselectLst.add("问吧");

    selectLst.addAll(0, undragLst);
  }

  private class MyAdapter extends FragmentStatePagerAdapter {

    MyAdapter(FragmentManager fm) {
      super(fm);
    }

    @Override public Fragment getItem(int position) {
      return PlaceholderFragment.newInstance(getPageTitle(position).toString());
    }

    @Override public int getItemPosition(Object object) {
      return POSITION_NONE;
    }

    @Override public int getCount() {
      return selectLst == null ? 0 : selectLst.size();
    }

    @Override public CharSequence getPageTitle(int position) {
      return selectLst == null ? null : selectLst.get(position);
    }
  }
}

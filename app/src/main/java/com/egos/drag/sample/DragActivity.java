package com.egos.drag.sample;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import com.egos.drag.DragView;
import java.io.Serializable;
import java.util.List;

/**
 * Created by Egos on 2017/11/7.
 */

public class DragActivity extends AppCompatActivity {

  private final static String TAG = "DragActivity";

  public final static String EXTRA_UNDRAG_LIST = "extra_undrag_list";
  public final static String EXTRA_SELECT_LIST = "extra_select_list";
  public final static String EXTRA_UNSELECT_LIST = "extra_unselect_list";

  private List<String> undragLst;
  private List<String> selectLst;
  private List<String> unselectLst;

  DragView dragView;

  private MyDragAdapter mAdapter;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_drag);
    dragView = (DragView) findViewById(R.id.dragView);

    undragLst = savedInstanceState != null ? (List<String>) savedInstanceState.getSerializable(
        EXTRA_UNDRAG_LIST) : (List<String>) getIntent().getSerializableExtra(EXTRA_UNDRAG_LIST);
    selectLst = savedInstanceState != null ? (List<String>) savedInstanceState.getSerializable(
        EXTRA_SELECT_LIST) : (List<String>) getIntent().getSerializableExtra(EXTRA_SELECT_LIST);
    unselectLst = savedInstanceState != null ? (List<String>) savedInstanceState.getSerializable(
        EXTRA_UNSELECT_LIST) : (List<String>) getIntent().getSerializableExtra(EXTRA_UNSELECT_LIST);

    mAdapter = new MyDragAdapter(this, undragLst, selectLst, unselectLst);
    dragView.setAdapter(mAdapter);
  }

  @Override public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    outState.putSerializable(EXTRA_UNDRAG_LIST, (Serializable) undragLst);
    outState.putSerializable(EXTRA_SELECT_LIST, (Serializable) selectLst);
    outState.putSerializable(EXTRA_UNSELECT_LIST, (Serializable) unselectLst);
  }

  @Override public void onBackPressed() {
    Intent intent = new Intent();
    intent.putExtra(DragActivity.EXTRA_SELECT_LIST, (Serializable) selectLst);
    intent.putExtra(DragActivity.EXTRA_UNSELECT_LIST, (Serializable) unselectLst);
    intent.putExtra(DragActivity.EXTRA_UNDRAG_LIST, (Serializable) undragLst);
    setResult(RESULT_OK, intent);
    finish();
  }
}

package com.egos.drag.sample;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by Egos on 2017/11/11.
 */

public class PlaceholderFragment extends Fragment {
  private static final String ARG_TITLE = "section_number";

  public PlaceholderFragment() {
  }

  public static PlaceholderFragment newInstance(String title) {
    PlaceholderFragment fragment = new PlaceholderFragment();
    Bundle args = new Bundle();
    args.putString(ARG_TITLE, title);
    fragment.setArguments(args);
    return fragment;
  }

  @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    View rootView = inflater.inflate(R.layout.fragment_main, container, false);
    TextView textView = (TextView) rootView.findViewById(R.id.section_label);
    textView.setText(getArguments().getString(ARG_TITLE));
    return rootView;
  }
}
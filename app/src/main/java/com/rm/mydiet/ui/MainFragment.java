package com.rm.mydiet.ui;


import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rm.mydiet.R;
import com.rm.mydiet.model.DayPart;
import com.rm.mydiet.ui.adapter.DayPartsAdapter;
import com.rm.mydiet.utils.base.BaseFragment;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends BaseFragment {

    private RecyclerView mDayParts;
    private ArrayList<DayPart> mDayPartsList = new ArrayList<>();
    private DayPartsAdapter mDayPartsAdapter;

    public MainFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO init some logic part
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initDayParts();
    }

    private void initDayParts() {
        mDayParts = (RecyclerView) findViewById(R.id.day_parts);
        mDayParts.setLayoutManager(new GridLayoutManager(getActivity(), 4));
        mDayPartsList = getEmptyDayParts();
        mDayPartsAdapter = new DayPartsAdapter(mDayPartsList);
        mDayParts.setAdapter(mDayPartsAdapter);
    }

    public ArrayList<DayPart> getEmptyDayParts() {
        ArrayList<DayPart> emptyParts = new ArrayList<>();
        for (int i = 0; i < 4; i++) emptyParts.add(new DayPart(i));
        return emptyParts;
    }
}

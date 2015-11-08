package com.rm.mydiet.ui;


import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.rm.mydiet.R;
import com.rm.mydiet.utils.base.BaseFragment;

/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends BaseFragment {

    private ProgressBar mTimerProgress;
    private TextView mTimerCountDown;
    private TextView mTimerBadge;
    private RelativeLayout mTimerBox;
    private RecyclerView mDayParts;
    private ProgressBar mCalsProgress;
    private TextView mCalsText;
    private RelativeLayout mSkipTimer;
    private RelativeLayout mAddFood;
    private RelativeLayout mAddMore;
    private RelativeLayout mEatenBox;
    private RecyclerView mEatenList;
    private TextView mEatenText;

    public MainFragment() {
        // Required empty public constructor
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
        initTimer();
        initDayEaten();
        initFooter();
    }

    private void initDayEaten() {
        mEatenBox = (RelativeLayout) findViewById(R.id.day_eaten);
        mEatenList = (RecyclerView) findViewById(R.id.day_eaten_list);
        mEatenText = (TextView) findViewById(R.id.day_eaten_text);
    }

    private void initDayParts() {
        mDayParts = (RecyclerView) findViewById(R.id.day_parts);
    }

    private void initFooter() {
        mCalsProgress = (ProgressBar) findViewById(R.id.day_cals_left);
        mCalsText = (TextView) findViewById(R.id.day_cals_text);
        mSkipTimer = (RelativeLayout) findViewById(R.id.skip_timer_button);
        mAddFood = (RelativeLayout) findViewById(R.id.add_food_button);
        mAddMore = (RelativeLayout) findViewById(R.id.add_more_food_button);
    }

    private void initTimer() {
        mTimerProgress = (ProgressBar) findViewById(R.id.day_timer_progress);
        mTimerCountDown = (TextView) findViewById(R.id.day_timer_countdown);
        mTimerBadge = (TextView) findViewById(R.id.day_timer_badge);

        mTimerBox = (RelativeLayout) findViewById(R.id.day_timer);
    }

    private void selectDayPart(int dayPart) {

    }
}

package com.rm.mydiet.ui;


import android.app.Fragment;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.rm.mydiet.R;
import com.rm.mydiet.model.DayPart;
import com.rm.mydiet.utils.TimeUtil;

import static com.rm.mydiet.ui.MainFragment.KEY_DAY_CALORIES;
import static com.rm.mydiet.ui.MainFragment.KEY_DAY_PART;

/**
 * A simple {@link Fragment} subclass.
 */
public class TimerFragment extends TimelineFragment {

    private ProgressBar mTimerProgress;
    private TextView mTimerBadge;
    private TextView mTimerAdvice;
    private TextView mTimerCdText;
    private DayPart mCurrentDayPart;
    private long mTimerOffset;
    private long mDayStart;
    private CountDownTimer mCountDown;
    private int mCalories;

    public TimerFragment() {
        // Required empty public constructor
    }

    public static TimerFragment newInstance(DayPart dayPart, int cals) {
        Bundle arguments = new Bundle();
        arguments.putParcelable(KEY_DAY_PART, dayPart);
        arguments.putInt(MainFragment.KEY_DAY_CALORIES, cals);
        TimerFragment fragment = new TimerFragment();
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_timer, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mCurrentDayPart = (DayPart) getArguments().getParcelable(KEY_DAY_PART);
        mCalories = getArguments().getInt(KEY_DAY_CALORIES);
        if (mCurrentDayPart != null) {
            mDayStart = TimeUtil.getToday();
            mTimerOffset = mCurrentDayPart.getTimerOffset();
        } else {
            throw new RuntimeException("Null dayPart");
        }

        mTimerProgress = (ProgressBar) findViewById(R.id.day_timer_progress);
        mTimerBadge = (TextView) findViewById(R.id.day_timer_badge);
        mTimerAdvice = (TextView) findViewById(R.id.day_timer_advice);
        mTimerCdText = (TextView) findViewById(R.id.day_timer_countdown);
        initTimerProgress();
    }

    private void initTimerProgress() {
        long future = mTimerOffset - TimeUtil.unixTime();
        mTimerBadge.setText(getBadgeMessage());
//        mTimerAdvice.setText("");

        if (mCountDown != null) mCountDown = null;
        mCountDown = new CountDownTimer(future, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {
                mTimerCdText.setText(TimeUtil.formatCountDown(millisUntilFinished));
                mTimerProgress.setProgress(TimeUtil.calculateTimerProgress(
                        mDayStart,
                        mDayStart + mTimerOffset,
                        millisUntilFinished
                ));
            }

            @Override
            public void onFinish() {
                mInteractionListener.onFragmentAction(null, MainFragment.FRAGMENT_TIMER);
            }
        };
    }

    private String getBadgeMessage() {
        switch (mCurrentDayPart.getPartId()) {
            case DayPart.PART_BREAKFAST: return "До завтрака";
            case DayPart.PART_LUNCH: return "До обеда";
            case DayPart.PART_SNACK: return "До полдника";
            case DayPart.PART_DINNER: return "До ужина";
            default: return "Error";
        }
    }
}

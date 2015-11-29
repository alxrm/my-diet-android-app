package com.rm.mydiet.ui;


import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.rm.mydiet.R;
import com.rm.mydiet.model.DayPart;
import com.rm.mydiet.utils.Prefs;
import com.rm.mydiet.utils.TimeUtil;

import static com.rm.mydiet.ui.OnFragmentInteractionListener.FRAGMENT_TIMER;
import static com.rm.mydiet.utils.Prefs.KEY_MAX_CALS;

/**
 * A simple {@link Fragment} subclass.
 */
public class TimerFragment extends TimelineFragment {

    private CountDownTimer mCountDown;
    private int mCurrentCalories;
    private int mMaxCalories;

    private ProgressBar mTimerProgress;
    private TextView mTimerBadge;
    private TextView mTimerAdvice;
    private TextView mTimerCdText;
    private DayPart mCurrentDayPart;
    private long mTimerOffset;
    private long mDayStart;

    private RelativeLayout mSkipTimerButton;

    public TimerFragment() {
        // Required empty public constructor
    }

    public static TimerFragment newInstance(DayPart dayPart, int cals) {
        Bundle arguments = new Bundle();
        arguments.putParcelable(DataTransferring.FRAGMENT_TIMER_KEY_DAY_PART, dayPart);
        arguments.putInt(DataTransferring.FRAGMENT_TIMER_KEY_CALORIES, cals);
        TimerFragment fragment = new TimerFragment();
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
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
        mCurrentDayPart = (DayPart) getArguments().getParcelable(DataTransferring.FRAGMENT_TIMER_KEY_DAY_PART);
        mCurrentCalories = getArguments().getInt(DataTransferring.FRAGMENT_TIMER_KEY_CALORIES);
        mMaxCalories = Prefs.get().getInt(KEY_MAX_CALS, 2500);
        mDayStart = mCurrentDayPart.getDay();
        mTimerOffset = mCurrentDayPart.getTimerOffset();

        mTimerProgress = (ProgressBar) findViewById(R.id.day_timer_progress);
        mTimerBadge = (TextView) findViewById(R.id.day_timer_badge);
        mTimerAdvice = (TextView) findViewById(R.id.day_timer_advice);
        mTimerCdText = (TextView) findViewById(R.id.day_timer_countdown);
        mSkipTimerButton = (RelativeLayout) findViewById(R.id.skip_timer_button);
        mSkipTimerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopTimer(false);
            }
        });

        initTimerProgress();
        initCaloriesViews();
        setCaloriesProgress(mCurrentCalories, mMaxCalories);
    }

    private void initTimerProgress() {
        long future = (mTimerOffset + mDayStart) - TimeUtil.unixTime();
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
                stopTimer(true);
            }
        };
        mCountDown.start();
    }

    private void stopTimer(boolean isFinish) {
        if (!isFinish) mCountDown.cancel();
        mInteractionListener.onFragmentAction(mCurrentDayPart.getPartId(), FRAGMENT_TIMER);
    }

    @Override
    public void onDestroy() {
        if (mCountDown != null) {
            mCountDown.cancel();
            mCountDown = null;
        }
        super.onDestroy();
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

package com.rm.mydiet.ui;


import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.rm.mydiet.R;
import com.rm.mydiet.utils.KeyBoardUtil;
import com.rm.mydiet.utils.base.BaseFragment;

import java.util.ArrayList;
import java.util.HashMap;

import static com.rm.mydiet.utils.TimeUtil.DAY_MILLIES;
import static com.rm.mydiet.utils.TimeUtil.compareMillis;
import static com.rm.mydiet.utils.TimeUtil.formatTimelineDate;
import static com.rm.mydiet.utils.TimeUtil.getStartOfTheDay;
import static com.rm.mydiet.utils.TimeUtil.getToday;
import static com.rm.mydiet.utils.TimeUtil.isToday;

/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends BaseFragment implements OnFragmentInteractionListener {

    private ViewPager mDayPager;
    private DayPagerAdapter mDayPagerAdapter;
    private ArrayList<Long> mDayList = new ArrayList<>();
    private HashMap<Long, DayFragment> mPages = new HashMap<>();

    private TextView mCurrentDayText;
    private ImageView mPrevDayBtn;
    private ImageView mNextDayBtn;

    private long mStartingPoint;
    private int mCurrentPosition;
    private ProgressBar mTopProgress;

    public MainFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        KeyBoardUtil.hide(getActivity());
        for (int i = 0; i < 10; i++) {
            mDayList.add(getStartOfTheDay(getToday() - (DAY_MILLIES * i)));
        }
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

        mCurrentDayText = (TextView) findViewById(R.id.day_current);
        mNextDayBtn = (ImageView) findViewById(R.id.next_day_button);
        mPrevDayBtn = (ImageView) findViewById(R.id.previous_day_button);
        mDayPager = (ViewPager) findViewById(R.id.pager_day);
        mTopProgress = (ProgressBar) mParent.getToolbar().findViewById(R.id.day_cals_left);

        mStartingPoint = getToday();
        mCurrentPosition = mDayList.size() - 1;

        mDayPagerAdapter = new DayPagerAdapter(getChildFragmentManager());

        mDayPager.setAdapter(mDayPagerAdapter);
        mDayPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                mCurrentPosition = position;
                long dayStart = mDayList.get(getFormattedPosition(position));
                DayFragment page = mPages.get(dayStart);

                if (page != null) {
                    mTopProgress.setProgress(page.getCaloriesProgress());
                }

                mCurrentDayText.setText(formatTimelineDate(dayStart));
                mNextDayBtn.setVisibility(isToday(dayStart) ?
                                View.INVISIBLE : View.VISIBLE
                );
                mPrevDayBtn.setVisibility(mCurrentPosition == 0 ?
                                View.INVISIBLE : View.VISIBLE
                );
            }
        });
        mDayPager.setCurrentItem(mCurrentPosition);

        mNextDayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrentPosition++;
                mDayPager.setCurrentItem(mCurrentPosition, true);
            }
        });

        mPrevDayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrentPosition--;
                mDayPager.setCurrentItem(mCurrentPosition, true);
            }
        });

    }

    int getFormattedPosition(int pos) {
        return Math.abs(pos - (mDayList.size() - 1));
    }

    @Override
    public <T> void onFragmentAction(T data, String key) {
        if (key.equals(DataTransferring.CALLBACK_DAY_WRAPPER)) {
            Bundle update = (Bundle) data;
            long day = update.getLong(DataTransferring.CALLBACK_DAY_KEY_DAY);
            int calsProgress = update.getInt(DataTransferring.CALLBACK_DAY_KEY_CALORIES_PROGRESS);
            int position = getFormattedPosition(mDayPager.getCurrentItem());
            long currentDay = mDayList.get(position);

            if (compareMillis(day, currentDay)) {
                mTopProgress.setProgress(calsProgress);
            }
        }
    }

    private class DayPagerAdapter extends FragmentStatePagerAdapter {

        public DayPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            long dayStart = mDayList.get(getFormattedPosition(position));
            DayFragment dayFragment = mPages.get(dayStart);

            if (dayFragment == null) {
                dayFragment = DayFragment.newInstance(dayStart);
                dayFragment.setInteractionListener(MainFragment.this);
                mPages.put(dayStart, dayFragment);
            }

            return dayFragment;
        }

        @Override
        public int getCount() {
            return mDayList.size();
        }
    }
}

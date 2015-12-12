package com.rm.mydiet.ui;


import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.rm.mydiet.R;
import com.rm.mydiet.utils.KeyBoardUtil;
import com.rm.mydiet.utils.base.BaseFragment;

import java.util.ArrayList;

import static com.rm.mydiet.utils.TimeUtil.DAY_MILLIES;
import static com.rm.mydiet.utils.TimeUtil.formatTimelineDate;
import static com.rm.mydiet.utils.TimeUtil.getStartOfTheDay;
import static com.rm.mydiet.utils.TimeUtil.getToday;
import static com.rm.mydiet.utils.TimeUtil.isToday;

/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends BaseFragment {

    private ViewPager mDayPager;
    private ArrayList<Long> mDayList = new ArrayList<>();
    private TextView mCurrentDayText;
    private ImageView mPrevDayBtn;
    private ImageView mNextDayBtn;

    private long mStartingPoint;
    private int mCurrentPosition;

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
        mStartingPoint = getToday();
        mCurrentPosition = mDayList.size() - 1;
        mDayPager = (ViewPager) findViewById(R.id.pager_day);
        mDayPager.setOffscreenPageLimit(2);
        mDayPager.setAdapter(new DayPagerAdapter(getChildFragmentManager()));
        mDayPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mCurrentPosition = position;
                long dayStart = mDayList.get(getFormattedPosition(position));
                mCurrentDayText.setText(formatTimelineDate(dayStart));
                mNextDayBtn.setVisibility(isToday(dayStart) ?
                                View.INVISIBLE : View.VISIBLE
                );
                mPrevDayBtn.setVisibility(mCurrentPosition == 0 ?
                                View.INVISIBLE : View.VISIBLE
                );
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        mDayPager.setCurrentItem(mCurrentPosition);

        mNextDayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrentPosition++;
                mDayPager.setCurrentItem(mCurrentPosition);
            }
        });

        mPrevDayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrentPosition--;
                Log.d("MainFragment", "curpos " + mCurrentPosition);
                mDayPager.setCurrentItem(mCurrentPosition);
            }
        });
    }

    int getFormattedPosition(int pos) {
        return Math.abs(pos - (mDayList.size() - 2));
    }

    private class DayPagerAdapter extends FragmentPagerAdapter {

        public DayPagerAdapter(FragmentManager fm) {
            super(fm);
            Log.d("DayPagerAdapter", "DayPagerAdapter");
        }

        @Override
        public Fragment getItem(int position) {
            long dayStart = mDayList.get(getFormattedPosition(position));
            Log.d("DayPagerAdapter", "time " + formatTimelineDate(dayStart));
            return DayFragment.newInstance(dayStart);
        }

        @Override
        public int getCount() {
            return mDayList.size() - 1;
        }
    }
}

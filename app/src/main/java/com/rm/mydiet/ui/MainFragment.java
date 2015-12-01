package com.rm.mydiet.ui;


import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.rm.mydiet.R;
import com.rm.mydiet.utils.TimeUtil;
import com.rm.mydiet.utils.base.BaseFragment;

import java.util.ArrayList;

import static com.rm.mydiet.utils.TimeUtil.DAY_MILLIES;
import static com.rm.mydiet.utils.TimeUtil.getStartOfTheDay;

/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends BaseFragment {

    private ViewPager mDayPager;
    private ArrayList<Long> mDayList = new ArrayList<>();
    private TextView mCurrentDayText;

    private long mStartingPoint;

    public MainFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        for (int i = 0; i < 10; i++) {
            mDayList.add(getStartOfTheDay(TimeUtil.getToday() - (DAY_MILLIES * i)));
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
        mDayPager = (ViewPager) findViewById(R.id.pager_day);
        mDayPager.setOffscreenPageLimit(2);
        mDayPager.setAdapter(new DayPagerAdapter(getFragmentManager(), TimeUtil.getToday()));
        mDayPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                long dayStart = mDayList.get(getFormattedPosition(position));
                mCurrentDayText.setText(TimeUtil.formatTimelineDate(dayStart));
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        mDayPager.setCurrentItem(mDayList.size() - 1);
    }

    int getFormattedPosition(int pos) {
        return Math.abs(pos - (mDayList.size() - 2));
    }

    private class DayPagerAdapter extends FragmentPagerAdapter {

        private static final int LENGTH = Integer.MAX_VALUE / 2;
        private static final int MIDDLE = LENGTH / 2;

        public DayPagerAdapter(FragmentManager fm, long startingPoint) {
            super(fm);
            mStartingPoint = startingPoint;
        }

        @Override
        public Fragment getItem(int position) {
//            Log.d("DayPagerAdapter", "pos: " + Math.abs(position - (mDayList.size() - 1)));
            long dayStart = mDayList.get(getFormattedPosition(position));
            return DayFragment.newInstance(dayStart);
        }

        @Override
        public int getCount() {
            return mDayList.size() - 1;
        }

//        @Override
//        public void destroyItem(ViewGroup container, int position, Object object) {
//            if (position < 2) {
//                super.destroyItem(container, position, object);
//            } else {
//                super.destroyItem(container, LENGTH - position - 1, object);
//            }
//        }
//
//        @Override
//        public Object instantiateItem(ViewGroup container, int position) {
//            if (position < 2) {
//                return super.instantiateItem(container, position);
//            } else {
//                return super.instantiateItem(container, LENGTH - position - 1);
//            }
//        }
    }
}

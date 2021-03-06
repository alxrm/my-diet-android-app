package com.rm.mydiet.ui;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.rm.mydiet.R;
import com.rm.mydiet.utils.CalculateUtils;
import com.rm.mydiet.utils.base.BaseFragment;

/**
 * Created by alex
 */
public class TimelineFragment extends BaseFragment {

    // TODO aggressive refactoring

    protected ProgressBar mCalsProgress;
    protected TextView mCalsText;
    protected LinearLayout mCalsBox;
    protected OnFragmentInteractionListener mInteractionListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mInteractionListener = (DayFragment) getParentFragment();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initCaloriesViews();
    }

    protected void setCaloriesProgress(int curCals, int maxCals) {
        int calsProgress = CalculateUtils.calcProgress(curCals, maxCals);

        if (mCalsProgress != null && mCalsText != null) {
            mCalsProgress.setProgress(calsProgress);
            mCalsText.setText(String.format("%d/%d", curCals, maxCals));
        } else {
            Log.e("TimelineFragment", "NULL PROGRESSES");
        }
    }

    protected void initCaloriesViews() {
        mCalsBox = (LinearLayout) findViewById(R.id.day_cals);
        mCalsProgress = (ProgressBar) findViewById(R.id.day_cals_left);
        mCalsText = (TextView) findViewById(R.id.day_cals_text);
    }
}

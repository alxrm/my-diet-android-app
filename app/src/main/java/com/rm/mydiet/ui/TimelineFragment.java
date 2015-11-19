package com.rm.mydiet.ui;

import android.content.Context;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.rm.mydiet.utils.base.BaseFragment;

/**
 * Created by alex
 */
public class TimelineFragment extends BaseFragment {

    protected ProgressBar mCalsProgress;
    protected TextView mCalsText;
    protected OnFragmentInteractionListener mInteractionListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d("TimelineFragment", "onAttach");
        mInteractionListener = (MainFragment) getParentFragment();
    }

    protected void setCaloriesProgress(int curCals, int maxCals) {
        int calsProgress = (int) ((float) curCals / maxCals * 100);
        Log.d("DiaryFragment", "setCaloriesProgress calsProgress " + calsProgress);

        if (mCalsProgress != null && mCalsText != null) {
            mCalsProgress.setProgress(calsProgress);
            mCalsText.setText(String.format("%d/%d", curCals, maxCals));
        }
    }
}

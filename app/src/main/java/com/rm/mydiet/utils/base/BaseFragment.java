package com.rm.mydiet.utils.base;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;

/**
 * Created by alex
 */
public class BaseFragment extends Fragment {

    protected View mRootView;
    protected BaseActivity mParent;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mParent = (BaseActivity) getActivity();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRootView = view;
    }

    public View findViewById(int layoutId){
        return mRootView.findViewById(layoutId);
    }
}

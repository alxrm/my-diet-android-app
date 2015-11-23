package com.rm.mydiet.utils.base;

import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.view.View;

import com.rm.mydiet.ui.OnFragmentInteractionListener;

/**
 * Created by alex
 */
public class BaseFragment extends Fragment {

    protected View mRootView;
    protected BaseActivity mParent;
    protected String mTitle;
    protected OnFragmentInteractionListener mInteractionListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mParent = (BaseActivity) getActivity();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRootView = view;
        if (mParent != null && mParent.getToolbar() != null) {
            mParent.getToolbar().setVisibility(View.VISIBLE);
            mParent.getToolbar().setTitle(mTitle);
        }
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public View findViewById(int layoutId){
        return mRootView.findViewById(layoutId);
    }

    public void setInteractionListener(OnFragmentInteractionListener interactionListener) {
        mInteractionListener = interactionListener;
    }
}

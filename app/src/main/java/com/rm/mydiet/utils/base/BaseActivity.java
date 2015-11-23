package com.rm.mydiet.utils.base;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.rm.mydiet.R;

/**
 * Created by alex
 */
public abstract class BaseActivity extends AppCompatActivity {

    protected Toolbar mToolbar;
    protected FragmentManager mFragmentManager;

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mFragmentManager = getFragmentManager();
        onCreateSupportiveViews();
        onToolbarCreated();
        onDrawerCreated();
    }

    protected abstract void onCreateSupportiveViews();
    protected abstract void onToolbarCreated();
    protected abstract void onDrawerCreated();
    public abstract Bundle getParentData();

    public Toolbar getToolbar() {
        return mToolbar;
    }

    public void switchFragment(final BaseFragment fragment,
                               final String title) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                FragmentTransaction transaction = mFragmentManager.beginTransaction();
                fragment.setTitle(title);
                transaction
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .replace(R.id.container, fragment);
                transaction.commit();
            }
        }, 200);
    }

    public void addFragment(final BaseFragment fragment, final String title) {
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        fragment.setTitle(title);
        transaction
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .replace(R.id.container, fragment)
                .addToBackStack(null);
        transaction.commit();

        Log.d("BaseActivity", "addFragment backStack: " + mFragmentManager.getBackStackEntryCount());
    }

    @Override
    public void onBackPressed() {
        if (mFragmentManager.getBackStackEntryCount() > 0) {
            mFragmentManager.popBackStack();
        } else {
            super.onBackPressed();
        }
    }
}

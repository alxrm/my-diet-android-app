package com.rm.mydiet.utils.base;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.rm.mydiet.R;
import com.rm.mydiet.ui.MainFragment;
import com.rm.mydiet.ui.SettingsFragment;
import com.rm.mydiet.ui.StatsFragment;

/**
 * Created by alex
 */
public class BaseActivity extends AppCompatActivity {

    protected Toolbar mToolbar;
    private NavigationView mNavigationView;
    private DrawerLayout mDrawerLayout;
    private FragmentManager mFragmentManager;

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mNavigationView = (NavigationView) findViewById(R.id.drawer_view);
        mFragmentManager = getFragmentManager();

        if (mToolbar != null) {
            mToolbar.setNavigationIcon(R.drawable.ic_action_navigation_menu);
            mToolbar.setTitle("Мои калории");
            mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mDrawerLayout.openDrawer(mNavigationView);
                }
            });
        }

        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                if (item.isChecked()) return false;
                mDrawerLayout.closeDrawers();

                switch (item.getItemId()) {
                    case R.id.navigation_calories: {
                        switchFragment(new MainFragment(), "Мои калории");
                        return true;
                    }
                    case R.id.navigation_stats: {
                        switchFragment(new StatsFragment(), "Статистика");
                        return true;
                    }
                    case R.id.navigation_settings: {
                        switchFragment(new SettingsFragment(), "Настройки");
                        return true;
                    }
                }
                return false;
            }
        });
    }

    public Toolbar getToolbar() {
        return mToolbar;
    }

    protected void switchFragment(BaseFragment fragment, String title) {
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        fragment.setTitle(title);
        transaction
                .replace(R.id.container, fragment)
                .commit();
    }
}

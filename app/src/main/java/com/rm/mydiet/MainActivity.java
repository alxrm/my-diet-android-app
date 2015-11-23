package com.rm.mydiet;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.rm.mydiet.ui.MainFragment;
import com.rm.mydiet.ui.SettingsFragment;
import com.rm.mydiet.ui.StatsFragment;
import com.rm.mydiet.utils.base.BaseActivity;
import com.rm.mydiet.utils.persistence.DatabaseManager;


public class MainActivity extends BaseActivity {

    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        MainFragment fragment = new MainFragment();
        switchFragment(fragment, null);
        inflateData();
    }

    private void inflateData() {
        DatabaseManager mgr = DatabaseManager.getInstance();
        mgr.update();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreateSupportiveViews() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mNavigationView = (NavigationView) findViewById(R.id.drawer_view);
    }

    @Override
    protected void onToolbarCreated() {
        mToolbar.setNavigationIcon(R.drawable.ic_action_navigation_menu);
        mToolbar.setTitle("Мои калории");
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawerLayout.openDrawer(mNavigationView);
            }
        });
    }

    @Override
    protected void onDrawerCreated() {
        mNavigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem item) {
                        if (item.isChecked()) return false;
                        mDrawerLayout.closeDrawers();

                        switch (item.getItemId()) {
                            case R.id.navigation_calories: {
                                switchFragment(new MainFragment(), null);
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

    @Override
    public Bundle getParentData() {
        return null;
    }
}

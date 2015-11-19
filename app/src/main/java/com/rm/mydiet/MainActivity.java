package com.rm.mydiet;

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.rm.mydiet.ui.MainFragment;
import com.rm.mydiet.utils.base.BaseActivity;
import com.rm.mydiet.utils.persistence.DatabaseManager;


public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        MainFragment fragment = new MainFragment();
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        fragment.setTitle(null);
        transaction.replace(R.id.container, fragment).commit();
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
}

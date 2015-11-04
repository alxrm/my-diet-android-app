package com.rm.mydiet;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.rm.mydiet.model.Product;
import com.rm.mydiet.utils.UserPermissionListener;
import com.rm.mydiet.utils.persistence.DatabaseProvider;
import com.rm.mydiet.utils.persistence.DatabaseUpdateHelper;
import com.rm.mydiet.utils.persistence.listeners.DatabaseResponseListener;
import com.rm.mydiet.utils.persistence.listeners.DatabaseUpdateListener;

import java.util.ArrayList;
import java.util.Collection;

import static com.rm.mydiet.MyDietApplication.app;


public class MainActivity extends AppCompatActivity
        implements
        DatabaseUpdateListener,
        DatabaseResponseListener {

    private ArrayList<Product> mProducts;
    private RecyclerView mListView;
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mProducts = new ArrayList<>();
        mProgressBar = (ProgressBar) findViewById(R.id.progress_indicator);
        mListView = (RecyclerView) findViewById(R.id.items);
        mListView.setLayoutManager(new LinearLayoutManager(this));
        checkForUpdates();
    }

    private void checkForUpdates() {
        DatabaseUpdateHelper updateHelper = new DatabaseUpdateHelper(this);
        updateHelper.tryUpdate();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onUpdateAvailable(UserPermissionListener listener) {
        listener.onGrant();
    }

    @Override
    public void onLoadComplete() {
        Log.d("MainActivity", "onLoadComplete");
        mListView.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.GONE);
        DatabaseProvider.retrieveProducts(app(), this);
    }

    @Override
    public void onLoadStarted() {
        mListView.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.VISIBLE);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onReceiveData(Collection<?> data) {
        mProducts = (ArrayList<Product>) data;
        mListView.setAdapter(new ProductsAdapter(mProducts));
    }

    @Override
    public void onError(Exception e) {
        Log.d("MainActivity", "onError err: " + e);
    }
}

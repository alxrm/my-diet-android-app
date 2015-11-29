package com.rm.mydiet.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.rm.mydiet.R;
import com.rm.mydiet.utils.base.BaseActivity;

public class AddProductActivity extends BaseActivity implements OnFragmentInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("AddProductActivity", "onCreate");
        setContentView(R.layout.box_generic_activity);
        ProductListFragment productListFragment = new ProductListFragment();
        switchFragment(productListFragment, null);
    }

    @Override
    protected void onCreateSupportiveViews() {

    }

    @Override
    protected void onToolbarCreated() {
        // TODO implement this
    }

    @Override
    protected void onDrawerCreated() {

    }

    @Override
    public Bundle getParentData() {
        Bundle data = new Bundle();
        int part = getIntent().getIntExtra(DataTransferring.ACTIVITY_ADD_KEY_DAY_PART, 0);
        long time = getIntent().getLongExtra(DataTransferring.ACTIVITY_ADD_KEY_TIME, 0);
        data.putInt(DataTransferring.PARENT_PRODUCT_INFO_DAY_PART, part);
        data.putLong(DataTransferring.PARENT_PRODUCT_INFO_TIME, time);
        return data;
    }

    @Override
    public <T> void onFragmentAction(T data, String key) {
        Intent callbackIntent = new Intent();
        callbackIntent.putExtra(DataTransferring.ACTIVITY_ADD_KEY_RESULT_DATA, (Bundle) data);
        setResult(DataTransferring.ACTIVITY_ADD_CODE_RESULT, callbackIntent);
        finish();
    }
}

package com.rm.mydiet.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.rm.mydiet.R;
import com.rm.mydiet.model.EatenProduct;
import com.rm.mydiet.utils.base.BaseActivity;

public class EditProductActivity extends BaseActivity implements OnFragmentInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.box_generic_activity);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(false);

        EatenProduct eatenProduct = getIntent().getParcelableExtra(DataTransferring.ACTIVITY_EDIT_KEY_EATEN_PRODUCT);
        int dayPart = getIntent().getIntExtra(DataTransferring.ACTIVITY_EDIT_KEY_DAY_PART, 0);
        ProductInfoFragment fragment = ProductInfoFragment.newInstance(eatenProduct, dayPart);

        switchFragment(fragment, "Информация о продукте");
    }

    @Override
    protected void onCreateSupportiveViews() {

    }

    @Override
    protected void onToolbarCreated() {
        getToolbar().findViewById(R.id.day_cals_left).setVisibility(View.GONE);
        setTitle("");
        setSupportActionBar(getToolbar());
    }

    @Override
    protected void onDrawerCreated() {

    }

    @Override
    public Bundle getParentData() {
        return null;
    }

    @Override
    public <T> void onFragmentAction(T data, String key) {
        Intent callbackIntent = new Intent();
        callbackIntent.putExtra(DataTransferring.ACTIVITY_EDIT_KEY_RESULT_DATA, (Bundle) data);
        setResult(DataTransferring.ACTIVITY_EDIT_CODE_RESULT, callbackIntent);
        finish();
    }
}

package com.rm.mydiet.ui;


import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.rm.mydiet.R;
import com.rm.mydiet.model.DayPart;
import com.rm.mydiet.model.EatenProduct;
import com.rm.mydiet.model.Product;
import com.rm.mydiet.ui.adapter.TimelineAdapter;
import com.rm.mydiet.utils.Prefs;
import com.rm.mydiet.utils.persistence.DatabaseListener;

import java.util.ArrayList;
import java.util.Collection;

import static com.rm.mydiet.ui.MainFragment.KEY_DAY_CALORIES;
import static com.rm.mydiet.ui.MainFragment.KEY_DAY_PART;
import static com.rm.mydiet.utils.Prefs.KEY_MAX_CALS;

/**
 * A simple {@link Fragment} subclass.
 */
public class DiaryFragment extends TimelineFragment
        implements View.OnClickListener, DatabaseListener {

    private TextView mNoProductsMessage;
    private DayPart mCurrentDayPart;
    private RelativeLayout mAddFood;
    private RelativeLayout mAddMore;
    private LinearLayout mCalsBox;

    private RecyclerView mProductsList;
    private ArrayList<EatenProduct> mEatenProducts;
    private TimelineAdapter mTimeLineAdapter;

    private int mCurrentCalories;
    private int mMaxCalories;

    public static DiaryFragment newInstance(DayPart dayPart, int cals) {
        Bundle arguments = new Bundle();
        arguments.putParcelable(KEY_DAY_PART, dayPart);
        arguments.putInt(MainFragment.KEY_DAY_CALORIES, cals);
        DiaryFragment fragment = new DiaryFragment();
        fragment.setArguments(arguments);
        return fragment;
    }

    public DiaryFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_diary, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mCurrentDayPart = (DayPart) getArguments().getParcelable(KEY_DAY_PART);
        mCurrentCalories = getArguments().getInt(KEY_DAY_CALORIES);
        mMaxCalories = Prefs.get().getInt(KEY_MAX_CALS, 2500);
        if (mCurrentDayPart != null) {
            mEatenProducts = mCurrentDayPart.getEatenProducts();
        } else {
            throw new RuntimeException("Null dayPart");
        }

        mProductsList = (RecyclerView) findViewById(R.id.day_eaten_list);
        mNoProductsMessage = (TextView) findViewById(R.id.day_eaten_text);
        boolean isEmpty = mEatenProducts.isEmpty();
        switchProductsList(isEmpty);
        switchFooter(isEmpty);

        mCalsBox = (LinearLayout) findViewById(R.id.day_cals);
        mCalsProgress = (ProgressBar) findViewById(R.id.day_cals_left);
        mCalsText = (TextView) findViewById(R.id.day_cals_text);
        setCaloriesProgress(mCurrentCalories, mMaxCalories);

        // same listener
        mAddFood = (RelativeLayout) findViewById(R.id.add_food_button);
        mAddMore = (RelativeLayout) findViewById(R.id.add_more_food_button);

        mAddFood.setOnClickListener(this);
        mAddMore.setOnClickListener(this);
    }

    private void switchProductsList(boolean isEmpty) {
        mProductsList.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
        mNoProductsMessage.setVisibility(isEmpty ? View.VISIBLE : View.GONE);

        if (!isEmpty) {
            mTimeLineAdapter = new TimelineAdapter(mEatenProducts);
            mTimeLineAdapter.setOnItemClickListener(new TimelineAdapter.OnItemClickListener() {
                @Override
                public <T> void onItemClick(T product, int position) {
                    showProductInfo((Product) product);
                }
            });
            mProductsList.setAdapter(mTimeLineAdapter);
        }
    }

    @Override
    public void onClick(View v) {
        addFood();
    }

    private void switchFooter(boolean isEmpty) {
        mCalsBox.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        mAddFood.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        mAddMore.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
    }

    private void showProductInfo(Product product) {
        // TODO implement this
    }

    private void addFood() {
        // TODO implement this
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onReceiveData(Collection<?> data) {
        mEatenProducts = (ArrayList<EatenProduct>) data;
        switchFooter(true);
    }

    @Override
    public void onError(Exception e) {

    }
}

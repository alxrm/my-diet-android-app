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
import com.rm.mydiet.ui.adapter.TimelineAdapter;
import com.rm.mydiet.utils.Prefs;

import java.util.ArrayList;

import static com.rm.mydiet.ui.MainFragment.KEY_DAY_CALORIES;
import static com.rm.mydiet.ui.MainFragment.KEY_DAY_PART;
import static com.rm.mydiet.utils.Prefs.KEY_MAX_CALS;

/**
 * A simple {@link Fragment} subclass.
 */
public class DiaryFragment extends TimelineFragment
        implements View.OnClickListener {

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
        mNoProductsMessage.setText(getEmptyMessageDayPart());
        boolean isEmpty = mEatenProducts.isEmpty();

        mCalsBox = (LinearLayout) findViewById(R.id.day_cals);
        mCalsProgress = (ProgressBar) findViewById(R.id.day_cals_left);
        mCalsText = (TextView) findViewById(R.id.day_cals_text);
        setCaloriesProgress(mCurrentCalories, mMaxCalories);

        // same listener
        mAddFood = (RelativeLayout) findViewById(R.id.add_food_button);
        mAddMore = (RelativeLayout) findViewById(R.id.add_more_food_button);

        mAddFood.setOnClickListener(this);
        mAddMore.setOnClickListener(this);

        switchProductsList(isEmpty);
        switchFooter(isEmpty);
    }

    private void switchProductsList(boolean isEmpty) {
        mProductsList.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
        mNoProductsMessage.setVisibility(isEmpty ? View.VISIBLE : View.GONE);

        if (!isEmpty) {
            mTimeLineAdapter = new TimelineAdapter(mEatenProducts);
            mTimeLineAdapter.setOnItemClickListener(new TimelineAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(View v, int position) {
                    showProductInfo(mEatenProducts.get(position));
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

    private String getEmptyMessageDayPart() {
        String prefix = "Добавьте продукты, которые\nвы съели на ";
        switch (mCurrentDayPart.getPartId()) {
            case DayPart.PART_BREAKFAST: return prefix + "завтрак";
            case DayPart.PART_LUNCH: return prefix + "обед";
            case DayPart.PART_SNACK: return prefix + "полдник";
            case DayPart.PART_DINNER: return prefix + "ужин";
            default: return "Error";
        }
    }

    private void showProductInfo(EatenProduct product) {

    }

    private void addFood() {
        // TODO implement this
    }
}

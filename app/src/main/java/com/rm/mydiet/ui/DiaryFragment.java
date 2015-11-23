package com.rm.mydiet.ui;


import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.rm.mydiet.R;
import com.rm.mydiet.model.DayPart;
import com.rm.mydiet.model.EatenProduct;
import com.rm.mydiet.ui.adapter.ProductAdapter;
import com.rm.mydiet.utils.Prefs;

import java.util.ArrayList;

import static com.rm.mydiet.ui.OnFragmentInteractionListener.FRAGMENT_DAIRY;
import static com.rm.mydiet.ui.OnFragmentInteractionListener.FRAGMENT_DAIRY_LIST;
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

    private RecyclerView mProductsList;
    private ArrayList<EatenProduct> mEatenProducts;
    private ProductAdapter mTimeLineAdapter;

    private int mCurrentCalories;
    private int mMaxCalories;

    public static DiaryFragment newInstance(DayPart dayPart, int cals) {
        Bundle arguments = new Bundle();
        arguments.putParcelable(DataTransfering.FRAGMENT_DIARY_KEY_DAY_PART, dayPart);
        arguments.putInt(DataTransfering.FRAGMENT_DIARY_KEY_CALORIES, cals);
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
        mCurrentDayPart = (DayPart) getArguments().getParcelable(DataTransfering.FRAGMENT_DIARY_KEY_DAY_PART);
        mEatenProducts = mCurrentDayPart.getEatenProducts();
        mCurrentCalories = getArguments().getInt(DataTransfering.FRAGMENT_DIARY_KEY_CALORIES);
        mMaxCalories = Prefs.get().getInt(KEY_MAX_CALS, 2500);

        mProductsList = (RecyclerView) findViewById(R.id.day_eaten_list);
        mProductsList.setLayoutManager(new LinearLayoutManager(getActivity()));
        mNoProductsMessage = (TextView) findViewById(R.id.day_eaten_text);
        mNoProductsMessage.setText(getEmptyMessageDayPart());
        boolean isEmpty = mEatenProducts.isEmpty();

        // same listener
        mAddFood = (RelativeLayout) findViewById(R.id.add_food_button);
        mAddMore = (RelativeLayout) findViewById(R.id.add_more_food_button);

        mAddFood.setOnClickListener(this);
        mAddMore.setOnClickListener(this);

        setCaloriesProgress(mCurrentCalories, mMaxCalories);
        switchProductsList(isEmpty);
        switchFooter(isEmpty);
    }

    private void switchProductsList(boolean isEmpty) {
        Log.d("DiaryFragment", "switchProductsList - isEmpty: "
                + isEmpty);
        mProductsList.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
        mNoProductsMessage.setVisibility(isEmpty ? View.VISIBLE : View.GONE);

        if (!isEmpty) {
            mTimeLineAdapter = new ProductAdapter(mEatenProducts);
            mTimeLineAdapter.setOnItemClickListener(new ProductAdapter.OnItemClickListener() {
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
        mInteractionListener.onFragmentAction(product, FRAGMENT_DAIRY_LIST);
    }

    private void addFood() {
        Bundle data = new Bundle();
        data.putInt(DataTransfering.CALLBACK_DIARY_DAY_PART, mCurrentDayPart.getPartId());
        data.putLong(DataTransfering.CALLBACK_DIARY_TIME, mCurrentDayPart.getDay());
        mInteractionListener.onFragmentAction(data, FRAGMENT_DAIRY);
    }
}

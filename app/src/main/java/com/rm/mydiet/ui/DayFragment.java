package com.rm.mydiet.ui;


import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rm.mydiet.R;
import com.rm.mydiet.model.DayPart;
import com.rm.mydiet.model.EatenProduct;
import com.rm.mydiet.model.Product;
import com.rm.mydiet.ui.adapter.DayPartsAdapter;
import com.rm.mydiet.utils.CalculateUtils;
import com.rm.mydiet.utils.Prefs;
import com.rm.mydiet.utils.TimeUtil;
import com.rm.mydiet.utils.base.BaseFragment;
import com.rm.mydiet.utils.persistence.DatabaseListener;
import com.rm.mydiet.utils.persistence.DatabaseManager;

import java.util.ArrayList;
import java.util.Collection;

import static com.rm.mydiet.model.EatenProduct.getScalars;

/**
 * A simple {@link Fragment} subclass.
 */
public class DayFragment extends BaseFragment
        implements OnFragmentInteractionListener, DatabaseListener {

    // TODO aggressive refactoring
    // TODO optimizations (!)

    private RecyclerView mDayParts;
    private DayPartsAdapter mDayPartsAdapter;
    private ArrayList<DayPart> mDayPartsList = new ArrayList<>();

    private long mCurrentStart; // MILLIS
    private int mCurrentCals;

    public static DayFragment newInstance(long dayStart) {
        Bundle args = new Bundle();
        args.putLong(DataTransferring.FRAGMENT_DAY_KEY_START, dayStart);
        DayFragment fragment = new DayFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public DayFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCurrentStart = getArguments().getLong(DataTransferring.FRAGMENT_DAY_KEY_START);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_day, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mDayParts = (RecyclerView) findViewById(R.id.day_parts);
        mDayParts.setLayoutManager(new GridLayoutManager(getActivity(), 4));
        mDayPartsAdapter = new DayPartsAdapter(mDayPartsList);
        mDayParts.setAdapter(mDayPartsAdapter);
        mDayPartsAdapter.setOnItemClickListener(new DayPartsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                if (position >= 0 && position < 4) {
                    mDayPartsAdapter.setItemSelected(position);
                    showDayPartData(mDayPartsList.get(position), false);
                }
            }
        });

        if (mDayPartsList.isEmpty()) {
            DatabaseManager.getInstance().retrieveDayParts(mCurrentStart, this);
        }
    }

    @Override
    public <T> void onFragmentAction(T data, String key) {
        switch (key) {
            case FRAGMENT_TIMER: {
                DayPart part = mDayPartsList.get((Integer) data);
                part.setExists(true);
                showDayPartData(part, false);
                DatabaseManager.getInstance().addDayPart(part);
                break;
            }
            case FRAGMENT_DAIRY: {
                Intent starter = new Intent(getActivity(), AddProductActivity.class);
                Bundle callbackData = (Bundle) data;
                long currentTime = callbackData.getLong(DataTransferring.CALLBACK_DIARY_TIME);
                int dayPart = callbackData.getInt(DataTransferring.CALLBACK_DIARY_DAY_PART);
                long time = TimeUtil.isToday(currentTime) ? TimeUtil.unixTimeMillis() : currentTime;
                starter.putExtra(DataTransferring.ACTIVITY_ADD_KEY_DAY_PART, dayPart);
                starter.putExtra(DataTransferring.ACTIVITY_ADD_KEY_TIME, time);
                startActivityForResult(starter, DataTransferring.ACTIVITY_ADD_CODE_REQUEST);
                break;
            }
            case FRAGMENT_DAIRY_LIST: {
                Intent starter = new Intent(getActivity(), EditProductActivity.class);
                Bundle callbackData = (Bundle) data;
                int dayPart = callbackData.getInt(DataTransferring.CALLBACK_DIARY_DAY_PART);
                EatenProduct eatenProduct = callbackData.getParcelable(DataTransferring.CALLBACK_DIARY_EATEN_PRODUCT);
                starter.putExtra(DataTransferring.ACTIVITY_EDIT_KEY_DAY_PART, dayPart);
                starter.putExtra(DataTransferring.ACTIVITY_EDIT_KEY_EATEN_PRODUCT, eatenProduct);
                startActivityForResult(starter, DataTransferring.ACTIVITY_EDIT_CODE_REQUEST);
                break;
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // TODO FROM 2 ACTIVITIES SWITCH CASE

        switch (resultCode) {
            case DataTransferring.ACTIVITY_ADD_CODE_RESULT: {
                Bundle resultData = data.getBundleExtra(DataTransferring.ACTIVITY_ADD_KEY_RESULT_DATA);
                int dayPartId = resultData.getInt(DataTransferring.CALLBACK_PRODUCT_INFO_DAY_PART);
                EatenProduct eaten = resultData.getParcelable(DataTransferring.CALLBACK_PRODUCT_INFO_EATEN_PRODUCT);
                DayPart updated = mDayPartsList.get(dayPartId);
                updated.addEatenProduct(eaten);
                updateDayParts(updated);
                showDayPartData(updated, true);
                break;
            }
            case DataTransferring.ACTIVITY_EDIT_CODE_RESULT: {
                Bundle resultData = data.getBundleExtra(DataTransferring.ACTIVITY_EDIT_KEY_RESULT_DATA);
                int dayPartId = resultData.getInt(DataTransferring.CALLBACK_PRODUCT_INFO_DAY_PART);
                EatenProduct eaten = resultData.getParcelable(DataTransferring.CALLBACK_PRODUCT_INFO_EATEN_PRODUCT);
                DayPart updated = mDayPartsList.get(dayPartId);
                updated.updateEatenProduct(eaten);
                updateDayParts(updated);
                showDayPartData(updated, true);
                break;
            }
        }
    }

    public void hideData() {
        // TODO implement this
    }

    public void showData() {
        // TODO implement this
    }

    public int getCaloriesProgress() {
        return CalculateUtils.calcProgress(
                mCurrentCals,
                Prefs.get().getInt(Prefs.KEY_MAX_CALS, 2500)
        );
    }

    private void updateDayParts(DayPart updated) {
        boolean isExists = updated.isExists();
        updated.setExists(true);
        mDayPartsList.set(updated.getPartId(), updated);
        mDayPartsAdapter.updateList(mDayPartsList);
        if (isExists) {
            DatabaseManager.getInstance().updateDayPart(updated);
        } else {
            DatabaseManager.getInstance().addDayPart(updated);
        }
    }

    private void calculateCalories(ArrayList<DayPart> dayPartsList) {
        int cals = 0;
        Product product;

        for (DayPart dayPart : dayPartsList) {
            for (EatenProduct eaten : dayPart.getEatenProducts()) {
                product = eaten.getProduct();
                cals += eaten.getCount()
                        * product.getCalories()
                        * getScalars(product).get(eaten.getScalarId());
            }
        }

        mCurrentCals = cals;

        if (mInteractionListener != null) {
            Bundle data = new Bundle();
            data.putInt(DataTransferring.CALLBACK_DAY_KEY_CALORIES_PROGRESS, getCaloriesProgress());
            data.putLong(DataTransferring.CALLBACK_DAY_KEY_DAY, mCurrentStart);
            mInteractionListener.onFragmentAction(data, DataTransferring.CALLBACK_DAY_WRAPPER);
        }
    }

    private void findRelevant(ArrayList<DayPart> dayPartsList) {
        DayPart selected;
        for (DayPart dayPart : dayPartsList) {
            if (!dayPart.isExists() && hasTimer(dayPart)) {
                showDayPartData(dayPart, false);
                return;
            }
        }
        selected = dayPartsList.get(0);
        showDayPartData(selected, false);
    }

    private void showDayPartData(DayPart dayPart, boolean updating) {
        TimelineFragment fragment;
        calculateCalories(mDayPartsList);

        dayPart.setSelected(true);

        if (hasTimer(dayPart)) {
            if (dayPart.isExists()) {
                fragment = DiaryFragment.newInstance(dayPart, mCurrentCals);
            } else {
                fragment = TimerFragment.newInstance(dayPart, mCurrentCals);
            }
        } else {
            fragment = DiaryFragment.newInstance(dayPart, mCurrentCals);
        }

        if (getActivity() != null) {
            FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
            if (!updating) transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            transaction
                    .replace(R.id.container_day, fragment)
                    .commitAllowingStateLoss();
        }
    }

    private boolean hasTimer(DayPart dayPart) {
        return (dayPart.getTimerOffset() + dayPart.getDay()) > TimeUtil.unixTimeMillis();
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onReceiveData(Collection<?> data) {
        mDayPartsList = (ArrayList<DayPart>) data;
        findRelevant(mDayPartsList);
        mDayPartsAdapter.updateList(mDayPartsList);
    }

    @Override
    public void onError(Exception e) {

    }
}

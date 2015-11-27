package com.rm.mydiet.ui;


import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rm.mydiet.R;
import com.rm.mydiet.model.DayPart;
import com.rm.mydiet.model.EatenProduct;
import com.rm.mydiet.ui.adapter.DayPartsAdapter;
import com.rm.mydiet.utils.TimeUtil;
import com.rm.mydiet.utils.base.BaseFragment;
import com.rm.mydiet.utils.persistence.DatabaseListener;
import com.rm.mydiet.utils.persistence.DatabaseManager;

import java.util.ArrayList;
import java.util.Collection;

/**
 * A simple {@link Fragment} subclass.
 */
public class DayFragment extends BaseFragment
        implements OnFragmentInteractionListener, DatabaseListener {

    private RecyclerView mDayParts;
    private DayPartsAdapter mDayPartsAdapter;
    private ArrayList<DayPart> mDayPartsList = new ArrayList<>();
    private long mCurrentStart;

    public static DayFragment newInstance(long dayStart) {
        Bundle args = new Bundle();
        args.putLong(DataTransfering.FRAGMENT_DAY_KEY_START, dayStart);
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
        mCurrentStart = getArguments().getLong(DataTransfering.FRAGMENT_DAY_KEY_START);
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
        DatabaseManager.getInstance().retrieveDayParts(mCurrentStart, this);
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
                long currentTime = callbackData.getLong(DataTransfering.CALLBACK_DIARY_TIME);
                int dayPart = callbackData.getInt(DataTransfering.CALLBACK_DIARY_DAY_PART);
                long time = TimeUtil.isToday(currentTime) ? TimeUtil.unixTime() : currentTime;
                starter.putExtra(DataTransfering.ACTIVITY_ADD_KEY_DAY_PART, dayPart);
                starter.putExtra(DataTransfering.ACTIVITY_ADD_KEY_TIME, time);
                startActivityForResult(starter, DataTransfering.ACTIVITY_ADD_CODE_REQUEST);
                break;
            }
            case FRAGMENT_DAIRY_LIST: {
                // TODO ACTIVITY USE HASHCODE
//                EatenProduct product = ((EatenProduct) data);
//                ProductInfoFragment fragment = ProductInfoFragment.newInstance(product, null);
//                mParent.addFragment(fragment, "Информация о продукте");
                break;
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // TODO FROM 2 ACTIVITIES SWITCH CASE

        Log.d("DayFragment", "onActivityResult OUT");

        switch (resultCode) {
            case DataTransfering.ACTIVITY_ADD_CODE_RESULT: {
                Log.d("DayFragment", "onActivityResult INSIDE");
                Bundle resultData = data.getBundleExtra(DataTransfering.ACTIVITY_ADD_KEY_RESULT_DATA);
                int dayPartId = resultData.getInt(DataTransfering.CALLBACK_PRODUCT_INFO_DAY_PART);
                EatenProduct eaten = resultData.getParcelable(DataTransfering.CALLBACK_PRODUCT_INFO_EATEN_PRODUCT);
                DayPart updated = mDayPartsList.get(dayPartId);
                updated.addEatenProduct(eaten);
                updateDayParts(updated);
                showDayPartData(updated, true);
                break;
            }
            case DataTransfering.ACTIVITY_EDIT_CODE_RESULT: {
                break;
            }
        }
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

    private int calculateCalories(ArrayList<DayPart> dayPartsList) {
        int cals = 0;
        for (DayPart dayPart : dayPartsList) {
            for (EatenProduct eaten : dayPart.getEatenProducts()) {
                cals += eaten.getCount() * eaten.getProduct().getCalories();
            }
        }
        return cals;
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
        int currentCals = calculateCalories(mDayPartsList);

        dayPart.setSelected(true);

        if (hasTimer(dayPart)) {
            if (dayPart.isExists()) {
                fragment = DiaryFragment.newInstance(dayPart, currentCals);
            } else {
                fragment = TimerFragment.newInstance(dayPart, currentCals);
            }
        } else {
            fragment = DiaryFragment.newInstance(dayPart, currentCals);
        }

        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        if (!updating) transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        transaction
                .replace(R.id.container_day, fragment)
                .commitAllowingStateLoss();
    }

    private boolean hasTimer(DayPart dayPart) {
        return (dayPart.getTimerOffset() + dayPart.getDay()) > TimeUtil.unixTime();
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onReceiveData(Collection<?> data) {
        mDayPartsList = (ArrayList<DayPart>) data;
        findRelevant(mDayPartsList);
        mDayPartsAdapter = new DayPartsAdapter(mDayPartsList);
        mDayPartsAdapter.setOnItemClickListener(new DayPartsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                if (position >= 0 && position < 4) {
                    mDayPartsAdapter.setItemSelected(position);
                    showDayPartData(mDayPartsList.get(position), false);
                }
            }
        });
        mDayParts.setAdapter(mDayPartsAdapter);
    }

    @Override
    public void onError(Exception e) {

    }
}

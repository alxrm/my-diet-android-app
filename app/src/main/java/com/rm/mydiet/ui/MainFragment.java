package com.rm.mydiet.ui;


import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rm.mydiet.R;
import com.rm.mydiet.model.DayPart;
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
public class MainFragment extends BaseFragment
        implements OnFragmentInteractionListener, DatabaseListener {


    public static final String KEY_INFO_PRODUCT = "product";
    public static final String KEY_INFO_EATEN = "eaten";

    public static final String KEY_DAY_PART = "daypart";
    public static final String KEY_DAY_CALORIES = "calories";

    private RecyclerView mDayParts;
    private ArrayList<DayPart> mDayPartsList = new ArrayList<>();
    private DayPartsAdapter mDayPartsAdapter;

    public MainFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mDayParts = (RecyclerView) findViewById(R.id.day_parts);
        mDayParts.setLayoutManager(new GridLayoutManager(getActivity(), 4));
        DatabaseManager.getInstance().retrieveDayParts(TimeUtil.getToday(), this);
    }

    @Override
    public <T> void onFragmentAction(T data, int key) {
        switch (key) {
            case FRAGMENT_TIMER: {
                DayPart part = mDayPartsList.get((Integer) data);
                part.setExists(true);
                showDayPartData(part);
                DatabaseManager.getInstance().addDayPart(part);
                break;
            }
            case FRAGMENT_DAIRY: {
                break;
            }
            case FRAGMENT_PROD_INFO: {
                // TODO implement this
                break;
            }
        }
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
                mDayPartsAdapter.setItemSelected(position);
                showDayPartData(mDayPartsList.get(position));
            }
        });
        mDayParts.setAdapter(mDayPartsAdapter);
    }

    private void findRelevant(ArrayList<DayPart> dayPartsList) {
        DayPart selected;
        for (DayPart dayPart : dayPartsList) {
            Log.d("MainFragment", "findRelevant - dayPart.isExists(): "
                    + dayPart.isExists());
            if ((dayPart.getTimerOffset() + dayPart.getDay()) > TimeUtil.unixTime()) {
                showDayPartData(dayPart);
                return;
            }
        }
        selected = dayPartsList.get(0);
        showDayPartData(selected);
    }

    private void showDayPartData(DayPart dayPart) {
        TimelineFragment fragment;
        boolean hasTimer = (dayPart.getTimerOffset() + dayPart.getDay()) > TimeUtil.unixTime();
        dayPart.setSelected(true);
        if (hasTimer) {
            if (dayPart.isExists()) {
                fragment = DiaryFragment.newInstance(dayPart, 0);
            } else {
                fragment = TimerFragment.newInstance(dayPart, 0);
            }
        } else {
            fragment = DiaryFragment.newInstance(dayPart, 0);
        }
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .replace(R.id.container_day, fragment)
                .commit();
    }

    @Override
    public void onError(Exception e) {

    }
}

package com.rm.mydiet.model;

import com.rm.mydiet.utils.Prefs;

import java.util.ArrayList;

import static com.rm.mydiet.utils.Prefs.KEY_TIMER_OFFSET;

/**
 * Created by alex
 */
public class DayPart {

    public static final int PART_BREAKFAST = 0;
    public static final int PART_LUNCH = 1;
    public static final int PART_SNACK = 2;
    public static final int PART_DINNER = 3;

    private ArrayList<Eating> mEatings = new ArrayList<>();
    private int mPartKey;
    private long mTimerOffset;
    private boolean mIsSelected;

    public DayPart(int partKey) {
        if (partKey > 4) partKey = 0;
        if (partKey < 0) partKey = 4;
        mPartKey = partKey;
        setTimerOffset(partKey);
    }

    private void setTimerOffset(int partKey) {
        mTimerOffset = Prefs.get().getLong(
                KEY_TIMER_OFFSET + partKey,
                1000 * 3600 * (4 * (partKey + 2) + 1)
        );
    }

    public ArrayList<Eating> getEatings() {
        return mEatings;
    }

    public void setEatings(ArrayList<Eating> eatings) {
        mEatings = eatings;
    }

    public void addEating(Eating e) {
        mEatings.add(e);
    }

    public int getPartKey() {
        return mPartKey;
    }

    public boolean isSelected() {
        return mIsSelected;
    }

    public void setSelected(boolean isSelected) {
        mIsSelected = isSelected;
    }

    public long getTimerOffset() {
        return mTimerOffset;
    }
}

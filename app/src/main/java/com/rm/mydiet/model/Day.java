package com.rm.mydiet.model;

import java.util.ArrayList;

/**
 * Created by alex
 */
public class Day {
    private ArrayList<DayPart> mDayParts;
    private long mDayStart;

    public Day(ArrayList<DayPart> dayParts, long dayStart) {
        mDayParts = dayParts;
        mDayStart = dayStart;
    }

    public ArrayList<DayPart> getDayParts() {
        return mDayParts;
    }

    public long getDayStart() {
        return mDayStart;
    }
}

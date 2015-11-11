package com.rm.mydiet.model;

import android.util.Log;

import com.rm.mydiet.utils.Prefs;
import com.rm.mydiet.utils.TimeUtil;

import java.util.ArrayList;

/**
 * Created by alex
 */
public class Day {
    private long mStartTime;
    private DayPart mCurPart;
    private ArrayList<DayPart> mParts;
    private int mMaxCals;
    private int mCurCals;

    public static Day formatDay(ArrayList<Eating> bigData) {
        Day day = new Day();
        ArrayList<DayPart> parts = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            DayPart part = new DayPart(i);
            part.setEatings(new ArrayList<Eating>());
            parts.add(part);
        }
        for (Eating eating : bigData) {
            parts.get(eating.getDayPart()).addEating(eating);
            day.setStartTime(TimeUtil.getStartOfTheDay(eating.getTime()));
            day.setCurCals(day.getCurCals() + eating.getProduct().getCalories());
        }
        for (int i = 0; i < parts.size(); i++) {
            DayPart p = parts.get(i);
            if (p.getEatings().isEmpty()) {
                p.setSelected(true);
                day.setCurPart(p);
                break;
            }
        }
        day.setParts(parts);
        day.setMaxCals(Prefs.get().getInt(Prefs.KEY_MAX_CALS, 2500));
        Log.d("Day", "formatDay: " + day);
        return day;
    }

    public int getPercent() {
        int percent = (int) ((float) mCurCals / mMaxCals * 100);
        return percent > 100 ? 100 : percent;
    }

    public ArrayList<DayPart> getParts() {
        return mParts;
    }

    public void setParts(ArrayList<DayPart> parts) {
        mParts = parts;
    }

    public long getStartTime() {
        return mStartTime;
    }

    public long getEndTime() {
        return mStartTime + TimeUtil.DAY_MILLIES;
    }

    public void setStartTime(long startTime) {
        mStartTime = startTime;
    }

    public int getMaxCals() {
        return mMaxCals;
    }

    public void setMaxCals(int maxCals) {
        mMaxCals = maxCals;
    }

    public int getCurCals() {
        return mCurCals;
    }

    public void setCurCals(int curCals) {
        mCurCals = curCals;
    }

    @Override
    public String toString() {
        return "Parts: " + getParts().size() + " Cals: " + getCurCals();
    }

    public DayPart getCurPart() {
        return mCurPart;
    }

    public void setCurPart(DayPart curPart) {
        mCurPart = curPart;
    }
}

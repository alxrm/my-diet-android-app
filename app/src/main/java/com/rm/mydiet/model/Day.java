package com.rm.mydiet.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by alex
 */
public class Day implements Parcelable {
    private ArrayList<DayPart> mDayParts;
    private long mDayStart;

    public Day(ArrayList<DayPart> dayParts, long dayStart) {
        mDayParts = dayParts;
        mDayStart = dayStart;
    }

    protected Day(Parcel in) {
        mDayParts = in.createTypedArrayList(DayPart.CREATOR);
        mDayStart = in.readLong();
    }

    public static final Creator<Day> CREATOR = new Creator<Day>() {
        @Override
        public Day createFromParcel(Parcel in) {
            return new Day(in);
        }

        @Override
        public Day[] newArray(int size) {
            return new Day[size];
        }
    };

    public ArrayList<DayPart> getDayParts() {
        return mDayParts;
    }

    public long getDayStart() {
        return mDayStart;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(mDayParts);
        dest.writeLong(mDayStart);
    }
}

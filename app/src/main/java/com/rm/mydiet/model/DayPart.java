package com.rm.mydiet.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.rm.mydiet.utils.Prefs;

import java.util.ArrayList;

import static com.rm.mydiet.utils.Prefs.KEY_TIMER_OFFSET;

/**
 * Created by alex
 */
public class DayPart implements Parcelable {

    public static final int PART_BREAKFAST = 0;
    public static final int PART_LUNCH = 1;
    public static final int PART_SNACK = 2;
    public static final int PART_DINNER = 3;

    private final int mPartId;

    private ArrayList<EatenProduct> mEatenProducts = new ArrayList<>();
    private long mTimerOffset;
    private boolean mExists;
    private boolean mSelected = false;
    private long mDay;

    protected DayPart(Parcel in) {
        mPartId = in.readInt();
        mTimerOffset = in.readLong();
        mExists = in.readByte() != 0;
        mDay = in.readLong();
    }

    public static DayPart empty(int partId, long day) {
        return new DayPart(partId, false, day);
    }

    private DayPart(int partId, boolean exists, long day) {
        this.mPartId = partId;
        this.mExists = exists;
        this.mDay = day;
        setTimerOffset(partId);
    }

    public DayPart(int partId, long day) {
        if (partId > 4) partId = 0;
        if (partId < 0) partId = 4;
        this.mPartId = partId;
        this.mDay = day;
        this.mExists = true;
        setTimerOffset(partId);
    }

    private void setTimerOffset(int partId) {
        mTimerOffset = Prefs.get().getLong(
                KEY_TIMER_OFFSET + partId,
                1000 * 3600 * (4 * (partId + 2) + 2)
        );
    }

    public ArrayList<EatenProduct> getEatenProducts() {
        return mEatenProducts;
    }

    public void setEatenProducts(ArrayList<EatenProduct> eatenProducts) {
        mEatenProducts = eatenProducts;
    }

    public void addEatenProduct(EatenProduct e) {
        mEatenProducts.add(e);
    }

    public int getPartId() {
        return mPartId;
    }

    public boolean isExists() {
        return mExists;
    }

    public void setExists(boolean exists) {
        this.mExists = exists;
    }

    public long getTimerOffset() {
        return mTimerOffset;
    }

    public long getDay() {
        return mDay;
    }

    public static final Creator<DayPart> CREATOR = new Creator<DayPart>() {
        @Override
        public DayPart createFromParcel(Parcel in) {
            return new DayPart(in);
        }

        @Override
        public DayPart[] newArray(int size) {
            return new DayPart[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mPartId);
        dest.writeLong(mTimerOffset);
        dest.writeByte((byte) (mExists ? 1 : 0));
        dest.writeLong(mDay);
    }

    public boolean isSelected() {
        return mSelected;
    }

    public void setSelected(boolean selected) {
        mSelected = selected;
    }
}

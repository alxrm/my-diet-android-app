package com.rm.mydiet.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import java.util.ArrayList;

/**
 * Created by alex
 */
public class EatenProduct implements Parcelable {

    public static final int DEFAULT_COUNT = 1;
    public static final int DEFAULT_SCALAR_ID = 0;

    private static final float ONE_GRAM_SCALAR = 0.01F;
    private static final String OUTER_SEPARATOR = ",";
    private static final String INNER_SEPARATOR = " ";

    private Product mProduct;
    private long mTime;
    private int mCount;
    private int mScalarId;

    protected EatenProduct(Parcel in) {
        mProduct = in.readParcelable(Product.class.getClassLoader());
        mTime = in.readLong();
        mCount = in.readInt();
        mScalarId = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(mProduct, flags);
        dest.writeLong(mTime);
        dest.writeInt(mCount);
        dest.writeInt(mScalarId);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<EatenProduct> CREATOR = new Creator<EatenProduct>() {
        @Override
        public EatenProduct createFromParcel(Parcel in) {
            return new EatenProduct(in);
        }

        @Override
        public EatenProduct[] newArray(int size) {
            return new EatenProduct[size];
        }
    };

    public static ArrayList<String> getScalarNames(Product src) {
        String data = src.getInfo();
        ArrayList<String> scalNames = new ArrayList<>();
        scalNames.add("гр.");

        if (!TextUtils.isEmpty(data)) {
            for (String scalarName : data.split(OUTER_SEPARATOR)) {
                String name = scalarName.trim().replace(" +", "");
                scalNames.add(name);
            }
        }

        return scalNames;
    }

    public static ArrayList<Float> getScalars(Product src) {
        String data = src.getInfo();
        ArrayList<Float> scalars = new ArrayList<>();
        scalars.add(ONE_GRAM_SCALAR);

        if (!TextUtils.isEmpty(data)) {
            for (String scalarSrc : data.split(OUTER_SEPARATOR)) {
                String[] raw = scalarSrc.split(INNER_SEPARATOR);
                scalars.add(Float.parseFloat(raw[raw.length - 2]) / 100);
            }
        }


        return scalars;
    }

    public EatenProduct(long time) {
        this.mTime = time;
    }

    public Product getProduct() {
        return mProduct;
    }

    public void setProduct(Product product) {
        mProduct = product;
    }

    public long getTime() {
        return mTime;
    }

    public void setTime(long time) {
        mTime = time;
    }

    public int getCount() {
        return mCount;
    }

    public void setCount(int count) {
        mCount = count;
    }

    @Override
    public int hashCode() {
        if (mProduct == null || mTime == 0) return super.hashCode();
        return mProduct.hashCode() | Long.valueOf(mTime).hashCode();
    }

    public int getScalarId() {
        return mScalarId;
    }

    public void setScalarId(int scalarId) {
        mScalarId = scalarId;
    }
}

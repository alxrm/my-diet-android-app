package com.rm.mydiet.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by alex
 */
public class EatenProduct implements Parcelable {

    private Product mProduct;
    private long mTime;
    private int mCount;

    public EatenProduct(long time) {
        this.mTime = time;
    }

    protected EatenProduct(Parcel in) {
        mProduct = in.readParcelable(Product.class.getClassLoader());
        mTime = in.readLong();
        mCount = in.readInt();
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
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(mProduct, flags);
        dest.writeLong(mTime);
        dest.writeInt(mCount);
    }
}

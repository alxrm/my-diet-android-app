package com.rm.mydiet.model;

/**
 * Created by alex
 */
public class Eating {
    private Product mProduct;
    private long mTime;
    private int mDayPart;

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

    public int getDayPart() {
        return mDayPart;
    }

    public void setDayPart(int dayPart) {
        mDayPart = dayPart;
    }
}

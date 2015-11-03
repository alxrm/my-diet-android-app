package com.rm.mydiet.model;

/**
 * Created by alex
 */
public class DBConfig {
    private String mDbHash;
    private int mRowCount;

    public String getDbHash() {
        return mDbHash;
    }

    public void setDbHash(String dbHash) {
        mDbHash = dbHash;
    }

    public int getRowCount() {
        return mRowCount;
    }

    public void setProductsCount(int rowCount) {
        mRowCount = rowCount;
    }

    public boolean isEmpty() {
        return mDbHash == null || mRowCount == 0;
    }

    @Override
    public boolean equals(Object rhs) {
        return rhs instanceof DBConfig
                && getRowCount() == ((DBConfig) rhs).getRowCount()
                && ((DBConfig) rhs).getDbHash().equals(getDbHash());
    }
}

package com.rm.mydiet.utils.persistence.fetcher;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;

import com.rm.mydiet.model.Product;
import com.rm.mydiet.utils.background.DispatchQueue;
import com.rm.mydiet.utils.persistence.SQLQueryBuilder;

import java.util.ArrayList;

import static com.rm.mydiet.utils.StringUtils.md5;
import static com.rm.mydiet.utils.persistence.ProductsTable.COLUMN_CALORIES;
import static com.rm.mydiet.utils.persistence.ProductsTable.COLUMN_NAME;
import static com.rm.mydiet.utils.persistence.ProductsTable.PRODUCTS_TABLE;
import static com.rm.mydiet.utils.persistence.SQLQueryBuilder.ALL;
import static com.rm.mydiet.utils.persistence.SQLQueryBuilder.LIKE;

/**
 * Created by alex
 */

@SuppressWarnings("unchecked")
public class ProductFetcher extends BaseFetcher {

    private static final String SALT_PRODUCT_CURSOR = "productCursor";
    private static final String SALT_PRODUCT_DATA = "productData";

    public ProductFetcher(@NonNull DispatchQueue dispatchQueue,
                          @NonNull SQLiteDatabase db) {
        super(dispatchQueue, db);
    }

    @Override
    protected Runnable dataTask() {
        return new Runnable() {
            @Override
            public void run() {
                ArrayList<Product> result = (ArrayList<Product>) mDataHolder.get(mDataKey);
                if (result != null) return;

                Cursor dataCursor = (Cursor) mDataHolder.get(mCursorKey);
                result = new ArrayList<>(dataCursor.getCount());

                if (dataCursor.getCount() > 0) {
                    do result.add(Product.cursorToProduct(dataCursor));
                    while (dataCursor.moveToNext());
                }

                mDataHolder.put(mDataKey, result);
            }
        };
    }

    @Override
    protected String buildQuery() {
        return SQLQueryBuilder.newInstance()
                .select(ALL)
                .from(PRODUCTS_TABLE)
                .where()
                .stringClause(COLUMN_NAME, LIKE, mQuery)
                .orderBy(new String[]{COLUMN_NAME, COLUMN_CALORIES })
                .build();
    }

    @Override
    protected Runnable callback() {
        return new Runnable() {
            @Override
            public void run() {
                ArrayList<Product> products = (ArrayList<Product>) mDataHolder.get(mDataKey);
                mDatabaseListener.onReceiveData(products);
            }
        };
    }

    @Override
    protected void setupCursorKey() {
        this.mCursorKey = md5(SALT_PRODUCT_CURSOR + mQuery);
    }

    @Override
    protected void setupDataKey() {
        this.mDataKey = md5(SALT_PRODUCT_DATA + mQuery);
    }
}

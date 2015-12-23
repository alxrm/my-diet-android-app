package com.rm.mydiet.utils.persistence.fetcher;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.util.Log;

import com.rm.mydiet.model.DayPart;
import com.rm.mydiet.model.EatenProduct;
import com.rm.mydiet.model.Product;
import com.rm.mydiet.utils.background.DispatchQueue;
import com.rm.mydiet.utils.persistence.SQLQueryBuilder;

import java.util.ArrayList;

import static com.rm.mydiet.utils.StringUtils.md5;
import static com.rm.mydiet.utils.persistence.ProductsTable.COLUMN_ID;
import static com.rm.mydiet.utils.persistence.ProductsTable.PRODUCTS_TABLE;
import static com.rm.mydiet.utils.persistence.SQLQueryBuilder.ALL;
import static com.rm.mydiet.utils.persistence.SQLQueryBuilder.LIKE;

/**
 * Created by alex
 */

@SuppressWarnings("unchecked")
public class EatenProductFetcher extends BaseFetcher {

    private static final String SALT_EATEN_PRODUCT_CURSOR = "eatenProductCursor";
    private static final String SALT_EATEN_PRODUCT_DATA = "eatenProductData";

    public int ScalarId = EatenProduct.DEFAULT_SCALAR_ID;
    public int Count = EatenProduct.DEFAULT_COUNT;
    public long Time = 0L;

    private final DayPart mDayPartToAttach;
    private final String mDayHash;

    public EatenProductFetcher(@NonNull DispatchQueue dispatchQueue,
                               @NonNull SQLiteDatabase db,
                               @NonNull DayPart root,
                               @NonNull String dayHash) {
        super(dispatchQueue, db);
        this.mDayPartToAttach = root;
        this.mDayHash = dayHash;
    }

    @Override
    public void fetch(boolean immediate) {
        super.fetch(immediate);
    }

    @Override
    protected Runnable dataTask() {
        return new Runnable() {
            @Override
            public void run() {
                Log.d("EatenProductFetcher", "run");
                ArrayList<DayPart> day = (ArrayList<DayPart>) mDataHolder.get(mDayHash);
                Cursor dataCursor = (Cursor) mDataHolder.get(mCursorKey);
                mDayPartToAttach.addEatenProduct(buildEatenProduct(dataCursor));
                day.set(mDayPartToAttach.getPartId(), mDayPartToAttach);
                mDataHolder.put(mDayHash, day);
                dataCursor.close();
            }
        };
    }

    private EatenProduct buildEatenProduct(Cursor dataCursor) {
        EatenProduct eatenProduct = new EatenProduct(this.Time);
        Product product = Product.cursorToProduct(dataCursor);
        eatenProduct.setScalarId(this.ScalarId);
        eatenProduct.setCount(this.Count);
        eatenProduct.setProduct(product);
        Log.d("EatenProductFetcher", "product name " + product.getName());
        return eatenProduct;
    }

    @Override
    protected String buildQuery() {
        return SQLQueryBuilder.newInstance()
                .select(ALL)
                .from(PRODUCTS_TABLE)
                .where()
                .stringClause(COLUMN_ID, LIKE, mQuery)
                .build();
    }

    @Override
    protected Runnable callback() {
        return null;
    }

    @Override
    protected void setupCursorKey() {
        this.mCursorKey = md5(SALT_EATEN_PRODUCT_CURSOR + mQuery);
    }

    @Override
    protected void setupDataKey() {
        this.mDataKey = md5(SALT_EATEN_PRODUCT_DATA + mQuery);
    }
}

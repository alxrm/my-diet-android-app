package com.rm.mydiet.utils.persistence;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.rm.mydiet.api.Api;
import com.rm.mydiet.model.DBConfig;
import com.rm.mydiet.model.DayPart;
import com.rm.mydiet.model.EatenProduct;
import com.rm.mydiet.model.Product;
import com.rm.mydiet.utils.Prefs;
import com.rm.mydiet.utils.StringUtils;
import com.rm.mydiet.utils.StringUtils.InputValidator.StringFilter;
import com.rm.mydiet.utils.TimeUtil;
import com.rm.mydiet.utils.background.DispatchQueue;
import com.rm.mydiet.utils.background.SharedDataHolder;
import com.rm.mydiet.utils.persistence.fetcher.Fetcher;
import com.rm.mydiet.utils.persistence.fetcher.ProductFetcher;
import com.rm.mydiet.utils.persistence.fetcher.TimelineFetcher;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import static com.rm.mydiet.MyDietApplication.context;
import static com.rm.mydiet.utils.StringUtils.md5;
import static com.rm.mydiet.utils.persistence.ProductsTable.COLUMN_CALORIES;
import static com.rm.mydiet.utils.persistence.ProductsTable.COLUMN_CARBOHYDRATES;
import static com.rm.mydiet.utils.persistence.ProductsTable.COLUMN_FATS;
import static com.rm.mydiet.utils.persistence.ProductsTable.COLUMN_ID;
import static com.rm.mydiet.utils.persistence.ProductsTable.COLUMN_IMG;
import static com.rm.mydiet.utils.persistence.ProductsTable.COLUMN_INFO;
import static com.rm.mydiet.utils.persistence.ProductsTable.COLUMN_NAME;
import static com.rm.mydiet.utils.persistence.ProductsTable.COLUMN_PROTEINS;
import static com.rm.mydiet.utils.persistence.ProductsTable.PRODUCTS_TABLE;
import static com.rm.mydiet.utils.persistence.SQLQueryBuilder.ALL;
import static com.rm.mydiet.utils.persistence.TimelineTable.COLUMN_DAY_START;
import static com.rm.mydiet.utils.persistence.TimelineTable.COLUMN_PART_ID;
import static com.rm.mydiet.utils.persistence.TimelineTable.COLUMN_PRODUCTS;
import static com.rm.mydiet.utils.persistence.TimelineTable.JSON_COUNT;
import static com.rm.mydiet.utils.persistence.TimelineTable.JSON_PRODUCT_ID;
import static com.rm.mydiet.utils.persistence.TimelineTable.JSON_SCALAR_ID;
import static com.rm.mydiet.utils.persistence.TimelineTable.JSON_TIME;
import static com.rm.mydiet.utils.persistence.TimelineTable.TIMELINE_TABLE;

/**
 * Created by alex
 */
@SuppressWarnings("ALL")
public class DatabaseManager extends SQLiteOpenHelper {

    private static final String DB_NAME = "diet.db";
    private static final int DB_VERSION = 1;

    private static final String KEY_NEW_DATA = md5("fillData");

    private static DispatchQueue sFetcherQueue = new DispatchQueue("fetcher");
    private static DispatchQueue sDatabaseQueue = new DispatchQueue("database");
    private static DatabaseManager sInstance;

    private Queue<Fetcher> mFetchers;
    private static boolean sUnLocked = false;

    private static StringFilter sDefaultQueryValidator = new StringFilter() {
        @Override
        public String filter(String data) {
            return data.replace("\"", "");
        }
    };

    private DBConfig mNewConfig;
    private SharedDataHolder mDataHolder;

    private DatabaseManager(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.mDataHolder = SharedDataHolder.getInstance();
        this.mFetchers = new LinkedList<>();
    }

    public static DatabaseManager getInstance() {
        if (null == sInstance) {
            sInstance = new DatabaseManager(context());
        }
        return sInstance;
    }

    public void retrieveDayParts(long day, DatabaseListener listener) {
        TimelineFetcher timelineFetcher = new TimelineFetcher(sFetcherQueue, getReadableDatabase());
        String dayQuery = String.valueOf(TimeUtil.toSeconds(day));
        Log.d("DatabaseManager", "dayQuery KEY: " + dayQuery);

        timelineFetcher.setQuery(dayQuery);
        timelineFetcher.setDatabaseListener(listener);

        if (isFetchersLocked()) timelineFetcher.fetch();
        else mFetchers.add(timelineFetcher);
    }

    public void retrieveProducts(String query, DatabaseListener listener) {
        ProductFetcher productFetcher = new ProductFetcher(sFetcherQueue, getReadableDatabase());
        String filtered = sDefaultQueryValidator.filter(StringUtils.nullAsEmpty(query));

        productFetcher.setQuery(filtered);
        productFetcher.setDatabaseListener(listener);

        if (isFetchersLocked()) productFetcher.fetch();
        else mFetchers.add(productFetcher);
    }

    private boolean isFetchersLocked() {
        return sUnLocked;
    }

    private void unlockFetchers() {
        Log.d("DatabaseManager", "unlockFetchers");
        while (!mFetchers.isEmpty()) mFetchers.remove().fetch();
        this.sUnLocked = true;
    }

    public void addDayPart(DayPart dayPart) {
        // todo make it fast!
        ContentValues values = new ContentValues();
        SQLiteDatabase writableDatabase = getWritableDatabase();
        values.put(COLUMN_DAY_START, String.valueOf(TimeUtil.toSeconds(dayPart.getDay())));
        values.put(COLUMN_PART_ID, dayPart.getPartId());
        values.put(COLUMN_PRODUCTS, getJsonFromProducts(dayPart.getEatenProducts()));
        long t = writableDatabase.insert(TIMELINE_TABLE, null, values);
        Log.d("DatabaseManager", "ADDED daypart index: " + t);
    }


    public void updateDayPart(DayPart dayPart) {
        // todo make it fast!
        ContentValues values = new ContentValues();
        SQLiteDatabase writableDatabase = getWritableDatabase();
        String clause = COLUMN_DAY_START + "=?" + " AND " + COLUMN_PART_ID + "=?";
        String[] dayPartArgs = new String[2];
        dayPartArgs[0] = String.valueOf(TimeUtil.toSeconds(dayPart.getDay()));
        dayPartArgs[1] = String.valueOf(dayPart.getPartId());
        values.put(COLUMN_PRODUCTS, getJsonFromProducts(dayPart.getEatenProducts()));
        long t = writableDatabase.update(TIMELINE_TABLE, values, clause, dayPartArgs);
        Log.d("DatabaseManager", "UPDATED daypart index:  " + t);
    }

    // todo move this somewhere
    private String getJsonFromProducts(ArrayList<EatenProduct> products) {
        String resultJson = "[]";
        try {
            JSONObject product;
            JSONArray all = new JSONArray();

            if (products.size() > 0) {
                for (EatenProduct prod : products) {
                    product = new JSONObject();
                    product.put(JSON_TIME, prod.getTime());
                    product.put(JSON_COUNT, prod.getCount());
                    product.put(JSON_SCALAR_ID, prod.getScalarId());
                    product.put(JSON_PRODUCT_ID, prod.getProduct().getId());
                    all.put(product);
                }

                resultJson = all.toString();
            }

            return resultJson;
        } catch (JSONException e) {
            e.printStackTrace();
            return resultJson;
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(ProductsTable.CREATE);
        db.execSQL(TimelineTable.CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // no-op
    }

    public void update() {
        Api.getConfig(
                new Response.Listener<DBConfig>() {
                    @Override
                    public void onResponse(DBConfig newConfig) {
                        if (isReadyToUpdate(newConfig, sInstance)) {
                            mNewConfig = newConfig;
                            downloadProducts();
                        } else {
                            unlockFetchers();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("DatabaseManager", "getConfig error: " + error);
                        unlockFetchers();
                    }
                }
        );
    }

    private void downloadProducts() {
        Api.getProducts(
                new Response.Listener<ArrayList<Product>>() {
                    @Override
                    public void onResponse(ArrayList<Product> response) {
                        mDataHolder.put(KEY_NEW_DATA, response);
                        pushNewProducts();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("DatabaseManager", "getConfig error: " + error);
                        unlockFetchers();
                    }
                }
        );
    }

    // todo refactor
    private boolean isReadyToUpdate(DBConfig newConf, DatabaseManager db) {
        DBConfig oldConf = Prefs.getSavedDatabaseConfig();
        boolean isReady;
        isReady = oldConf.isEmpty();
        isReady |= !oldConf.equals(newConf);
        isReady |= newConf.getRowCount() != db.getProductsCount();
        Log.d("DatabaseManager", "isReadyToUpdate " + isReady);
        return isReady;
    }

    // todo refactor
    private int getProductsCount() {
        String countQuery = SQLQueryBuilder.newInstance().select(ALL).from(PRODUCTS_TABLE).build();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int productsCount = 0;
        if (cursor != null) {
            productsCount = cursor.getCount();
            cursor.close();
        }
        return productsCount;
    }

    private void pushNewProducts() {
        sDatabaseQueue.postRunnable(clearTask());
        sDatabaseQueue.postRunnable(fillTask());
    }

    private Runnable clearTask() {
        return new Runnable() {
            @Override
            public void run() {
                SQLiteDatabase db = getWritableDatabase();
                String clearQuery = SQLQueryBuilder.newInstance().delete(PRODUCTS_TABLE).build();
                db.execSQL(clearQuery);
            }
        };
    }

    private Runnable fillTask() {
        return new Runnable() {
            @Override
            public void run() {
                Log.d("fillTask", "run");
                ArrayList<Product> products = (ArrayList<Product>) mDataHolder.get(KEY_NEW_DATA);
                if (products == null) throw new IllegalStateException("Products cannot be null");

                SQLiteDatabase db = getWritableDatabase();

                try {
                    db.beginTransaction();
                    try {
                        fillAllProducts(db, products);
                        db.setTransactionSuccessful();
                    } finally {
                        db.endTransaction();
                        Prefs.saveDbConfig(mNewConfig);
                        unlockFetchers();
                        Log.d("DatabaseManager", "success");
                    }
                } catch (Exception e) {
                    Log.e("My Diet Database", "preFillDatabase: " + e);
                }
            }
        };
    }

    private void fillAllProducts(SQLiteDatabase db, ArrayList<Product> products) {
        ContentValues values = new ContentValues(); // reduce, reuse
        for (Product product : products) fillProduct(db, values, product);
    }

    private void fillProduct(SQLiteDatabase db, ContentValues values, Product product) {
        values.clear();
        values.put(COLUMN_NAME, product.getName());
        values.put(COLUMN_PROTEINS, product.getProteins());
        values.put(COLUMN_FATS, product.getFats());
        values.put(COLUMN_CARBOHYDRATES, product.getCarbohydrates());
        values.put(COLUMN_CALORIES, product.getCalories());
        values.put(COLUMN_INFO, product.getInfo());
        values.put(COLUMN_IMG, product.getImg());
        values.put(COLUMN_ID, product.getId());
        db.insert(PRODUCTS_TABLE, null, values);
    }
}

package com.rm.mydiet.utils.persistence;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.rm.mydiet.api.Api;
import com.rm.mydiet.model.DBConfig;
import com.rm.mydiet.model.DayPart;
import com.rm.mydiet.model.EatenProduct;
import com.rm.mydiet.model.Product;
import com.rm.mydiet.utils.InputValidator.StringFilter;
import com.rm.mydiet.utils.Prefs;
import com.rm.mydiet.utils.background.WorkerThreadExecutor;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

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
import static com.rm.mydiet.utils.persistence.SQLQueryBuilder.LIKE;
import static com.rm.mydiet.utils.persistence.TimelineTable.COLUMN_DAY_START;
import static com.rm.mydiet.utils.persistence.TimelineTable.COLUMN_PART_ID;
import static com.rm.mydiet.utils.persistence.TimelineTable.COLUMN_PRODUCTS;
import static com.rm.mydiet.utils.persistence.TimelineTable.JSON_COUNT;
import static com.rm.mydiet.utils.persistence.TimelineTable.JSON_ID;
import static com.rm.mydiet.utils.persistence.TimelineTable.JSON_TIME;
import static com.rm.mydiet.utils.persistence.TimelineTable.TIMELINE_TABLE;

/**
 * Created by alex
 */
public class DatabaseManager extends SQLiteOpenHelper {

    private static final String DB_NAME = "diet.db";
    private static final int DB_VERSION = 1;
    private static final String EMPTY_HASH = md5("");

    private static DatabaseManager sInstance;
    private static DBConfig sOldConfig;
    private static DBConfig sNewConfig;
    private static ArrayList<Runnable> sCallbacks = new ArrayList<>();
    private static String sCurrentQueryHash = EMPTY_HASH;
    private static boolean sDatabaseUpdated = false;

    private static StringFilter sDefaultQueryValidator = new StringFilter() {
        @Override
        public String filter(String data) {
            return data.replace("\"", "");
        }
    };

    private DatabaseManager(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    public static DatabaseManager getInstance() {
        if (null == sInstance) {
            sInstance = new DatabaseManager(context());
        }
        return sInstance;
    }

    private static synchronized void handleLoadComplete() {
        sDatabaseUpdated = true;
        for (Runnable callback : sCallbacks) callback.run();
        sCallbacks = new ArrayList<>();
    }

    public void retrieveDayParts(final long day, final DatabaseListener listener) {
        Runnable loadTimelineTask = new Runnable() {
            @Override
            public void run() {
                executeLoadDayParts(day, listener);
            }
        };

        if (sDatabaseUpdated) loadTimelineTask.run();
        else sCallbacks.add(loadTimelineTask);
    }

    public void retrieveProducts(final String query, final DatabaseListener listener) {
        Runnable loadProductsTask = new Runnable() {
            @Override
            public void run() {
                sCurrentQueryHash = TextUtils.isEmpty(query) ? EMPTY_HASH : md5(query);
                executeLoadProducts(query, sCurrentQueryHash, listener);
            }
        };

        if (sDatabaseUpdated) loadProductsTask.run();
        else sCallbacks.add(loadProductsTask);
    }

    public void addDayPart(DayPart dayPart) {
        ContentValues values = new ContentValues();
        SQLiteDatabase writableDatabase = getWritableDatabase();
        values.put(COLUMN_DAY_START, String.valueOf(dayPart.getDay() / 1000));
        values.put(COLUMN_PART_ID, dayPart.getPartId());
        values.put(COLUMN_PRODUCTS, getJsonFromProducts(dayPart.getEatenProducts()));
        writableDatabase.insert(TIMELINE_TABLE, null, values);
    }

    public void updateDayPart(DayPart dayPart) {
        ContentValues values = new ContentValues();
        SQLiteDatabase writableDatabase = getWritableDatabase();
        String clause = COLUMN_DAY_START + "=?" + " AND " + COLUMN_PART_ID + "=?";
        String[] dayPartArgs = new String[2];
        dayPartArgs[0] = String.valueOf(dayPart.getDay() / 1000);
        dayPartArgs[1] = String.valueOf(dayPart.getPartId());
        values.put(COLUMN_PRODUCTS, getJsonFromProducts(dayPart.getEatenProducts()));
        writableDatabase.update(TIMELINE_TABLE, values, clause, dayPartArgs);
    }

    private void executeLoadProducts(final String query,
                                     final String hash,
                                     final DatabaseListener listener) {
        final ArrayList<Product> products = new ArrayList<>();
        Runnable fetchTask = new Runnable() {
            @Override
            public void run() {
                Cursor productsCursor = TextUtils.isEmpty(query) ?
                        getProductsCursor() : getProductsCursor(query);
                products.addAll(fetchProducts(productsCursor));
            }
        };
        Runnable onReceive = new Runnable() {
            @Override
            public void run() {
                if (hash.equals(sCurrentQueryHash)) {
                    listener.onReceiveData(products);
                }
            }
        };
        WorkerThreadExecutor.start()
                .task(fetchTask)
                .callback(onReceive)
                .execute();
    }

    private void executeLoadDayParts(final long day,
                                     final DatabaseListener listener) {
        final ArrayList<DayPart> dayParts = new ArrayList<>();
        Runnable fetchTask = new Runnable() {
            @Override
            public void run() {
                Cursor dayPartsCursor = getDayPartsCursor(day / 1000);
                dayParts.addAll(fetchDayParts(dayPartsCursor, day));
            }
        };
        Runnable onReceive = new Runnable() {
            @Override
            public void run() {
                listener.onReceiveData(dayParts);
            }
        };
        WorkerThreadExecutor.start()
                .task(fetchTask)
                .callback(onReceive)
                .execute();
    }

    private Cursor getDayPartsCursor(long start) {
        SQLiteDatabase readableDatabase = getReadableDatabase();
        String selectQuery = SQLQueryBuilder.getInstance()
                .select(ALL)
                .from(TIMELINE_TABLE)
                .where()
                .integerClause(COLUMN_DAY_START, LIKE, start)
                .orderBy(new String[] { COLUMN_PART_ID })
                .build();
        Cursor data = readableDatabase.rawQuery(selectQuery, null);
        data.moveToFirst();
        return data;
    }

    private Cursor getProductsCursor() {
        SQLiteDatabase readableDatabase = getReadableDatabase();
        String selectQuery = SQLQueryBuilder.getInstance()
                .select(ALL)
                .from(PRODUCTS_TABLE)
                .orderBy(new String[]{ COLUMN_NAME, COLUMN_CALORIES })
                .build();
        Cursor data = readableDatabase.rawQuery(selectQuery, null);
        data.moveToFirst();
        return data;
    }

    private Cursor getProductsCursor(String query) {
        query = sDefaultQueryValidator.filter(query);
        SQLiteDatabase readableDatabase = getReadableDatabase();
        String selectQuery = SQLQueryBuilder.getInstance()
                .select(ALL)
                .from(PRODUCTS_TABLE)
                .where()
                .stringClause(COLUMN_NAME, LIKE, query)
                .orderBy(new String[]{COLUMN_NAME, COLUMN_CALORIES })
                .build();
        Cursor data = readableDatabase.rawQuery(selectQuery, null);
        data.moveToFirst();
        return data;
    }

    private ArrayList<DayPart> fetchDayParts(final Cursor data,
                                             final long day) {
        final ArrayList<DayPart> dayPartsTmp = getEmptyDayPartsForDay(day);
        if (data.getCount() > 0) {
            do {
                final DayPart dayPart = getDayPart(data, day);
                dayPartsTmp.set(dayPart.getPartId(), dayPart);
            } while (data.moveToNext());
        }
        return dayPartsTmp;
    }

    private ArrayList<Product> fetchProducts(final Cursor data) {
        final ArrayList<Product> tmpProducts = new ArrayList<>(data.getCount());
        if (data.getCount() != 0) {
            do {
                final Product product = getProduct(data);
                tmpProducts.add(product);
            }
            while (data.moveToNext());
        }
        return tmpProducts;
    }

    private DayPart getDayPart(Cursor data, long day) {
        DayPart dayPart = new DayPart(data.getInt(1), day);
        dayPart.setEatenProducts(getProductsFromJson(data.getString(2)));
        return dayPart;
    }

    private Product getProduct(Cursor data) {
        Product product = new Product();
        final String id = data.getString(0);
        final String name = data.getString(1);
        final float proteins = getFloatFromDatabase(data.getString(2));
        final float fats = getFloatFromDatabase(data.getString(3));
        final float carbohydrates = getFloatFromDatabase(data.getString(4));
        final int calories = getIntegerFromDatabase(data.getString(5));
        final String info = data.getString(6);
        final String img = data.getString(7);
        product.setId(id);
        product.setName(name);
        product.setProteins(proteins);
        product.setFats(fats);
        product.setCarbohydrates(carbohydrates);
        product.setCalories(calories);
        product.setInfo(info);
        product.setImg(img);
        return product;
    }

    private Product getProductById(String id) {
        SQLiteDatabase readableDatabase = getReadableDatabase();
        String query = SQLQueryBuilder.getInstance()
                .select(ALL)
                .from(PRODUCTS_TABLE)
                .where()
                .stringClause(COLUMN_ID, LIKE, id)
                .build();
        Cursor data = readableDatabase.rawQuery(query, null);
        data.moveToFirst();
        return getProduct(data);
    }

    private ArrayList<EatenProduct> getProductsFromJson(String json) {
//        Log.d("DatabaseManager", "getProductsFromJson json " + json);
        ArrayList<EatenProduct> result = new ArrayList<>();
        try {
            JSONArray items = new JSONArray(json);
            JSONObject eatenData;
            EatenProduct eatenProduct;
            for (int i = 0; i < items.length(); i++) {
                eatenData = items.getJSONObject(i);
                eatenProduct = new EatenProduct(eatenData.getLong(JSON_TIME));
                eatenProduct.setCount(eatenData.getInt(JSON_COUNT));
                eatenProduct.setProduct(getProductById(eatenData.getString(JSON_ID)));
                result.add(eatenProduct);
            }
            return result;
        } catch (JSONException e) {
            e.printStackTrace();
            return result;
        }
    }

    private ArrayList<DayPart> getEmptyDayPartsForDay(long day) {
        ArrayList<DayPart> dayParts = new ArrayList<>(4);
        for (int i = 0; i < 4; i++)
            dayParts.add(DayPart.empty(i, day));
        return dayParts;
    }

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
                    product.put(JSON_ID, prod.getProduct().getId());
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

    private float getFloatFromDatabase(String f) {
        return Float.valueOf(f);
    }

    private int getIntegerFromDatabase(String f) {
        return Integer.valueOf(f);
    }

    private long getLongFromDatabase(String f) {
        return Long.valueOf(f);
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
        sOldConfig = Prefs.getSavedDatabaseConfig();
        Api.getConfig(
                new Response.Listener<DBConfig>() {
                    @Override
                    public void onResponse(DBConfig newConfig) {
                        if (isReadyToUpdate(sOldConfig, newConfig, sInstance)) {
                            Log.d("DatabaseUpdateHelper", "NEW CONFIG");
                            sNewConfig = newConfig;
                            downloadProducts();
                        } else {
                            Log.d("DatabaseUpdateHelper", "NO NEW CONFIG");
                            handleLoadComplete();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("DatabaseManager", "getConfig error: " + error);
                        handleLoadComplete();
                    }
                }
        );
    }

    private boolean isReadyToUpdate(DBConfig oldConf, DBConfig newConf, DatabaseManager db) {
        boolean isReady;
        isReady = oldConf.isEmpty();
        isReady |= !oldConf.equals(newConf);
        isReady |= newConf.getRowCount() != db.getProductsCount();
        return isReady;
    }

    private void downloadProducts() {
        sInstance.clearProducts();
        Api.getProducts(
                new Response.Listener<ArrayList<Product>>() {
                    @Override
                    public void onResponse(ArrayList<Product> response) {
                        sInstance.pushProducts(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("DatabaseManager", "getConfig error: " + error);
                        handleLoadComplete();
                    }
                }
        );
    }

    private int getProductsCount() {
        String countQuery = SQLQueryBuilder.getInstance().select(ALL).from(PRODUCTS_TABLE).build();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int productsCount = 0;
        if (cursor != null) {
            productsCount = cursor.getCount();
            cursor.close();
        }
        return productsCount;
    }

    private void clearProducts() {
        SQLiteDatabase db = getWritableDatabase();
        String clearQuery = SQLQueryBuilder.getInstance().delete(PRODUCTS_TABLE).build();
        db.execSQL(clearQuery);
    }

    private void pushProducts(final ArrayList<Product> products) {
        Runnable pushTask = new Runnable() {
            @Override
            public void run() {
                SQLiteDatabase db = getWritableDatabase();
                try {
                    db.beginTransaction();
                    try {
                        fillAllProducts(db, products);
                        db.setTransactionSuccessful();
                    } finally {
                        db.endTransaction();
                        Prefs.saveDbConfig(sNewConfig);
                        handleLoadComplete();
                    }
                } catch (Exception e) {
                    Log.e("My Diet Database", "preFillDatabase: " + e);
                }
            }
        };
        WorkerThreadExecutor.start().task(pushTask).execute();
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


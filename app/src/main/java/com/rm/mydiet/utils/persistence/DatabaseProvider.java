package com.rm.mydiet.utils.persistence;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.rm.mydiet.model.Product;
import com.rm.mydiet.utils.BackgroundTaskExecutor;
import com.rm.mydiet.utils.persistence.listeners.DatabaseResponseListener;

import java.util.ArrayList;

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

/**
 * Created by alex
 */
public class DatabaseProvider extends SQLiteOpenHelper {

    private static final String DB_NAME = "diet.db";
    private static final int DB_VERSION = 1;

    private static DatabaseProvider sInstance;

    private DatabaseProvider(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    public static DatabaseProvider getInstance(Context context) {
        if (null == sInstance) {
            sInstance = new DatabaseProvider(context);
        }
        return sInstance;
    }

    public static void retrieveProducts(Context c, DatabaseResponseListener listener) {
        loadProducts(getProductsCursor(c), listener);
    }

    private static Cursor getProductsCursor(Context c) {
        SQLiteDatabase readableDatabase = getReadableDatabase(c);
        String selectQuery = SQLQueryBuilder.getInstance()
                .select(ALL)
                .from(PRODUCTS_TABLE)
                .orderBy(new String[]{ COLUMN_NAME })
                .build();
        Cursor data = readableDatabase.rawQuery(selectQuery, null);
        data.moveToFirst();
        return data;
    }

    private static void loadProducts(final Cursor data, final DatabaseResponseListener listener) {
        final ArrayList<Product> tmpProducts = new ArrayList<>(data.getCount());

        Runnable loadTask = new Runnable() {
            @Override
            public void run() {

                if (data.getCount() != 0) {
                    do {
                        final Product product = getProduct(data);
                        tmpProducts.add(product);
                    } while (data.moveToNext());
                }
            }
        };

        Runnable onReceive = new Runnable() {
            @Override
            public void run() {
                listener.onReceiveData(tmpProducts);
            }
        };

        BackgroundTaskExecutor.start()
                .task(loadTask)
                .callback(onReceive)
                .execute();
    }

    private static Product getProduct(Cursor data) {
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

    private static float getFloatFromDatabase(String f) {
        return Float.valueOf(f);
    }

    private static int getIntegerFromDatabase(String f) {
        return Integer.valueOf(f);
    }

    private static SQLiteDatabase getWritableDatabase(Context context) {
        return getInstance(context).getWritableDatabase();
    }

    private static SQLiteDatabase getReadableDatabase(Context context) {
        return getInstance(context).getReadableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(ProductsTable.CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // no-op
    }

    int getProductsCount() {
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

    void clearProducts() {
        Log.d("DatabaseProvider", "clearProducts");
        SQLiteDatabase db = getWritableDatabase();
        String clearQuery = SQLQueryBuilder.getInstance().delete(PRODUCTS_TABLE).build();
        db.execSQL(clearQuery);
    }

    void pushProducts(final ArrayList<Product> products, final Runnable afterUpdate) {
        Log.d("DatabaseProvider", "pushProducts");
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
                    }
                } catch (Exception e) {
                    Log.e("My Diet Database", "preFillDatabase: " + e);
                }
            }
        };
        BackgroundTaskExecutor.start()
                .task(pushTask)
                .callback(afterUpdate)
                .execute();
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


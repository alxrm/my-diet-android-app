package com.rm.mydiet.utils.persistence;

import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.rm.mydiet.api.Api;
import com.rm.mydiet.model.DBConfig;
import com.rm.mydiet.model.Product;
import com.rm.mydiet.utils.Prefs;
import com.rm.mydiet.utils.UserPermissionListener;
import com.rm.mydiet.utils.persistence.listeners.DatabaseUpdateListener;

import java.util.ArrayList;

import static com.rm.mydiet.MyDietApplication.app;

/**
 * Created by alex
 */
public class DatabaseUpdateHelper implements UserPermissionListener {

    private DatabaseUpdateListener mUpdateListener;
    private DatabaseProvider mDatabase;
    private DBConfig mOldConfig;
    private DBConfig mNewConfig;

    private Runnable mAfterUpdate = new Runnable() {
        @Override
        public void run() {
            Prefs.saveDbConfig(mNewConfig);
            mUpdateListener.onLoadComplete();
        }
    };


    public DatabaseUpdateHelper(DatabaseUpdateListener listener) {
        mDatabase = DatabaseProvider.getInstance(app());
        mOldConfig = Prefs.getSavedDatabaseConfig();
        mUpdateListener = listener;
    }

    @Override
    public void onGrant() {
        downloadProducts();
    }

    @Override
    public void onDecline() {
        mUpdateListener.onLoadComplete();
    }

    public void tryUpdate() {
        Api.getConfig(
                new Response.Listener<DBConfig>() {
                    @Override
                    public void onResponse(DBConfig newConfig) {
                        if (isReadyToUpdate(mOldConfig, newConfig, mDatabase)) {
                            mNewConfig = newConfig;
                            mUpdateListener.onUpdateAvailable(DatabaseUpdateHelper.this);
                        } else {
                            mUpdateListener.onLoadComplete();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("DatabaseProvider", "getConfig error: " + error);
                        mUpdateListener.onLoadComplete();
                    }
                }
        );
    }

    private boolean isReadyToUpdate(DBConfig oldConf, DBConfig newConf, DatabaseProvider db) {
        boolean isReady;
        isReady = oldConf.isEmpty();
        isReady |= !oldConf.equals(newConf);
        isReady |= newConf.getRowCount() != db.getProductsCount();
        return isReady;
    }

    private void downloadProducts() {
        mDatabase.clearProducts();
        Api.getProducts(
                new Response.Listener<ArrayList<Product>>() {
                    @Override
                    public void onResponse(ArrayList<Product> response) {
                        mDatabase.pushProducts(response, mAfterUpdate);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("DatabaseProvider", "getConfig error: " + error);
                        mUpdateListener.onLoadComplete();
                    }
                }
        );
    }
}

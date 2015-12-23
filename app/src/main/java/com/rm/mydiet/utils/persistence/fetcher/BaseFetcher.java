package com.rm.mydiet.utils.persistence.fetcher;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.util.Log;

import com.rm.mydiet.utils.StringUtils;
import com.rm.mydiet.utils.background.DispatchQueue;
import com.rm.mydiet.utils.background.MainThreadHandler;
import com.rm.mydiet.utils.background.SharedDataHolder;
import com.rm.mydiet.utils.persistence.DatabaseListener;

/**
 * Created by alex
 */

public abstract class BaseFetcher implements Fetcher {

    protected final DispatchQueue mDispatchQueue;
    protected final SharedDataHolder mDataHolder;
    protected final SQLiteDatabase mDatabase;

    protected DatabaseListener mDatabaseListener;
    protected String mCursorKey;
    protected String mDataKey;
    protected String mQuery;

    protected final Runnable DEFAULT_CURSOR_TASK = new Runnable() {
        @Override
        public void run() {
            String rawQuery = buildQuery();
            Log.d("BaseFetcher", "run - rawQuery: "
                    + rawQuery);
            Cursor data = mDatabase.rawQuery(rawQuery, null);

            if (data != null) {
                data.moveToFirst();
                mDataHolder.put(mCursorKey, data);
            } else {
                Log.d("BaseFetcher", "NULL");
                cancelFetcher();
            }
        }
    };

    public BaseFetcher(@NonNull DispatchQueue dispatchQueue,
                       @NonNull SQLiteDatabase db) {
        this.mDatabase = db;
        this.mDispatchQueue = dispatchQueue;
        this.mDataHolder = SharedDataHolder.getInstance();
    }

    @Override
    public void fetch() {
        this.fetch(false);
    }

    public void fetch(boolean immediate) {
        Runnable dataTask = dataTask();
        Runnable callbackTask = callbackTask();

        if (immediate) {
            mDispatchQueue.postInFrontOfQueue(dataTask);
            createCursor(mQuery, true);
        } else {
            createCursor(mQuery, false);
            mDispatchQueue.postRunnable(dataTask);
        }

        if (callback() != null) mDispatchQueue.postRunnable(callbackTask);
    }

    public void setDatabaseListener(DatabaseListener databaseListener) {
        this.mDatabaseListener = databaseListener;
    }

    public void setQuery(String query) {
        this.mQuery = StringUtils.nullAsEmpty(query);
    }

    protected Runnable callbackTask() {
        return new Runnable() {
            @Override
            public void run() {
                // todo implement error callback
                MainThreadHandler.getInstance().postToMain(callback());
            }
        };
    }

    protected void cancelFetcher() {
        Runnable dataTask = dataTask();
        Runnable cursorTask = cursorTask();
        mDispatchQueue.cancelRunnable(cursorTask);
        mDispatchQueue.cancelRunnable(dataTask);
        mDispatchQueue.cancelRunnable(callbackTask());
    }

    protected void createCursor(String query, boolean immediate) {
        setQuery(query);
        setupCursorKey();
        setupDataKey();

        Runnable cursorTask = cursorTask();

        if (immediate) {
            mDispatchQueue.postInFrontOfQueue(cursorTask);
        } else {
            mDispatchQueue.postRunnable(cursorTask);
        }
    }

    protected Runnable cursorTask() {
        return DEFAULT_CURSOR_TASK;
    }

    protected abstract void setupCursorKey();

    protected abstract void setupDataKey();

    protected abstract String buildQuery();

    protected abstract Runnable callback();

    protected abstract Runnable dataTask();

}

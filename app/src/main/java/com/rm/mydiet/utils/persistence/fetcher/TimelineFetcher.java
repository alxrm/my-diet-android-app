package com.rm.mydiet.utils.persistence.fetcher;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.util.Log;

import com.rm.mydiet.model.DayPart;
import com.rm.mydiet.utils.background.DispatchQueue;
import com.rm.mydiet.utils.persistence.SQLQueryBuilder;
import com.rm.mydiet.utils.persistence.TimelineTable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.rm.mydiet.utils.StringUtils.getLongFromString;
import static com.rm.mydiet.utils.StringUtils.md5;
import static com.rm.mydiet.utils.persistence.SQLQueryBuilder.ALL;
import static com.rm.mydiet.utils.persistence.SQLQueryBuilder.EQUALS;
import static com.rm.mydiet.utils.persistence.TimelineTable.JSON_COUNT;
import static com.rm.mydiet.utils.persistence.TimelineTable.JSON_PRODUCT_ID;
import static com.rm.mydiet.utils.persistence.TimelineTable.JSON_SCALAR_ID;
import static com.rm.mydiet.utils.persistence.TimelineTable.JSON_TIME;
import static com.rm.mydiet.utils.persistence.TimelineTable.TIMELINE_TABLE;

/**
 * Created by alex
 */

@SuppressWarnings("unchecked")
public class TimelineFetcher extends BaseFetcher {

    private static final String SALT_TIMELINE_CURSOR = "timelineCursor";
    private static final String SALT_TIMELINE_DATA = "timelineData";

    public TimelineFetcher(@NonNull DispatchQueue dispatchQueue,
                           @NonNull SQLiteDatabase db) {
        super(dispatchQueue, db);
    }

    @Override
    protected String buildQuery() {
        return SQLQueryBuilder.newInstance()
                .select(ALL)
                .from(TIMELINE_TABLE)
                .where()
                .integerClause(TimelineTable.COLUMN_DAY_START, EQUALS, mQuery)
                .orderBy(new String[]{TimelineTable.COLUMN_PART_ID})
                .build();
    }

    @Override
    protected Runnable dataTask() {
        return new Runnable() {
            @Override
            public void run() {
                long day = getLongFromString(mQuery);
                Log.d("TimelineFetcher", "day " + day);
                Cursor dataCursor = (Cursor) mDataHolder.get(mCursorKey);
                Log.d("TimelineFetcher", "cursor " + dataCursor.getCount());
                mDataHolder.put(mDataKey, DayPart.getEmptyDayPartsForDay(day));

                if (dataCursor.getCount() > 0) {
                    do buildDayPart(dataCursor, day);
                    while (dataCursor.moveToNext());
                }
                dataCursor.close();
            }
        };
    }

    @Override
    protected Runnable callback() {
        return new Runnable() {
            @Override
            public void run() {
                ArrayList<DayPart> dayParts = (ArrayList<DayPart>) mDataHolder.get(mDataKey);
                mDatabaseListener.onReceiveData(dayParts);
            }
        };
    }

    private void buildDayPart(Cursor data, long day) {
        int partId = data.getInt(1);
        Log.d("TimelineFetcher", "buildDayPart PART ID " + partId);
        String dayPartJson = data.getString(2);
        DayPart root = new DayPart(partId, day);
        String hash = this.mDataKey;

        try {
            JSONArray items = new JSONArray(dayPartJson);
            JSONObject productJson;
            EatenProductFetcher eatenProductFetcher;

            for (int i = items.length() - 1; i >= 0; i--) {
                productJson = items.getJSONObject(i);
                long time = productJson.getLong(JSON_TIME);
                int count = productJson.getInt(JSON_COUNT);
                int scalarId = productJson.getInt(JSON_SCALAR_ID);
                String productId = productJson.getString(JSON_PRODUCT_ID);

                eatenProductFetcher = new EatenProductFetcher(mDispatchQueue, mDatabase, root, hash);
                eatenProductFetcher.Time = time;
                eatenProductFetcher.Count = count;
                eatenProductFetcher.ScalarId = scalarId;
                eatenProductFetcher.setQuery(productId);
                eatenProductFetcher.fetch(true);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void setupCursorKey() {
        this.mCursorKey = md5(SALT_TIMELINE_CURSOR + mQuery);
    }

    @Override
    protected void setupDataKey() {
        this.mDataKey = md5(SALT_TIMELINE_DATA + mQuery);
    }
}

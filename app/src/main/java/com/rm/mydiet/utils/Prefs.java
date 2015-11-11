package com.rm.mydiet.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.rm.mydiet.model.DBConfig;

/**
 * Created by alex
 */
public class Prefs {

    public static final String KEY_AUTO_UPDATE = "auto_update";
    public static final String KEY_DB_CONFIG_HASH = "db_config_hash";
    public static final String KEY_DB_CONFIG_COUNT = "db_config_count";
    private static final String KEY_LAST_UPDATE = "last_update";
    public static final String KEY_TIMER_OFFSET = "key_timer_offset";
    public static final String KEY_MAX_CALS = "key_max_cals";

    private static final String PREF_MAIN_NAME = "mydiet_preferences";
    private static final String TAG = "Prefs";

    private static SharedPreferences sPreferences;
    private static SharedPreferences.Editor sEditor;

    public static void init(Context context) {
        sPreferences        = context.getSharedPreferences(PREF_MAIN_NAME, Context.MODE_PRIVATE);
        sEditor             = sPreferences.edit();
    }

    //region Pref methods
    public static <T> void put(String key, T value) {
        if (value instanceof Integer) {
            sEditor.putInt(key, (Integer) value);
        }
        else if (value instanceof Long) {
            sEditor.putLong(key, (Long) value);
        }
        else if (value instanceof String) {
            sEditor.putString(key, (String) value);
        }
        else if (value instanceof Float) {
            sEditor.putFloat(key, (Float) value);
        }
        else if (value instanceof Boolean) {
            sEditor.putBoolean(key, (Boolean) value);
        }

        Log.d(TAG, "put: key(" + key + ") value(" + value + ")");
        sEditor.commit();
    }

    public static SharedPreferences get() {
        return sPreferences;
    }
    //endregion

    public static long getSavedToday() {
        return sPreferences.getLong(KEY_LAST_UPDATE, 0);
    }

    public static void saveDbConfig(DBConfig config) {
        put(KEY_DB_CONFIG_COUNT, config.getRowCount());
        put(KEY_DB_CONFIG_HASH, config.getDbHash());
    }

    public static DBConfig getSavedDatabaseConfig() {
        DBConfig config = new DBConfig();
        config.setDbHash(sPreferences.getString(KEY_DB_CONFIG_HASH, null));
        config.setProductsCount(sPreferences.getInt(KEY_DB_CONFIG_COUNT, 0));
        return config;
    }

    public static void saveToday() {
        put(KEY_LAST_UPDATE, TimeUtil.getToday());
    }

}
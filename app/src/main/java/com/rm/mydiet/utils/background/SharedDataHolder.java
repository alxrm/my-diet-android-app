package com.rm.mydiet.utils.background;

import android.support.annotation.NonNull;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by alex
 */
public final class SharedDataHolder {

    private static volatile ConcurrentHashMap<String, Object> dataHolder = null;
    private static volatile SharedDataHolder instance = null;

    public static SharedDataHolder getInstance() {

        if (instance == null) {
            synchronized (SharedDataHolder.class) {
                if (instance == null) {
                    instance = new SharedDataHolder();
                }
            }
        }

        return instance;
    }

    private SharedDataHolder() {
        dataHolder = new ConcurrentHashMap<>();
    }

    public synchronized <T> void put(String key, @NonNull T value) {
        dataHolder.put(key, value);
    }

    public synchronized Object get(String key) {
        return dataHolder.get(key);
    }

    public synchronized void clear() {
        dataHolder.clear();
    }
}

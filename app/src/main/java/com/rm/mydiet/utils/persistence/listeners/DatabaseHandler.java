package com.rm.mydiet.utils.persistence.listeners;

import android.os.Handler;
import android.os.Looper;

/**
 * Created by alex
 */
public class DatabaseHandler {

    private static Handler sHandler;

    public static void init() {
        sHandler = new Handler(Looper.getMainLooper());
    }

    public static void callback(Runnable method) {
        sHandler.post(method);
    }
}

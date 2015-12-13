package com.rm.mydiet.utils.background;

import android.os.Handler;
import android.os.Looper;

/**
 * Created by alex
 */
public class UiHandler {
    private static Handler sHandler = new Handler(Looper.getMainLooper());
    public static void remove(Runnable r) {
        sHandler.removeCallbacks(r);
    }
    public static void post(Runnable r) { sHandler.post(r); }
    public static void post(Runnable r, long delay) { sHandler.postDelayed(r, delay); }
}

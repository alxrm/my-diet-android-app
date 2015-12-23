package com.rm.mydiet.utils.background;

import android.os.Handler;
import android.os.Looper;

/**
 * Created by alex
 */
public class MainThreadHandler {

    private static volatile MainThreadHandler sInstance = null;
    private volatile Handler mHandler = null;

    public static MainThreadHandler getInstance() {
        if (sInstance == null) {
            synchronized (MainThreadHandler.class) {
                if (sInstance == null) {
                    sInstance = new MainThreadHandler();
                }
            }
        }

        return sInstance;
    }

    private MainThreadHandler() {
        mHandler = new Handler(Looper.getMainLooper());
    }

    public synchronized void postToMain(Runnable runnable) {
        postToMain(runnable, 0);
    }

    public synchronized void postToMain(Runnable runnable, long delay) {
        if (delay <= 0) {
            mHandler.post(runnable);
        } else {
            mHandler.postDelayed(runnable, delay);
        }
    }

    public synchronized void postAtFrontOfQueue(Runnable runnable) {
        mHandler.postAtFrontOfQueue(runnable);
    }

    public synchronized void cancelRunnable(Runnable runnable) {
        mHandler.removeCallbacks(runnable);
    }

    public synchronized void cleanUp() {
        mHandler.removeCallbacksAndMessages(null);
    }
}

package com.rm.mydiet.utils;

import android.os.Looper;

import com.rm.mydiet.utils.persistence.listeners.DatabaseHandler;

/**
 * Created by alex
 */
public class BackgroundTaskExecutor {

    private Runnable mBefore;
    private Runnable mTask;
    private Runnable mAfter;
    private Runnable mCallback;

    private BackgroundTaskExecutor() {}

    public static BackgroundTaskExecutor start() {
        return new BackgroundTaskExecutor();
    }

    public BackgroundTaskExecutor before(Runnable r) {
        mBefore = r;
        return this;
    }

    public BackgroundTaskExecutor task(Runnable r) {
        mTask = r;
        return this;
    }

    public BackgroundTaskExecutor after(Runnable r) {
        mAfter = r;
        return this;
    }

    public BackgroundTaskExecutor callback(Runnable r) {
        mCallback = r;
        return this;
    }

    public void execute() {
        Thread executorThread = new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                if (mBefore != null) mBefore.run();
                if (mTask != null) mTask.run();
                if (mAfter != null) mAfter.run();
                if (mCallback != null) DatabaseHandler.callback(mCallback);
            }
        });
        executorThread.start();
    }
}
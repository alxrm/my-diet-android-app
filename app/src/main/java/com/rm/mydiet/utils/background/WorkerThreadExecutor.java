package com.rm.mydiet.utils.background;

import android.os.Handler;
import android.os.Looper;

/**
 * Created by alex
 */
public class WorkerThreadExecutor {

    private static Handler sUiHandler;
    private Runnable mBefore;
    private Runnable mTask;
    private Runnable mAfter;
    private Runnable mCallback;

    private WorkerThreadExecutor() {}

    public static WorkerThreadExecutor start() {
        if (sUiHandler == null) sUiHandler = new Handler(Looper.getMainLooper());
        return new WorkerThreadExecutor();
    }

    public WorkerThreadExecutor before(Runnable r) {
        mBefore = r;
        return this;
    }

    public WorkerThreadExecutor task(Runnable r) {
        mTask = r;
        return this;
    }

    public WorkerThreadExecutor after(Runnable r) {
        mAfter = r;
        return this;
    }

    public WorkerThreadExecutor callback(Runnable r) {
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
                if (mCallback != null) sUiHandler.post(mCallback);
            }
        });
        executorThread.start();
    }
}
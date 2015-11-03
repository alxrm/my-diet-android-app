package com.rm.mydiet;

import android.app.Application;
import android.content.Context;

import com.rm.mydiet.api.WebCore;
import com.rm.mydiet.utils.Prefs;
import com.rm.mydiet.utils.persistence.listeners.DatabaseHandler;

/**
 * Created by alex
 */
public class MyDietApplication extends Application {

    private static MyDietApplication sApp;

    @Override
    public void onCreate() {
        super.onCreate();
        Prefs.init(this);
        WebCore.init(this);
        DatabaseHandler.init();
        sApp = this;
    }

    public static Context app() {
        return sApp;
    }
}

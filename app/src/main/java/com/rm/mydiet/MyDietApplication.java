package com.rm.mydiet;

import android.app.Application;
import android.content.Context;

import com.rm.mydiet.api.WebCore;
import com.rm.mydiet.utils.Prefs;

/**
 * Created by alex
 */
public class MyDietApplication extends Application {

    private static Context sContext;
    private static MyDietApplication sApplication;

    @Override
    public void onCreate() {
        super.onCreate();
        sApplication = this;
        sContext = this.getApplicationContext();
        Prefs.init(this);
        WebCore.init(this);
    }

    public static Context context() {
        if (sContext == null) sContext = sApplication.getApplicationContext();
        return sContext;
    }
}

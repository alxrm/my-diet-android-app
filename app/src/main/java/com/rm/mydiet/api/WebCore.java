package com.rm.mydiet.api;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by alex
 */
public class WebCore {

    private static final String REQ_TAG = "request_tag";
    private static Context sContext;
    private static RequestQueue sRequestQueue;

    public static void init(Context c) {
        sContext = c;
        sRequestQueue = getRequestQueue();
    }

    public static RequestQueue getRequestQueue() {
        if (sRequestQueue == null) {
            sRequestQueue = Volley.newRequestQueue(sContext.getApplicationContext());
        }
        return sRequestQueue;
    }

    public static <T> void addToRequestQueue(Request<T> req) {
        req.setTag(REQ_TAG);
        Log.d("WebCore", "addToRequestQueue - req.getUrl(): " + req.getUrl());
        getRequestQueue().add(req);
    }

    public static void cancelAll() {
        sRequestQueue.cancelAll(REQ_TAG);
    }
}

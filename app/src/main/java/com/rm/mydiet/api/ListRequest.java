package com.rm.mydiet.api;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.rm.mydiet.model.Product;

import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.util.ArrayList;

/**
 * Created by alex
 */
public class ListRequest extends Request<ArrayList<Product>> {

    private static final String UTF_8 = "UTF-8";
    private final Gson mGson = new Gson();
    private final Type mType;
    private final Listener<ArrayList<Product>> mListener;

    public ListRequest(String url, Listener<ArrayList<Product>> listener,
                       ErrorListener errorListener) {
        super(Method.GET, url, errorListener);
        mListener = listener;
        mType = new TypeToken<ArrayList<Product>>() {}.getType();
    }

    @Override
    protected void deliverResponse(ArrayList<Product> response) {
        mListener.onResponse(response);
    }

    @Override
    protected Response<ArrayList<Product>> parseNetworkResponse(NetworkResponse response) {
        try {
            String json = new String(response.data, Charset.forName(UTF_8));
            ArrayList<Product> result = mGson.fromJson(json, mType);
            return Response.success(result, HttpHeaderParser.parseCacheHeaders(response));
        } catch (JsonSyntaxException e) {
            return Response.error(new ParseError(e));
        }
    }
}
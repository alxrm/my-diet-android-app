package com.rm.mydiet.api;

import android.util.Log;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.JsonSyntaxException;
import com.rm.mydiet.model.DBConfig;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.Charset;

/**
 * Created by alex
 */
public class ConfigRequest extends Request<DBConfig> {

    private static final String UTF_8 = "UTF-8";
    private static final String DB_V = "db_v";
    private static final String PRODUCTS_COUNT = "products_count";
    private Response.Listener<DBConfig> mListener;

    public ConfigRequest(String url, Response.Listener<DBConfig> listener,
                       Response.ErrorListener errorListener) {
        super(Method.GET, url, errorListener);
        mListener = listener;
    }

    @Override
    protected void deliverResponse(DBConfig response) {
        mListener.onResponse(response);
    }

    @Override
    protected Response<DBConfig> parseNetworkResponse(NetworkResponse response) {
        try {
            String json = new String(response.data, Charset.forName(UTF_8));
            Log.d("ConfigRequest", "parseNetworkResponse - json: " + json);
            JSONObject jsonObj = new JSONObject(json);

            DBConfig config = new DBConfig();
            String dbHash = jsonObj.getString(DB_V);
            int productsCount = jsonObj.getInt(PRODUCTS_COUNT);

            config.setDbHash(dbHash);
            config.setProductsCount(productsCount);

            return Response.success(config, HttpHeaderParser.parseCacheHeaders(response));
        } catch (JsonSyntaxException e) {
            return Response.error(new ParseError(e));
        } catch (JSONException e) {
            return Response.error(new ParseError(e));
        }
    }
}
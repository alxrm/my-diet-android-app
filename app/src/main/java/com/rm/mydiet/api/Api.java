package com.rm.mydiet.api;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.rm.mydiet.model.DBConfig;
import com.rm.mydiet.model.Product;
import com.rm.mydiet.utils.StringUtils;

import java.util.ArrayList;

/**
 * Created by alex
 */
public class Api {

    private static final String BASE_URL = "http://abovyan.me/products/";
    private static final String IMAGE_URL = "http://abovyan.me/productsStatic/img/";
    private static final String GET_CONFIG_METHOD = "getConfig";
    private static final String GET_PRODUCTS_METHOD = "getProducts";
    private static final String SALT = "kQDJAOXMtOFEP8fEYhLl";

    public static void getProducts(Listener<ArrayList<Product>> callback, ErrorListener error) {
        ListRequest request = new ListRequest(getProductsUrl(), callback, error);
        WebCore.addToRequestQueue(request);
    }

    public static void getConfig(Listener<DBConfig> callback, ErrorListener error) {
        ConfigRequest request = new ConfigRequest(getConfigUrl(), callback, error);
        WebCore.addToRequestQueue(request);
    }

    public static String getImageUrl(String imageCode) {
        return IMAGE_URL + imageCode;
    }

    private static String getConfigUrl() {
        return BASE_URL + GET_CONFIG_METHOD;
    }

    private static String getProductsUrl() {
        String hash = StringUtils.getRandomHash(7);
        String md5 = StringUtils.md5(hash + SALT);
        return BASE_URL + GET_PRODUCTS_METHOD + "/" + hash + "/" + md5;
    }
}

package com.rm.mydiet.api;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.rm.mydiet.model.DBConfig;
import com.rm.mydiet.model.Product;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by alex
 */
public class Api {

    private static final String BASE_URL = "http://altox.tk/products/";
    private static final String IMAGE_URL = "http://altox.tk/productsStatic/img/";
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
        String hash = getRandomHash(7);
        String md5 = md5(hash + SALT);
        return BASE_URL + GET_PRODUCTS_METHOD + "/" + hash + "/" + md5;
    }

    private static String getRandomHash(int len) {
        String possible = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        Random rnd = new Random();
        StringBuilder sb = new StringBuilder(len);
        for( int i = 0; i < len; i++ )
            sb.append(possible.charAt(rnd.nextInt(possible.length())));
        return sb.toString();
    }

    private static String md5(String src) {
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update(src.getBytes(), 0, src.length());
            return new BigInteger(1, digest.digest()).toString(16);
        } catch (Throwable e) {
            e.printStackTrace();
            throw new RuntimeException("Cannot assembly md5 hash");
        }
    }
}

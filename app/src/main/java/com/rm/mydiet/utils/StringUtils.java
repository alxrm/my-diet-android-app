package com.rm.mydiet.utils;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.Random;

/**
 * Created by alex
 */
public class StringUtils {

    public static String getRandomHash(int len) {
        String possible = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        Random rnd = new Random();
        StringBuilder sb = new StringBuilder(len);
        for( int i = 0; i < len; i++ )
            sb.append(possible.charAt(rnd.nextInt(possible.length())));
        return sb.toString();
    }

    public static String md5(String src) {
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

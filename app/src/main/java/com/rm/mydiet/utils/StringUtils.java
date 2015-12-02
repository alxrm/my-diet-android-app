package com.rm.mydiet.utils;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Random;

/**
 * Created by alex
 */
public class StringUtils {
    private static final DecimalFormatSymbols sDecimalSymbols =
            new DecimalFormatSymbols();
    private static final DecimalFormat sDecimalFormat =
            new DecimalFormat();

    static {
        sDecimalSymbols.setDecimalSeparator('.');
        sDecimalSymbols.setGroupingSeparator(',');

        sDecimalFormat.setGroupingSize(3);
        sDecimalFormat.applyPattern("#,###.#");
        sDecimalFormat.setDecimalFormatSymbols(sDecimalSymbols);
    }

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

    public static String formatFloat(float f) {
        return sDecimalFormat.format(f);
    }
}

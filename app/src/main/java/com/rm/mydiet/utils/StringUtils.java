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
    private static final String DEFAULT_PATTERN = "#,###.#";

    static {
        sDecimalSymbols.setDecimalSeparator('.');
        sDecimalSymbols.setGroupingSeparator(',');

        sDecimalFormat.setGroupingSize(3);
        sDecimalFormat.applyPattern(DEFAULT_PATTERN);
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
        sDecimalFormat.applyPattern(DEFAULT_PATTERN);
        return sDecimalFormat.format(f);
    }

    public static String formatFloat(float f, int toFixed) {
        if (toFixed > 10 || toFixed < 0) return formatFloat(f);

        String pattern = "#,###";
        if (toFixed > 0) {
            pattern += ".";
            for (int i = 0; i < toFixed; i++) pattern += "#";
        }

        sDecimalFormat.applyPattern(pattern);
        return sDecimalFormat.format(f);
    }
}

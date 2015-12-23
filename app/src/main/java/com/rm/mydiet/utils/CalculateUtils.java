package com.rm.mydiet.utils;

/**
 * Created by alex
 */
public class CalculateUtils {
    public static int calcProgress(int cur, int max) {
        return (int) ((float) cur / max * 100);
    }
}

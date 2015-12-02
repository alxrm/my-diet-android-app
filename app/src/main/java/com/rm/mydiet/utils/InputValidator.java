package com.rm.mydiet.utils;

/**
 * Created by alex
 */
public interface InputValidator {
    interface IntegerValidator {
        boolean isValid(int data);
    }

    interface StringValidator {
        boolean isValid(String data);
    }

    interface StringFilter {
        String filter(String invalidString);
    }
}


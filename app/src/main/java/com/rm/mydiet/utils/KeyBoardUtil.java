package com.rm.mydiet.utils;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

/**
 * Created by alex
 */
public class KeyBoardUtil {
    public static void hide(View focusedView, Activity activity) {
        if (focusedView != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(focusedView.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            if (focusedView.hasFocus()) {
                focusedView.clearFocus();
            }
        }

    }
    public static void hide(Activity activity){
        if (activity != null) {
            View focusedView = activity.getCurrentFocus();
            if (focusedView != null) {
                InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(focusedView.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                focusedView.clearFocus();
            }
        }
    }
}
package com.rm.mydiet.ui;

/**
 * Created by alex
 */
public interface OnFragmentInteractionListener {

    int FRAGMENT_TIMER = 1000;
    int FRAGMENT_DAIRY = 1001;
    int FRAGMENT_PROD_INFO = 1010;

    <T> void onFragmentAction(T data, int key);
}

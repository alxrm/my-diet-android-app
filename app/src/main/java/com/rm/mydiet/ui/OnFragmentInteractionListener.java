package com.rm.mydiet.ui;

/**
 * Created by alex
 */
public interface OnFragmentInteractionListener {

    String FRAGMENT_TIMER = "timer";
    String FRAGMENT_DAIRY = "diary_add";
    String FRAGMENT_DAIRY_LIST = "diary_list";
    String FRAGMENT_DIARY_EATEN_PRODUCT_CREATED = "diary_edit";

    <T> void onFragmentAction(T data, String key);
}

package com.rm.mydiet.ui;

/**
 * Created by alex
 */
public interface DataTransferring {

    // add product activity
    int ACTIVITY_EDIT_CODE_RESULT = 1001;
    int ACTIVITY_EDIT_CODE_REQUEST = 1011;
    String ACTIVITY_EDIT_KEY_RESULT_DATA = "edit_result_data";
    String ACTIVITY_EDIT_KEY_DAY_PART = "edit_day_part";
    String ACTIVITY_EDIT_KEY_EATEN_PRODUCT = "edit_eaten_product";

    // add product activity
    int ACTIVITY_ADD_CODE_RESULT = 1000;
    int ACTIVITY_ADD_CODE_REQUEST = 1010;
    String ACTIVITY_ADD_KEY_RESULT_DATA = "add_result_data";
    String ACTIVITY_ADD_KEY_TIME = "add_time";
    String ACTIVITY_ADD_KEY_DAY_PART = "add_day_part";

    // day fragment
    String FRAGMENT_DAY_KEY_START = "day_day";

    // diary fragment
    String FRAGMENT_DIARY_KEY_DAY_PART = "diary_day_part";
    String FRAGMENT_DIARY_KEY_CALORIES = "diary_calories";
    String CALLBACK_DIARY_EATEN_PRODUCT = "diary_callback_eaten_product";
    String CALLBACK_DIARY_DAY_PART = "diary_callback_day_part";
    String CALLBACK_DIARY_TIME = "diary_callback_time";

    // timer fragment
    String FRAGMENT_TIMER_KEY_DAY_PART = "timer_day_part";
    String FRAGMENT_TIMER_KEY_CALORIES = "timer_calories";

    // product info fragment
    String FRAGMENT_PRODUCT_INFO_KEY_DAY_PART = "product_info_day_part";
    String FRAGMENT_PRODUCT_INFO_KEY_PRODUCT = "product_info_product";
    String FRAGMENT_PRODUCT_INFO_KEY_EATEN_PRODUCT = "product_info_eaten";
    String CALLBACK_PRODUCT_INFO_EATEN_PRODUCT = "product_info_callback_eaten";
    String CALLBACK_PRODUCT_INFO_DAY_PART = "product_info_callback_day_part";



    // product info parent
    String PARENT_PRODUCT_INFO_DAY_PART = "parent_product_info_day_part";
    String PARENT_PRODUCT_INFO_TIME = "parent_product_info_time";
}

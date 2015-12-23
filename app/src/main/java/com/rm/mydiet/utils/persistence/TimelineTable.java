package com.rm.mydiet.utils.persistence;

import static com.rm.mydiet.utils.persistence.SQLQueryBuilder.DEFAULT;
import static com.rm.mydiet.utils.persistence.SQLQueryBuilder.NOT;
import static com.rm.mydiet.utils.persistence.SQLQueryBuilder.NULL;
import static com.rm.mydiet.utils.persistence.SQLQueryBuilder.TYPE_INT;
import static com.rm.mydiet.utils.persistence.SQLQueryBuilder.TYPE_TEXT;

/**
 * Created by alex
 */
public interface TimelineTable {
    String TIMELINE_TABLE = "dayparts";

    String COLUMN_PRODUCTS = "products";
    String COLUMN_DAY_START = "day_start";
    String COLUMN_PART_ID = "part_id";

    String JSON_TIME = "time";
    String JSON_COUNT = "count";
    String JSON_SCALAR_ID = "scalar_id";
    String JSON_PRODUCT_ID = "prod_id";

    String CREATE = SQLQueryBuilder.newInstance()
            .create(TIMELINE_TABLE)
            .columnsStart()
            .column(COLUMN_DAY_START, new String[]{TYPE_TEXT, NOT + NULL})
            .column(COLUMN_PART_ID, new String[]{TYPE_INT, NOT + NULL})
            .column(COLUMN_PRODUCTS, new String[]{TYPE_TEXT, DEFAULT + " \"[]\" "})
            .columnsEnd()
            .build();
}

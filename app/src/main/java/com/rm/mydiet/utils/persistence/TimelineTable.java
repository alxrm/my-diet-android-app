package com.rm.mydiet.utils.persistence;

import static com.rm.mydiet.utils.persistence.SQLQueryBuilder.NOT;
import static com.rm.mydiet.utils.persistence.SQLQueryBuilder.NULL;
import static com.rm.mydiet.utils.persistence.SQLQueryBuilder.PRIMARY_KEY;
import static com.rm.mydiet.utils.persistence.SQLQueryBuilder.TYPE_INT;
import static com.rm.mydiet.utils.persistence.SQLQueryBuilder.TYPE_TEXT;

/**
 * Created by alex
 */
public interface TimelineTable {
    String TIMELINE_TABLE = "timeline";

    String COLUMN_PRODUCT_ID  = "id";
    String COLUMN_DAYPART = "daypart";
    String COLUMN_TIME = "time";

    String CREATE = SQLQueryBuilder.getInstance()
            .create(TIMELINE_TABLE)
            .columnsStart()
            .column(COLUMN_TIME, new String[] {TYPE_INT, NOT+NULL, PRIMARY_KEY})
            .column(COLUMN_DAYPART, new String[] {TYPE_INT, NOT+NULL})
            .column(COLUMN_PRODUCT_ID, new String[] {TYPE_TEXT, NOT+NULL})
            .columnsEnd()
            .build();
}

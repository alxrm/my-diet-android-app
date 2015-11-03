package com.rm.mydiet.utils.persistence;

import static com.rm.mydiet.utils.persistence.SQLQueryBuilder.NOT;
import static com.rm.mydiet.utils.persistence.SQLQueryBuilder.NULL;
import static com.rm.mydiet.utils.persistence.SQLQueryBuilder.PRIMARY_KEY;
import static com.rm.mydiet.utils.persistence.SQLQueryBuilder.TYPE_TEXT;

/**
 * Created by alex
 */
public interface ProductsTable {
    String PRODUCTS_TABLE = "products";

    String COLUMN_ID  = "id";
    String COLUMN_NAME = "name";
    String COLUMN_PROTEINS = "proteins";
    String COLUMN_FATS = "fats";
    String COLUMN_CARBOHYDRATES = "carbohydrates";
    String COLUMN_CALORIES = "calories";
    String COLUMN_INFO = "info";
    String COLUMN_IMG = "img";

    String CREATE = SQLQueryBuilder.getInstance()
            .create(PRODUCTS_TABLE)
            .columnsStart()
            .column(COLUMN_ID, new String[] {TYPE_TEXT, NOT+NULL, PRIMARY_KEY})
            .column(COLUMN_NAME, new String[] {TYPE_TEXT, NOT+NULL})
            .column(COLUMN_PROTEINS, new String[] {TYPE_TEXT, NOT+NULL})
            .column(COLUMN_FATS, new String[] {TYPE_TEXT, NOT+NULL})
            .column(COLUMN_CARBOHYDRATES, new String[] {TYPE_TEXT, NOT+NULL})
            .column(COLUMN_CALORIES, new String[] {TYPE_TEXT, NOT+NULL})
            .column(COLUMN_INFO, new String[] {TYPE_TEXT, NOT+NULL})
            .column(COLUMN_IMG, new String[] {TYPE_TEXT, NOT+NULL})
            .columnsEnd()
            .build();
}

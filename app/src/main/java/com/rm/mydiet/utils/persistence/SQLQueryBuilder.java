package com.rm.mydiet.utils.persistence;

import android.support.annotation.NonNull;
import android.support.v4.util.Pair;

import java.util.ArrayList;

/**
 * Created by alex
 */
public class SQLQueryBuilder {

    //region Integer operations
    public static final String BIGGER = " > ";
    public static final String LESS = " < ";
    public static final String LESS_OR_EQUALS = " <= ";
    public static final String BIGGER_OR_EQUALS = " >= ";
    public static final String EQUALS = " = ";
    public static final String NOT_EQUALS = " != ";
    //endregion

    //region Boolean operations
    public static final String NOT = " NOT ";
    private static final String AND = " AND ";
    private static final String OR = " OR ";
    //endregion

    //region String operations
    public static final String LIKE = " LIKE ";
    private static final String STRING_START = " \"%";
    private static final String STRING_END = "%\" ";
    //endregion

    //region SQL keywords
    public static final String ALL = "*";
    public static final String PRIMARY_KEY = " PRIMARY KEY ";
    private static final String SPACE = " ";
    private static final String COMA = ", ";
    private static final String START_PARAMS = " ( ";
    private static final String END_PARAMS = " ) ";
    private static final String SELECT = " SELECT ";
    private static final String FROM = " FROM ";
    private static final String WHERE = " WHERE ";
    private static final String TABLE = " TABLE ";
    private static final String CREATE = " CREATE ";
    private static final String ORDER_BY = " ORDER BY ";
    private static final String INNER_JOIN = " INNER JOIN ";
    private static final String ON = " ON ";
    private static final String DROP = " DROP ";
    private static final String DELETE = " DELETE ";
    //endregion

    //region Data types
    public static final String TYPE_TEXT = " TEXT ";
    public static final String TYPE_INT = " INTEGER ";
    public static final String NULL = " NULL ";
    public static final String DEFAULT = " DEFAULT ";
    //endregion

    private volatile StringBuilder mBuilder = null;

    private SQLQueryBuilder() {
        mBuilder = new StringBuilder();
    }

    public static SQLQueryBuilder newInstance() {
        return new SQLQueryBuilder();
    }

    private void removeLastComa() {
        mBuilder.deleteCharAt(mBuilder.length() - 2);
    }

    //region Create table
    public SQLQueryBuilder create(String tableName) {
        mBuilder.append(CREATE).append(TABLE).append(tableName);
        return this;
    }

    public SQLQueryBuilder columnsStart() {
        mBuilder.append(START_PARAMS);
        return this;
    }

    /**
     * @param columnParams { TYPE, NULL/NOT NULL, PRIMARY_KEY }
     */
    public SQLQueryBuilder column(@NonNull String name, String[] columnParams) {
        mBuilder.append(name);
        if (columnParams != null)
            for (String param : columnParams) mBuilder.append(param);
        mBuilder.append(COMA);
        return this;
    }

    public SQLQueryBuilder columnsEnd() {
        removeLastComa();
        mBuilder.append(END_PARAMS);
        return this;
    }
    //endregion

    //region Select
    public SQLQueryBuilder select(String criteria) {
        mBuilder.append(SELECT).append(criteria == null ? ALL : criteria);
        return this;
    }

    /**
     * <Table, Column>
     */
    public SQLQueryBuilder select(ArrayList<Pair<String, String>> tableColumn) {
        mBuilder.append(SELECT);
        if (tableColumn != null && !tableColumn.isEmpty()) {
            for (Pair<String, String> tabCol : tableColumn) {
                mBuilder.append(tabCol.first)
                        .append(".")
                        .append(tabCol.second)
                        .append(COMA);
            }
            removeLastComa();
        } else {
            mBuilder.append(ALL);
        }
        return this;
    }

    public SQLQueryBuilder from(@NonNull String table) {
        mBuilder.append(FROM).append(table);
        return this;
    }

    public SQLQueryBuilder where() {
        mBuilder.append(WHERE);
        return this;
    }

    public SQLQueryBuilder integerClause(@NonNull String column,
                                         @NonNull String operators,
                                         @NonNull String operand) {
        mBuilder.append(column)
                .append(operators)
                .append(operand);
        return this;
    }

    public SQLQueryBuilder stringClause(@NonNull String column,
                                        @NonNull String operators,
                                        @NonNull String operand) {
        mBuilder.append(column)
                .append(operators)
                .append(STRING_START)
                .append(operand)
                .append(STRING_END);
        return this;
    }

    public SQLQueryBuilder and() {
        mBuilder.append(AND);
        return this;
    }

    public SQLQueryBuilder or() {
        mBuilder.append(OR);
        return this;
    }

    public SQLQueryBuilder orderBy(@NonNull String[] columns) {
        if (columns.length == 0)
            throw new IllegalArgumentException("Must be at least one column");

        mBuilder.append(ORDER_BY);
        for (String col : columns) mBuilder.append(col).append(COMA);
        removeLastComa();
        return this;
    }
    //endregion

    //region Inner join
    /**
     * SELECT Orders.OrderID, Customers.CustomerName, Orders.OrderDate
     * FROM Orders
     * INNER JOIN Customers
     * ON Orders.CustomerID=Customers.CustomerID;
     */
    public SQLQueryBuilder join(@NonNull String tableName) {
        mBuilder.append(INNER_JOIN).append(tableName);
        return this;
    }

    public SQLQueryBuilder on(@NonNull String lCol,
                                           @NonNull String operator,
                                           @NonNull String rCol) {
        mBuilder.append(ON).append(lCol).append(operator).append(rCol);
        return this;
    }
    //endregion

    public SQLQueryBuilder drop(String tableName) {
        mBuilder.append(DROP).append(TABLE).append(tableName);
        return this;
    }

    public SQLQueryBuilder delete(String tableName) {
        mBuilder.append(DELETE).append(FROM).append(tableName);
        return this;
    }

    public String build() {
        String result = mBuilder.toString().trim().replace(" +", SPACE);
        mBuilder = new StringBuilder();
        return result;
    }
}

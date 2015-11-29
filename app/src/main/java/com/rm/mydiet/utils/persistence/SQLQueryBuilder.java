package com.rm.mydiet.utils.persistence;

import android.support.annotation.NonNull;
import android.support.v4.util.Pair;
import android.util.Log;

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

    private static final ThreadLocal<StringBuilder> sBuilder = new ThreadLocal<>();

    private SQLQueryBuilder() {
        sBuilder.set(new StringBuilder());
    }

    public static SQLQueryBuilder getInstance() {
        return new SQLQueryBuilder();
    }

    private static void removeLastComa() {
        sBuilder.get().deleteCharAt(sBuilder.get().length() - 2);
    }

    //region Create table
    public SQLQueryBuilder create(String tableName) {
        sBuilder.get().append(CREATE).append(TABLE).append(tableName);
        return this;
    }

    public SQLQueryBuilder columnsStart() {
        sBuilder.get().append(START_PARAMS);
        return this;
    }

    /**
     * @param columnParams { TYPE, NULL/NOT NULL, PRIMARY_KEY }
     */
    public SQLQueryBuilder column(@NonNull String name, String[] columnParams) {
        sBuilder.get().append(name);
        if (columnParams != null)
            for (String param : columnParams) sBuilder.get().append(param);
        sBuilder.get().append(COMA);
        return this;
    }

    public SQLQueryBuilder columnsEnd() {
        removeLastComa();
        sBuilder.get().append(END_PARAMS);
        return this;
    }
    //endregion

    //region Select
    public SQLQueryBuilder select(String criteria) {
        sBuilder.get().append(SELECT).append(criteria == null ? ALL : criteria);
        return this;
    }

    /**
     * <Table, Column>
     */
    public SQLQueryBuilder select(ArrayList<Pair<String, String>> tableColumn) {
        sBuilder.get().append(SELECT);
        if (tableColumn != null && !tableColumn.isEmpty()) {
            for (Pair<String, String> tabCol : tableColumn) {
                sBuilder.get().append(tabCol.first)
                        .append(".")
                        .append(tabCol.second)
                        .append(COMA);
            }
            removeLastComa();
        } else {
            sBuilder.get().append(ALL);
        }
        return this;
    }

    public SQLQueryBuilder from(@NonNull String table) {
        sBuilder.get().append(FROM).append(table);
        return this;
    }

    public SQLQueryBuilder where() {
        sBuilder.get().append(WHERE);
        return this;
    }

    public SQLQueryBuilder integerClause(@NonNull String column,
                                         @NonNull String operators,
                                         long operand) {
        sBuilder.get().append(column)
                .append(operators)
                .append(operand);
        return this;
    }

    public SQLQueryBuilder stringClause(@NonNull String column,
                                        @NonNull String operators,
                                        @NonNull String operand) {
        sBuilder.get().append(column)
                .append(operators)
                .append(STRING_START)
                .append(operand)
                .append(STRING_END);
        return this;
    }

    public SQLQueryBuilder and() {
        sBuilder.get().append(AND);
        return this;
    }

    public SQLQueryBuilder or() {
        sBuilder.get().append(OR);
        return this;
    }

    public SQLQueryBuilder orderBy(@NonNull String[] columns) {
        if (columns.length == 0)
            throw new IllegalArgumentException("Must be at least one column");

        sBuilder.get().append(ORDER_BY);
        for (String col : columns) sBuilder.get().append(col).append(COMA);
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
        sBuilder.get().append(INNER_JOIN).append(tableName);
        return this;
    }

    public SQLQueryBuilder on(@NonNull String lCol,
                              @NonNull String operator,
                              @NonNull String rCol) {
        sBuilder.get().append(ON).append(lCol).append(operator).append(rCol);
        return this;
    }
    //endregion

    public SQLQueryBuilder drop(String tableName) {
        sBuilder.get().append(DROP).append(TABLE).append(tableName);
        return this;
    }

    public SQLQueryBuilder delete(String tableName) {
        sBuilder.get().append(DELETE).append(FROM).append(tableName);
        return this;
    }

    public String build() {
        String q = sBuilder.get().toString().trim().replace(" +", SPACE);
        Log.d("SQLQueryBuilder", "query: " + q);
        return q;
    }
}

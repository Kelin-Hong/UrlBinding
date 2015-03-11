package com.kelin.library.dao;

import android.content.ContentValues;

/**
 * Created by kelin on 15-3-6.
 */
public class ContentValueUtils {
    public final static String COLUMN_PREFIX = "tbl_";

    public static void insertContentValues(ContentValues values, String key, Object object) {
        String columnName = new StringBuffer(COLUMN_PREFIX).append(key).toString();
        if (object instanceof String) {
            values.put(columnName, (String) object);
        }
        if (object instanceof Boolean) {
            values.put(columnName, (Boolean) object);
        } else if (object instanceof Integer) {
            values.put(columnName, (Integer) object);
        } else if (object instanceof Long) {
            values.put(columnName, (Long) object);
        } else if (object instanceof Float) {
            values.put(columnName, (Float) object);
        } else if (object instanceof Double) {
            values.put(columnName, (Double) object);
        }
    }

}

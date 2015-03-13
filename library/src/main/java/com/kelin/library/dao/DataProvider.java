package com.kelin.library.dao;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.provider.BaseColumns;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class DataProvider extends ContentProvider {
    private static final String TAG = "DataProvider";

    static final Object DBLock = new Object();

    public static final String AUTHORITY = "com.kelin.project";
    public static final String SCHEME = "content://";
    public static final String URI_MATCHER_BANNER = "/uri/matcher";

    public static DBHelper mDBHelper;

    public static UriMatcher uriMatcher;

    public static final String COLUMN_URI_MD5 = "tbl_" + "uri_binding" + "_url_md5";

    public static final String COLUMN_URI_ID = "tbl_" + "uri_binding" + "_url_id";

    public static final String COLUMN_TABLE_NAME = "tbl_" + "uri_binding" + "_table_name";

    public static final Uri URI_MATCHER_URI = Uri.parse(SCHEME + AUTHORITY + URI_MATCHER_BANNER);


    public static HashMap<Integer, String> tableNameMap = new HashMap<>();

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTHORITY, "uri/matcher", 0);
        tableNameMap.put(0, "uri_matcher");
    }

    @Override
    public boolean onCreate() {
        DBHelper.DB_NAME = "UriBinding.db";
        mDBHelper = new DBHelper(getContext());
        return true;
    }

    public static void addUriMatcherToDB(Context context, Uri dataUri, String tableName) {
        Cursor cursor = context.getContentResolver().query(DataProvider.URI_MATCHER_URI, null, "table_name = ?", new String[]{tableName}, null);
        if (cursor.getCount() > 0) {
            cursor.close();
            return;
        }
        cursor.close();
        List<String> paths = dataUri.getPathSegments();
        StringBuffer path = new StringBuffer();
        for (String pathItem : paths) {
            try {
                Integer.parseInt(pathItem);
                path.append("#/");
            } catch (NumberFormatException e) {
                path.append(pathItem + '/');
            }
        }
        int code = DataProvider.tableNameMap.size();
        DataProvider.uriMatcher.addURI(DataProvider.AUTHORITY, path.toString(), code);
        DataProvider.uriMatcher.addURI(DataProvider.AUTHORITY, path.toString() + "#", code);
        DataProvider.tableNameMap.put(code, tableName);
        ContentValues uriValues = new ContentValues();
        uriValues.put("path", path.toString());
        uriValues.put("code", code);
        uriValues.put("table_name", tableName);
        context.getContentResolver().insert(DataProvider.URI_MATCHER_URI, uriValues);
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        synchronized (DBLock) {
            int code = uriMatcher.match(uri);
            String tableName = tableNameMap.get(code);
            if (tableName == null && tableName.length() <= 0) {
                return null;
            }
            SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
            queryBuilder.setTables(tableName);
            boolean isDir = true;
            int id = -1;
            try {
                String s = uri.getLastPathSegment();
                id = Integer.parseInt(s);
                isDir = false;
            } catch (NumberFormatException e) {

            }
            if (!isDir) {
                queryBuilder.appendWhere(BaseColumns._ID + "=" + id);
            }
            SQLiteDatabase db = mDBHelper.getReadableDatabase();

            Cursor cursor = queryBuilder.query(db, // The database to query
                    projection, // The columns to return from the query
                    selection, // The columns for the where clause
                    selectionArgs, // The values for the where clause
                    null, // don't group the rows
                    null, // don't filter by row groups
                    sortOrder // The sort order
            );
//            cursor.setNotificationUri(getContext().getContentResolver(), uri);
            return cursor;
        }
    }

    public static String getTableNameByUri(Uri uri) {
        int code = uriMatcher.match(uri);
        String tableName = tableNameMap.get(code);
        return tableName;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) throws SQLException {
        synchronized (DBLock) {
            int code = uriMatcher.match(uri);
            String table = tableNameMap.get(code);
            if (table == null && table.length() <= 0) {
                return uri;
            }
            SQLiteDatabase db = mDBHelper.getWritableDatabase();
            if (!isTableExists(table)) {
                createTable(db, table, values);
            }
            long rowId = 0;
            db.beginTransaction();
            try {
                rowId = db.insertWithOnConflict(table, BaseColumns._ID, values, SQLiteDatabase.CONFLICT_REPLACE);
                db.setTransactionSuccessful();
            } catch (SQLException e) {
                if (e.getMessage().contains("has no column named")) {
                    addColumn(db, table, values);
                    rowId = db.insertWithOnConflict(table, BaseColumns._ID, values, SQLiteDatabase.CONFLICT_REPLACE);
                    db.setTransactionSuccessful();
                }
                Log.e(TAG, e.getMessage());
            } finally {
                db.endTransaction();
            }
            if (rowId > 0) {
                Uri returnUri = ContentUris.withAppendedId(uri, rowId);
                Log.v("Uri-Change", "insert" + returnUri.toString());
                getContext().getContentResolver().notifyChange(returnUri, null);
                return returnUri;
            }
            throw new SQLException("Failed to insert row into " + uri);
        }
    }

    private void createTable(SQLiteDatabase db, String tableName, ContentValues values) {
        db.execSQL("PRAGMA foreign_keys=ON");
        Iterator<Map.Entry<String, Object>> iterator = values.valueSet().iterator();
        SQLiteTable sqLiteTable = new SQLiteTable(tableName);
        while (iterator.hasNext()) {
            Map.Entry<String, Object> entry = iterator.next();
            if (entry.getKey().equals(DataProvider.COLUMN_URI_MD5)) {
                if (values.keySet().contains(COLUMN_URI_ID)) {
                    sqLiteTable.addColumn(new ColumnsDefinition(entry.getKey(), null, ColumnsDefinition.DataType.TEXT));
                } else {
                    sqLiteTable.addColumn(new ColumnsDefinition(entry.getKey(), ColumnsDefinition.Constraint.UNIQUE, ColumnsDefinition.DataType.TEXT));
                }
                continue;
            }
            if (entry.getKey().equals(DataProvider.COLUMN_URI_ID)) {
                ColumnsDefinition columnsDefinition = new ColumnsDefinition(entry.getKey(), ColumnsDefinition.Constraint.FOREIGN_KEY_REFERENCE, ColumnsDefinition.DataType.INTEGER);
                String parentTableName = tableName.substring(0, tableName.indexOf("_"));
                columnsDefinition.setForeignKey(" references " + parentTableName + "(_id)  on delete cascade on update cascade");
                sqLiteTable.addColumn(columnsDefinition);
                continue;
            }
            if (entry.getValue() instanceof String) {
                sqLiteTable.addColumn(new ColumnsDefinition(entry.getKey(), null, ColumnsDefinition.DataType.TEXT));
            } else if (entry.getValue() instanceof Long || entry.getValue() instanceof Integer) {
                sqLiteTable.addColumn(new ColumnsDefinition(entry.getKey(), null, ColumnsDefinition.DataType.INTEGER));
            } else if (entry.getValue() instanceof Boolean) {
                sqLiteTable.addColumn(new ColumnsDefinition(entry.getKey(), null, ColumnsDefinition.DataType.BLOB));
            } else if (entry.getValue() instanceof Double || entry.getValue() instanceof Float) {
                sqLiteTable.addColumn(new ColumnsDefinition(entry.getKey(), null, ColumnsDefinition.DataType.REAL));
            } else {
                sqLiteTable.addColumn(new ColumnsDefinition(entry.getKey(), null, ColumnsDefinition.DataType.NULL));
            }
        }
        sqLiteTable.create(db);
    }

    private void addColumn(SQLiteDatabase db, String tableName, ContentValues values) {
        Iterator<Map.Entry<String, Object>> iterator = values.valueSet().iterator();
        List<String> columnsName = getColumns(db, tableName);
        while (iterator.hasNext()) {
            Map.Entry<String, Object> entry = iterator.next();
            String type = ColumnsDefinition.DataType.TEXT.name();
            if (entry.getValue() instanceof String) {
                type = ColumnsDefinition.DataType.TEXT.name();
            } else if (entry.getValue() instanceof Long || entry.getValue() instanceof Integer) {
                type = ColumnsDefinition.DataType.INTEGER.name();
            } else if (entry.getValue() instanceof Boolean) {
                type = ColumnsDefinition.DataType.BLOB.name();
            } else if (entry.getValue() instanceof Double || entry.getValue() instanceof Float) {
                type = ColumnsDefinition.DataType.REAL.name();
            } else {
                type = ColumnsDefinition.DataType.NULL.name();
            }
            if (!columnsName.contains(entry.getKey())) {
                db.execSQL("ALTER TABLE " + tableName + " ADD COLUMN " + entry.getKey() + " " + type);
            }
        }
    }

    public List<String> getColumns(SQLiteDatabase db, String tableName) {
        List<String> ar = null;
        Cursor c = null;
        try {
            c = db.rawQuery("select * from " + tableName + " limit 1", null);
            if (c != null) {
                ar = new ArrayList<String>(Arrays.asList(c.getColumnNames()));
            }
        } catch (Exception e) {
            Log.v(tableName, e.getMessage(), e);
            e.printStackTrace();
        } finally {
            if (c != null)
                c.close();
        }
        return ar;
    }


    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        synchronized (DBLock) {
            int code = uriMatcher.match(uri);
            String tableName = tableNameMap.get(code);
            if (tableName == null && tableName.length() <= 0) {
                return -1;
            }
            SQLiteDatabase db = mDBHelper.getWritableDatabase();
            db.execSQL("PRAGMA foreign_keys=ON");
            boolean isDir = true;
            String id = null;
            try {
                String s = uri.getLastPathSegment();
                id = Integer.parseInt(s) + "";
                isDir = false;
            } catch (NumberFormatException e) {

            }
            String finalWhere = null;
            String finalWhereArgs[] = null;
            if (!isDir) {
                if (selection != null) {
                    finalWhere = new StringBuffer(selection).append(" AND " + BaseColumns._ID + "= ?").toString();
                    finalWhereArgs = new String[selectionArgs.length + 1];
                    for (int i = 0; i < selectionArgs.length; i++) {
                        finalWhereArgs[i] = selectionArgs[i];
                    }
                    finalWhereArgs[selectionArgs.length] = id;
                } else {
                    finalWhere = BaseColumns._ID + "= ?";
                    finalWhereArgs = new String[]{id};
                }

            } else {
                finalWhere = selection;
            }
            int count;
            db.beginTransaction();
            try {
                count = db.delete(tableName, finalWhere, finalWhereArgs);
                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }
            getContext().getContentResolver().notifyChange(uri, null);
            return count;
        }
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        synchronized (DBLock) {
            int code = uriMatcher.match(uri);
            String tableName = tableNameMap.get(code);
            if (tableName == null && tableName.length() <= 0) {
                return -1;
            }
            SQLiteDatabase db = mDBHelper.getWritableDatabase();
            db.execSQL("PRAGMA foreign_keys=ON");
            boolean isDir = true;
            String id = null;
            try {
                String s = uri.getLastPathSegment();
                id = Integer.parseInt(s) + "";
                isDir = false;
            } catch (NumberFormatException e) {

            }
            String finalWhere = null;
            String finalWhereArgs[] = null;
            if (!isDir) {
                if (selection != null) {
                    finalWhere = new StringBuffer(selection).append(" AND " + BaseColumns._ID + "= ?").toString();
                    finalWhereArgs = new String[selectionArgs.length + 1];
                    for (int i = 0; i < selectionArgs.length; i++) {
                        finalWhereArgs[i] = selectionArgs[i];
                    }
                    finalWhereArgs[selectionArgs.length] = id;
                } else {
                    finalWhere = BaseColumns._ID + "= ?";
                    finalWhereArgs = new String[]{id};
                }

            } else {
                finalWhere = selection;
            }
            int count = 0;
            db.beginTransaction();
            try {
                count = db.update(tableName, values, finalWhere, finalWhereArgs);
                db.setTransactionSuccessful();

            } catch (SQLException e) {
                if (e.getMessage().contains("no such column")) {
                    addColumn(db, tableName, values);
                    count = db.update(tableName, values, finalWhere, finalWhereArgs);
                    db.setTransactionSuccessful();
                }
                Log.e(TAG, e.getMessage());
            } finally {
                db.endTransaction();
            }
            getContext().getContentResolver().notifyChange(uri, null);
            return count;
        }
    }

    public boolean isTableExists(String tableName) {
        SQLiteDatabase db = mDBHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select DISTINCT tbl_name from sqlite_master where tbl_name = '" + tableName + "'", null);
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                cursor.close();
                return true;
            }
            cursor.close();
        }
        return false;
    }


    public static class DBHelper extends SQLiteOpenHelper {
        // 数据库名
        public static String DB_NAME = "UriBinding.db";

        // 数据库版本
        private static final int VERSION = 1;

        public DBHelper(Context context) {
            super(context, DB_NAME, null, VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            new SQLiteTable("uri_matcher").addColumn(new ColumnsDefinition("path", ColumnsDefinition.Constraint.UNIQUE, ColumnsDefinition.DataType.TEXT))
                    .addColumn(new ColumnsDefinition("code", ColumnsDefinition.Constraint.UNIQUE, ColumnsDefinition.DataType.INTEGER))
                    .addColumn(new ColumnsDefinition("table_name", ColumnsDefinition.Constraint.UNIQUE, ColumnsDefinition.DataType.TEXT))
                    .create(db);

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            if (oldVersion < newVersion) {
                onCreate(db);
            }
        }
    }
}

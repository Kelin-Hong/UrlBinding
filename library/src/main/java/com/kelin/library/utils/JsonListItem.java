package com.kelin.library.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;

import com.kelin.library.dao.ContentValueUtils;

import java.util.HashMap;

/**
 * Created by kelin on 15-3-10.
 */
public class JsonListItem {

    private JsonListData jsonListData;

    private Context context;

    private Uri itemUri;

    private String listName;

    private boolean mListItemIsModel2DB = false;

    private HashMap<String, Object> jsonFieldMap = new HashMap<>();


    private ContentObserver listContentObserver = new ContentObserver(new Handler()) {
        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            if (mListItemIsModel2DB) {
                mListItemIsModel2DB = false;
                return;
            }
            loadDataFromDB(itemUri);
        }
    };


    public JsonListItem(Context context, JsonListData jsonListData, String listName) {
        this.context = context;
        this.jsonListData = jsonListData;
        this.listName = listName;
    }

    public void loadDataFromDB(Uri itemUri) {
        this.itemUri = itemUri;
        Cursor cursor = context.getContentResolver().query(itemUri, null, null, null, null);
        cursor.moveToFirst();
        for (String columnName : cursor.getColumnNames()) {
            String jsonItemKey = columnName.substring(columnName.indexOf("_") + 1);
            switch (cursor.getType(cursor.getColumnIndex(columnName))) {
                case Cursor.FIELD_TYPE_INTEGER:
                    add(jsonItemKey, cursor.getInt(cursor.getColumnIndex(columnName)));
                    break;
                case Cursor.FIELD_TYPE_FLOAT:
                    add(jsonItemKey, cursor.getFloat(cursor.getColumnIndex(columnName)));
                    break;
                case Cursor.FIELD_TYPE_BLOB:
                    add(jsonItemKey, cursor.getBlob(cursor.getColumnIndex(columnName)));
                    break;
                case Cursor.FIELD_TYPE_STRING:
                    add(jsonItemKey, cursor.getString(cursor.getColumnIndex(columnName)));
                    break;
                case Cursor.FIELD_TYPE_NULL:
                    break;
            }
        }
    }


    public Uri getItemUri() {
        return itemUri;
    }

    public Object get(String fieldName) {
        return jsonFieldMap.get(fieldName);
    }

    public HashMap<String, Object> getJsonFieldMap() {
        return jsonFieldMap;
    }

    public void setListName(String listName) {
        this.listName = listName;
    }

    public void setItemUri(Uri itemUri) {
        this.itemUri = itemUri;
    }

    public void add(String key, Object value) {
        jsonFieldMap.put(key, value);
        if (listName != null && !jsonListData.getAllFieldWithName().contains(key)) {
            jsonListData.getAllFieldWithName().add(listName + "_" + key);
        }
        if (listName != null && !jsonListData.getAllFieldName().contains(key)) {
            jsonListData.getAllFieldName().add(key);
        }
    }

    public boolean update(String key, Object value) {
        if (jsonFieldMap.containsKey(key)) {
            jsonFieldMap.put(key, value);
            return true;
        }
        return false;
    }

    public boolean remove(String fieldName) {
        if (jsonFieldMap.containsKey(fieldName)) {
            jsonFieldMap.remove(fieldName);
            return true;
        }
        return false;
    }


    public void addAndChangeDB(String key, Object value) {
        add(key, value);
        if (itemUri != null) {
            ContentValues contentValues = new ContentValues();
            ContentValueUtils.insertContentValues(contentValues, key, value);
            mListItemIsModel2DB = false;
            context.getContentResolver().insert(itemUri, contentValues);
        }
    }

    public boolean updateAddChangeDB(String key, Object value) {
        if (jsonFieldMap.containsKey(key)) {
            jsonFieldMap.put(key, value);
            if (itemUri != null) {
                ContentValues contentValues = new ContentValues();
                ContentValueUtils.insertContentValues(contentValues, key, value);
                mListItemIsModel2DB = false;
                context.getContentResolver().update(itemUri, contentValues, null, null);
            }
            return true;
        }
        return false;
    }

    public boolean removeAndChangeDB(String fieldName) {
        if (jsonFieldMap.containsKey(fieldName)) {
            jsonFieldMap.remove(fieldName);
            ContentValues contentValues = new ContentValues();
            contentValues.putNull(fieldName);
            if (itemUri != null) {
                mListItemIsModel2DB = false;
                context.getContentResolver().update(itemUri, contentValues, null, null);
            }
            return true;
        }
        return false;
    }

}

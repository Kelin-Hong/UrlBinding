package com.kelin.library.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;

import com.kelin.library.dao.ContentValueUtils;

import org.robobinding.presentationmodel.PresentationModelChangeSupport;

import java.util.HashMap;

/**
 * Created by kelin on 15-3-10.
 */
public class JsonListItem {

    private JsonListData jsonListData;

    private Context context;

    private Uri itemUri;

    private String listName;

    private boolean mIsModel2DB = true;

    private PresentationModelChangeSupport changeSupport;

    private HashMap<String, Object> jsonFieldMap = new HashMap<>();

    public ContentObserver getmContentObserver() {
        return mContentObserver;
    }

    private ContentObserver mContentObserver = new ContentObserver(new Handler()) {

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            if (mIsModel2DB) {
                mIsModel2DB = false;
                return;
            }
            loadDataFromDB(itemUri);
            if (changeSupport != null) {
                changeSupport.refreshPresentationModel();
            }

        }

        private boolean isNumber(String s) {
            try {
                Integer.parseInt(s);
                return true;
            } catch (NumberFormatException e) {
                return false;
            }

        }
    };


    public JsonListItem(Context context, JsonListData jsonListData) {
        this.context = context;
        this.jsonListData = jsonListData;
    }


    public JsonListItem(Context context, JsonListData jsonListData, Uri uri) {
        this.context = context;
        this.jsonListData = jsonListData;
        loadDataFromDB(uri);
    }

    public void loadDataFromDB(Uri itemUri) {
        setItemUri(itemUri);
        Cursor cursor = context.getContentResolver().query(itemUri, null, null, null, null);
        cursor.moveToFirst();
        if (cursor.getCount() <= 0) {
            if (jsonListData != null) {
                jsonListData.remove(this);
            }
            return;
        }
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


    public void setItemUri(Uri itemUri) {
        this.itemUri = itemUri;
        context.getContentResolver().registerContentObserver(this.itemUri, false, mContentObserver);
    }

    public JsonListData getJsonListData() {
        return jsonListData;
    }

    public void setChangeSupport(PresentationModelChangeSupport changeSupport) {
        this.changeSupport = changeSupport;
    }

    public void setJsonListData(JsonListData jsonListData) {
        this.jsonListData = jsonListData;
        this.listName = jsonListData.getName();
    }

    public void add(String key, Object value) {
        jsonFieldMap.put(key, value);
        if (!jsonListData.getAllFieldWithName().contains(key)) {
            jsonListData.getAllFieldWithName().add(key);
        }
        if (!jsonListData.getAllFieldName().contains(key)) {
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
            mIsModel2DB = false;
            context.getContentResolver().insert(itemUri, contentValues);
        }
    }

    public boolean updateAddChangeDB(String key, Object value) {
        if (jsonFieldMap.containsKey(key)) {
            jsonFieldMap.put(key, value);
            if (itemUri != null) {
                ContentValues contentValues = new ContentValues();
                ContentValueUtils.insertContentValues(contentValues, key, value);
                mIsModel2DB = false;
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
                mIsModel2DB = false;
                context.getContentResolver().update(itemUri, contentValues, null, null);
            }
            return true;
        }
        return false;
    }

    public void setmIsModel2DB(boolean mIsModel2DB) {
        this.mIsModel2DB = mIsModel2DB;
    }

}

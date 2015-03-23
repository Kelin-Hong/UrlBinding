package com.kelin.library.data;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.provider.BaseColumns;

import com.kelin.library.base.BaseFragment;
import com.kelin.library.dao.ContentValueUtils;

import org.robobinding.presentationmodel.PresentationModelChangeSupport;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * Created by kelin on 15-3-12.
 */
public class JsonPrimary {
    private Context context;

    private boolean mIsModel2DB = false;

    private PresentationModelChangeSupport changeSupport;

    private List<Uri> mUris = new ArrayList<>();

    private String mUrl;

    private HashMap<String, Uri> fieldUriMap = new HashMap<>();

    private HashMap<String, Object> jsonPrimaryHashMap = new HashMap<>();

    public ContentObserver getmContentObserver() {
        return mContentObserver;
    }

    private ContentObserver mContentObserver = new ContentObserver(null) {

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            if (mIsModel2DB) {
                mIsModel2DB = false;
                return;
            }
            for (Uri uri : mUris) {
                addCursorToJsonPrimaryHashMap(uri);
            }
            if (changeSupport != null) {
                changeSupport.refreshPresentationModel();
            }
        }
    };


    public JsonPrimary(BaseFragment fragment, Uri loadUri) {
        this.context = fragment.getActivity();
        addCursorToJsonPrimaryHashMap(loadUri);
    }

    public JsonPrimary(BaseFragment fragment) {
        this.context = fragment.getActivity();
    }


    public void addCursorToJsonPrimaryHashMap(Uri uri) {
        setmUri(uri);
        Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        if (cursor == null || cursor.getCount() <= 0) {
            return;
        }
        for (String columnName : cursor.getColumnNames()) {
            if (columnName.equals(BaseColumns._ID)) {
                continue;
            }
            String key = columnName.substring(columnName.indexOf("_") + 1);
            fieldUriMap.put(key, uri);
            switch (cursor.getType(cursor.getColumnIndex(columnName))) {
                case Cursor.FIELD_TYPE_INTEGER:
                    if (jsonPrimaryHashMap.containsKey(key) && jsonPrimaryHashMap.get(key) instanceof Boolean) {
                        Boolean aBoolean = (cursor.getInt(cursor.getColumnIndex(columnName)) == 1 ? true : false);
                        add(key, aBoolean);
                    } else {
                        add(key, cursor.getInt(cursor.getColumnIndex(columnName)));
                    }
                    break;
                case Cursor.FIELD_TYPE_FLOAT:
                    add(key, cursor.getFloat(cursor.getColumnIndex(columnName)));
                    break;
                case Cursor.FIELD_TYPE_BLOB:
                    add(key, cursor.getBlob(cursor.getColumnIndex(columnName)));
                    break;
                case Cursor.FIELD_TYPE_STRING:
                    add(key, cursor.getString(cursor.getColumnIndex(columnName)));
                    break;
                case Cursor.FIELD_TYPE_NULL:
                    break;
            }
        }
        cursor.close();

    }

    public void setChangeSupport(PresentationModelChangeSupport changeSupport) {
        this.changeSupport = changeSupport;
    }

    public void setmUri(Uri mUri) {
        mUris.add(mUri);
        context.getContentResolver().registerContentObserver(mUri, false, mContentObserver);
    }

    public Set<String> keySet() {
        return jsonPrimaryHashMap.keySet();
    }

    public Object get(String name) {
        if (jsonPrimaryHashMap.containsKey(name)) {
            return jsonPrimaryHashMap.get(name);
        }
        return null;
    }

    public void add(String key, Object value) {

        if (jsonPrimaryHashMap.containsKey(key)) {
            if (!jsonPrimaryHashMap.get(key).equals(value)) {
                jsonPrimaryHashMap.put(key, value);
            }
        } else {
            jsonPrimaryHashMap.put(key, value);
        }


    }

    public boolean update(String key, Object value) {
        if (jsonPrimaryHashMap.containsKey(key)) {
            jsonPrimaryHashMap.put(key, value);
            Intent intent = new Intent();
            intent.putExtra("key", key);

            return true;
        }
        return false;
    }

    public boolean remove(String fieldName) {
        if (jsonPrimaryHashMap.containsKey(fieldName)) {
            jsonPrimaryHashMap.remove(fieldName);
            return true;
        }
        return false;
    }

    public void addAndChangeDB(String key, Object value) {
        if (fieldUriMap.get(key) != null) {
            ContentValues values = new ContentValues();
            ContentValueUtils.insertContentValues(values, key, value);
            mIsModel2DB = true;
            context.getContentResolver().update(fieldUriMap.get(key), values, null, null);
        }
        jsonPrimaryHashMap.put(key, value);

    }

    public boolean updateAndChangeDB(String key, Object value) {
        if (!jsonPrimaryHashMap.containsKey(key)) {
            return false;
        }
        if (get(key).equals(value)) {
            return false;
        }
        if (fieldUriMap.get(key) == null) {
            return false;
        }
        jsonPrimaryHashMap.put(key, value);
        ContentValues values = new ContentValues();
        ContentValueUtils.insertContentValues(values, key, value);
        mIsModel2DB = true;
        context.getContentResolver().update(fieldUriMap.get(key), values, null, null);
        return true;
    }

    public boolean removeAndChangeDB(String fieldName) {
        if (jsonPrimaryHashMap.containsKey(fieldName)) {
            jsonPrimaryHashMap.remove(fieldName);
            ContentValues contentValues = new ContentValues();
            String columnName = new StringBuffer(ContentValueUtils.COLUMN_PREFIX).append(fieldName).toString();
            contentValues.putNull(columnName);
            if (fieldUriMap.get(fieldName) != null) {
                mIsModel2DB = true;
                context.getContentResolver().update(fieldUriMap.get(fieldName), contentValues, null, null);
            }
            return true;
        }
        return false;
    }

    public List<Uri> getmUris() {
        return mUris;
    }

}

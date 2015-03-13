package com.kelin.library.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.provider.BaseColumns;

import com.kelin.library.base.BaseFragment;
import com.kelin.library.dao.ContentValueUtils;
import com.kelin.library.dao.DataProvider;

import org.robobinding.presentationmodel.PresentationModelChangeSupport;

import java.util.HashMap;
import java.util.Set;

/**
 * Created by kelin on 15-3-12.
 */
public class JsonPrimary {
    private Context context;

    private boolean mIsModel2DB = false;

    private PresentationModelChangeSupport changeSupport;

    private Uri mUri;

    private String mUrl;

    private HashMap<String, Object> jsonPrimaryHashMap = new HashMap<>();

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

            addCursorToJsonPrimaryHashMap(mUri);
            if (changeSupport != null) {
                changeSupport.refreshPresentationModel();
            }

        }
    };

    public JsonPrimary(BaseFragment fragment, Uri loadUri) {
        this.mUrl = fragment.getmUrl();
        this.context = fragment.getActivity();
        addCursorToJsonPrimaryHashMap(loadUri);
    }

    public JsonPrimary(BaseFragment fragment) {
        this.mUrl = fragment.getmUrl();
        this.context = fragment.getActivity();
    }


    private void addCursorToJsonPrimaryHashMap(Uri uri) {
        Cursor cursor = context.getContentResolver().query(uri, null, DataProvider.COLUMN_URI_MD5 + "= ?", new String[]{UtilMethod.getMD5Str(mUrl)}, null);
        cursor.moveToFirst();
        if (cursor == null || cursor.getCount() <= 0) {
            return;
        }
        for (String columnName : cursor.getColumnNames()) {
            if (columnName.equals(BaseColumns._ID)) {
                String id = String.valueOf(cursor.getInt(cursor.getColumnIndex(columnName)));
                setmUri(uri.buildUpon().appendEncodedPath(id).build());
                continue;
            }
            String key = columnName.substring(columnName.indexOf("_") + 1);
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
        this.mUri = mUri;
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
        if (mUri != null) {
            ContentValues values = new ContentValues();
            ContentValueUtils.insertContentValues(values, key, value);
            mIsModel2DB = true;
            context.getContentResolver().update(mUri, values, null, null);
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
        jsonPrimaryHashMap.put(key, value);
        ContentValues values = new ContentValues();
        ContentValueUtils.insertContentValues(values, key, value);
        mIsModel2DB = true;
        context.getContentResolver().update(mUri, values, null, null);
        return true;
    }

    public boolean removeAndChangeDB(String fieldName) {
        if (jsonPrimaryHashMap.containsKey(fieldName)) {
            jsonPrimaryHashMap.remove(fieldName);
            ContentValues contentValues = new ContentValues();
            String columnName = new StringBuffer(ContentValueUtils.COLUMN_PREFIX).append(fieldName).toString();
            contentValues.putNull(columnName);
            if (mUri != null) {
                mIsModel2DB = true;
                context.getContentResolver().update(mUri, contentValues, null, null);
            }
            return true;
        }
        return false;
    }

    public Uri getmUri() {
        return mUri;
    }

}

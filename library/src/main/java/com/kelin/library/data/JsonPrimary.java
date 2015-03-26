package com.kelin.library.data;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;

import com.google.gson.Gson;
import com.kelin.library.base.BaseFragment;
import com.kelin.library.dao.ContentValueUtils;
import com.kelin.library.utils.UtilMethod;

import org.robobinding.presentationmodel.PresentationModelChangeSupport;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

/**
 * Created by kelin on 15-3-12.
 */
public class JsonPrimary {

    private static final String ACTION_NAME_PREFIX = "Com.UrlBinding.";

    private Context context;

    private boolean mIsModel2DB = false;

    private boolean mIsManualOP = true;

    private String id;

    private List<String> observerIds = new ArrayList<>();

    private PresentationModelChangeSupport changeSupport;

    public BroadcastReceiverHelper getBroadcastReceiver() {
        return broadcastReceiver;
    }

    public BroadcastReceiverHelper broadcastReceiver;

    private List<Uri> mUris = new ArrayList<>();

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
        id = UtilMethod.getUUID();
        registerBroadCastReceiver();
        addCursorToJsonPrimaryHashMap(loadUri);

    }

    public JsonPrimary(BaseFragment fragment, LinkedHashMap<String, Object> jsonMap, ArrayList<String> jsonObjectName) {
        this.context = fragment.getActivity();
        for (String objectName : jsonObjectName) {
            add(objectName, jsonMap.get(objectName));
        }
        id = UtilMethod.getUUID();
        registerBroadCastReceiver();
    }

    private void registerBroadCastReceiver() {
        broadcastReceiver = new BroadcastReceiverHelper(context);
        broadcastReceiver.registerAction(ACTION_NAME_PREFIX + id);
    }

    public void setObserverIds(List<String> observerIds) {
        this.observerIds = observerIds;
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

    public String getJsonString() {
        return new Gson().toJson(jsonPrimaryHashMap);
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

    public String getId() {
        return id;
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
            mIsManualOP = true;
            jsonPrimaryHashMap.put(key, value);
            for (String id : observerIds) {
                Intent intent = new Intent(ACTION_NAME_PREFIX + id);
                intent.putExtra("key", key);
                if (value instanceof String) {
                    intent.putExtra("value", value.toString());
                } else if (value instanceof Long || value instanceof Integer) {
                    intent.putExtra("value", (int) value);
                } else if (value instanceof Boolean) {
                    intent.putExtra("value", (boolean) value);
                } else if (value instanceof Double || value instanceof Float) {
                    intent.putExtra("value", (float) value);
                }
                context.sendBroadcast(intent);
            }
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

    public class BroadcastReceiverHelper extends BroadcastReceiver {
        private Context context = null;

        public BroadcastReceiverHelper(Context c) {
            context = c;
        }

        public void registerAction(String action) {
            IntentFilter filter = new IntentFilter();
            filter.addAction(action);
            context.registerReceiver(this, filter);
        }

        public void unregisterReceiver() {
            context.unregisterReceiver(this);
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(ACTION_NAME_PREFIX + id)) {
                if (mIsManualOP) {
                    mIsManualOP = false;
                    return;
                }
                String key = intent.getStringExtra("key");
                Object value = jsonPrimaryHashMap.get(key);
                Object newValue = null;
                if (value instanceof String) {
                    newValue = intent.getStringExtra("value");
                } else if (value instanceof Long || value instanceof Integer) {
                    newValue = intent.getIntExtra("value", -1);
                } else if (value instanceof Boolean) {
                    newValue = intent.getBooleanExtra("value", false);
                } else if (value instanceof Double || value instanceof Float) {
                    newValue = intent.getFloatExtra("value", -1);
                }
                if (jsonPrimaryHashMap.containsKey(key) && !jsonPrimaryHashMap.get(key).equals(newValue)) {
                    jsonPrimaryHashMap.put(key, newValue);
                    changeSupport.firePropertyChange(key);
                }
            }

        }
    }

}

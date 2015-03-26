package com.kelin.library.data;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;

import com.google.gson.Gson;
import com.kelin.library.dao.ContentValueUtils;
import com.kelin.library.utils.UtilMethod;

import org.robobinding.presentationmodel.PresentationModelChangeSupport;

import java.util.HashMap;

/**
 * Created by kelin on 15-3-10.
 */
public class JsonListItem {
    private static final String ACTION_NAME_PREFIX = "Com.UrlBinding.";

    private String id;

    private JsonListData jsonListData;

    public BroadcastReceiverHelper getBroadcastReceiver() {
        return broadcastReceiver;
    }

    public BroadcastReceiverHelper broadcastReceiver;

    private Context context;

    private Uri itemUri;

    private String mUrl;

    private String listName;

    private boolean mIsModel2DB = true;

    private boolean mIsManualOP = true;

    private PresentationModelChangeSupport changeSupport;

    private HashMap<String, Object> jsonFieldMap = new HashMap<>();

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
        this.id = UtilMethod.getUUID();
        this.jsonListData = jsonListData;
//        jsonFieldMap.put("json_data_id", id);
        broadcastReceiver = new BroadcastReceiverHelper(context);
        broadcastReceiver.registerAction(ACTION_NAME_PREFIX + id);
    }


    public JsonListItem(Context context, JsonListData jsonListData, Uri uri) {
        this.context = context;
        this.jsonListData = jsonListData;
        this.id = UtilMethod.getUUID();
        broadcastReceiver = new BroadcastReceiverHelper(context);
        broadcastReceiver.registerAction(ACTION_NAME_PREFIX + id);
        loadDataFromDB(uri);
    }

    public String getJsonString() {
        return new Gson().toJson(jsonFieldMap);
    }

    public String getId() {
        return id;
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
            mIsManualOP = true;
            jsonFieldMap.put(key, value);

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
                Object value = jsonFieldMap.get(key);
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
                if (jsonFieldMap.containsKey(key) && !jsonFieldMap.get(key).equals(newValue)) {
                    jsonFieldMap.put(key, newValue);
                    changeSupport.firePropertyChange(jsonListData.getName());
                }
            }

        }
    }

}

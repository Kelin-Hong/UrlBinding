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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.robobinding.presentationmodel.PresentationModelChangeSupport;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by kelin on 15-1-12.
 */
public class JsonData {

    private BaseFragment mFragment;

    private Context context;

    private boolean mIsModel2DB = false;

    private PresentationModelChangeSupport changeSupport;

    private Uri mUri;

    private String mUrl;

    private boolean isCacheDB = true;

    private HashMap<String, JsonListData> listDataHashMap = new HashMap<>();

    private LinkedHashMap<String, Object> jsonMap = new LinkedHashMap<String, Object>();

    private ArrayList<String> jsonListName = new ArrayList<String>();

    private ArrayList<String> jsonObjectName = new ArrayList<String>();

    private HashMap<String, Integer> jsonArraySize = new HashMap<String, Integer>();

    private HashMap<String, Object> jsonPrimaryHashMap = new HashMap<>();

    private ContentObserver mContentObserver = new ContentObserver(new Handler()) {
        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            if (mIsModel2DB) {
                mIsModel2DB = false;
                return;
            }
            onChange(selfChange, null);
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            super.onChange(selfChange);
            if (uri != null) {
                if (uri.toString().equals(mUrl)) {
                    addCursorToJsonPrimaryHashMap(mUri);
                    if (changeSupport != null) {
                        changeSupport.refreshPresentationModel();
                    }
                }
            } else {
                addCursorToJsonPrimaryHashMap(mUri);
                if (changeSupport != null) {
                    changeSupport.refreshPresentationModel();
                }
            }
        }
    };

    public JsonData(BaseFragment fragment, JSONObject jsonObject, String tableName, String url) {
        this.mFragment = fragment;
        this.context = fragment.getActivity();
        this.mUrl = url;
        parseJsonObjec("", jsonObject);
        for (String listName : jsonListName) {
            JsonListData jsonListData = new JsonListData(listName, context, mUrl);
            jsonListData.setJsonListItems(getJsonListItems(listName, jsonListData));
            listDataHashMap.put(listName, jsonListData);
        }
        for (String objectName : jsonObjectName) {
            jsonPrimaryHashMap.put(objectName, jsonMap.get(objectName));
        }
        if (tableName != null) {
            cacheToDB(this, tableName, url);
        }
    }

    private List<JsonListItem> getJsonListItems(String listName, JsonListData jsonListData) {
        List<JsonListItem> items = new ArrayList<JsonListItem>();
        JsonListItem item = null;
        for (Iterator<String> iterator = jsonMap.keySet().iterator(); iterator.hasNext(); ) {
            String jsonName = iterator.next();
            if (jsonName.contains("$") && jsonName.startsWith(listName)) {
                String subName = jsonName.substring(listName.length());
                String fieldName = subName.substring(subName.lastIndexOf('_') + 1);
                if (item == null || item.getJsonFieldMap().containsKey(fieldName)) {
                    item = new JsonListItem(context, jsonListData, listName);
                    items.add(item);
                }
                item.add(fieldName, jsonMap.get(jsonName));
            }
        }
        return items;
    }


//    public JsonData(BaseFragment fragment, List<Cursor> cursors) {
//        this.mFragment = fragment;
//        this.context = fragment.getActivity();
//        for (Cursor cursor : cursors) {
//            if (Arrays.asList(cursor.getColumnNames()).contains(DataProvider.COLUMN_URI_ID)) {
//                addCursorToJsonPrimaryHashMap(cursor);
//            } else {
//                addCursorToJsonPrimaryHashMap(cursor);
//            }
//        }
//    }

    public JsonData(BaseFragment fragment, String url) {
        this.mUrl = url;
        this.mFragment = fragment;
        this.context = fragment.getActivity();
        List<Uri> uriList = UriConvertUtil.getDataUri(Uri.parse(url));
        for (Uri uri : uriList) {
            if (!uri.toString().contains("_")) {
                addCursorToJsonPrimaryHashMap(uri);
            } else {
                JsonListData jsonListData = new JsonListData(context, mUrl, uri);
                listDataHashMap.put(jsonListData.getName(), jsonListData);
            }
        }
    }

    public JsonData(Context context, String url) {
        this.mUrl = url;
        this.context = context;
        List<Uri> uriList = UriConvertUtil.getDataUri(Uri.parse(url));
        for (Uri uri : uriList) {
            if (!uri.toString().contains("_")) {
                addCursorToJsonPrimaryHashMap(uri);
            } else {
                JsonListData jsonListData = new JsonListData(context, mUrl, uri);
                listDataHashMap.put(jsonListData.getName(), jsonListData);
            }
        }
    }

    public JsonData(BaseFragment fragment, Uri loadUri, String url) {
        this.mUrl = url;
        this.mFragment = fragment;
        this.context = fragment.getActivity();
        if (!loadUri.toString().contains("_")) {
            addCursorToJsonPrimaryHashMap(loadUri);
        } else {
            JsonListData jsonListData = new JsonListData(context, mUrl, loadUri);
            listDataHashMap.put(jsonListData.getName(), jsonListData);
        }

    }

    public void setmUri(Uri mUri) {
        this.mUri = mUri;
        context.getContentResolver().registerContentObserver(mUri, false, mContentObserver);
    }

    public void setChangeSupport(PresentationModelChangeSupport changeSupport) {
        this.changeSupport = changeSupport;
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


    private void cacheToDB(JsonData jsonData, String tableName, String url) {
        String queryMd5 = UtilMethod.getMD5Str(url);
        Uri dataUri = UriConvertUtil.getDataUri(Uri.parse(url), tableName);
        DataProvider.addUriMatcherToDB(context, dataUri, tableName);
        ContentValues values = new ContentValues();
        for (String key : jsonData.getJsonPrimaryHashMap().keySet()) {
            ContentValueUtils.insertContentValues(values, key, jsonData.getJsonPrimaryHashMap().get(key));
        }
        values.put(DataProvider.COLUMN_URI_MD5, queryMd5);
        Uri returnUri = context.getContentResolver().insert(dataUri, values);
        setmUri(returnUri);
        String uriId = returnUri.getLastPathSegment();
        for (String keyItem : jsonData.getListDataHashMap().keySet()) {
            String listTableName = tableName + "_" + keyItem;
            Uri listDataUri = UriConvertUtil.getDataUri(Uri.parse(url), listTableName);
            DataProvider.addUriMatcherToDB(context, listDataUri, listTableName);
            jsonData.getList(keyItem).setListUri(listDataUri);
            for (JsonListItem item : jsonData.getList(keyItem).getJsonListItems()) {
                values = new ContentValues();
                for (String columnName : jsonData.getList(keyItem).getAllFieldName()) {
                    ContentValueUtils.insertContentValues(values, columnName, item.get(columnName));
                }
                values.put(DataProvider.COLUMN_URI_ID, Integer.parseInt(uriId));
                values.put(DataProvider.COLUMN_TABLE_NAME, listTableName);
                values.put(DataProvider.COLUMN_URI_MD5, queryMd5);
                item.setItemUri(context.getContentResolver().insert(listDataUri, values));
            }
        }
    }


    private void parseJsonObjec(String parentKey, JSONObject jsonObject) {
        JSONArray jsonArray = jsonObject.names();
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                String key = jsonArray.get(i).toString();
                Object value = jsonObject.get(key);
                String name = jsonArray.get(i).toString();
                if (parentKey != null && parentKey.length() > 0) {
                    name = parentKey + "_" + jsonArray.get(i).toString();
                }
                if (value instanceof JSONObject) {

                    parseJsonObjec(name, jsonObject.getJSONObject(key));
                } else if (value instanceof JSONArray) {
                    jsonListName.add(name);
                    jsonArraySize.put(name, ((JSONArray) value).length());
                    for (int j = 0; j < ((JSONArray) value).length(); j++) {
                        parseJsonObjec(name + "$" + j + "$", ((JSONArray) value).getJSONObject(j));
                    }
                } else {
                    System.out.println(name + "  ---->  " + jsonObject.get(key));
                    jsonMap.put(name, jsonObject.get(key));
//                    }
                    if (!name.contains("$")) {
                        jsonObjectName.add(name);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public HashMap<String, JsonListData> getListDataHashMap() {
        return listDataHashMap;
    }

    public HashMap<String, Object> getJsonPrimaryHashMap() {
        return jsonPrimaryHashMap;
    }

    public Object get(String name) {
        if (jsonPrimaryHashMap.containsKey(name)) {
            return jsonPrimaryHashMap.get(name);
        }
        return null;
    }

    public JsonListData getList(String name) {
        return listDataHashMap.get(name);
    }

    public BaseFragment getmFragment() {
        return mFragment;
    }

    public Uri getmUri() {
        return mUri;
    }


    public void add(String key, Object value) {
        if (jsonPrimaryHashMap.containsKey(key)) {
            if (!jsonPrimaryHashMap.get(key).equals(value)) {
                jsonPrimaryHashMap.put(key, value);
//                changeSupport.firePropertyChange(key);
            }
        } else {
            jsonPrimaryHashMap.put(key, value);
//            changeSupport.firePropertyChange(key);
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


//    public class JsonListData {
//        private Uri listUri;
//        private String name;
//        private int size;
//        private Set<String> allFieldName;
//        private Set<String> allFieldWithName;
//        private List<JsonListItem> items;
//        private boolean mIsModel2DB = false;
//        private ContentObserver listContentObserver = new ContentObserver(new Handler()) {
//            @Override
//            public void onChange(boolean selfChange) {
//                super.onChange(selfChange);
//                if(mIsModel2DB){
//                    mIsModel2DB=false;
//                    return;
//                }
//                Cursor cursor = context.getContentResolver().query(listUri, null, DataProvider.COLUMN_URI_MD5 + " = ?", new String[]{UtilMethod.getMD5Str(mUrl)}, null);
//
//            }
//        };
//
//        public JsonListData(String name) {
//            this.name = name;
//            allFieldName = getItemFieldSetByArrayName(name);
//            allFieldWithName = getItemFieldWithArrayNameSetByArrayName(name);
//            items = getJsonListItems(name);
//        }
//
//        public void setListUri(final Uri listUri) {
//            this.listUri = listUri;
//            context.getContentResolver().registerContentObserver(listUri, false, listContentObserver);
//        }
//
//
//        public int getSize() {
//            return items.size();
//        }
//
//        public List<JsonListItem> getJsonListItems() {
//            return items;
//        }
//
//        public Set<String> getAllFieldWithName() {
//            return allFieldWithName;
//        }
//
//
//        public Set<String> getAllFieldName() {
//            return allFieldName;
//        }
//
//        public JsonListItem get(int index) {
//            if (items != null && !items.isEmpty()) {
//                return items.get(index);
//            }
//            return null;
//        }
//
//        private List<JsonListItem> getJsonListItems(String name) {
//            List<JsonListItem> items = new ArrayList<>();
//            JsonListItem item = null;
//            for (Iterator<String> iterator = jsonMap.keySet().iterator(); iterator.hasNext(); ) {
//                String jsonName = iterator.next();
//                if (jsonName.contains("$") && jsonName.startsWith(name)) {
//                    String subName = jsonName.substring(name.length());
//                    String fieldName = subName.substring(subName.lastIndexOf('_') + 1);
//                    if (item == null || item.listItem.containsKey(fieldName)) {
//                        item = new JsonListItem();
//                        items.add(item);
//                    }
//                    item.listItem.put(fieldName, jsonMap.get(jsonName));
//                    item.listName = name;
//                }
//            }
//            return items;
//        }
//
//        public void add(JsonListItem item) {
//            items.add(item);
//        }
//
//        public void addAndChangeDB(JsonListItem item) {
//            items.add(item);
//            if (listUri != null) {
//                ContentValues contentValues = new ContentValues();
//                for (String key : item.listItem.keySet())
//                    ContentValueUtils.insertContentValues(contentValues, key, item.get(key));
//                mIsModel2DB = true;
//                context.getContentResolver().update(listUri, contentValues, null, null);
//            }
//
//        }
//
//        public JsonListItem remove(int index) {
//            return items.remove(index);
//        }
//
//        public JsonListItem removeAndChangeD(int index) {
//            mIsModel2DB = true;
//            int count = context.getContentResolver().delete(items.get(index).itemUri, null, null);
//            if (count > 0) {
//                JsonListItem item = items.remove(index);
//                return item;
//            }
//            return null;
//        }
//    }

//    public class JsonListItem {
//
//        private Uri itemUri;
//
//        private String listName;
//
//        private HashMap<String, Object> listItem = new HashMap<>();
//
//        public Object get(String fieldName) {
//            return listItem.get(fieldName);
//        }
//
//        public boolean remove(String fieldName) {
//            if (listItem.containsKey(fieldName)) {
//                listItem.remove(fieldName);
//                return true;
//            }
//            return false;
//        }
//
//        public boolean removeAndChangeDB(String fieldName) {
//            if (listItem.containsKey(fieldName)) {
//                listItem.remove(fieldName);
//                ContentValues contentValues = new ContentValues();
//                contentValues.putNull(fieldName);
//                if (mUri != null) {
//                    context.getContentResolver().update(itemUri, contentValues, null, null);
//                }
//                return true;
//            }
//            return false;
//        }
//
//
//        public void add(String key, Object value) {
//            listItem.put(key, value);
//            if (listName != null && !getListDataHashMap(listName).getAllFieldWithName().contains(key)) {
//                getListDataHashMap(listName).getAllFieldWithName().add(listName + "_" + key);
//            }
//            if (listName != null && !getListDataHashMap(listName).allFieldName.contains(key)) {
//                getListDataHashMap(listName).allFieldName.add(key);
//            }
//        }
//
//        public void addAndChangeDB(String key, Object value) {
//            add(key, value);
//            if (itemUri != null) {
//                ContentValues contentValues = new ContentValues();
//                ContentValueUtils.insertContentValues(contentValues, key, value);
//                context.getContentResolver().insert(itemUri, contentValues);
//            }
//        }
//
//        public boolean update(String key, Object value) {
//            if (listItem.containsKey(key)) {
//                listItem.put(key, value);
//                return true;
//            }
//            return false;
//        }
//
//        public boolean updateAddChangeDB(String key, Object value) {
//            if (listItem.containsKey(key)) {
//                listItem.put(key, value);
//                if (itemUri != null) {
//                    ContentValues contentValues = new ContentValues();
//                    ContentValueUtils.insertContentValues(contentValues, key, value);
//                    context.getContentResolver().update(itemUri, contentValues, null, null);
//                }
//                return true;
//            }
//            return false;
//        }
//
//        public void setListName(String listName) {
//            this.listName = listName;
//        }
//
//    }

    //    private Set<String> getItemFieldWithArrayNameSetByArrayName(String arrayName) {
//        Set<String> arrayFieldName = new HashSet<>();
//        for (Iterator<String> iterator = jsonMap.keySet().iterator(); iterator.hasNext(); ) {
//            String jsonName = iterator.next();
//            if (jsonName.contains("$") && jsonName.startsWith(arrayName)) {
//                String name = jsonName.substring(arrayName.length());
//                String postfix = name.substring(name.lastIndexOf('_'));
//                arrayFieldName.add(arrayName + postfix);
//            }
//        }
//        return arrayFieldName;
//    }
//
//    private Set<String> getItemFieldSetByArrayName(String arrayName) {
//        Set<String> arrayFieldName = new HashSet<>();
//        for (Iterator<String> iterator = jsonMap.keySet().iterator(); iterator.hasNext(); ) {
//            String jsonName = iterator.next();
//            if (jsonName.contains("$") && jsonName.startsWith(arrayName)) {
//                String name = jsonName.substring(arrayName.length());
//                String postfix = name.substring(name.lastIndexOf('_') + 1);
//                arrayFieldName.add(postfix);
//            }
//        }
//        return arrayFieldName;
//    }


}

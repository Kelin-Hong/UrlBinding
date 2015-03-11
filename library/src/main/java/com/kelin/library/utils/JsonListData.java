package com.kelin.library.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.provider.BaseColumns;

import com.kelin.library.dao.ContentValueUtils;
import com.kelin.library.dao.DataProvider;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by kelin on 15-3-10.
 */
public class JsonListData {
    private Context context;
    private Uri listUri;
    private String url;
    private String name;
    private int size;
    private Set<String> allFieldName = new HashSet<>();
    private Set<String> allFieldWithName = new HashSet<>();
    private List<JsonListItem> jsonListItems=new ArrayList<>();
    private boolean mListDataIsModel2DB = false;
    private ContentObserver listContentObserver = new ContentObserver(new Handler()) {
        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            if (mListDataIsModel2DB) {
                mListDataIsModel2DB = false;
                return;
            }
            Cursor cursor = context.getContentResolver().query(listUri, null, DataProvider.COLUMN_URI_MD5 + "= ?", new String[]{UtilMethod.getMD5Str(url)}, null);

        }
    };

    public JsonListData(String name, Context context, String url) {
        this.name = name;
        this.context = context;
        this.url = url;
    }

    public JsonListData(Context context, String url, Uri uri) {
        this.listUri = uri;
        this.context = context;
        this.url = url;
        setJsonListItems(uri, url);

    }

    public void setListUri(final Uri listUri) {
        this.listUri = listUri;
        context.getContentResolver().registerContentObserver(listUri, false, listContentObserver);
    }


    public void setJsonListItems(List<JsonListItem> jsonListItems) {
        this.jsonListItems = jsonListItems;
    }

    public void setJsonListItems(Uri listUri, String url) {
        Cursor cursor = context.getContentResolver().query(listUri, null, DataProvider.COLUMN_URI_MD5 + "= ?", new String[]{UtilMethod.getMD5Str(url)}, null);
        while (cursor.moveToNext()) {
            String tableName = cursor.getString(cursor.getColumnIndex(DataProvider.COLUMN_TABLE_NAME));
            this.name = tableName.substring(tableName.indexOf("_") + 1);
            Uri itemUri = listUri.buildUpon().appendPath(String.valueOf(cursor.getInt(cursor.getColumnIndex(BaseColumns._ID)))).build();
            JsonListItem jsonListItem = new JsonListItem(context, this, name);
            jsonListItem.loadDataFromDB(itemUri);
            jsonListItems.add(jsonListItem);

        }
    }


//    private List<JsonListItem> getJsonListItems(String name) {
//        List<JsonListItem> jsonListItems = new ArrayList<>();
//        JsonListItem item = null;
//        for (Iterator<String> iterator = jsonMap.keySet().iterator(); iterator.hasNext(); ) {
//            String jsonName = iterator.next();
//            if (jsonName.contains("$") && jsonName.startsWith(name)) {
//                String subName = jsonName.substring(name.length());
//                String fieldName = subName.substring(subName.lastIndexOf('_') + 1);
//                if (item == null || item.listItem.containsKey(fieldName)) {
//                    item = new JsonListItem(context,null,name);
//                    jsonListItems.add(item);
//                }
//                item.listItem.put(fieldName, jsonMap.get(jsonName));
//                item.listName = name;
//            }
//        }
//        return jsonListItems;
//    }

    public String getName() {
        return name;
    }

    public JsonListItem get(int index) {
        if (jsonListItems != null && !jsonListItems.isEmpty()) {
            return jsonListItems.get(index);
        }
        return null;
    }

    public int getSize() {
        return jsonListItems.size();
    }

    public List<JsonListItem> getJsonListItems() {
        return jsonListItems;
    }

    public Set<String> getAllFieldWithName() {
        return allFieldWithName;
    }


    public Set<String> getAllFieldName() {
        return allFieldName;
    }

    public void add(JsonListItem item) {
        jsonListItems.add(item);
    }


    public JsonListItem remove(int index) {
        return jsonListItems.remove(index);
    }

    public JsonListItem removeAndChangeD(int index) {
        mListDataIsModel2DB = true;
        int count = context.getContentResolver().delete(jsonListItems.get(index).getItemUri(), null, null);
        if (count > 0) {
            JsonListItem item = jsonListItems.remove(index);
            return item;
        }
        return null;
    }

    public void addAndChangeDB(JsonListItem item) {
        jsonListItems.add(item);
        if (listUri != null) {
            ContentValues contentValues = new ContentValues();
            for (String key : item.getJsonFieldMap().keySet())
                ContentValueUtils.insertContentValues(contentValues, key, item.get(key));
            mListDataIsModel2DB = true;
            context.getContentResolver().update(listUri, contentValues, null, null);
        }

    }
}

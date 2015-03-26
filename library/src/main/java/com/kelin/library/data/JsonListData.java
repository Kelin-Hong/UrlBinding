package com.kelin.library.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.provider.BaseColumns;
import android.util.Log;

import com.google.gson.Gson;
import com.kelin.library.dao.ContentValueUtils;
import com.kelin.library.dao.DataProvider;
import com.kelin.library.utils.UtilMethod;

import org.robobinding.presentationmodel.PresentationModelChangeSupport;

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
    private PresentationModelChangeSupport changeSupport;
    private Set<String> allFieldName = new HashSet<>();
    private Set<String> allFieldWithName = new HashSet<>();
    private List<JsonListItem> jsonListItems = new ArrayList<>();

    public void setmListDataIsModel2DB(boolean mListDataIsModel2DB) {
        this.mListDataIsModel2DB = mListDataIsModel2DB;
    }

    private boolean mListDataIsModel2DB = true;

    public ContentObserver getListContentObserver() {
        return listContentObserver;
    }

    private ContentObserver listContentObserver = new ContentObserver(null) {
        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            Log.v("Uri-Change", "onChange  ");
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

    public JsonListData(Context context, List<Uri> itemUriList) {
        this.context = context;
        if (itemUriList != null && itemUriList.size() > 0) {
            String path = itemUriList.get(0).getEncodedPath();
            String newPath = path.substring(0, path.lastIndexOf('/'));
            this.listUri = itemUriList.get(0).buildUpon().encodedPath(newPath).build();
            setJsonListItemsByItemUris(itemUriList);
        }
    }




    public void setListUri(final Uri listUri) {
        this.listUri = listUri;
        context.getContentResolver().registerContentObserver(listUri, false, listContentObserver);
    }


    public void setJsonListItems(List<JsonListItem> jsonListItems) {
        this.jsonListItems = jsonListItems;
    }

    public void setChangeSupport(PresentationModelChangeSupport changeSupport) {
        this.changeSupport = changeSupport;
        if (jsonListItems != null) {
            for (JsonListItem jsonListItem : jsonListItems) {
                jsonListItem.setChangeSupport(changeSupport);
            }
        }
    }

    public void setJsonListItemsByItemUris(List<Uri> listUri) {
        for (Uri itemUri : listUri) {
            JsonListItem jsonListItem = new JsonListItem(context, this, itemUri);
            add(jsonListItem);
        }
    }

    public void setJsonListItems(Uri listUri, String url) {
        Cursor cursor = context.getContentResolver().query(listUri, null, DataProvider.COLUMN_URI_MD5 + "= ?", new String[]{UtilMethod.getMD5Str(url)}, null);
        while (cursor.moveToNext()) {
//            String tableName = cursor.getString(cursor.getColumnIndex(DataProvider.COLUMN_TABLE_NAME));
            String tableName = listUri.getLastPathSegment();
            this.name = tableName.substring(tableName.indexOf("_") + 1);
            Uri itemUri = listUri.buildUpon().appendPath(String.valueOf(cursor.getInt(cursor.getColumnIndex(BaseColumns._ID)))).build();
            JsonListItem jsonListItem = new JsonListItem(context, this, itemUri);
            add(jsonListItem);
        }
        cursor.close();
    }


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
        if (item.getJsonListData() == null) {
            item.setJsonListData(this);
        }
    }


    public JsonListItem remove(int index) {
        return jsonListItems.remove(index);
    }

    public boolean remove(JsonListItem jsonListItem) {
        return jsonListItems.remove(jsonListItem);
    }

    public JsonListItem removeAndChangeDB(int index) {
        int count = context.getContentResolver().delete(jsonListItems.get(index).getItemUri(), null, null);
        if (count > 0) {
            JsonListItem item = jsonListItems.remove(index);
            return item;
        }
        return null;
    }

    public void addAndChangeDB(JsonListItem item) {
        jsonListItems.add(item);
        if (item.getJsonListData() != null) {
            item.setJsonListData(this);
        }
        if (listUri != null) {
            ContentValues contentValues = new ContentValues();
            for (String key : item.getJsonFieldMap().keySet())
                ContentValueUtils.insertContentValues(contentValues, key, item.get(key));
            context.getContentResolver().insert(listUri, contentValues);
        }

    }
}

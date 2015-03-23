package com.kelin.library.data;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;

import com.kelin.library.base.BaseFragment;
import com.kelin.library.dao.ContentValueUtils;
import com.kelin.library.dao.DataProvider;
import com.kelin.library.utils.UriConvertUtil;
import com.kelin.library.utils.UtilMethod;

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

    private JsonPrimary jsonPrimary;

    private HashMap<String, JsonListData> listDataHashMap = new HashMap<>();

    private LinkedHashMap<String, Object> jsonMap = new LinkedHashMap<String, Object>();

    private ArrayList<String> jsonListName = new ArrayList<String>();

    private ArrayList<String> jsonObjectName = new ArrayList<String>();

    private HashMap<String, Integer> jsonArraySize = new HashMap<String, Integer>();


    public JsonData(BaseFragment fragment, JSONObject jsonObject) {
        this.mFragment = fragment;
        this.context = fragment.getActivity();
        parseJsonObjec("", jsonObject);
        for (String listName : jsonListName) {
            JsonListData jsonListData = new JsonListData(listName, context, fragment.getmUrl());
            jsonListData.setJsonListItems(getJsonListItems(listName, jsonListData));
            listDataHashMap.put(listName, jsonListData);
        }
        jsonPrimary = new JsonPrimary(fragment);
        for (String objectName : jsonObjectName) {
            jsonPrimary.add(objectName, jsonMap.get(objectName));
        }
        if (mFragment.getmTableName() != null) {
            cacheToDB(this, mFragment.getmTableName(), mFragment.getmUrl());
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
                if (item == null || item.getJsonFieldMap().containsKey(listName+"_"+fieldName)) {
                    item = new JsonListItem(context, jsonListData);
                    items.add(item);
                }
                item.add(listName+"_"+fieldName, jsonMap.get(jsonName));
            }
        }
        return items;
    }


    public JsonData(BaseFragment fragment, String url) {
        this.mFragment = fragment;
        this.context = fragment.getActivity();
        List<Uri> uriList = UriConvertUtil.getDataUri(fragment.getActivity(), Uri.parse(url));
        addDataByUriList(uriList);
    }

    public JsonData(BaseFragment fragment, List<Uri> loadUris) {
        this.mFragment = fragment;
        this.context = fragment.getActivity();
        addDataByUriList(loadUris);
    }

    private void addDataByUriList(List<Uri> loadUris) {
        for (Uri loadUri : loadUris) {
            boolean isItem = false;
            try {
                Integer.parseInt(loadUri.getLastPathSegment());
                isItem = true;
            } catch (NumberFormatException e) {
                isItem = false;
            }
            if (isItem) {
                if (jsonPrimary == null) {
                    jsonPrimary = new JsonPrimary(mFragment, loadUri);
                } else {
                    jsonPrimary.addCursorToJsonPrimaryHashMap(loadUri);
                }
            } else {
                JsonListData jsonListData = new JsonListData(context, mFragment.getmUrl(), loadUri);
                listDataHashMap.put(jsonListData.getName(), jsonListData);
            }
        }
    }

    public void setChangeSupport(PresentationModelChangeSupport changeSupport) {
        jsonPrimary.setChangeSupport(changeSupport);
        for (JsonListData jsonListData : listDataHashMap.values()) {
            jsonListData.setChangeSupport(changeSupport);
        }
    }


    private void cacheToDB(JsonData jsonData, String tableName, String url) {
        String queryMd5 = UtilMethod.getMD5Str(url);
        Uri dataUri = UriConvertUtil.getDataUri(Uri.parse(url), tableName);
        DataProvider.addUriMatcherToDB(context, dataUri, tableName);
        ContentValues values = new ContentValues();
        for (String key : jsonData.jsonPrimary.keySet()) {
            ContentValueUtils.insertContentValues(values, key, jsonData.jsonPrimary.get(key));
        }
        values.put(DataProvider.COLUMN_URI_MD5, queryMd5);
        Uri returnUri = context.getContentResolver().insert(dataUri, values);
        jsonPrimary.setmUri(returnUri);
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
//                values.put(DataProvider.COLUMN_TABLE_NAME, listTableName);
                values.put(DataProvider.COLUMN_URI_MD5, queryMd5);
                item.setmIsModel2DB(true);
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

    public JsonListData getList(String name) {
        return listDataHashMap.get(name);
    }

    public BaseFragment getmFragment() {
        return mFragment;
    }

    public JsonPrimary getJsonPrimary() {
        return jsonPrimary;
    }

    public void unRegisterContentObserver() {
        context.getContentResolver().unregisterContentObserver(jsonPrimary.getmContentObserver());
        for (String keyItem : listDataHashMap.keySet()) {
            context.getContentResolver().unregisterContentObserver(getList(keyItem).getListContentObserver());
            for (JsonListItem item : getList(keyItem).getJsonListItems()) {
                context.getContentResolver().unregisterContentObserver(item.getmContentObserver());
            }
        }
    }
}


package com.kelin.library.data;

import android.content.Context;

import com.kelin.library.base.BaseFragment;

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
public class UrlJsonData {

    private BaseFragment mFragment;

    private Context context;

    private UrlJsonPrimary jsonPrimary;

    private HashMap<String, UrlJsonListData> listDataHashMap = new HashMap<>();

    private LinkedHashMap<String, Object> jsonMap = new LinkedHashMap<String, Object>();

    private ArrayList<String> jsonListName = new ArrayList<String>();

    private ArrayList<String> jsonObjectName = new ArrayList<String>();

    private HashMap<String, Integer> jsonArraySize = new HashMap<String, Integer>();


    public UrlJsonData(BaseFragment fragment, JSONObject jsonObject) {
        this.mFragment = fragment;
        this.context = fragment.getActivity();
        parseJsonObjec("", jsonObject);
        for (String listName : jsonListName) {
            UrlJsonListData jsonListData = new UrlJsonListData(listName, context);
            jsonListData.setUrlJsonListItems(getUrlJsonListItems(listName, jsonListData));
            listDataHashMap.put(listName, jsonListData);
        }
        jsonPrimary = new UrlJsonPrimary(fragment);

        for (String objectName : jsonObjectName) {
            jsonPrimary.add(objectName, jsonMap.get(objectName));
        }
    }

    private List<UrlJsonListItem> getUrlJsonListItems(String listName, UrlJsonListData jsonListData) {
        List<UrlJsonListItem> items = new ArrayList<UrlJsonListItem>();
        UrlJsonListItem item = null;
        for (Iterator<String> iterator = jsonMap.keySet().iterator(); iterator.hasNext(); ) {
            String jsonName = iterator.next();
            if (jsonName.contains("$") && jsonName.startsWith(listName)) {
                String subName = jsonName.substring(listName.length());
                String fieldName = subName.substring(subName.lastIndexOf('_') + 1);
                if (item == null || item.getJsonFieldMap().containsKey(listName+"_"+fieldName)) {
                    item = new UrlJsonListItem(context, jsonListData,jsonName);
                    items.add(item);
                }
                item.add(listName+"_"+fieldName, jsonMap.get(jsonName));
            }
        }
        return items;
    }

    public void setChangeSupport(PresentationModelChangeSupport changeSupport) {
        jsonPrimary.setChangeSupport(changeSupport);
        for (UrlJsonListData jsonListData : listDataHashMap.values()) {
            jsonListData.setChangeSupport(changeSupport);
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

    public HashMap<String, UrlJsonListData> getListDataHashMap() {
        return listDataHashMap;
    }

    public UrlJsonListData getList(String name) {
        return listDataHashMap.get(name);
    }

    public BaseFragment getmFragment() {
        return mFragment;
    }

    public UrlJsonPrimary getUrlJsonPrimary() {
        return jsonPrimary;
    }


}


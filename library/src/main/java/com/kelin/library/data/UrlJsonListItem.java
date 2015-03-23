package com.kelin.library.data;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.robobinding.presentationmodel.PresentationModelChangeSupport;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by kelin on 15-3-10.
 */
public class UrlJsonListItem {

    private UrlJsonListData jsonListData;

    private String listName;

    private String mUrl;

    public BroadcastReceiverHelper broadcastReceiver;

    private PresentationModelChangeSupport changeSupport;

    private HashMap<String, Object> jsonFieldMap = new HashMap<>();


    public UrlJsonListItem(Context context, UrlJsonListData jsonListData) {
        this.jsonListData = jsonListData;
        broadcastReceiver = new BroadcastReceiverHelper(context);

        if (jsonListData.getUrl() != null) {
            broadcastReceiver.registerAction(jsonListData.getUrl());
        }
    }

    public UrlJsonListItem(Context context, String jsonStr, String url) {
        broadcastReceiver = new BroadcastReceiverHelper(context);
        jsonFieldMap = new Gson().fromJson(jsonStr,
                new TypeToken<Map<String, Object>>() {
                }.getType());
        if (url != null) {
            broadcastReceiver.registerAction(url);
        }
    }

    public String getJsonString() {
        return new Gson().toJson(jsonFieldMap);
    }


    public Object get(String fieldName) {
        return jsonFieldMap.get(fieldName);
    }

    public HashMap<String, Object> getJsonFieldMap() {
        return jsonFieldMap;
    }

    public UrlJsonListData getUrlJsonListData() {
        return jsonListData;
    }

    public void setChangeSupport(PresentationModelChangeSupport changeSupport) {
        this.changeSupport = changeSupport;
    }

    public void setUrlJsonListData(UrlJsonListData jsonListData) {
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

        @Override
        public void onReceive(Context context, Intent intent) {
//            for (String action : mUrls) {
//                if (intent.getAction().equals(action)) {
//                    String key = intent.getStringExtra("key");
//                    changeSupport.firePropertyChange(key);
//                }
//            }
        }
    }

}

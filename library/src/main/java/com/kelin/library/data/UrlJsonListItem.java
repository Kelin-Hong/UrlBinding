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
    private static final String ACTION_NAME_PREFIX = "Com.UrlBinding.ListItem.";

    private UrlJsonListData jsonListData;

    private Context context;

    private String itemId;

    public BroadcastReceiverHelper broadcastReceiver;

    private PresentationModelChangeSupport changeSupport;

    private HashMap<String, Object> jsonFieldMap = new HashMap<>();


    public UrlJsonListItem(Context context, UrlJsonListData jsonListData, String itemId) {
        this.jsonListData = jsonListData;
        this.itemId = itemId;
        this.context = context;
        jsonFieldMap.put("item_id", itemId);
        broadcastReceiver = new BroadcastReceiverHelper(context);
        broadcastReceiver.registerAction(ACTION_NAME_PREFIX + itemId);
    }

    public UrlJsonListItem(Context context, String jsonStr, String itemId) {
        this.context = context;
        broadcastReceiver = new BroadcastReceiverHelper(context);
        jsonFieldMap = new Gson().fromJson(jsonStr,
                new TypeToken<Map<String, Object>>() {
                }.getType());
        broadcastReceiver.registerAction(ACTION_NAME_PREFIX + itemId);
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
            Intent intent = new Intent(ACTION_NAME_PREFIX + this.itemId);
            intent.putExtra("key", key);
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
            if (intent.getAction().equals(ACTION_NAME_PREFIX + itemId)) {
                String key = intent.getStringExtra("key");
                changeSupport.firePropertyChange(key);
            }

        }
    }

}

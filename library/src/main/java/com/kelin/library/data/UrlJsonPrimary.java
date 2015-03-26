package com.kelin.library.data;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kelin.library.base.BaseFragment;
import com.kelin.library.utils.UtilMethod;

import org.robobinding.presentationmodel.PresentationModelChangeSupport;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by kelin on 15-3-12.
 */
public class UrlJsonPrimary {
    private static final String ACTION_NAME_PREFIX = "Com.UrlBinding.Primary.";

    private Context context;

    private PresentationModelChangeSupport changeSupport;

    public BroadcastReceiverHelper broadcastReceiver;

    private String primaryId;

    private HashMap<String, Object> jsonPrimaryHashMap = new HashMap<>();

    public UrlJsonPrimary(BaseFragment fragment) {
        this.context = fragment.getActivity();
        broadcastReceiver = new BroadcastReceiverHelper(context);
        this.primaryId = UtilMethod.getMD5Str(fragment.getmUrl());
        jsonPrimaryHashMap.put("primaryId", this.primaryId);
        broadcastReceiver.registerAction(ACTION_NAME_PREFIX + this.primaryId);
    }

    public UrlJsonPrimary(BaseFragment fragment, String jsonStr) {
        this.context = fragment.getActivity();
        broadcastReceiver = new BroadcastReceiverHelper(context);
        jsonPrimaryHashMap = new Gson().fromJson(jsonStr,
                new TypeToken<Map<String, Object>>() {
                }.getType());
        this.primaryId = (String) jsonPrimaryHashMap.get("primaryId");
        broadcastReceiver.registerAction(ACTION_NAME_PREFIX + this.primaryId);


    }

    public String getJsonString() {
        return new Gson().toJson(jsonPrimaryHashMap);
    }


    public void setChangeSupport(PresentationModelChangeSupport changeSupport) {
        this.changeSupport = changeSupport;
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
            Intent intent = new Intent(ACTION_NAME_PREFIX + this.primaryId);
            intent.putExtra("key", key);
            context.sendBroadcast(intent);
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
            if (intent.getAction().equals(ACTION_NAME_PREFIX + UrlJsonPrimary.this.primaryId)) {
                String key = intent.getStringExtra("key");
                changeSupport.firePropertyChange(key);
            }

        }
    }

}

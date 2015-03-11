package com.kelin.library.loader;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.kelin.library.utils.JsonData;

import org.json.JSONObject;

/**
 * Created by kelin on 15-2-28.
 */
public class AsyncLoader {
    private Context mContext;
    private String mUrl;
    private long mLoaderId;
    private RequestQueue mQueue;
    private OnLoadFinishedListener mOnLoadFinishedListener;
    private boolean mNeedCache;

    public AsyncLoader(long loaderId, String url, Context context) {
        mLoaderId = loaderId;
        mContext = context;
        mUrl = url;
    }

    public AsyncLoader(long loaderId, String url, Context context, boolean needCache) {
        this(loaderId, url, context);
        this.mNeedCache = needCache;
    }

    public AsyncLoader(long loaderId, String url, Context context, boolean needCache, OnLoadFinishedListener onLoadFinishedListener) {
        this(loaderId, url, context, needCache);
        this.mOnLoadFinishedListener = onLoadFinishedListener;

    }

    public void loadDataFromNet() {
        mQueue = Volley.newRequestQueue(mContext);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(mUrl, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
//                JsonData jsonData = new JsonData(jsonObject);
//                if (mOnLoadFinishedListener != null) {
//                    mOnLoadFinishedListener.onLoadFinished(mLoaderId, jsonData, null);
//                }
//                if (replaceFragment) {
//                    mJsonData = jsonData;
//                    BaseDataFragment baseDataFragment = BaseDataFragment.newInstance(mUrl, mLayoutId);
//                    FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
//                    transaction.replace(R.id.content, baseDataFragment);
//                    transaction.commit();
//                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
//                if (mOnLoadFinishedListener != null) {
//                    mOnLoadFinishedListener.onLoadFinished(mLoaderId, null, volleyError);
//                }
            }
        });
        mQueue.add(jsonObjectRequest);
    }

    public boolean ismNeedCache() {
        return mNeedCache;
    }

    public void setmNeedCache(boolean mNeedCache) {
        this.mNeedCache = mNeedCache;
    }

    public OnLoadFinishedListener getmOnLoadFinishedListener() {
        return mOnLoadFinishedListener;
    }

    public void setmOnLoadFinishedListener(OnLoadFinishedListener mOnLoadFinishedListener) {
        this.mOnLoadFinishedListener = mOnLoadFinishedListener;
    }

}

package com.kelin.library.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.kelin.library.R;
import com.kelin.library.loader.OnLoadFinishedListener;
import com.kelin.library.utils.JsonData;
import com.kelin.library.viewmodel.PresentationModelParent;

import org.json.JSONObject;

public abstract class BaseFragment extends android.support.v4.app.Fragment {

    protected String mUrl;
    protected int mLayoutId;
    protected String mTableName;
    protected JsonData mJsonData;
    protected boolean mIsLoadFromDB = false;

    protected PresentationModelParent mPresentationModel;

    OnLoadFinishedListener mOnLoadFinishedListener = new OnLoadFinishedListener() {
        @Override
        public void onLoadFinished(JsonData jsonData, VolleyError volleyError) {
            onDataLoadedFinish(jsonData, volleyError);
            if (jsonData != null) {
                mJsonData = jsonData;
                BaseDataFragment baseDataFragment = BaseDataFragment.newInstance(mUrl, mLayoutId);
                FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
                transaction.replace(R.id.content, baseDataFragment);
                transaction.commit();
            }

        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mUrl = getArguments().getString("url");
            mLayoutId = getArguments().getInt("layout_id");
            mTableName = getArguments().getString("table_name");
            mIsLoadFromDB = getArguments().getBoolean("from_db", false);
            if (mIsLoadFromDB) {
                return;
            }
            if (mTableName != null) {
                loadDataAndCache(mUrl, mTableName, mOnLoadFinishedListener);
            } else {
                loadData(mUrl, mOnLoadFinishedListener);
            }
        }
    }


    protected void loadDataFromDB(String url, final OnLoadFinishedListener onLoadFinishedListener) {
        JsonData jsonData = new JsonData(BaseFragment.this);
        if (onLoadFinishedListener != null) {
            onLoadFinishedListener.onLoadFinished(jsonData, null);
        }
    }


    protected void loadData(final String url, final OnLoadFinishedListener onLoadFinishedListener) {
        RequestQueue mQueue = Volley.newRequestQueue(this.getActivity());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                JsonData jsonData = new JsonData(BaseFragment.this, jsonObject);
                if (onLoadFinishedListener != null) {
                    onLoadFinishedListener.onLoadFinished(jsonData, null);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                if (onLoadFinishedListener != null) {
                    onLoadFinishedListener.onLoadFinished(null, volleyError);
                }
            }
        });
        mQueue.add(jsonObjectRequest);
    }

    protected void loadDataAndCache(final String url, final String tableName, final OnLoadFinishedListener onLoadFinishedListener) {
        RequestQueue mQueue = Volley.newRequestQueue(this.getActivity());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                JsonData jsonData = new JsonData(BaseFragment.this, jsonObject);
                if (onLoadFinishedListener != null) {
                    onLoadFinishedListener.onLoadFinished(jsonData, null);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                if (onLoadFinishedListener != null) {
                    onLoadFinishedListener.onLoadFinished(null, volleyError);
                }
            }
        });
        mQueue.add(jsonObjectRequest);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragmennt_base, null, false);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.content, createLoadingFragment());
        transaction.commit();
        if (mIsLoadFromDB) {
            loadDataFromDB(mUrl, mOnLoadFinishedListener);
        }
    }

    public JsonData getmJsonData() {
        return mJsonData;
    }

    public String getmTableName() {
        return mTableName;
    }

    public String getmUrl() {
        return mUrl;
    }

    private Fragment loadingFragment;

    protected Fragment createLoadingFragment() {
        loadingFragment = new BaseLoadingFragment();
        return loadingFragment;
    }

    protected Class functionPresentationModelClass() {
        return PresentationModelParent.class;
    }

    public abstract void onDataLoadedAndViewCreated(View view, @Nullable Bundle savedInstanceState);


    public abstract void onDataLoadedFinish(JsonData jsonData, VolleyError volleyError);


    @Override
    public void onDestroy() {
        super.onDestroy();
        mJsonData.unRegisterContentObserver();
        mJsonData = null;
    }
}
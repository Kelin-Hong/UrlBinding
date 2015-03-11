package com.kelin.urlbinding.demo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.android.volley.VolleyError;
import com.kelin.library.base.BaseFragment;
import com.kelin.library.utils.JsonData;

/**
 * Created by kelin on 15-3-10.
 */
public class CacheListFragment extends BaseFragment {
    public static CacheListFragment newInstance(String url, int layoutId, String tableName) {
        CacheListFragment simpelListFragment = new CacheListFragment();
        Bundle bundle = new Bundle();
        bundle.putString("url", url);
        bundle.putInt("layout_id", layoutId);
        bundle.putString("table_name", tableName);
        simpelListFragment.setArguments(bundle);
        return simpelListFragment;
    }

    public static CacheListFragment newInstance(String url, int layoutId, boolean isFromDB) {
        CacheListFragment simpelListFragment = new CacheListFragment();
        Bundle bundle = new Bundle();
        bundle.putString("url", url);
        bundle.putInt("layout_id", layoutId);
        bundle.putBoolean("from_db", true);
        simpelListFragment.setArguments(bundle);
        return simpelListFragment;
    }

    @Override
    public void onDataLoadedAndViewCreated(View view, @Nullable Bundle savedInstanceState) {

    }

    @Override
    public void onDataLoadedFinish(JsonData jsonData, VolleyError volleyError) {

    }
}

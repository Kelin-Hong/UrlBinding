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
public class ChangeModelBeforeShowFragment extends BaseFragment {
    public static ChangeModelBeforeShowFragment newInstance(String url, int layoutId, String tableName) {
        ChangeModelBeforeShowFragment simpelListFragment = new ChangeModelBeforeShowFragment();
        Bundle bundle = new Bundle();
        bundle.putString("url", url);
        bundle.putInt("layout_id", layoutId);
        bundle.putString("table_name", tableName);
        simpelListFragment.setArguments(bundle);
        return simpelListFragment;
    }

    @Override
    public void onDataLoadedAndViewCreated(View view, @Nullable Bundle savedInstanceState) {

    }

    @Override
    public void onDataLoadedFinish(JsonData jsonData, VolleyError volleyError) {
        jsonData.getJsonPrimary().add("stid_visibility", false);
        jsonData.getJsonPrimary().update("stid", "Hello UrlBinding");
//      jsonData.getList("data_homepage").removeAndChangeDB(0);
    }
}

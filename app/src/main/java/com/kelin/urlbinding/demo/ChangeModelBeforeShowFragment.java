package com.kelin.urlbinding.demo;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.android.volley.VolleyError;
import com.kelin.library.base.BaseFragment;
import com.kelin.library.data.JsonData;

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
        jsonData.getJsonPrimary().add("stid_visibility", true);
        jsonData.getJsonPrimary().add("stid_textColor", getResources().getColor(android.R.color.holo_blue_dark));
        jsonData.getJsonPrimary().update("stid", "Hello UrlBinding");
        Drawable drawable = getResources().getDrawable(android.R.drawable.btn_star);
        for (int i = 0; i < jsonData.getList("data_homepage").getSize(); i++) {
            jsonData.getList("data_homepage").get(i).addAndChangeDB("data_homepage_src", android.R.drawable.btn_star);
        }
    }
}

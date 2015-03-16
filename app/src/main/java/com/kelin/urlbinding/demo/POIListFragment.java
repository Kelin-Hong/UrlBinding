package com.kelin.urlbinding.demo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.android.volley.VolleyError;
import com.kelin.library.base.BaseFragment;
import com.kelin.library.utils.JsonData;
import com.kelin.library.utils.JsonListData;
import com.kelin.library.utils.JsonListItem;

/**
 * Created by kelin on 15-3-10.
 */
public class POIListFragment extends BaseFragment {
    public static POIListFragment newInstance(String url, int layoutId) {
        POIListFragment simpelListFragment = new POIListFragment();
        Bundle bundle = new Bundle();
        bundle.putString("url", url);
        bundle.putInt("layout_id", layoutId);
//        bundle.putBoolean("from_db",true);
        simpelListFragment.setArguments(bundle);
        return simpelListFragment;
    }

    @Override
    public void onDataLoadedAndViewCreated(View view, @Nullable Bundle savedInstanceState) {

    }

    @Override
    public void onDataLoadedFinish(JsonData jsonData, VolleyError volleyError) {
        JsonListData jsonListData = jsonData.getList("data");
        for (JsonListItem jsonListItem : jsonListData.getJsonListItems()) {
            String imageUrl = (String) jsonListItem.get("data_frontImg");
            String newImageUrl=imageUrl.replace("/w.h/", "/200.120/");
            jsonListItem.update("data_frontImg", newImageUrl);
        }
    }
}

package com.kelin.urlbinding.demo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kelin.library.base.BaseFragment;
import com.kelin.library.data.JsonData;

import java.util.List;

/**
 * Created by kelin on 15-3-10.
 */
public class CategoryDetailFragment extends BaseFragment {
    public static CategoryDetailFragment newInstance(String url, int layoutId, List<String> uris) {
        CategoryDetailFragment simpelListFragment = new CategoryDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putString("url", url);
        bundle.putInt("layout_id", layoutId);
        String uriListStr = new Gson().toJson(uris, new TypeToken<List<String>>() {
        }.getType());
        bundle.putString("uri_list", uriListStr);
        simpelListFragment.setArguments(bundle);
        return simpelListFragment;
    }

    @Override
    public void onDataLoadedAndViewCreated(View view, @Nullable Bundle savedInstanceState) {

    }

    @Override
    public void onDataLoadedFinish(JsonData jsonData, VolleyError volleyError) {
    }

    @Override
    protected Class eventPresentationModelClass() {
        return CategoryDetailEventPresentationModel.class;
    }
}

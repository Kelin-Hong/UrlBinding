package com.kelin.urlbinding.demo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.android.volley.VolleyError;
import com.kelin.library.base.BaseFragment;
import com.kelin.library.data.JsonData;

/**
 * Created by kelin on 15-3-10.
 */
public class SimpelListFragment extends BaseFragment {
    public static SimpelListFragment newInstance(String url, int layoutId) {
        SimpelListFragment simpelListFragment = new SimpelListFragment();
        Bundle bundle = new Bundle();
        bundle.putString("url", url);
        bundle.putInt("layout_id", layoutId);
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

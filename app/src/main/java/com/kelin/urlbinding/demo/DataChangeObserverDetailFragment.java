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
public class DataChangeObserverDetailFragment extends BaseFragment {
    public static DataChangeObserverDetailFragment newInstance(String jsonString, int layoutId, String ids) {
        DataChangeObserverDetailFragment simpelListFragment = new DataChangeObserverDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("layout_id", layoutId);
        bundle.putString("json_str", jsonString);
        bundle.putString("json_data_ids", ids);
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

package com.kelin.urlbinding;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.android.volley.VolleyError;
import com.kelin.library.base.BaseFragment;
import com.kelin.library.utils.JsonData;
import com.kelin.library.viewmodel.PresentationModelParent;

/**
 * Created by kelin on 15-2-9.
 */
public class CategoryBarFragment extends BaseFragment {
    public static JsonData jsonData;

    public static CategoryBarFragment newInstance(String url, int layoutId) {
        CategoryBarFragment categoryBarFragment = new CategoryBarFragment();
        Bundle bundle = new Bundle();
        bundle.putString("url", url);
        bundle.putInt("layout_id", layoutId);
        categoryBarFragment.setArguments(bundle);
        return categoryBarFragment;
    }


    @Override
    protected Class functionPresentationModelClass() {
        return FunctionPresentationModel.class;
    }

    @Override
    public void onDataLoadedAndViewCreated(View view, @Nullable Bundle savedInstanceState) {

    }

    @Override
    public void onDataLoadedFinish(JsonData jsonData, VolleyError volleyError) {

    }


}

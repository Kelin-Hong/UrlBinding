package com.kelin.library.loader;

import android.net.Uri;
import android.support.v4.content.AsyncTaskLoader;

import com.kelin.library.base.BaseFragment;
import com.kelin.library.data.JsonData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kelin on 15-3-23.
 */
public class LoadDataFromDBLoader extends AsyncTaskLoader<JsonData> {
    private BaseFragment fragment;
    protected List<Uri> uriList = new ArrayList<Uri>();

    public LoadDataFromDBLoader(BaseFragment fragment, List<Uri> uris) {
        super(fragment.getActivity());
        this.fragment = fragment;
        this.uriList = uris;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public JsonData loadInBackground() {
        return new JsonData(fragment, uriList);
    }
}


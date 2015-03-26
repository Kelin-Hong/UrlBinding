package com.kelin.library.loader;

import com.android.volley.VolleyError;
import com.kelin.library.data.JsonData;
import com.kelin.library.data.UrlJsonData;

/**
 * Created by kelin on 15-2-28.
 */
public interface OnUrlDataLoadFinishedListener {
    public void onLoadFinished(UrlJsonData jsonData, VolleyError volleyError);
}

package com.kelin.library.loader;

import com.android.volley.VolleyError;
import com.kelin.library.data.JsonData;

/**
 * Created by kelin on 15-2-28.
 */
public interface OnLoadFinishedListener {
    public void onLoadFinished(JsonData jsonData,VolleyError volleyError);
}

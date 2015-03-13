package com.kelin.urlbinding;

import android.database.ContentObserver;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.kelin.library.base.BaseActivity;
import com.kelin.library.utils.JsonData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class TopicListActivity extends BaseActivity {
    private ContentObserver mContentObserver = new ContentObserver(new Handler()) {
        @Override
        public void onChange(boolean selfChange,Uri uri) {
            super.onChange(selfChange);
            int i = 0;
//            mFragment.getmPresentationModel().getPresentationModelChangeSupport().refreshPresentationModel();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.fragmennt_base);
        Uri dataUri = new Uri.Builder().scheme("content").authority("com.kelin.project").appendEncodedPath("volga/api/v2/trip/zhoubianyou/cate/menu/category/1").build();
        Uri notifyUri = new Uri.Builder().scheme("content").authority("com.kelin.project").appendEncodedPath("volga/api/v2/trip/zhoubianyou/cate/menu/category/1").build();
        getContentResolver().registerContentObserver(dataUri, false, mContentObserver);
        getContentResolver().notifyChange(notifyUri, null);
//        try {
//            loadData();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        setContentView(R.layout.activity_topic_list);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void loadData() {
        RequestQueue mQueue = Volley.newRequestQueue(this);
        final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest("http://lvyou.meituan.com/volga/api/v2/trip/zhoubianyou/cate/menu?cityId=1&version=5.4&client=android", null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("TAG", response.toString());
                        JSONArray jsonArray = response.names();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            try {
                                System.out.println(response.get((String) jsonArray.get(i)));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
//                        JsonData jsonData = new JsonData(response);
//                        try {
//                            presentation(jsonData);
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("TAG", error.getMessage(), error);
            }
        });
        mQueue.add(jsonObjectRequest);
    }

    private void presentation(JsonData jsonData) throws Exception {
//        UrlBinderFactoryBuilder urlBinderFactoryBuilder = new UrlBinderFactoryBuilder();
//        urlBinderFactoryBuilder.mapView(NetImageView.class, new NetImageViewBinding());
////        myBinderFactoryBuilder.add(new DynamicViewBinding().forView(NetworkImageView.class).oneWayProperties("imageUrl"));
//        final PresentationModelParent presentationModelParent = (PresentationModelParent) PresentationModelGen.generatePresentationModel(this, "PresentationModel3", jsonParser);
//        AbstractPresentationModelParent abstractPresentationModelParent = AbstractPresentationModelObjectGen.generateAbstractPresentationModel(this, jsonParser, "AbstractPresentationModel3", presentationModelParent);
//        initializeContentView(R.layout.activity_topic_list, urlBinderFactoryBuilder, abstractPresentationModelParent, presentationModelParent);
    }
}

package com.kelin.urlbinding.demo;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.kelin.library.base.BaseFragment;
import com.kelin.urlbinding.R;

public class DataChangeObserverDetailActivity extends ActionBarActivity {
    private final static String mUrl = "http://lvyou.meituan.com/volga/api/v2/trip/zhoubianyou/cate/menu?cityId=1&version=5.4&client=android";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple_list);
        String jsonStr = getIntent().getStringExtra(DataChangeObserverPresentationModel.ARG_ITEM_JSON_STRING);
        String jsonIDs = getIntent().getStringExtra(DataChangeObserverPresentationModel.ARG_ITEM_JSON_DATA_ID);
        BaseFragment baseFragment = DataChangeObserverDetailFragment.newInstance(jsonStr, R.layout.fragment_category_detail, jsonIDs);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(com.kelin.library.R.id.content, baseFragment);
        transaction.commit();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_event_binding, menu);
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
}

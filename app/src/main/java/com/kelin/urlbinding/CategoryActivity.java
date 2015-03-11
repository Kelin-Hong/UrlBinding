package com.kelin.urlbinding;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.kelin.library.base.BaseFragment;


public class CategoryActivity extends ActionBarActivity {
    private String mUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        if (getIntent().getData() != null) {
            mUrl = getIntent().getData().buildUpon().scheme("http").authority("lvyou.meituan.com").toString();
        }
        BaseFragment baseFragment = HotCityFragment.newInstance(mUrl, R.layout.fragment_hot_city);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(com.kelin.library.R.id.content, baseFragment);
        transaction.commit();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_category, menu);
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

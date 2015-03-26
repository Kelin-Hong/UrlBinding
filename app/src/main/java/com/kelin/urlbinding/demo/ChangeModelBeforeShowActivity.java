package com.kelin.urlbinding.demo;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.kelin.library.base.BaseFragment;
import com.kelin.urlbinding.R;

public class ChangeModelBeforeShowActivity extends ActionBarActivity {
    private final static String mUrl = "http://lvyou.meituan.com/volga/api/v2/trip/zhoubianyou/cate/menu?cityId=1&version=5.4&client=android";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple_list);
        BaseFragment baseFragment = ChangeModelBeforeShowFragment.newInstance(mUrl, R.layout.fragment_change_model_before_show, "category");
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(com.kelin.library.R.id.content, baseFragment);
        transaction.commit();
    }


}

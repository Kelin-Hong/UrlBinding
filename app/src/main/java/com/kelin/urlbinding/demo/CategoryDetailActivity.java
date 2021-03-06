package com.kelin.urlbinding.demo;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.kelin.library.base.BaseFragment;
import com.kelin.library.utils.UriConvertUtil;
import com.kelin.urlbinding.R;

import java.util.ArrayList;
import java.util.List;

public class CategoryDetailActivity extends ActionBarActivity {
    private final static String mUrl = "http://lvyou.meituan.com/volga/api/v2/trip/zhoubianyou/cate/menu?cityId=1&version=5.4&client=android";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple_list);
        String uri = getIntent().getStringExtra(EventPresentationModel.ARG_ITEM_URI);
        List<String> uriList = new ArrayList<>();
        List<Uri> urlList = UriConvertUtil.getDataUri(this, Uri.parse(mUrl));
        uriList.add(uri);
        uriList.add(urlList.get(0).toString());
        BaseFragment baseFragment = CategoryDetailFragment.newInstance(mUrl, R.layout.fragment_category_detail, uriList);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(com.kelin.library.R.id.content, baseFragment);
        transaction.commit();
    }


}

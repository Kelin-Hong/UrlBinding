package com.kelin.urlbinding;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.kelin.library.UrlBindingApp;
import com.kelin.library.base.BaseActivity;
import com.kelin.library.dao.DataProvider;
import com.kelin.library.utils.JsonData;
import com.kelin.library.utils.UriConvertUtil;
import com.kelin.library.utils.UtilMethod;

import org.robobinding.dynamicbinding.DynamicViewBinding;
import org.robobinding.widget.textview.TextViewBinding;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends BaseActivity {
    private final static String mUrl = "http://lvyou.meituan.com/volga/api/v2/trip/zhoubianyou/cate/menu?cityId=1&version=5.4&client=android";
    private TextView mTextView;
    JsonData jsonData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ((UrlBindingApp) getApplication()).urlBinderFactoryBuilder.add(new DynamicViewBinding().extend(TextView.class, new TextViewBinding()).oneWayProperties("textColor"));
        List<Uri> uris = UriConvertUtil.getDataUri(Uri.parse(mUrl));
        mTextView = (TextView) findViewById(R.id.text);
//        query();
        findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                Uri uri = new Uri.Builder().scheme("kelin").authority("www.kelin.com").appendEncodedPath("volga/api/v2/trip/zhoubianyou/cate/menu")
                        .appendQueryParameter("cityId", "1").appendQueryParameter("version", "5.4").appendQueryParameter("client", "android").build();
                intent.setData(uri);
//                int count = delete();
//                query();
                startActivity(intent);
            }
        });


//        BaseFragment baseFragment = HotCityFragment.newInstance(mUrl, R.layout.fragment_hot_city);
//        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//        transaction.replace(com.kelin.library.R.id.content, baseFragment);
//        transaction.commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        int i = 0;
    }

//    private void query() {
//        Uri uri = new Uri.Builder().scheme("http").authority("lvyou.meituan.com").appendEncodedPath("volga/api/v2/trip/zhoubianyou/cate/menu")
//                .appendQueryParameter("cityId", "1").appendQueryParameter("version", "5.4").appendQueryParameter("client", "android").build();
//        Uri dataUri = UriConvertUtil.getDataUri(uri).get(0);
//        Cursor cursor = getContentResolver().query(dataUri, null, DataProvider.COLUMN_URI_MD5 + " = ?", new String[]{UtilMethod.getMD5Str(uri.toString())}, null);
//        cursor.moveToFirst();
//        int id = cursor.getInt(cursor.getColumnIndex("_id"));
//        List<Cursor> cursorList = new ArrayList<Cursor>();
//        cursorList.add(cursor);
//        jsonData = new JsonData(this, cursorList);
//        Uri testUri=dataUri.buildUpon().appendEncodedPath(id + "").build();
//        jsonData.setmUri(testUri);
//        mTextView.setText(jsonData.get("stid").toString());
//
//    }

    private void query2() {
        Uri dataUri = new Uri.Builder().scheme("content").authority("com.kelin.project").appendEncodedPath("volga/api/v2/trip/zhoubianyou/cate/menu/category_data_homepage/").build();
        Cursor cursor = getContentResolver().query(dataUri, new String[]{"tbl_iconUrl"}, null, null, null);
        cursor.moveToNext();
        String s = cursor.getString(cursor.getColumnIndex("tbl_iconUrl"));
        cursor.close();
    }

    private int delete() {
        Uri dataUri = new Uri.Builder().scheme("content").authority("com.kelin.project").appendEncodedPath("volga/api/v2/trip/zhoubianyou/cate/menu/category").build();
        return getContentResolver().delete(dataUri, null, null);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

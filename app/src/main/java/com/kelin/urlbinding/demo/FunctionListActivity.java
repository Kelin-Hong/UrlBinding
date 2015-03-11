package com.kelin.urlbinding.demo;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.kelin.library.dao.DataProvider;
import com.kelin.urlbinding.R;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class FunctionListActivity extends ActionBarActivity {
    private final static String MAP_TITLE = "title";
    private final static String MAP_INTENT = "intent";
    private static final String INTENT_CATEGORY = "com.kelin.uribinding.main.DEMO";
    private ListView mListView;
    private List<String> mTitleList = new ArrayList<String>();
    private List<Map<String, Object>> mData = new ArrayList<Map<String, Object>>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_function_list);
        DataProvider.DBHelper  dbHelper=new DataProvider.DBHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        mListView = (ListView) findViewById(R.id.listview);
        getActivityInfo();
        Iterator<Map<String, Object>> it = mData.iterator();
        while (it.hasNext()) {
            mTitleList.add(it.next().get(MAP_TITLE).toString());

        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, mTitleList);
        mListView.setAdapter(adapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                startActivity((Intent) mData.get(position).get(MAP_INTENT));
            }
        });
    }

    public String getCategory() {
        return INTENT_CATEGORY;
    }

    private void getActivityInfo() {
        PackageManager pm = getPackageManager();
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(this.getCategory());
        List<ResolveInfo> list = pm.queryIntentActivities(mainIntent, 0);
        if (null == list) return;
        for (int i = 0; i < list.size(); i++) {
            ResolveInfo info = list.get(i);
            String lable = info.loadLabel(pm).toString();
            if (lable.contains("/")) {
                String[] lableArr = lable.split("/");
                String title = lableArr[1];
                String packageName = info.activityInfo.applicationInfo.packageName;
                String className = info.activityInfo.name;
                Intent intent = new Intent();
                intent.setClassName(packageName, className);
                Map<String, Object> temp = new HashMap<String, Object>();
                temp.put(MAP_TITLE, title);
                temp.put(MAP_INTENT, intent);
                mData.add(temp);
            }
        }
        Collections.sort(mData, comparator);
    }

    private Comparator<Map<String, Object>> comparator = new Comparator<Map<String, Object>>() {
        private Collator collator = Collator.getInstance();

        @Override
        public int compare(Map<String, Object> lhs, Map<String, Object> rhs) {
            return collator.compare(lhs.get(MAP_TITLE), rhs.get(MAP_TITLE));
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_function_list, menu);
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

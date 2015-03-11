package com.kelin.library;

import android.app.Application;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.Environment;
import android.os.Handler;

import com.kelin.library.dao.DataProvider;
import com.kelin.library.utils.UriConvertUtil;
import com.kelin.library.widget.NetImageView;
import com.kelin.library.widget.NetImageViewBinding;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import org.robobinding.binder.UrlBinderFactory;
import org.robobinding.binder.UrlBinderFactoryBuilder;

import java.io.File;


/**
 * Created by kelin on 15-1-12.
 */
public class UrlBindingApp extends Application {
    private UrlBinderFactory reusableBinderFactory;
    public UrlBinderFactoryBuilder urlBinderFactoryBuilder;
    public static String IMAGE_PATH = Environment.getExternalStorageDirectory()
            + "/UrlBinding/images/";
//    private DatabaseHelper databaseHelper;

    @Override
    public void onCreate() {
        super.onCreate();

//        databaseHelper = new DatabaseHelper(this);
        urlBinderFactoryBuilder = new UrlBinderFactoryBuilder();
        urlBinderFactoryBuilder.mapView(NetImageView.class, new NetImageViewBinding());
        reusableBinderFactory = urlBinderFactoryBuilder.build();
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true).cacheOnDisk(true).build();
        File cacheDir = new File(IMAGE_PATH);
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                getApplicationContext()).defaultDisplayImageOptions(defaultOptions)
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .memoryCache(new LruMemoryCache(2 * 1024 * 1024))
                .memoryCacheSize(2 * 1024 * 1024)
                .diskCache(new UnlimitedDiscCache(cacheDir))
                .denyCacheImageMultipleSizesInMemory()
                .tasksProcessingOrder(QueueProcessingType.LIFO).build();
        ImageLoader.getInstance().init(config);
        loadUriMatcherData();
        getContentResolver().registerContentObserver(DataProvider.URI_MATCHER_URI, true, new ContentObserver(new Handler()) {
            @Override
            public void onChange(boolean selfChange) {
                super.onChange(selfChange);
                loadUriMatcherData();
            }
        });

    }

    private void loadUriMatcherData() {
        Cursor cursor = getContentResolver().query(DataProvider.URI_MATCHER_URI, null, null, null, null);

        while (cursor.moveToNext()) {
            if (UriConvertUtil.sAllUriPaths.contains(cursor.getString(cursor.getColumnIndex("path")))) {
                continue;
            }
            UriConvertUtil.sAllUriPaths.add(cursor.getString(cursor.getColumnIndex("path")));
            DataProvider.uriMatcher.addURI(DataProvider.AUTHORITY, cursor.getString(cursor.getColumnIndex("path")), cursor.getInt(cursor.getColumnIndex("code")));
            DataProvider.uriMatcher.addURI(DataProvider.AUTHORITY, cursor.getString(cursor.getColumnIndex("path")).toString() + "#", cursor.getInt(cursor.getColumnIndex("code")));
            DataProvider.tableNameMap.put(cursor.getInt(cursor.getColumnIndex("code")), cursor.getString(cursor.getColumnIndex("table_name")));
        }
        cursor.close();

    }


    public UrlBinderFactory getReusableBinderFactory() {
        return reusableBinderFactory;
    }

//    public DatabaseHelper getDatabaseHelper() {
//        return databaseHelper;
//    }
}

package com.kelin.library.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.kelin.library.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

/**
 * Created by kelin on 15-2-6.
 */
public class NetImageView extends ImageView {

    /*
      the resource id of default image when loading
     */
    private Drawable mImageForLoading;

    private Drawable mImageForEmptyUri;

    private Drawable mImageForOnFail;

    private boolean mCacheInMemory;

    private boolean mCacheOnDisc;

    private int mRoundedAngle;

    private DisplayImageOptions options;

    public NetImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs,R.styleable.NetImageView);

        mImageForLoading = a.getDrawable(R.styleable.NetImageView_default_image);
        mImageForEmptyUri = a.getDrawable(R.styleable.NetImageView_empty_uri_image);
        mImageForOnFail = a.getDrawable(R.styleable.NetImageView_fail_image);
        mCacheInMemory = a.getBoolean(R.styleable.NetImageView_cache_memory, true);
        mCacheOnDisc = a.getBoolean(R.styleable.NetImageView_cache_disc, true);
        mRoundedAngle = a.getInteger(R.styleable.NetImageView_round_angle, 0);
        setUrl(a.getString(R.styleable.NetImageView_url));
        a.recycle();
    }

    public void setUrl(String url) {
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(mImageForLoading)
                .showImageForEmptyUri(mImageForEmptyUri)
                .showImageOnFail(mImageForOnFail)
                .cacheInMemory(mCacheInMemory)
                .cacheOnDisk(mCacheOnDisc)
                .displayer(new RoundedBitmapDisplayer(mRoundedAngle))
                .build();
        ImageLoader.getInstance().displayImage(url, this, options);
    }

    public DisplayImageOptions getOptions() {
        return options;
    }

    public Drawable getmImageForLoading() {
        return mImageForLoading;
    }

    public void setmImageForLoading(Drawable mImageForLoading) {
        this.mImageForLoading = mImageForLoading;
    }

    public Drawable getmImageForEmptyUri() {
        return mImageForEmptyUri;
    }

    public void setmImageForEmptyUri(Drawable mImageForEmptyUri) {
        this.mImageForEmptyUri = mImageForEmptyUri;
    }

    public Drawable getmImageForOnFail() {
        return mImageForOnFail;
    }

    public void setmImageForOnFail(Drawable mImageForOnFail) {
        this.mImageForOnFail = mImageForOnFail;
    }

    public boolean ismCacheInMemory() {
        return mCacheInMemory;
    }

    public void setmCacheInMemory(boolean mCacheInMemory) {
        this.mCacheInMemory = mCacheInMemory;
    }

    public boolean ismCacheOnDisc() {
        return mCacheOnDisc;
    }

    public void setmCacheOnDisc(boolean mCacheOnDisc) {
        this.mCacheOnDisc = mCacheOnDisc;
    }

    public int getmRoundedAngle() {
        return mRoundedAngle;
    }

    public void setmRoundedAngle(int mRoundedAngle) {
        this.mRoundedAngle = mRoundedAngle;
    }

    public void setOptions(DisplayImageOptions options) {
        this.options = options;
    }
}

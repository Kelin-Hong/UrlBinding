package com.kelin.library.widget;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.robobinding.viewattribute.property.PropertyViewAttribute;

/**
 * Created by kelin on 15-2-8.
 */
public class UrlImagePropertyViewAttribute implements PropertyViewAttribute<NetImageView, String> {
    @Override
    public void updateView(NetImageView imageView, String url) {
        DisplayImageOptions options=imageView.getOptions();
        ImageLoader.getInstance().displayImage(url, imageView,options);
    }
}

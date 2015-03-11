package com.kelin.library.widget;

import org.robobinding.viewattribute.BindingAttributeMappings;
import org.robobinding.viewattribute.ViewBinding;

/**
 * Created by kelin on 15-2-4.
 */
public class NetImageViewBinding implements ViewBinding<NetImageView> {
    @Override
    public void mapBindingAttributes(BindingAttributeMappings<NetImageView> netImageViewBindingAttributeMappings) {
        netImageViewBindingAttributeMappings.mapProperty(UrlImagePropertyViewAttribute.class, "url");
    }
}


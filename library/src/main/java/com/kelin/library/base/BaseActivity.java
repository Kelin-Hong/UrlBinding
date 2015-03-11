package com.kelin.library.base;

import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;


import com.kelin.library.UrlBindingApp;
import com.kelin.library.viewmodel.AbstractPresentationModelParent;
import com.kelin.library.widget.NetImageView;
import com.kelin.library.widget.NetImageViewBinding;

import org.robobinding.MenuBinder;
import org.robobinding.ViewBinder;
import org.robobinding.binder.UrlBinderFactory;
import org.robobinding.binder.UrlBinderFactoryBuilder;

public abstract class BaseActivity extends ActionBarActivity {
    public void initializeContentView(int layoutId, AbstractPresentationModelParent presentationModelObjectType, Object presentationModel) {
        ViewBinder viewBinder = createViewBinder(presentationModelObjectType);
        View rootView = viewBinder.inflateAndBind(layoutId, presentationModel);
        setContentView(rootView);
    }

    public void initializeContentView(int layoutId, UrlBinderFactoryBuilder urlBinderFactoryBuilder, AbstractPresentationModelParent presentationModelObjectType, Object presentationModel) {

        ViewBinder viewBinder = urlBinderFactoryBuilder.build().createViewBinder(this, presentationModelObjectType);
        View rootView = viewBinder.inflateAndBind(layoutId, presentationModel);
        setContentView(rootView);
    }

    private ViewBinder createViewBinder(AbstractPresentationModelParent presentationModelObjectType) {
        UrlBinderFactory binderFactory = getReusableBinderFactory();
        return binderFactory.createViewBinder(this, presentationModelObjectType);
    }

    private UrlBinderFactory getReusableBinderFactory() {
        UrlBinderFactory binderFactory = getApp().getReusableBinderFactory();
        return binderFactory;
    }

    private UrlBindingApp getApp() {
        return (UrlBindingApp) getApplicationContext();
    }

    protected MenuBinder createMenuBinder(Menu menu, MenuInflater menuInflater) {
        UrlBinderFactory binderFactory = getReusableBinderFactory();
        return binderFactory.createMenuBinder(menu, menuInflater, this);
    }
}
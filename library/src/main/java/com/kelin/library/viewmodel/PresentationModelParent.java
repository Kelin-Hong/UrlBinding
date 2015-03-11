package com.kelin.library.viewmodel;

import com.kelin.library.utils.JsonData;

import org.robobinding.presentationmodel.HasPresentationModelChangeSupport;
import org.robobinding.presentationmodel.PresentationModelChangeSupport;

/**
 * Created by kelin on 14-12-2.
 */
@org.robobinding.annotation.PresentationModel
public class PresentationModelParent implements HasPresentationModelChangeSupport {

    protected JsonData mJsonData;

    protected PresentationModelChangeSupport changeSupport;

    public PresentationModelParent() {
        changeSupport = new PresentationModelChangeSupport(this);
    }

    @Override
    public PresentationModelChangeSupport getPresentationModelChangeSupport() {
        return changeSupport;
    }

    public void setJsonData(JsonData jsonData) {
        this.mJsonData = jsonData;
    }

    public JsonData getJsonData() {
        return mJsonData;
    }


}
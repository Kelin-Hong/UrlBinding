package com.kelin.urlbinding.demo;

import com.kelin.library.viewmodel.PresentationModelParent;

/**
 * Created by kelin on 15-3-13.
 */
@org.robobinding.annotation.PresentationModel
public class CategoryDetailEventPresentationModel extends PresentationModelParent {

    public void changeStid() {
        mJsonData.getJsonPrimary().update("stid", "stid has been changed!");
        changeSupport.firePropertyChange("stid");
    }
}

package com.kelin.urlbinding.demo;

import android.content.Intent;

import com.kelin.library.viewmodel.PresentationModelParent;
import com.kelin.urlbinding.TestActivity;

import org.robobinding.widget.adapterview.ItemClickEvent;

/**
 * Created by kelin on 15-3-13.
 */
@org.robobinding.annotation.PresentationModel
public class EventPresentationModel extends PresentationModelParent {
    public void categoryItemClick(ItemClickEvent event) {
//        mJsonData.updateAndChangeDB("visibility",false);
//        mJsonData.updateAndChangeDB("stid", "呵呵呵呵呵");
//        changeSupport.firePropertyChange("stid");
//        changeSupport.firePropertyChange("visibility");
        Intent intent = new Intent(mJsonData.getmFragment().getActivity(), TestActivity.class);
        mJsonData.getmFragment().getActivity().startActivity(intent);

    }

    public void deleteFirstItem() {
        mJsonData.getList("data_homepage").removeAndChangeDB(0);
        changeSupport.firePropertyChange("data_homepage");
    }

    public void changeFirstItem() {
        mJsonData.getList("data_homepage").getJsonListItems().get(0).updateAddChangeDB("name", "哈哈哈哈");
        changeSupport.firePropertyChange("data_homepage");
    }
}

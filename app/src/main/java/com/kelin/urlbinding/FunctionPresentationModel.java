package com.kelin.urlbinding;

import android.content.Intent;

import com.kelin.library.viewmodel.PresentationModelParent;

import org.robobinding.widget.adapterview.ItemClickEvent;


/**
 * Created by kelin on 15-2-12.
 */

@org.robobinding.annotation.PresentationModel
public class FunctionPresentationModel extends PresentationModelParent {

    public void onItemClick(ItemClickEvent event) {
//        mJsonData.updateAndChangeDB("visibility",false);
//        mJsonData.updateAndChangeDB("stid", "呵呵呵呵呵");
//        changeSupport.firePropertyChange("stid");
//        changeSupport.firePropertyChange("visibility");
//        mJsonData.getmFragment().getActivity().startActivity(intent);

    }

    public void changeFooterVisibility() {
//        mJsonData.getList("data_homepage").remove(0);
//        mJsonData.getList("data_homepage").remove(0);
//        mJsonData.getList("data_homepage").remove(0);
//        mJsonData.getList("data_homepage").remove(0);
//        mJsonData.getList("data_homepage").remove(0);
        mJsonData.getList("data_homepage").removeAndChangeDB(0);
        mJsonData.getList("data_homepage").removeAndChangeDB(0);

        mJsonData.getList("data_homepage").getJsonListItems().get(0).updateAddChangeDB("name", "哈哈哈哈");
        changeSupport.firePropertyChange("data_homepage");
    }


}

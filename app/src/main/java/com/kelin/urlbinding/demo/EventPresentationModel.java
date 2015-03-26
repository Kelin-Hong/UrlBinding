package com.kelin.urlbinding.demo;

import android.content.Intent;

import com.kelin.library.data.JsonListItem;
import com.kelin.library.viewmodel.PresentationModelParent;

import org.robobinding.widget.adapterview.ItemClickEvent;

/**
 * Created by kelin on 15-3-13.
 */
@org.robobinding.annotation.PresentationModel
public class EventPresentationModel extends PresentationModelParent {
    public final static String ARG_ITEM_URI = "item_uri";

    public void categoryItemClick(ItemClickEvent event) {
        int position = event.getPosition();
        JsonListItem jsonListItem = mJsonData.getList("data_homepage").getJsonListItems().get(position);
        Intent intent = new Intent(mJsonData.getmFragment().getActivity(), CategoryDetailActivity.class);
        intent.putExtra(ARG_ITEM_URI, jsonListItem.getItemUri().toString());
        mJsonData.getmFragment().getActivity().startActivity(intent);
    }

    public void deleteFirstItem() {
        mJsonData.getList("data_homepage").remove(0);
        changeSupport.firePropertyChange("data_homepage");
    }

    public void changeFirstItem() {
        mJsonData.getList("data_homepage").getJsonListItems().get(0).updateAddChangeDB("data_homepage_name", "哈哈哈哈");
        changeSupport.firePropertyChange("data_homepage");
    }
}

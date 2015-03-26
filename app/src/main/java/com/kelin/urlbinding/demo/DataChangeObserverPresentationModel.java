package com.kelin.urlbinding.demo;

import android.content.Intent;

import com.google.gson.Gson;
import com.kelin.library.data.JsonListItem;
import com.kelin.library.data.JsonPrimary;
import com.kelin.library.viewmodel.PresentationModelParent;

import org.robobinding.widget.adapterview.ItemClickEvent;

import java.util.HashMap;

/**
 * Created by kelin on 15-3-13.
 */
@org.robobinding.annotation.PresentationModel
public class DataChangeObserverPresentationModel extends PresentationModelParent {
    public final static String ARG_ITEM_JSON_STRING = "item_json_string";
    public final static String ARG_ITEM_JSON_DATA_ID = "item_json_data_id";

    public void categoryItemClick(ItemClickEvent event) {
        int position = event.getPosition();
        JsonListItem jsonListItem = mJsonData.getList("data_homepage").get(position);
        JsonPrimary jsonPrimary = mJsonData.getJsonPrimary();
        HashMap<String, Object> newHashMap = new HashMap<>(jsonListItem.getJsonFieldMap());
        newHashMap.put("stid", jsonPrimary.get("stid"));
        String jsonString = new Gson().toJson(newHashMap);
        Intent intent = new Intent(mJsonData.getmFragment().getActivity(), DataChangeObserverDetailActivity.class);
        intent.putExtra(ARG_ITEM_JSON_STRING, jsonString);
        intent.putExtra(ARG_ITEM_JSON_DATA_ID, jsonListItem.getId() + "," + jsonPrimary.getId());
        mJsonData.getmFragment().getActivity().startActivity(intent);
    }

    public void deleteFirstItem() {
        mJsonData.getList("data_homepage").remove(0);
        changeSupport.firePropertyChange("data_homepage");
    }

    public void changeFirstItem() {
        mJsonData.getList("data_homepage").getJsonListItems().get(0).update("data_homepage_name", "哈哈哈哈");
        changeSupport.firePropertyChange("data_homepage");
    }
}

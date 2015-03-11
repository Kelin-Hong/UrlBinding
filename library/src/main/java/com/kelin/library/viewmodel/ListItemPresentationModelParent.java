package com.kelin.library.viewmodel;

import com.kelin.library.utils.JsonData;
import com.kelin.library.utils.JsonListItem;

import org.robobinding.itempresentationmodel.ItemContext;
import org.robobinding.itempresentationmodel.ItemPresentationModel;

/**
 * Created by kelin on 14-12-2.
 */
public class ListItemPresentationModelParent implements ItemPresentationModel<JsonListItem> {

//    public Integer getIndex() {
//        return index;
//    }
//
//    private Integer index;
//
//    @Override
//    public void updateData(Integer index, ItemContext itemContext) {
//        this.index = index;
//    }

    public JsonListItem getJsonListItem() {
        return jsonListItem;
    }

    private JsonListItem jsonListItem;

    @Override
    public void updateData(JsonListItem jsonListItem, ItemContext itemContext) {
       this.jsonListItem=jsonListItem;
    }
}
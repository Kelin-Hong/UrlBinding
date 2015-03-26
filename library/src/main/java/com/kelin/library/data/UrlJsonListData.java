package com.kelin.library.data;

import android.content.Context;

import org.robobinding.presentationmodel.PresentationModelChangeSupport;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by kelin on 15-3-10.
 */
public class UrlJsonListData {
    private Context context;
    private String name;
    private int size;
    private PresentationModelChangeSupport changeSupport;
    private Set<String> allFieldName = new HashSet<>();
    private Set<String> allFieldWithName = new HashSet<>();
    private List<UrlJsonListItem> jsonListItems = new ArrayList<>();

    public UrlJsonListData(String name, Context context) {
        this.name = name;
        this.context = context;
    }

    public void setUrlJsonListItems(List<UrlJsonListItem> jsonListItems) {
        this.jsonListItems = jsonListItems;
    }

    public void setChangeSupport(PresentationModelChangeSupport changeSupport) {
        this.changeSupport = changeSupport;
        if (jsonListItems != null) {
            for (UrlJsonListItem jsonListItem : jsonListItems) {
                jsonListItem.setChangeSupport(changeSupport);
            }
        }
    }

    public String getName() {
        return name;
    }


    public UrlJsonListItem get(int index) {
        if (jsonListItems != null && !jsonListItems.isEmpty()) {
            return jsonListItems.get(index);
        }
        return null;
    }

    public int getSize() {
        return jsonListItems.size();
    }

    public List<UrlJsonListItem> getUrlJsonListItems() {
        return jsonListItems;
    }

    public Set<String> getAllFieldWithName() {
        return allFieldWithName;
    }


    public Set<String> getAllFieldName() {
        return allFieldName;
    }

    public void add(UrlJsonListItem item) {
        jsonListItems.add(item);
        if (item.getUrlJsonListData() == null) {
            item.setUrlJsonListData(this);
        }
    }


    public UrlJsonListItem remove(int index) {
        return jsonListItems.remove(index);
    }

    public boolean remove(UrlJsonListItem jsonListItem) {
        return jsonListItems.remove(jsonListItem);
    }

}

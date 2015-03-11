package com.kelin.library.utils;

import android.net.Uri;

import com.kelin.library.dao.DataProvider;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kelin on 15-3-5.
 */
public class UriConvertUtil {
    public static final String DATA_SCHEME = "content";
    public static final String STRING_EMPTY = "";
    public static List<String> sAllUriPaths = new ArrayList<>();

    public static List<Uri> getDataUri(Uri netUri) {
        List<Uri> relativeUri = new ArrayList<>();
        List<String> paths = netUri.getPathSegments();
        StringBuffer path = new StringBuffer();
        for (String pathItem : paths) {
            try {
                Integer.parseInt(pathItem);
                path.append("#/");
            } catch (NumberFormatException e) {
                path.append(pathItem + '/');
            }
        }
        for (String item : sAllUriPaths) {
            if (item.startsWith(path.toString())) {
                Uri dataUri = netUri.buildUpon().scheme(DATA_SCHEME).authority(DataProvider.AUTHORITY).path(item).query(STRING_EMPTY).build();
                relativeUri.add(dataUri);
            }
        }
        return relativeUri;
    }

    public static Uri getDataUri(Uri netUri, String tableName) {
        Uri dataUri = null;
        dataUri = netUri.buildUpon().scheme(DATA_SCHEME).authority(DataProvider.AUTHORITY).appendEncodedPath(tableName).query(STRING_EMPTY).build();
        return dataUri;
    }

}

package com.kelin.library.utils;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;

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

    public static List<Uri> getDataUri(Context context, Uri netUri) {
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

        for (Uri uri : relativeUri) {
            if (!uri.getLastPathSegment().contains("_")) {
                Cursor cursor = context.getContentResolver().query(uri, null, DataProvider.COLUMN_URI_MD5 + "= ?", new String[]{UtilMethod.getMD5Str(netUri.toString())}, null);
                cursor.moveToFirst();
                int id = cursor.getInt(cursor.getColumnIndex(BaseColumns._ID));
                Uri uriWithId = uri.buildUpon().appendEncodedPath(String.valueOf(id)).build();
                cursor.close();
                relativeUri.remove(uri);
                relativeUri.add(0, uriWithId);
                break;
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

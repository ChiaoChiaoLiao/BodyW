package com.mccc.bodyw;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

public class AttributeContentProvider extends ContentProvider {

    public static final Uri CONTENT_URI = Uri.parse("content://com.example.bodyw.provider");
    private static final String PATH_MAIN = "main";
    public static final Uri RECORD_URI = Uri.withAppendedPath(CONTENT_URI, PATH_MAIN);

    MainDatabaseHelper mOpenHelper;

    @Override
    public boolean onCreate() {
        mOpenHelper = new MainDatabaseHelper(getContext());
        return true;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        mOpenHelper.getWritableDatabase().insert(MainDatabaseHelper.TABLE, null, values);
        return null;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        return mOpenHelper.getReadableDatabase().query(
                MainDatabaseHelper.TABLE,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder);
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        // TODO: Implement this to handle requests to update one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Implement this to handle requests to delete one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public String getType(Uri uri) {
        // TODO: Implement this to handle requests for the MIME type of the data
        // at the given URI.
        throw new UnsupportedOperationException("Not yet implemented");
    }

}

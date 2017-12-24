package dev.learn.movies.app.popular_movies.data.providers;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.List;

import dev.learn.movies.app.popular_movies.data.DataContract;
import dev.learn.movies.app.popular_movies.data.DataContract.MediaEntry;
import dev.learn.movies.app.popular_movies.data.DbHelper;

/**
 * BookmarksProvider - Bookmarks Content Provider
 */

public class BookmarksProvider extends ContentProvider {

    private static final int BOOKMARKS = 200;

    private static final int BOOKMARKS_WITH_ID = 201;

    private static final UriMatcher sUriMatcher = builderUriMatcher();

    private static final String PARAM = " ? ";

    private static final String EQUAL_TO = " = ";

    private static final String AND = " AND ";

    private Context mContext;
    private DbHelper mDbHelper;

    private static UriMatcher builderUriMatcher() {
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(DataContract.BOOKMARKS_AUTHORITY, DataContract.BOOKMARKS_PATH, BOOKMARKS);
        uriMatcher.addURI(DataContract.BOOKMARKS_AUTHORITY, DataContract.BOOKMARKS_PATH + "/*/#/", BOOKMARKS_WITH_ID);
        return uriMatcher;
    }

    @Override
    public boolean onCreate() {
        mContext = getContext();
        mDbHelper = new DbHelper(mContext);
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        final SQLiteDatabase db = mDbHelper.getReadableDatabase();
        int match = sUriMatcher.match(uri);
        Cursor cursor;
        switch (match) {
            case BOOKMARKS:
                cursor = db.query(MediaEntry.TABLE_NAME, projection, MediaEntry.COLUMN_IS_BOOKMARKED + " = 1 ", null, null, null, sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        mContext.getContentResolver().notifyChange(uri, null);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);
        long res = -1;
        switch (match) {
            case BOOKMARKS_WITH_ID:
                List<String> pathSegments = uri.getPathSegments();
                if (pathSegments != null && pathSegments.size() > 2) {
                    String type = pathSegments.get(1);
                    String id = pathSegments.get(2);

                    if (values != null) {
                        values.put(MediaEntry.COLUMN_IS_BOOKMARKED, 1);
                        values.put(MediaEntry.COLUMN_MEDIA_TYPE, type);

                        int numRows = db.update(MediaEntry.TABLE_NAME, values,
                                MediaEntry.COLUMN_MEDIA_TYPE + " = ? AND " +
                                        MediaEntry.COLUMN_MEDIA_ID + " = ? ", new String[]{type, id});
                        if (numRows <= 0) {
                            res = db.insert(MediaEntry.TABLE_NAME, null, values);
                            if (res == -1) {
                                throw new SQLiteException("Failed to insert record: " + uri);
                            }
                        }
                    }
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        mContext.getContentResolver().notifyChange(uri, null);
        return ContentUris.withAppendedId(uri, res);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKMARKS_WITH_ID:
                List<String> pathSegments = uri.getPathSegments();
                if (pathSegments != null && pathSegments.size() > 1) {
                    String type = pathSegments.get(1);
                    String id = pathSegments.get(2);
                    db.execSQL("UPDATE " + MediaEntry.TABLE_NAME + " SET " + MediaEntry.COLUMN_IS_BOOKMARKED + " = 0 WHERE " +
                            MediaEntry.COLUMN_MEDIA_TYPE + EQUAL_TO + PARAM + AND + MediaEntry.COLUMN_MEDIA_ID + EQUAL_TO + PARAM, new String[]{type, id});
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri : " + uri);
        }
        mContext.getContentResolver().notifyChange(uri, null);
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        throw new UnsupportedOperationException("Unknown action");
    }
}

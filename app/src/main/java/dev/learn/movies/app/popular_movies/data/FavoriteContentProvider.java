package dev.learn.movies.app.popular_movies.data;

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

import static dev.learn.movies.app.popular_movies.data.DataContract.FavoriteEntry;

/**
 * Created by sudharti on 11/4/17.
 */

public class FavoriteContentProvider extends ContentProvider {

    private static final int FAVORITES = 100;
    private static final int FAVORITE_WITH_MOVIE_ID = 101;

    private static final UriMatcher sUriMatcher = builderUriMatcher();

    private Context mContext;
    private DbHelper mDbHelper;

    private static UriMatcher builderUriMatcher() {
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(DataContract.AUTHORITY, DataContract.FAVORITES_PATH, FAVORITES);
        uriMatcher.addURI(DataContract.AUTHORITY, DataContract.FAVORITES_PATH + "/#", FAVORITE_WITH_MOVIE_ID);
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
            case FAVORITES:
                cursor = db.query(FavoriteEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case FAVORITE_WITH_MOVIE_ID:
                List<String> pathSegments = uri.getPathSegments();
                String id = "0";
                if (pathSegments != null && pathSegments.size() > 1) {
                    id = uri.getPathSegments().get(1);
                }
                cursor = db.query(FavoriteEntry.TABLE_NAME, projection, FavoriteEntry.COLUMN_MOVIE_ID + " = ? ", new String[]{id}, null, null, sortOrder);
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
        long res;
        switch (match) {
            case FAVORITES:
                res = db.insert(FavoriteEntry.TABLE_NAME, null, values);
                if (res == -1) {
                    throw new SQLiteException("Failed to insert record");
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
        int match = sUriMatcher.match(uri), res = 0;
        switch (match) {
            case FAVORITE_WITH_MOVIE_ID:
                List<String> pathSegments = uri.getPathSegments();
                if (pathSegments != null && pathSegments.size() > 1) {
                    String id = uri.getPathSegments().get(1);
                    res = db.delete(FavoriteEntry.TABLE_NAME, FavoriteEntry.COLUMN_MOVIE_ID + " = ? ", new String[]{id});
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri : " + uri);
        }
        mContext.getContentResolver().notifyChange(uri, null);
        return res;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }
}

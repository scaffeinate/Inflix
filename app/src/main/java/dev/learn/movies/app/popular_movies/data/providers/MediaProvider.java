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
import dev.learn.movies.app.popular_movies.data.DataContract.MovieEntry;
import dev.learn.movies.app.popular_movies.data.DbHelper;

/**
 * Created by sudharti on 11/30/17.
 */

public class MoviesProvider extends ContentProvider {

    private static final int MOVIE = 300;

    private static final UriMatcher sUriMatcher = builderUriMatcher();

    private Context mContext;
    private DbHelper mDbHelper;

    private static UriMatcher builderUriMatcher() {
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(DataContract.AUTHORITY, DataContract.MOVIE_PATH + "/#", MOVIE);
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
        Cursor cursor = null;
        switch (match) {
            case MOVIE:
                List<String> pathSegments = uri.getPathSegments();
                if (pathSegments != null && pathSegments.size() > 1) {
                    String id = uri.getPathSegments().get(1);
                    cursor = db.query(MovieEntry.TABLE_NAME, projection, MovieEntry.COLUMN_MEDIA_ID + " = ? ", new String[]{id}, null, null, sortOrder);
                }
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
            case MOVIE:
                res = db.insert(MovieEntry.TABLE_NAME, null, values);
                if (res == -1) {
                    throw new SQLiteException("Failed to insert record: " + uri);
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
            case MOVIE:
                List<String> pathSegments = uri.getPathSegments();
                if (pathSegments != null && pathSegments.size() > 1) {
                    String id = uri.getPathSegments().get(1);
                    res = db.delete(MovieEntry.TABLE_NAME,
                            MovieEntry.COLUMN_MEDIA_ID + " = ? ", new String[]{id});
                    if (res == 0) {
                        throw new SQLiteException("Failed to delete record: " + uri);
                    }
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
        throw new UnsupportedOperationException("Unknown action");
    }
}

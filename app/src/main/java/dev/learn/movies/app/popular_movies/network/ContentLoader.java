package dev.learn.movies.app.popular_movies.network;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;

/**
 * Created by sudharti on 11/4/17.
 */

public class ContentLoader implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = ContentLoader.class.getSimpleName();
    public static final String URI_EXTRA = "url_extra";

    private final Context mContext;
    private final ContentLoaderCallback mCallback;

    public ContentLoader(Context context, ContentLoaderCallback callback) {
        this.mContext = context;
        this.mCallback = callback;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri uri = (args == null) ? null : (Uri) args.getParcelable(URI_EXTRA);
        Log.i(TAG, "Fetching content from uri: " + uri);
        if(uri != null) {
            return new CursorLoader(mContext, uri, null, null, null, null) {
                @Override
                protected void onStartLoading() {
                    super.onStartLoading();
                    mCallback.onContentLoadStarted();
                }
            };
        }

        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (mCallback != null) {
            mCallback.onContentLoadFinished(loader, data);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}

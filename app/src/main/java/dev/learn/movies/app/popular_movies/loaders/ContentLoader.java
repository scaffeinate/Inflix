package dev.learn.movies.app.popular_movies.loaders;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;

/**
 * ContentLoader - Generic Loader class which implements LoaderCallbacks
 * <p>Creates a new CursorLoader when onCreateLoader() is called. Fetches content from a ContentProvider.</p>
 */
public class ContentLoader implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final String URI_EXTRA = "url_extra";
    private static final String TAG = ContentLoader.class.getSimpleName();
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
        if (uri != null) {
            return new CursorLoader(mContext, uri, null, null, null, null);
        }

        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (mCallback != null) {
            mCallback.onLoadFinished(loader, data);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // Do nothing
    }

    /**
     * ContentLoaderCallback - Callbacks to communicate back to the Activity/Fragment
     */
    public interface ContentLoaderCallback {
        void onLoadFinished(Loader loader, Cursor cursor);
    }
}

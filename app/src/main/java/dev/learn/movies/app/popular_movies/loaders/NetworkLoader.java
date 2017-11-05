package dev.learn.movies.app.popular_movies.loaders;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.util.Log;

import java.io.IOException;
import java.net.URL;

import dev.learn.movies.app.popular_movies.util.HTTPHelper;

/**
 * NetworkLoader - Generic AsyncTask to fetch results from URL. Returns the response string
 * <p>
 * AsyncTask as an inner class has an implicit reference to the outer Activity which causes memory leaks.
 * So declaring it as a standalone class and use callbacks to update UI.</p>
 */

public class NetworkLoader implements LoaderManager.LoaderCallbacks<String> {

    private static final String TAG = NetworkLoader.class.getSimpleName();

    public static final String URL_EXTRA = "url_extra";
    public static final String SHOULD_CALL_LOAD_STARTED_EXTRA = "should_call_load_started_extra";

    private final Context mContext;
    private final NetworkLoaderCallback mCallback;
    private boolean mShouldCallOnLoadStarted = true;

    public NetworkLoader(Context context, NetworkLoaderCallback callback) {
        this.mContext = context;
        this.mCallback = callback;
    }

    @Override
    public Loader<String> onCreateLoader(int id, final Bundle args) {
        return new AsyncTaskLoader<String>(mContext) {

            private String mResponse;

            @Override
            protected void onStartLoading() {
                if (args == null) return;
                if (mResponse == null) {
                    if (mShouldCallOnLoadStarted) {
                        mCallback.onNetworkStartLoading();
                    }
                    forceLoad();
                } else {
                    deliverResult(mResponse);
                }
            }

            @Nullable
            @Override
            public String loadInBackground() {
                URL url = (java.net.URL) args.getSerializable(URL_EXTRA);
                mShouldCallOnLoadStarted = args.getBoolean(SHOULD_CALL_LOAD_STARTED_EXTRA, true);
                if (url != null) {
                    try {
                        Log.i(TAG, "Requesting Data From: " + url.toString());
                        return HTTPHelper.getHTTPResponse(url);
                    } catch (IOException e) {
                        Log.e(TAG, e.getMessage());
                    }
                }
                return null;
            }

            @Override
            public void deliverResult(@Nullable String data) {
                super.deliverResult(data);
                mResponse = data;
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<String> loader, String data) {
        mCallback.onNetworkLoadFinished(loader, data);
    }

    @Override
    public void onLoaderReset(Loader<String> loader) {

    }
}

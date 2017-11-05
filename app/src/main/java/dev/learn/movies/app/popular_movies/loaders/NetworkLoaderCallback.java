package dev.learn.movies.app.popular_movies.loaders;

import android.support.v4.content.Loader;

/**
 * NetworkLoaderCallback - Callback to update the UI from the NetworkLoader
 */

public interface NetworkLoaderCallback {
    void onStartLoading();

    void onLoadFinished(Loader loader, String s);
}

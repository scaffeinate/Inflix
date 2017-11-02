package dev.learn.movies.app.popular_movies.network;

/**
 * NetworkLoaderCallback - Callback to update the UI from the NetworkLoader
 */

public interface NetworkLoaderCallback {
    void onLoadStarted();

    void onLoadFinished(String s);
}

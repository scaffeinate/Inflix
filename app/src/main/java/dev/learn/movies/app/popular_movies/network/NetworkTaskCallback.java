package dev.learn.movies.app.popular_movies.network;

/**
 * NetworkTaskCallback - Callback to update the UI from the NetworkTask
 */

public interface NetworkTaskCallback {
    void onPreExecute();

    void onPostExecute(String s);
}

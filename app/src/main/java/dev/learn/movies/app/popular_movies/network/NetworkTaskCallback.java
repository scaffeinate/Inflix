package dev.learn.movies.app.popular_movies.network;

/**
 * NetworkTaskCallback
 */

public interface NetworkTaskCallback {
    void onPreExecute();

    void onPostExecute(String s);
}

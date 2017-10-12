package dev.learn.movies.app.popularmovies_udacity;

/**
 * Created by sudhar on 10/11/17.
 */

public interface NetworkTaskCallback {
    void onPreExecute();

    void onPostExecute(String s);
}

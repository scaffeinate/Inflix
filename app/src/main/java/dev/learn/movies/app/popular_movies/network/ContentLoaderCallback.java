package dev.learn.movies.app.popular_movies.network;

import android.database.Cursor;
import android.support.v4.content.Loader;

/**
 * Created by sudharti on 11/4/17.
 */

public interface ContentLoaderCallback {
    void onStartLoading();

    void onLoadFinished(Loader loader, Cursor cursor);
}

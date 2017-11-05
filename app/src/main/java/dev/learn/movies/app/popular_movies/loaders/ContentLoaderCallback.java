package dev.learn.movies.app.popular_movies.loaders;

import android.database.Cursor;
import android.support.v4.content.Loader;

/**
 * Created by sudharti on 11/4/17.
 */

public interface ContentLoaderCallback {
    void onContentStartLoading();

    void onContentLoadFinished(Loader loader, Cursor cursor);
}

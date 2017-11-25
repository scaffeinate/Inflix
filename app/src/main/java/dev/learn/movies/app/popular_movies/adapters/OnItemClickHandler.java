package dev.learn.movies.app.popular_movies.adapters;

import android.view.View;
import android.view.ViewGroup;

/**
 * OnItemClickHandler interface to handle listItem clicks
 */
public interface OnItemClickHandler {
    void onItemClicked(ViewGroup parent, View view, int position);
}

package dev.learn.movies.app.popular_movies.activities;

import android.net.Uri;

/**
 * DetailActivityCallbacks - DetailActivity Callbacks
 */
public interface DetailActivityCallbacks {


    void updateBackdrop(Uri imageUri);

    void updateFavBtn(boolean favored);

    void showFavToast(boolean favored);

    void showFavBtn();

    void hideFavBtn();

    void scrollToTop();
}
package dev.learn.movies.app.popular_movies.activities;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;

import com.google.gson.Gson;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import dev.learn.movies.app.popular_movies.R;
import dev.learn.movies.app.popular_movies.adapters.MovieReviewsAdapter;
import dev.learn.movies.app.popular_movies.common.movies.MovieReview;
import dev.learn.movies.app.popular_movies.common.movies.MovieReviewsResult;
import dev.learn.movies.app.popular_movies.databinding.ActivityReviewsBinding;
import dev.learn.movies.app.popular_movies.loaders.NetworkLoader;
import dev.learn.movies.app.popular_movies.util.HTTPHelper;

import static dev.learn.movies.app.popular_movies.util.AppConstants.MOVIE_ID;
import static dev.learn.movies.app.popular_movies.util.AppConstants.MOVIE_NAME;
import static dev.learn.movies.app.popular_movies.util.AppConstants.MOVIE_REVIEWS_LOADER_ID;

/**
 * Created by sudharti on 11/14/17.
 */

public class MovieReviewsActivity extends AppCompatActivity implements NetworkLoader.NetworkLoaderCallback {

    private static final String MOVIE_REVIEWS = "movie_reviews";

    private long mMovieId = 0L;
    private String mMovieName;

    private final Gson gson = new Gson();
    private NetworkLoader mNetworkLoader;

    private List<MovieReview> mReviewsList;
    private MovieReviewsAdapter mMovieReviewsAdapter;

    private ActivityReviewsBinding mBinding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_reviews);
        mNetworkLoader = new NetworkLoader(this, this);

        setSupportActionBar(mBinding.toolbar.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        mMovieReviewsAdapter = new MovieReviewsAdapter();
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mBinding.layoutUserReviews.rvUserReviews.setLayoutManager(layoutManager);
        mBinding.layoutUserReviews.rvUserReviews.setHasFixedSize(true);
        mBinding.layoutUserReviews.rvUserReviews.setAdapter(mMovieReviewsAdapter);

        if (savedInstanceState == null) {
            mReviewsList = new ArrayList<>();
            mMovieId = getIntent().getLongExtra(MOVIE_ID, 0);
            mMovieName = getIntent().getStringExtra(MOVIE_NAME);

            if (mMovieId != 0) {
                loadMovieReviewsFromNetwork(String.valueOf(mMovieId));
            }
        } else {
            mMovieName = savedInstanceState.getString(MOVIE_NAME);
            mReviewsList = savedInstanceState.getParcelableArrayList(MOVIE_REVIEWS);
            updateReviewsUI();
        }

        if (!TextUtils.isEmpty(mMovieName)) {
            mBinding.toolbar.tvToolbarTitle.setText(mMovieName);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(MOVIE_ID, mMovieId);
        outState.putString(MOVIE_NAME, mMovieName);
        outState.putParcelableArrayList(MOVIE_REVIEWS, (ArrayList<? extends Parcelable>) mReviewsList);
    }

    @Override
    public void onLoadFinished(Loader loader, String s) {
        switch (loader.getId()) {
            case MOVIE_REVIEWS_LOADER_ID:
                // Handle reviews response
                MovieReviewsResult movieReviewsResult = (s == null) ? null : gson.fromJson(s, MovieReviewsResult.class);
                mReviewsList = (movieReviewsResult == null) ? null : movieReviewsResult.getResults();
                updateReviewsUI();
                break;
        }
    }

    /**
     * Loads movie reviews from Network
     */
    private void loadMovieReviewsFromNetwork(String movieId) {
        if (HTTPHelper.isNetworkEnabled(this)) {
            URL url = HTTPHelper.buildMovieReviewsURL(String.valueOf(movieId));
            Bundle args = new Bundle();
            args.putSerializable(NetworkLoader.URL_EXTRA, url);
            getSupportLoaderManager().restartLoader(MOVIE_REVIEWS_LOADER_ID, args, mNetworkLoader);
        } else {
            showReviewsErrorMessage();
        }
    }

    /**
     * Updates Reviews list
     */
    private void updateReviewsUI() {
        if (mReviewsList == null || mReviewsList.isEmpty()) {
            showReviewsErrorMessage();
            return;
        }

        mMovieReviewsAdapter.setReviewList(mReviewsList);
        showReviews();
    }

    /**
     * Shows MovieDetailLayout, Hides ProgressBar and ErrorMessage
     */
    private void showReviews() {
        mBinding.layoutUserReviews.rvUserReviews.setVisibility(View.VISIBLE);
        mBinding.layoutUserReviews.pbUserReviews.setVisibility(View.INVISIBLE);
        mBinding.layoutUserReviews.tvReviewsErrorMessageDisplay.setVisibility(View.INVISIBLE);
    }

    /**
     * Shows ErrorMessage, Hides ProgressBar and MovieDetailLayout
     */
    private void showReviewsErrorMessage() {
        mBinding.layoutUserReviews.tvReviewsErrorMessageDisplay.setVisibility(View.VISIBLE);
        mBinding.layoutUserReviews.pbUserReviews.setVisibility(View.INVISIBLE);
        mBinding.layoutUserReviews.rvUserReviews.setVisibility(View.INVISIBLE);
    }
}

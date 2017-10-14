package dev.learn.movies.app.popular_movies;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.List;

import dev.learn.movies.app.popular_movies.common.Genre;
import dev.learn.movies.app.popular_movies.common.MovieDetail;
import dev.learn.movies.app.popular_movies.network.HTTPHelper;
import dev.learn.movies.app.popular_movies.network.NetworkTask;
import dev.learn.movies.app.popular_movies.network.NetworkTaskCallback;
import dev.learn.movies.app.popular_movies.util.DisplayUtils;

/**
 * DetailActivity
 */
public class DetailActivity extends AppCompatActivity implements NetworkTaskCallback {

    public static final String MOVIE_ID = "movie_id";
    public static final String MOVIE_NAME = "movie_name";
    private final Gson gson = new Gson();
    private String movieName = "";
    private long movieId = 0L;
    private LinearLayout mMovieDetailLayout;
    private FrameLayout mBackdropLayout;
    private FrameLayout mPosterLayout;
    private ProgressBar mProgressBar;
    private TextView mErrorMessageDisplay;
    private ImageView mBackdropImageView;
    private ImageView mPosterImageView;
    private TextView mMovieTitleTextView;
    private TextView mMovieRuntimeTextView;
    private TextView mMovieGenreTextView;
    private TextView mMovieRatingTextView;
    private TextView mMovieTaglineTextView;
    private TextView mMoviePlotTextView;
    private RatingBar mMovieRatingBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        TextView mToolbarTitle = (TextView) findViewById(R.id.tv_toolbar_title);
        mMovieDetailLayout = (LinearLayout) findViewById(R.id.layout_movie_detail);
        mBackdropLayout = (FrameLayout) findViewById(R.id.layout_backdrop);
        mPosterLayout = (FrameLayout) findViewById(R.id.layout_poster);
        mProgressBar = (ProgressBar) findViewById(R.id.pb_loading_indicator);
        mErrorMessageDisplay = (TextView) findViewById(R.id.tv_error_message_display);

        mBackdropImageView = (ImageView) findViewById(R.id.image_view_backdrop);
        mPosterImageView = (ImageView) findViewById(R.id.image_view_poster);
        mMovieTitleTextView = (TextView) findViewById(R.id.tv_movie_title);
        mMovieRuntimeTextView = (TextView) findViewById(R.id.tv_movie_runtime);
        mMovieGenreTextView = (TextView) findViewById(R.id.tv_movie_genre);
        mMovieRatingTextView = (TextView) findViewById(R.id.tv_movie_rating);
        mMovieTaglineTextView = (TextView) findViewById(R.id.tv_movie_tagline);
        mMoviePlotTextView = (TextView) findViewById(R.id.tv_movie_plot);
        mMovieRatingBar = (RatingBar) findViewById(R.id.rb_movie_rating);

        setSupportActionBar(mToolbar);
        ActionBar mActionBar = getSupportActionBar();

        if (mActionBar != null) {
            mActionBar.setDisplayHomeAsUpEnabled(true);
            mActionBar.setDisplayShowHomeEnabled(true);
        }

        if (savedInstanceState == null) {
            Intent intent = getIntent();
            if (intent != null && intent.getExtras() != null) {
                Bundle bundle = intent.getExtras();
                movieId = bundle.getLong(MOVIE_ID, 0L);
                movieName = bundle.getString(MOVIE_NAME, "");
            }
        } else {
            movieId = (savedInstanceState.containsKey(MOVIE_ID)) ? savedInstanceState.getLong(MOVIE_ID) : 0L;
            movieName = (savedInstanceState.containsKey(MOVIE_NAME) ? savedInstanceState.getString(MOVIE_NAME) : "");
        }

        mToolbarTitle.setText(movieName);
        adjustImageSize();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (movieId != 0) {
            fetchMovieDetails();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(MOVIE_ID, movieId);
        outState.putString(MOVIE_NAME, movieName);
    }

    @Override
    public void onPreExecute() {
        showProgressBar();
    }

    @Override
    public void onPostExecute(String s) {
        MovieDetail movieDetail = (s == null) ? null : gson.fromJson(s, MovieDetail.class);
        if (movieDetail == null) {
            showErrorMessage();
        } else {
            loadMovieDetails(movieDetail);
            showMovieDetails();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void fetchMovieDetails() {
        if (HTTPHelper.isNetworkEnabled(this)) {
            new NetworkTask(this).execute(HTTPHelper.buildMovieDetailsURL(String.valueOf(movieId)));
        } else {
            DisplayUtils.setNoNetworkConnectionMessage(this, mErrorMessageDisplay);
            showErrorMessage();
        }
    }

    private void loadMovieDetails(MovieDetail movieDetail) {
        int year = DisplayUtils.getYear(movieDetail.getReleaseDate());
        double voteAverage = movieDetail.getVoteAverage();
        String backdropURL = movieDetail.getBackdropPath();
        String posterURL = movieDetail.getPosterPath();
        String title = movieDetail.getTitle();
        String runningTime = movieDetail.getRuntime() + " min";
        String rating = voteAverage + "/10 (" + movieDetail.getVoteCount() + ")";
        String tagline = movieDetail.getTagline();
        String moviePlot = movieDetail.getOverview();
        List<Genre> genres = movieDetail.getGenres();

        if (backdropURL != null) {
            Uri backdropUri = HTTPHelper.buildImageResourceUri(backdropURL, HTTPHelper.IMAGE_SIZE_XLARGE);
            DisplayUtils.fitImageInto(mBackdropImageView, backdropUri, null);
        }

        if (posterURL != null) {
            Uri posterUri = HTTPHelper.buildImageResourceUri(posterURL, HTTPHelper.IMAGE_SIZE_SMALL);
            DisplayUtils.fitImageInto(mPosterImageView, posterUri, null);
        }

        mMovieTitleTextView.setText(DisplayUtils.formatTitle(title, year));

        mMovieRuntimeTextView.setText(runningTime);

        mMovieGenreTextView.setText(DisplayUtils.formatGenres(genres));

        mMovieRatingBar.setRating((float) voteAverage);

        mMovieRatingTextView.setText(rating);

        mMovieTaglineTextView.setText(DisplayUtils.formatTagline(tagline));

        if (moviePlot != null && !moviePlot.isEmpty()) {
            mMoviePlotTextView.setText(moviePlot);
        }
    }

    private void showProgressBar() {
        mProgressBar.setVisibility(View.VISIBLE);
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
        mMovieDetailLayout.setVisibility(View.INVISIBLE);
    }

    private void showMovieDetails() {
        mMovieDetailLayout.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.INVISIBLE);
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
    }

    private void showErrorMessage() {
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.INVISIBLE);
        mMovieDetailLayout.setVisibility(View.INVISIBLE);
    }

    private void adjustImageSize() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int screenHeight = displayMetrics.heightPixels;
        int screenWidth = displayMetrics.widthPixels;

        int max = Math.max(screenHeight, screenWidth);
        int min = Math.min(screenHeight, screenWidth);

        mBackdropLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) (max / 2.75)));
        mPosterLayout.setLayoutParams(new RelativeLayout.LayoutParams((min / 3), (max / 4)));
    }
}

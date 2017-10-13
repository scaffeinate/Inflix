package dev.learn.movies.app.popularmovies_udacity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.List;

import dev.learn.movies.app.popularmovies_udacity.common.Genre;
import dev.learn.movies.app.popularmovies_udacity.common.MovieDetail;
import dev.learn.movies.app.popularmovies_udacity.network.HTTPHelper;
import dev.learn.movies.app.popularmovies_udacity.network.NetworkTask;
import dev.learn.movies.app.popularmovies_udacity.network.NetworkTaskCallback;
import dev.learn.movies.app.popularmovies_udacity.util.DisplayUtils;

/**
 * Created by sudharti on 10/11/17.
 */
public class DetailActivity extends AppCompatActivity implements NetworkTaskCallback {

    public static final String MOVIE_ID = "movie_id";
    public static final String MOVIE_NAME = "movie_name";

    private String movieName = null;
    private long movieId = 0L;

    private LinearLayout mMovieDetailLayout;
    private ProgressBar mProgressBar;
    private TextView mErrorMessageDisplay;

    private ImageView mBackdropImageView;
    private ImageView mPosterImageView;
    private TextView mMovieTitleTextView;
    private TextView mMovieRuntimeTextView;
    private TextView mMovieGenreTextView;
    private TextView mMovieRatingTextView;
    private TextView mMoviePlotTextView;

    private RatingBar mMovieRatingBar;

    private final Gson gson = new Gson();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        TextView mToolbarTitle = (TextView) findViewById(R.id.tv_toolbar_title);
        mMovieDetailLayout = (LinearLayout) findViewById(R.id.layout_movie_detail);
        mProgressBar = (ProgressBar) findViewById(R.id.pb_loading_indicator);
        mErrorMessageDisplay = (TextView) findViewById(R.id.tv_error_message_display);

        mBackdropImageView = (ImageView) findViewById(R.id.imageview_backdrop);
        mPosterImageView = (ImageView) findViewById(R.id.imageview_poster);
        mMovieTitleTextView = (TextView) findViewById(R.id.tv_movie_title);
        mMovieRuntimeTextView = (TextView) findViewById(R.id.tv_movie_runtime);
        mMovieGenreTextView = (TextView) findViewById(R.id.tv_movie_genre);
        mMovieRatingTextView = (TextView) findViewById(R.id.tv_movie_rating);
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
                movieName = bundle.getString(MOVIE_NAME, "Detail");
            }
        } else {
            movieId = (savedInstanceState.containsKey(MOVIE_ID)) ? savedInstanceState.getLong(MOVIE_ID) : 0L;
            movieName = (savedInstanceState.containsKey(MOVIE_NAME) ? savedInstanceState.getString(MOVIE_NAME) : "Detail");
        }

        mToolbarTitle.setText(movieName);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (movieId != 0) {
            new NetworkTask(this).execute(HTTPHelper.buildMovieDetailsURL(String.valueOf(movieId)));
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

    private void loadMovieDetails(MovieDetail movieDetail) {
        String backdropURL = movieDetail.getBackdropPath();
        String posterURL = movieDetail.getPosterPath();

        if (backdropURL != null) {
            Uri backdropUri = HTTPHelper.buildImageResourceUri(backdropURL, HTTPHelper.IMAGE_SIZE_XLARGE);
            DisplayUtils.fitImageInto(mBackdropImageView, backdropUri, null);
        }

        if (posterURL != null) {
            Uri posterUri = HTTPHelper.buildImageResourceUri(posterURL, HTTPHelper.IMAGE_SIZE_SMALL);
            DisplayUtils.fitImageInto(mPosterImageView, posterUri, null);
        }

        StringBuilder titleBuilder = new StringBuilder();
        if (movieDetail.getTitle() != null) {
            titleBuilder.append(movieDetail.getTitle());
        }
        int year = DisplayUtils.getYear(movieDetail.getReleaseDate());
        if (year != -1) {
            titleBuilder.append(" ").append("(").append(String.valueOf(year)).append(")");
        }

        mMovieTitleTextView.setText(titleBuilder.toString());
        String runningTime = movieDetail.getRuntime() + " min";
        mMovieRuntimeTextView.setText(runningTime);

        List<Genre> genres = movieDetail.getGenres();
        StringBuilder genreBuilder = new StringBuilder();
        if (genres != null && !genres.isEmpty()) {
            for (int i = 0; i < genres.size(); i++) {
                genreBuilder.append(genres.get(i).getName()).append((i < genres.size() - 1) ? " | " : "");
            }
        }
        mMovieGenreTextView.setText(genreBuilder.toString());

        double rating = movieDetail.getVoteAverage();
        mMovieRatingBar.setRating((float) rating);

        String ratingText = rating + "/10";
        mMovieRatingTextView.setText(ratingText);

        if (movieDetail.getOverview() != null) {
            mMoviePlotTextView.setText(movieDetail.getOverview());
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
}

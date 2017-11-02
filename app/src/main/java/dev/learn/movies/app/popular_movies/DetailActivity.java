package dev.learn.movies.app.popular_movies;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuInflater;
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

import java.net.URL;
import java.util.List;

import dev.learn.movies.app.popular_movies.common.Genre;
import dev.learn.movies.app.popular_movies.common.MovieDetail;
import dev.learn.movies.app.popular_movies.network.HTTPHelper;
import dev.learn.movies.app.popular_movies.network.NetworkLoader;
import dev.learn.movies.app.popular_movies.network.NetworkLoaderCallback;
import dev.learn.movies.app.popular_movies.util.DisplayUtils;

/**
 * DetailActivity - To show the movie details
 */
public class DetailActivity extends AppCompatActivity implements NetworkLoaderCallback {

    public static final String MOVIE_ID = "movie_id";
    public static final String MOVIE_NAME = "movie_name";
    private static final int NETWORK_LOADER_ID = 532;
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
    private FloatingActionButton mFavoriteButton;
    private boolean favored = false;
    private NetworkLoader mNetworkLoader;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        mNetworkLoader = new NetworkLoader(this, this);

        Toolbar mToolbar = findViewById(R.id.toolbar);
        TextView mToolbarTitle = findViewById(R.id.tv_toolbar_title);
        mMovieDetailLayout = findViewById(R.id.layout_movie_detail);
        mBackdropLayout = findViewById(R.id.layout_backdrop);
        mPosterLayout = findViewById(R.id.layout_poster);
        mProgressBar = findViewById(R.id.pb_loading_indicator);
        mErrorMessageDisplay = findViewById(R.id.tv_error_message_display);

        mBackdropImageView = findViewById(R.id.image_view_backdrop);
        mPosterImageView = findViewById(R.id.image_view_poster);
        mMovieTitleTextView = findViewById(R.id.tv_movie_title);
        mMovieRuntimeTextView = findViewById(R.id.tv_movie_runtime);
        mMovieGenreTextView = findViewById(R.id.tv_movie_genre);
        mMovieRatingTextView = findViewById(R.id.tv_movie_rating);
        mMovieTaglineTextView = findViewById(R.id.tv_movie_tagline);
        mMoviePlotTextView = findViewById(R.id.tv_movie_plot);
        mMovieRatingBar = findViewById(R.id.rb_movie_rating);
        mFavoriteButton = findViewById(R.id.btn_fav);

        mFavoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (favored) {
                    mFavoriteButton.setImageResource(R.drawable.ic_heart_outline_white_24dp);
                } else {
                    mFavoriteButton.setImageResource(R.drawable.ic_heart_white_24dp);
                }
                favored = !favored;
            }
        });

        setSupportActionBar(mToolbar);
        ActionBar mActionBar = getSupportActionBar();

        // Show back button in ActionBar
        if (mActionBar != null) {
            mActionBar.setDisplayHomeAsUpEnabled(true);
        }

        /* If savedInstanceState is not null then fetch movieId and movieName
         * Else try to get from Intent Bundle Extra
         */
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
        adjustImageLayouts();
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
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.detail, menu);
        return true;
    }

    /**
     * Overrides onLoadStarted() from NetworkLoaderCallback
     */
    @Override
    public void onLoadStarted() {
        showProgressBar();
    }

    /**
     * Overrides onLoadFinished() from NetworkLoaderCallback
     *
     * @param s AsyncTask result String
     */
    @Override
    public void onLoadFinished(String s) {
        MovieDetail movieDetail = (s == null) ? null : gson.fromJson(s, MovieDetail.class);
        if (movieDetail == null) {
            showErrorMessage();
        } else {
            loadMovieDetails(movieDetail);
            showMovieDetails();
        }
    }

    /**
     * Fetches movie details using the NetworkLoader if Network connection is present.
     * Otherwise shows an error message.
     */
    private void fetchMovieDetails() {
        if (HTTPHelper.isNetworkEnabled(this)) {
            URL url = HTTPHelper.buildMovieDetailsURL(String.valueOf(movieId));
            Bundle args = new Bundle();
            args.putSerializable(NetworkLoader.URL_EXTRA, url);
            getSupportLoaderManager().initLoader(NETWORK_LOADER_ID, args, mNetworkLoader);
        } else {
            DisplayUtils.setNoNetworkConnectionMessage(this, mErrorMessageDisplay);
            showErrorMessage();
        }
    }

    /**
     * Formats and sets the movie details into appropriate views
     *
     * @param movieDetail MovieDetail Bean
     */
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

        if (tagline == null || tagline.isEmpty()) {
            mMovieTaglineTextView.setVisibility(View.GONE);
        } else {
            mMovieTaglineTextView.setText(DisplayUtils.formatTagline(tagline));
        }

        if (moviePlot != null && !moviePlot.isEmpty()) {
            mMoviePlotTextView.setText(moviePlot);
        }
    }

    /**
     * Shows ProgressBar, Hides ErrorMessage and MovieDetailLayout
     */
    private void showProgressBar() {
        mProgressBar.setVisibility(View.VISIBLE);
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
        mMovieDetailLayout.setVisibility(View.INVISIBLE);
        mFavoriteButton.setVisibility(View.INVISIBLE);
    }

    /**
     * Shows MovieDetailLayout, Hides ProgressBar and ErrorMessage
     */
    private void showMovieDetails() {
        mMovieDetailLayout.setVisibility(View.VISIBLE);
        mFavoriteButton.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.INVISIBLE);
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
    }

    /**
     * Shows ErrorMessage, Hides ProgressBar and MovieDetailLayout
     */
    private void showErrorMessage() {
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.INVISIBLE);
        mFavoriteButton.setVisibility(View.INVISIBLE);
        mMovieDetailLayout.setVisibility(View.INVISIBLE);
    }

    /**
     * Based on the screen size and orientation scales the parent image layouts.
     */
    private void adjustImageLayouts() {
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

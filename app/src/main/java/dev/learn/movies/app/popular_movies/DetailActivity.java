package dev.learn.movies.app.popular_movies;

import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.gson.Gson;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import dev.learn.movies.app.popular_movies.adapters.MovieReviewsAdapter;
import dev.learn.movies.app.popular_movies.common.Genre;
import dev.learn.movies.app.popular_movies.common.MovieDetail;
import dev.learn.movies.app.popular_movies.common.Review;
import dev.learn.movies.app.popular_movies.common.ReviewsResult;
import dev.learn.movies.app.popular_movies.common.Video;
import dev.learn.movies.app.popular_movies.common.VideosResult;
import dev.learn.movies.app.popular_movies.data.DataContract.FavoriteEntry;
import dev.learn.movies.app.popular_movies.databinding.ActivityDetailBinding;
import dev.learn.movies.app.popular_movies.loaders.ContentLoader;
import dev.learn.movies.app.popular_movies.loaders.ContentLoaderCallback;
import dev.learn.movies.app.popular_movies.loaders.NetworkLoader;
import dev.learn.movies.app.popular_movies.loaders.NetworkLoaderCallback;
import dev.learn.movies.app.popular_movies.util.DisplayUtils;
import dev.learn.movies.app.popular_movies.util.HTTPHelper;

import static dev.learn.movies.app.popular_movies.loaders.ContentLoader.URI_EXTRA;

/**
 * DetailActivity - To show the movie details
 */
public class DetailActivity extends AppCompatActivity implements View.OnClickListener,
        NetworkLoaderCallback, ContentLoaderCallback {

    public static final String MOVIE_ID = "movie_id";
    public static final String MOVIE_NAME = "movie_name";

    private static final int MOVIE_DETAILS_LOADER_ID = 100;
    private static final int MOVIE_REVIEWS_LOADER_ID = 101;
    private static final int MOVIE_TRAILERS_LOADER_ID = 102;
    private static final int FAVORITE_LOADER_ID = 301;

    private final Gson gson = new Gson();
    private String movieName = "";
    private long movieId = 0L;
    private MovieReviewsAdapter mMovieReviewsAdapter;
    private ActivityDetailBinding mBinding;

    private NetworkLoader mNetworkLoader;
    private ContentLoader mContentLoader;

    private List<Video> mVideoList = null;
    private MovieDetail mMovieDetail = null;

    private boolean mFavored = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_detail);

        mNetworkLoader = new NetworkLoader(this, this);
        mContentLoader = new ContentLoader(this, this);

        setSupportActionBar(mBinding.toolbar);
        ActionBar mActionBar = getSupportActionBar();

        // Show back button in ActionBar
        if (mActionBar != null) {
            mActionBar.setDisplayHomeAsUpEnabled(true);
            mActionBar.setDisplayShowTitleEnabled(false);
        }

        mBinding.btnFav.setOnClickListener(this);

        mBinding.layoutUserReviews.rvUserReviews.setNestedScrollingEnabled(false);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mBinding.layoutUserReviews.rvUserReviews.setLayoutManager(layoutManager);
        mMovieReviewsAdapter = new MovieReviewsAdapter();
        mBinding.layoutUserReviews.rvUserReviews.setAdapter(mMovieReviewsAdapter);

        mVideoList = new ArrayList<>();

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

        adjustImageLayouts();
        if (movieId != 0) {
            //Move this to onResume and reset the lists for reviews
            fetchMovie();

            Bundle args = new Bundle();
            args.putParcelable(URI_EXTRA, FavoriteEntry.CONTENT_URI.buildUpon().appendPath(String.valueOf(movieId)).build());
            getSupportLoaderManager().initLoader(FAVORITE_LOADER_ID, args, mContentLoader);
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_share:
                if (mVideoList != null && !mVideoList.isEmpty()) {
                    Video video = mVideoList.get(0);
                    shareVideo(video);
                }
                return true;
            case R.id.action_watch_trailer:
                if (mVideoList != null && !mVideoList.isEmpty()) {
                    watchVideo(mVideoList.get(0));
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        if (mFavored) {
            Uri uri = FavoriteEntry.CONTENT_URI.buildUpon().appendPath(String.valueOf(movieId)).build();
            getContentResolver().delete(uri, null, null);
            Toast.makeText(this, getResources().getString(R.string.removed_from_favorites), Toast.LENGTH_SHORT).show();
        } else {
            Uri uri = FavoriteEntry.CONTENT_URI;
            ContentValues cv = getContentValues();
            if (cv != null) {
                getContentResolver().insert(uri, cv);
                Toast.makeText(this, getResources().getString(R.string.added_to_favorites), Toast.LENGTH_SHORT).show();
            }
        }
        mFavored = !mFavored;
        setFavored();
    }

    /**
     * Overrides onLoadStarted() from NetworkLoaderCallback
     */
    @Override
    public void onNetworkStartLoading() {
        showProgressBar();
    }

    @Override
    public void onContentStartLoading() {
    }

    @Override
    public void onContentLoadFinished(Loader loader, Cursor cursor) {
        if (cursor != null && cursor.getCount() > 0) {
            mFavored = true;
        }
        setFavored();
    }

    /**
     * Overrides onLoadFinished() from NetworkLoaderCallback
     *
     * @param s AsyncTask result String
     */
    @Override
    public void onNetworkLoadFinished(Loader loader, String s) {
        switch (loader.getId()) {
            case MOVIE_DETAILS_LOADER_ID:
                MovieDetail movieDetail = (s == null) ? null : gson.fromJson(s, MovieDetail.class);
                if (movieDetail == null) {
                    showErrorMessage();
                } else {
                    mMovieDetail = movieDetail;
                    loadIntoView(movieDetail);
                    showMovieDetails();
                }
                break;
            case MOVIE_REVIEWS_LOADER_ID:
                ReviewsResult reviewsResult = (s == null) ? null : gson.fromJson(s, ReviewsResult.class);
                if (reviewsResult == null || reviewsResult.getResults() == null) {
                    showErrorMessage();
                } else {
                    List<Review> reviewList = reviewsResult.getResults();
                    mMovieReviewsAdapter.setReviewList(reviewList);
                }
                break;
            case MOVIE_TRAILERS_LOADER_ID:
                VideosResult videosResult = (s == null) ? null : gson.fromJson(s, VideosResult.class);
                if (videosResult == null || videosResult.getVideos() == null) {
                    showErrorMessage();
                } else {
                    mVideoList = videosResult.getVideos();
                }
                break;
        }
    }

    private void setFavored() {
        mBinding.btnFav.setImageResource(mFavored ? R.drawable.ic_heart_white_24dp : R.drawable.ic_heart_outline_white_24dp);
    }

    /**
     * Fetches movie details using the NetworkLoader if Network connection is present.
     * Otherwise shows an error message.
     */
    private void fetchMovie() {
        if (HTTPHelper.isNetworkEnabled(this)) {
            loadMovieDetails();
            loadMovieReviews();
            loadMovieTrailers();
        } else {
            DisplayUtils.setNoNetworkConnectionMessage(this, mBinding.tvErrorMessageDisplay);
            showErrorMessage();
        }
    }

    private void loadMovieDetails() {
        URL url = HTTPHelper.buildMovieDetailsURL(String.valueOf(movieId));
        Bundle args = new Bundle();
        args.putSerializable(NetworkLoader.URL_EXTRA, url);
        getSupportLoaderManager().initLoader(MOVIE_DETAILS_LOADER_ID, args, mNetworkLoader);
    }

    private void loadMovieReviews() {
        URL url = HTTPHelper.buildMovieReviewsURL(String.valueOf(movieId));
        Bundle args = new Bundle();
        args.putSerializable(NetworkLoader.URL_EXTRA, url);
        getSupportLoaderManager().initLoader(MOVIE_REVIEWS_LOADER_ID, args, mNetworkLoader);
    }

    private void loadMovieTrailers() {
        URL url = HTTPHelper.buildMovieTrailersURL(String.valueOf(movieId));
        Bundle args = new Bundle();
        args.putSerializable(NetworkLoader.URL_EXTRA, url);
        getSupportLoaderManager().initLoader(MOVIE_TRAILERS_LOADER_ID, args, mNetworkLoader);
    }

    /**
     * Formats and sets the movie details into appropriate views
     *
     * @param movieDetail MovieDetail Bean
     */
    private void loadIntoView(MovieDetail movieDetail) {
        int year = DisplayUtils.getYear(movieDetail.getReleaseDate());
        double voteAverage = movieDetail.getVoteAverage();
        String backdropURL = movieDetail.getBackdropPath();
        String posterURL = movieDetail.getPosterPath();
        String title = movieDetail.getTitle();
        String runningTime = movieDetail.getRuntime() + " min";
        String rating = String.valueOf(voteAverage);
        String voteCount = "(" + movieDetail.getVoteCount() + ")";
        String tagline = movieDetail.getTagline();
        String moviePlot = movieDetail.getOverview();
        List<Genre> genres = movieDetail.getGenres();

        if (backdropURL != null) {
            Uri backdropUri = HTTPHelper.buildImageResourceUri(backdropURL, HTTPHelper.IMAGE_SIZE_XLARGE);
            DisplayUtils.fitImageInto(mBinding.imageViewBackdrop, backdropUri, null);
        }

        if (posterURL != null) {
            Uri posterUri = HTTPHelper.buildImageResourceUri(posterURL, HTTPHelper.IMAGE_SIZE_SMALL);
            DisplayUtils.fitImageInto(mBinding.layoutMovieInfo.layoutPoster.imageViewPoster, posterUri, null);
        }

        mBinding.layoutMovieInfo.tvMovieTitle.setText(DisplayUtils.formatTitle(title, year));

        mBinding.layoutMovieInfo.tvMovieRuntime.setText(runningTime);

        DisplayUtils.addGenres(genres, mBinding.layoutContent.layoutGenres, this);

        mBinding.layoutMovieInfo.rbMovieRating.setRating((float) voteAverage);

        mBinding.layoutMovieInfo.tvMovieRating.setText(rating);

        mBinding.layoutMovieInfo.tvMovieRatingNum.setText(voteCount);

        mBinding.layoutContent.tvMovieTagline.setText(DisplayUtils.formatTagline(this, tagline));

        if (moviePlot != null && !moviePlot.isEmpty()) {
            mBinding.layoutContent.tvMoviePlot.setText(moviePlot);
        }
    }

    /**
     * Shows ProgressBar, Hides ErrorMessage and MovieDetailLayout
     */
    private void showProgressBar() {
        mBinding.pbLoadingIndicator.setVisibility(View.VISIBLE);
        mBinding.tvErrorMessageDisplay.setVisibility(View.INVISIBLE);
        mBinding.layoutMovieDetail.setVisibility(View.INVISIBLE);
        mBinding.btnFav.setVisibility(View.INVISIBLE);
    }

    /**
     * Shows MovieDetailLayout, Hides ProgressBar and ErrorMessage
     */
    private void showMovieDetails() {
        mBinding.layoutMovieDetail.setVisibility(View.VISIBLE);
        mBinding.btnFav.setVisibility(View.VISIBLE);
        mBinding.pbLoadingIndicator.setVisibility(View.INVISIBLE);
        mBinding.tvErrorMessageDisplay.setVisibility(View.INVISIBLE);
    }

    /**
     * Shows ErrorMessage, Hides ProgressBar and MovieDetailLayout
     */
    private void showErrorMessage() {
        mBinding.tvErrorMessageDisplay.setVisibility(View.VISIBLE);
        mBinding.pbLoadingIndicator.setVisibility(View.INVISIBLE);
        mBinding.btnFav.setVisibility(View.INVISIBLE);
        mBinding.layoutMovieDetail.setVisibility(View.INVISIBLE);
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

        mBinding.appBarLayout.setLayoutParams(new CoordinatorLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) (max / 2.25)));
        mBinding.layoutMovieInfo.layoutPoster.getRoot().setLayoutParams(new ConstraintLayout.LayoutParams((min / 3), (int) (max / 3.15)));
    }

    private void shareVideo(Video video) {
        if (video != null && video.getKey() != null) {
            String mimeType = "text/plain";
            String title = (video.getName() == null) ? getResources().getString(R.string.trailer_1) : video.getName();
            URL url = HTTPHelper.buildYouTubeURL(video.getKey());
            if (url != null) {
                ShareCompat.IntentBuilder
                        .from(this)
                        .setType(mimeType)
                        .setChooserTitle("Share " + title)
                        .setText(url.toString())
                        .startChooser();
            }
        }
    }

    private void watchVideo(Video video) {
        if (video != null && video.getKey() != null) {
            String key = video.getKey();
            try {
                Intent youtubeAppIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd:youtube:" + key));
                startActivity(youtubeAppIntent);
            } catch (ActivityNotFoundException e) {
                URL url = HTTPHelper.buildYouTubeURL(key);
                if (url != null) {
                    Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url.toString()));
                    startActivity(webIntent);
                }
            }
        }
    }

    private ContentValues getContentValues() {
        ContentValues cv = null;
        if (mMovieDetail != null) {
            cv = new ContentValues();
            cv.put(FavoriteEntry.COLUMN_MOVIE_ID, mMovieDetail.getId());
            cv.put(FavoriteEntry.COLUMN_TITLE, mMovieDetail.getTitle());
            cv.put(FavoriteEntry.COLUMN_TAGLINE, mMovieDetail.getTagline());
            cv.put(FavoriteEntry.COLUMN_OVERVIEW, mMovieDetail.getOverview());
            cv.put(FavoriteEntry.COLUMN_POSTER_PATH, mMovieDetail.getPosterPath());
            cv.put(FavoriteEntry.COLUMN_BACKDROP_PATH, mMovieDetail.getBackdropPath());
            cv.put(FavoriteEntry.COLUMN_RELEASE_DATE, mMovieDetail.getReleaseDate());
            cv.put(FavoriteEntry.COLUMN_RUNTIME, mMovieDetail.getRuntime());
            cv.put(FavoriteEntry.COLUMN_VOTE_AVG, mMovieDetail.getVoteAverage());
            cv.put(FavoriteEntry.COLUMN_VOTE_COUNT, mMovieDetail.getVoteCount());
            StringBuilder builder = new StringBuilder();
            List<Genre> genres = mMovieDetail.getGenres();
            if (genres != null && !genres.isEmpty()) {
                for (Genre genre : genres) {
                    builder.append(genre.getName()).append(",");
                }
            }
            cv.put(FavoriteEntry.COLUMN_GENRES, builder.toString());
        }
        return cv;
    }
}

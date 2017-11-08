package dev.learn.movies.app.popular_movies;

import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView.LayoutManager;
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
import dev.learn.movies.app.popular_movies.loaders.NetworkLoader;
import dev.learn.movies.app.popular_movies.util.DialogBuilderHelper;
import dev.learn.movies.app.popular_movies.util.DisplayUtils;
import dev.learn.movies.app.popular_movies.util.HTTPHelper;

import static dev.learn.movies.app.popular_movies.data.DataContract.FavoriteEntry.COLUMN_BACKDROP_PATH;
import static dev.learn.movies.app.popular_movies.data.DataContract.FavoriteEntry.COLUMN_GENRES;
import static dev.learn.movies.app.popular_movies.data.DataContract.FavoriteEntry.COLUMN_MOVIE_ID;
import static dev.learn.movies.app.popular_movies.data.DataContract.FavoriteEntry.COLUMN_OVERVIEW;
import static dev.learn.movies.app.popular_movies.data.DataContract.FavoriteEntry.COLUMN_POSTER_PATH;
import static dev.learn.movies.app.popular_movies.data.DataContract.FavoriteEntry.COLUMN_RELEASE_DATE;
import static dev.learn.movies.app.popular_movies.data.DataContract.FavoriteEntry.COLUMN_RUNTIME;
import static dev.learn.movies.app.popular_movies.data.DataContract.FavoriteEntry.COLUMN_TAGLINE;
import static dev.learn.movies.app.popular_movies.data.DataContract.FavoriteEntry.COLUMN_TITLE;
import static dev.learn.movies.app.popular_movies.data.DataContract.FavoriteEntry.COLUMN_VOTE_AVG;
import static dev.learn.movies.app.popular_movies.data.DataContract.FavoriteEntry.COLUMN_VOTE_COUNT;
import static dev.learn.movies.app.popular_movies.loaders.ContentLoader.URI_EXTRA;
import static dev.learn.movies.app.popular_movies.util.AppConstants.ACTIVITY_DETAIL_LAZY_LOAD_DELAY_IN_MS;
import static dev.learn.movies.app.popular_movies.util.AppConstants.FAVORITE_LOADER_ID;
import static dev.learn.movies.app.popular_movies.util.AppConstants.MOVIE_DETAILS_LOADER_ID;
import static dev.learn.movies.app.popular_movies.util.AppConstants.MOVIE_REVIEWS_LOADER_ID;
import static dev.learn.movies.app.popular_movies.util.AppConstants.MOVIE_TRAILERS_LOADER_ID;

/**
 * DetailActivity - Movie Details Screen
 */
public class DetailActivity extends AppCompatActivity implements View.OnClickListener,
        NetworkLoader.NetworkLoaderCallback, ContentLoader.ContentLoaderCallback {

    public static final String MOVIE_ID = "movie_id";
    private static final String MOVIE_DETAILS = "movie_details";
    private static final String MOVIE_REVIEWS = "movie_reviews";
    private static final String MOVIE_TRAILERS = "movie_trailers";
    private static final String FAVORED = "favored";

    private final Gson gson = new Gson();
    private long movieId = 0L;
    private MovieReviewsAdapter mMovieReviewsAdapter;
    private ActivityDetailBinding mBinding;
    private Toast mFavToast;

    private NetworkLoader mNetworkLoader;
    private ContentLoader mContentLoader;

    private List<Video> mVideoList;
    private List<Review> mReviewsList;
    private MovieDetail mMovieDetail;
    private boolean mFavored = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_detail);

        LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mVideoList = new ArrayList<>();
        mReviewsList = new ArrayList<>();
        mMovieReviewsAdapter = new MovieReviewsAdapter();
        mNetworkLoader = new NetworkLoader(this, this);
        mContentLoader = new ContentLoader(this, this);

        setSupportActionBar(mBinding.toolbar);
        ActionBar mActionBar = getSupportActionBar();
        if (mActionBar != null) {
            mActionBar.setDisplayHomeAsUpEnabled(true);
            mActionBar.setDisplayShowTitleEnabled(false);
        }

        adjustImageLayouts();

        mBinding.btnFav.setOnClickListener(this);
        mBinding.layoutUserReviews.rvUserReviews.setNestedScrollingEnabled(false);
        mBinding.layoutUserReviews.rvUserReviews.setLayoutManager(layoutManager);
        mBinding.layoutUserReviews.rvUserReviews.setAdapter(mMovieReviewsAdapter);

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras != null && extras.containsKey(MOVIE_ID)) {
                movieId = getIntent().getExtras().getLong(MOVIE_ID);
                loadMovieDetailsFromDatabase(); //Check if the movieDetails are available from local
                lazyLoadAdditionalInfoFromNetwork();
            } else {
                showErrorMessage();
            }
        } else {
            movieId = savedInstanceState.getLong(MOVIE_ID);
            mMovieDetail = savedInstanceState.getParcelable(MOVIE_DETAILS);
            mReviewsList = savedInstanceState.getParcelableArrayList(MOVIE_REVIEWS);
            mVideoList = savedInstanceState.getParcelableArrayList(MOVIE_TRAILERS);
            mFavored = savedInstanceState.getBoolean(FAVORED);
            updateMovieDetailsUI(mMovieDetail);
            updateReviewsUI(mReviewsList);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (!HTTPHelper.isNetworkEnabled(this)) {
            Toast.makeText(this, getResources().getString(R.string.no_network_connection_error_message), Toast.LENGTH_SHORT).show();
            return false;
        }

        switch (item.getItemId()) {
            case R.id.action_share:
                if (mVideoList != null && !mVideoList.isEmpty()) {
                    List<String> values = new ArrayList<>();
                    for (Video video : mVideoList) {
                        values.add(video.getName());
                    }
                    DialogBuilderHelper.build(this, "Share Trailer", values, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            shareVideo(mVideoList.get(which));
                        }
                    });
                }
                return true;
            case R.id.action_watch_trailer:
                if (mVideoList != null && !mVideoList.isEmpty()) {
                    List<String> values = new ArrayList<>();
                    for (Video video : mVideoList) {
                        values.add(video.getName());
                    }
                    DialogBuilderHelper.build(this, "Watch Trailer", values, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            watchVideo(mVideoList.get(which));
                        }
                    });
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        String toastMessage;

        if (mFavored) {
            Uri uri = FavoriteEntry.CONTENT_URI.buildUpon().appendPath(String.valueOf(movieId)).build();
            toastMessage = getResources().getString(R.string.removed_from_favorites);
            getContentResolver().delete(uri, null, null);
        } else {
            Uri uri = FavoriteEntry.CONTENT_URI;
            toastMessage = getResources().getString(R.string.added_to_favorites);
            ContentValues cv = toContentValues(mMovieDetail);
            if (cv != null) {
                getContentResolver().insert(uri, cv);
            }
        }

        mFavored = !mFavored;
        if (mFavToast != null) {
            mFavToast.cancel();
        }
        mFavToast = Toast.makeText(this, toastMessage, Toast.LENGTH_SHORT);
        mFavToast.show();
        updateFavButton();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(MOVIE_ID, movieId);
        outState.putParcelable(MOVIE_DETAILS, mMovieDetail);
        outState.putParcelableArrayList(MOVIE_REVIEWS, (ArrayList<? extends Parcelable>) mReviewsList);
        outState.putParcelableArrayList(MOVIE_TRAILERS, (ArrayList<? extends Parcelable>) mVideoList);
        outState.putBoolean(FAVORED, mFavored);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mFavToast != null) {
            mFavToast.cancel();
        }
    }

    /**
     * Implement onLoadFinished(Loader,Cursor) from ContentLoader.ContentLoaderCallback
     *
     * @param loader Loader instance
     * @param cursor Cursor
     */
    @Override
    public void onLoadFinished(Loader loader, Cursor cursor) {
        // If movie details are stored locally then populate views
        // Otherwise make an API call if the Network is available
        if (cursor != null && cursor.moveToFirst()) {
            mFavored = true;
            updateMovieDetailsUI(fromCursor(cursor));
        } else {
            mFavored = false;
            if (HTTPHelper.isNetworkEnabled(this)) {
                loadMovieDetailsFromNetwork();
            } else {
                DisplayUtils.setNoNetworkConnectionMessage(this, mBinding.tvErrorMessageDisplay);
                showErrorMessage();
            }
        }
    }

    /**
     * Implement onLoadFinished(Loader,Cursor) from NetworkLoader.NetworkLoaderCallback
     *
     * @param loader Loader instance
     * @param s      Response string
     */
    @Override
    public void onLoadFinished(Loader loader, String s) {
        // Handle responses for the different loaderId calls
        switch (loader.getId()) {
            case MOVIE_DETAILS_LOADER_ID:
                // Handle movie info
                MovieDetail movieDetail = (s == null) ? null : gson.fromJson(s, MovieDetail.class);
                updateMovieDetailsUI(movieDetail);
                break;
            case MOVIE_REVIEWS_LOADER_ID:
                // Handle reviews response
                ReviewsResult reviewsResult = (s == null) ? null : gson.fromJson(s, ReviewsResult.class);
                List<Review> reviewList = (reviewsResult == null) ? null : reviewsResult.getResults();
                updateReviewsUI(reviewList);
                break;
            case MOVIE_TRAILERS_LOADER_ID:
                // Handle videos response
                VideosResult videosResult = (s == null) ? null : gson.fromJson(s, VideosResult.class);
                if (videosResult != null) {
                    mVideoList = videosResult.getVideos();
                }
                break;
        }
    }

    /**
     * Loads movie details from local ContentProvider
     */
    private void loadMovieDetailsFromDatabase() {
        Bundle args = new Bundle();
        Uri uri = FavoriteEntry.CONTENT_URI.buildUpon().appendPath(String.valueOf(movieId)).build();
        args.putParcelable(URI_EXTRA, uri);
        getSupportLoaderManager().restartLoader(FAVORITE_LOADER_ID, args, mContentLoader);
    }

    /**
     * Loads movie details from Network
     */
    private void loadMovieDetailsFromNetwork() {
        URL url = HTTPHelper.buildMovieDetailsURL(String.valueOf(movieId));
        Bundle args = new Bundle();
        args.putSerializable(NetworkLoader.URL_EXTRA, url);
        getSupportLoaderManager().restartLoader(MOVIE_DETAILS_LOADER_ID, args, mNetworkLoader);
    }

    /**
     * Lazy loads reviews and trailers
     */
    private void lazyLoadAdditionalInfoFromNetwork() {
        if (HTTPHelper.isNetworkEnabled(this)) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    loadMovieReviewsFromNetwork();
                    loadMovieTrailersFromNetwork();
                }
            }, ACTIVITY_DETAIL_LAZY_LOAD_DELAY_IN_MS);
        } else {
            showReviewsErrorMessage();
        }
    }

    /**
     * Loads movie reviews from Network
     */
    private void loadMovieReviewsFromNetwork() {
        URL url = HTTPHelper.buildMovieReviewsURL(String.valueOf(movieId));
        Bundle args = new Bundle();
        args.putSerializable(NetworkLoader.URL_EXTRA, url);
        getSupportLoaderManager().restartLoader(MOVIE_REVIEWS_LOADER_ID, args, mNetworkLoader);
    }

    /**
     * Loads movie trailers from Network
     */
    private void loadMovieTrailersFromNetwork() {
        URL url = HTTPHelper.buildMovieTrailersURL(String.valueOf(movieId));
        Bundle args = new Bundle();
        args.putSerializable(NetworkLoader.URL_EXTRA, url);
        getSupportLoaderManager().restartLoader(MOVIE_TRAILERS_LOADER_ID, args, mNetworkLoader);
    }

    /**
     * Formats and sets the movie details into appropriate views
     *
     * @param movieDetail MovieDetail Bean
     */
    private void updateMovieDetailsUI(MovieDetail movieDetail) {
        if (movieDetail == null) {
            showErrorMessage();
            return;
        }

        mMovieDetail = movieDetail;
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
            DisplayUtils.fitImageInto(mBinding.imageViewBackdrop, backdropUri);
        }

        if (posterURL != null) {
            Uri posterUri = HTTPHelper.buildImageResourceUri(posterURL, HTTPHelper.IMAGE_SIZE_SMALL);
            DisplayUtils.fitImageInto(mBinding.layoutMovieInfo.layoutPoster.imageViewPoster, posterUri);
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

        updateFavButton();
        showMovieDetails();
    }

    /**
     * Updates Reviews list
     *
     * @param reviewsList ReviewsList
     */
    private void updateReviewsUI(List<Review> reviewsList) {
        if (reviewsList == null || reviewsList.size() <= 0) {
            showReviewsErrorMessage();
            return;
        }

        mReviewsList = reviewsList;
        mMovieReviewsAdapter.setReviewList(mReviewsList);
        showReviews();
    }

    /**
     * Updates fav button based on mFavored
     */
    private void updateFavButton() {
        if (mFavored) {
            mBinding.btnFav.setImageResource(R.drawable.ic_heart_white_24dp);
        } else {
            mBinding.btnFav.setImageResource(R.drawable.ic_heart_outline_white_24dp);
        }
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
     * Shows MovieDetailLayout, Hides ProgressBar and ErrorMessage
     */
    private void showReviews() {
        mBinding.layoutUserReviews.rvUserReviews.setVisibility(View.VISIBLE);
        mBinding.layoutUserReviews.pbUserReviews.setVisibility(View.INVISIBLE);
        mBinding.layoutUserReviews.cvReviewsErrorMessage.setVisibility(View.INVISIBLE);
    }

    /**
     * Shows ErrorMessage, Hides ProgressBar and MovieDetailLayout
     */
    private void showReviewsErrorMessage() {
        mBinding.layoutUserReviews.cvReviewsErrorMessage.setVisibility(View.VISIBLE);
        mBinding.layoutUserReviews.pbUserReviews.setVisibility(View.INVISIBLE);
        mBinding.layoutUserReviews.rvUserReviews.setVisibility(View.INVISIBLE);
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

    /**
     * Build and call Share intent for a video
     *
     * @param video Video
     */
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

    /**
     * Build and call Youtube intent
     * Reference: https://stackoverflow.com/questions/574195/android-youtube-app-play-video-intent
     *
     * @param video Video
     */
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

    /**
     * Convert MovieDetail into ContentValues
     *
     * @param movieDetail MovieDetail object
     * @return contentValues
     */
    private ContentValues toContentValues(MovieDetail movieDetail) {
        ContentValues cv = null;
        if (movieDetail != null) {
            cv = new ContentValues();
            cv.put(COLUMN_MOVIE_ID, movieDetail.getId());
            cv.put(COLUMN_TITLE, movieDetail.getTitle());
            cv.put(COLUMN_TAGLINE, movieDetail.getTagline());
            cv.put(COLUMN_OVERVIEW, movieDetail.getOverview());
            cv.put(COLUMN_POSTER_PATH, movieDetail.getPosterPath());
            cv.put(COLUMN_BACKDROP_PATH, movieDetail.getBackdropPath());
            cv.put(COLUMN_RELEASE_DATE, movieDetail.getReleaseDate());
            cv.put(COLUMN_RUNTIME, movieDetail.getRuntime());
            cv.put(COLUMN_VOTE_AVG, movieDetail.getVoteAverage());
            cv.put(COLUMN_VOTE_COUNT, movieDetail.getVoteCount());
            StringBuilder builder = new StringBuilder();
            List<Genre> genreList = movieDetail.getGenres();
            if (genreList != null && !genreList.isEmpty()) {
                for (Genre genre : genreList) {
                    builder.append(genre.getName()).append(",");
                }
            }
            cv.put(COLUMN_GENRES, builder.toString());
        }
        return cv;
    }

    /**
     * Create a MovieDetail object from cursor
     *
     * @param cursor Cursor
     * @return movieDetail object
     */
    private MovieDetail fromCursor(Cursor cursor) {
        MovieDetail movieDetail = null;
        if (cursor.moveToFirst()) {
            movieDetail = new MovieDetail();
            movieDetail.setId(cursor.getLong(cursor.getColumnIndex(COLUMN_MOVIE_ID)));
            movieDetail.setTitle(cursor.getString(cursor.getColumnIndex(COLUMN_TITLE)));
            movieDetail.setTagline(cursor.getString(cursor.getColumnIndex(COLUMN_TAGLINE)));
            movieDetail.setOverview(cursor.getString(cursor.getColumnIndex(COLUMN_OVERVIEW)));
            movieDetail.setPosterPath(cursor.getString(cursor.getColumnIndex(COLUMN_POSTER_PATH)));
            movieDetail.setBackdropPath(cursor.getString(cursor.getColumnIndex(COLUMN_BACKDROP_PATH)));
            movieDetail.setReleaseDate(cursor.getString(cursor.getColumnIndex(COLUMN_RELEASE_DATE)));
            movieDetail.setRuntime(cursor.getLong(cursor.getColumnIndex(COLUMN_RUNTIME)));
            movieDetail.setVoteAverage(cursor.getDouble(cursor.getColumnIndex(COLUMN_VOTE_AVG)));
            movieDetail.setVoteCount(cursor.getLong(cursor.getColumnIndex(COLUMN_VOTE_COUNT)));
            String genresStr = cursor.getString(cursor.getColumnIndex(COLUMN_GENRES));
            List<Genre> genreList = new ArrayList<>();
            String[] genresArr = genresStr.split(",");
            for (String genre : genresArr) {
                genreList.add(new Genre(0, genre));
            }
            movieDetail.setGenres(genreList);
        }
        return movieDetail;
    }
}

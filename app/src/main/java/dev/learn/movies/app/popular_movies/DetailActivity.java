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
 * DetailActivity - To show the movie details
 */
public class DetailActivity extends AppCompatActivity implements View.OnClickListener,
        NetworkLoader.NetworkLoaderCallback, ContentLoader.ContentLoaderCallback {

    public static final String MOVIE_ID = "movie_id";

    private final Gson gson = new Gson();
    private long movieId = 0L;
    private MovieReviewsAdapter mMovieReviewsAdapter;
    private ActivityDetailBinding mBinding;
    private Toast mFavToast;

    private NetworkLoader mNetworkLoader;
    private ContentLoader mContentLoader;

    private List<Video> mVideoList = null;
    private MovieDetail mMovieDetail = null;
    private boolean mFavored = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_detail);

        LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mVideoList = new ArrayList<>();
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

        if (savedInstanceState != null) {
            movieId = savedInstanceState.getLong(MOVIE_ID, 0);
        } else if (getIntent().getExtras() != null) {
            movieId = getIntent().getExtras().getLong(MOVIE_ID, 0);
        }

        if (movieId == 0) {
            showErrorMessage();
        } else {
            loadMovieDetailsFromDatabase();
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
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mFavToast != null) {
            mFavToast.cancel();
        }
    }

    @Override
    public void onLoadFinished(Loader loader, Cursor cursor) {
        if (cursor != null && cursor.moveToFirst()) {
            mFavored = true;
            mMovieDetail = fromCursor(cursor);
            loadDataIntoView(mMovieDetail);
            showMovieDetails();
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

    @Override
    public void onLoadFinished(Loader loader, String s) {
        switch (loader.getId()) {
            case MOVIE_DETAILS_LOADER_ID:
                MovieDetail movieDetail = (s == null) ? null : gson.fromJson(s, MovieDetail.class);
                if (movieDetail == null) {
                    showErrorMessage();
                } else {
                    mMovieDetail = movieDetail;
                    loadDataIntoView(movieDetail);
                    showMovieDetails();
                }
                break;
            case MOVIE_REVIEWS_LOADER_ID:
                ReviewsResult reviewsResult = (s == null) ? null : gson.fromJson(s, ReviewsResult.class);
                if (reviewsResult != null && reviewsResult.getResults() != null && reviewsResult.getResults().size() > 0) {
                    List<Review> reviewList = reviewsResult.getResults();
                    mMovieReviewsAdapter.setReviewList(reviewList);
                    showReviews();
                } else {
                    showReviewsErrorMessage();
                }
                break;
            case MOVIE_TRAILERS_LOADER_ID:
                VideosResult videosResult = (s == null) ? null : gson.fromJson(s, VideosResult.class);
                if (videosResult != null && videosResult.getVideos() != null) {
                    mVideoList = videosResult.getVideos();
                }
                break;
        }
    }

    private void loadMovieDetailsFromDatabase() {
        Bundle args = new Bundle();
        Uri uri = FavoriteEntry.CONTENT_URI.buildUpon().appendPath(String.valueOf(movieId)).build();
        args.putParcelable(URI_EXTRA, uri);
        getSupportLoaderManager().restartLoader(FAVORITE_LOADER_ID, args, mContentLoader);
    }

    private void loadMovieDetailsFromNetwork() {
        URL url = HTTPHelper.buildMovieDetailsURL(String.valueOf(movieId));
        Bundle args = new Bundle();
        args.putSerializable(NetworkLoader.URL_EXTRA, url);
        getSupportLoaderManager().restartLoader(MOVIE_DETAILS_LOADER_ID, args, mNetworkLoader);
    }

    private void loadMovieReviewsFromNetwork() {
        URL url = HTTPHelper.buildMovieReviewsURL(String.valueOf(movieId));
        Bundle args = new Bundle();
        args.putSerializable(NetworkLoader.URL_EXTRA, url);
        getSupportLoaderManager().restartLoader(MOVIE_REVIEWS_LOADER_ID, args, mNetworkLoader);
    }

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
    private void loadDataIntoView(MovieDetail movieDetail) {
        if (movieDetail == null) return;
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
    }

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
        mBinding.layoutUserReviews.tvErrorMessageDisplay.setVisibility(View.INVISIBLE);
    }

    /**
     * Shows ErrorMessage, Hides ProgressBar and MovieDetailLayout
     */
    private void showReviewsErrorMessage() {
        mBinding.layoutUserReviews.tvErrorMessageDisplay.setVisibility(View.VISIBLE);
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

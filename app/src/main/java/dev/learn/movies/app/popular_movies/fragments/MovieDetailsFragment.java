package dev.learn.movies.app.popular_movies.fragments;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.gson.Gson;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import dev.learn.movies.app.popular_movies.R;
import dev.learn.movies.app.popular_movies.activities.DetailActivity;
import dev.learn.movies.app.popular_movies.activities.MovieDetailCallbacks;
import dev.learn.movies.app.popular_movies.activities.MovieReviewsActivity;
import dev.learn.movies.app.popular_movies.common.Genre;
import dev.learn.movies.app.popular_movies.common.Video;
import dev.learn.movies.app.popular_movies.common.VideosResult;
import dev.learn.movies.app.popular_movies.common.movies.MovieDetail;
import dev.learn.movies.app.popular_movies.data.DataContract;
import dev.learn.movies.app.popular_movies.databinding.FragmentMovieDetailsBinding;
import dev.learn.movies.app.popular_movies.loaders.ContentLoader;
import dev.learn.movies.app.popular_movies.loaders.NetworkLoader;
import dev.learn.movies.app.popular_movies.util.DisplayUtils;
import dev.learn.movies.app.popular_movies.util.HTTPHelper;
import dev.learn.movies.app.popular_movies.util.IntentUtils;

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
import static dev.learn.movies.app.popular_movies.util.AppConstants.MOVIE_ID;
import static dev.learn.movies.app.popular_movies.util.AppConstants.MOVIE_NAME;
import static dev.learn.movies.app.popular_movies.util.AppConstants.MOVIE_TRAILERS_LOADER_ID;

/**
 * Created by sudharti on 11/12/17.
 */

public class MovieDetailsFragment extends Fragment implements DetailActivity.OnFavBtnClickListener,
        ContentLoader.ContentLoaderCallback, NetworkLoader.NetworkLoaderCallback, View.OnClickListener {

    private static final String MOVIE_DETAILS = "movie_details";
    private static final String MOVIE_TRAILERS = "movie_trailers";
    private static final String FAVORED = "favored";

    private Context mContext;
    private MovieDetailCallbacks mCallbacks;

    private final Gson gson = new Gson();
    private long mMovieId = 0L;
    private String mMovieName;

    private NetworkLoader mNetworkLoader;
    private ContentLoader mContentLoader;

    private List<Video> mVideoList;

    private MovieDetail mMovieDetail;

    private boolean mFavored = false;

    private FragmentMovieDetailsBinding mBinding;

    public static MovieDetailsFragment newInstance(long movieId, String movieName) {
        MovieDetailsFragment movieDetailsFragment = new MovieDetailsFragment();

        Bundle args = new Bundle();
        args.putLong(MOVIE_ID, movieId);
        args.putString(MOVIE_NAME, movieName);
        movieDetailsFragment.setArguments(args);

        return movieDetailsFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getContext();
        mVideoList = new ArrayList<>();
        mNetworkLoader = new NetworkLoader(mContext, this);
        mContentLoader = new ContentLoader(mContext, this);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_movie_details, container, false);
        View view = mBinding.getRoot();
        mBinding.layoutMovieInfo.layoutRating.setOnClickListener(this);
        adjustPosterSize();
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mCallbacks = (MovieDetailCallbacks) getActivity();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState == null) {
            mMovieId = getArguments().getLong(MOVIE_ID, 0);
            mMovieName = getArguments().getString(MOVIE_NAME, "");
            if (mMovieId != 0) {
                loadMovieDetailsFromDatabase(); //Check if the movieDetails are available from local
                lazyLoadAdditionalInfoFromNetwork();
            } else {
                showErrorMessage();
            }
        } else {
            mMovieId = savedInstanceState.getLong(MOVIE_ID);
            mMovieName = savedInstanceState.getString(MOVIE_NAME);
            mMovieDetail = savedInstanceState.getParcelable(MOVIE_DETAILS);
            mVideoList = savedInstanceState.getParcelableArrayList(MOVIE_TRAILERS);
            mFavored = savedInstanceState.getBoolean(FAVORED);
            updateMovieDetailsUI(mMovieDetail);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (!HTTPHelper.isNetworkEnabled(mContext)) {
            Toast.makeText(mContext, getResources().getString(R.string.no_network_connection_error_message), Toast.LENGTH_SHORT).show();
            return false;
        }

        switch (item.getItemId()) {
            case R.id.action_share:
                IntentUtils.shareVideos(getActivity(), mVideoList);
                return true;
            case R.id.action_watch_trailer:
                IntentUtils.watchVideos(getActivity(), mVideoList);
                return true;
            case R.id.action_imdb:
                IntentUtils.openIMDBLink(mContext, mMovieDetail.getImdbId());
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(MOVIE_ID, mMovieId);
        outState.putString(MOVIE_NAME, mMovieName);
        outState.putParcelable(MOVIE_DETAILS, mMovieDetail);
        outState.putParcelableArrayList(MOVIE_TRAILERS, (ArrayList<? extends Parcelable>) mVideoList);
        outState.putBoolean(FAVORED, mFavored);
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
            if (HTTPHelper.isNetworkEnabled(mContext)) {
                loadMovieDetailsFromNetwork();
            } else {
                DisplayUtils.setNoNetworkConnectionMessage(mContext, mBinding.tvErrorMessageDisplay);
                showErrorMessage();
            }
        }
    }

    /**
     * Implement onLoadFinished(Loader,String) from NetworkLoader.NetworkLoaderCallback
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
            case MOVIE_TRAILERS_LOADER_ID:
                // Handle videos response
                VideosResult videosResult = (s == null) ? null : gson.fromJson(s, VideosResult.class);
                if (videosResult != null) {
                    mVideoList = videosResult.getVideos();
                }
                break;
        }
    }

    @Override
    public void onFavBtnClicked(View v) {
        if (mFavored) {
            Uri uri = DataContract.FavoriteEntry.CONTENT_URI.buildUpon().appendPath(String.valueOf(mMovieId)).build();
            getActivity().getContentResolver().delete(uri, null, null);
        } else {
            Uri uri = DataContract.FavoriteEntry.CONTENT_URI;
            ContentValues cv = toContentValues(mMovieDetail);
            if (cv != null) {
                getActivity().getContentResolver().insert(uri, cv);
            }
        }

        mFavored = !mFavored;
        mCallbacks.showFavToast(mFavored);
        mCallbacks.updateFavBtn(mFavored);
    }


    @Override
    public void onClick(View v) {
        if (HTTPHelper.isNetworkEnabled(mContext)) {
            Intent reviewsIntent = new Intent(mContext, MovieReviewsActivity.class);
            reviewsIntent.putExtra(MOVIE_ID, mMovieId);
            reviewsIntent.putExtra(MOVIE_NAME, mMovieName);
            startActivity(reviewsIntent);
        } else {
            Toast.makeText(mContext, getResources().getString(R.string.no_network_connection_error_message), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Loads movie details from local ContentProvider
     */
    private void loadMovieDetailsFromDatabase() {
        Bundle args = new Bundle();
        Uri uri = DataContract.FavoriteEntry.CONTENT_URI.buildUpon().appendPath(String.valueOf(mMovieId)).build();
        args.putParcelable(URI_EXTRA, uri);
        getActivity().getSupportLoaderManager().restartLoader(FAVORITE_LOADER_ID, args, mContentLoader);
    }

    /**
     * Loads movie details from Network
     */
    private void loadMovieDetailsFromNetwork() {
        URL url = HTTPHelper.buildMovieDetailsURL(String.valueOf(mMovieId));
        Bundle args = new Bundle();
        args.putSerializable(NetworkLoader.URL_EXTRA, url);
        getActivity().getSupportLoaderManager().restartLoader(MOVIE_DETAILS_LOADER_ID, args, mNetworkLoader);
    }

    /**
     * Lazy loads additional info
     */
    private void lazyLoadAdditionalInfoFromNetwork() {
        if (HTTPHelper.isNetworkEnabled(mContext)) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    loadMovieTrailersFromNetwork();
                }
            }, ACTIVITY_DETAIL_LAZY_LOAD_DELAY_IN_MS);
        }
    }

    /**
     * Loads movie trailers from Network
     */
    private void loadMovieTrailersFromNetwork() {
        URL url = HTTPHelper.buildMovieTrailersURL(String.valueOf(mMovieId));
        Bundle args = new Bundle();
        args.putSerializable(NetworkLoader.URL_EXTRA, url);
        getActivity().getSupportLoaderManager().restartLoader(MOVIE_TRAILERS_LOADER_ID, args, mNetworkLoader);
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
        String status = movieDetail.getStatus();
        String rating = String.valueOf(voteAverage);
        String voteCount = "(" + movieDetail.getVoteCount() + ")";
        String tagline = movieDetail.getTagline();
        String moviePlot = movieDetail.getOverview();
        long budget = movieDetail.getBudget();
        long revenue = movieDetail.getRevenue();
        List<Genre> genres = movieDetail.getGenres();

        if (backdropURL != null) {
            Uri backdropUri = HTTPHelper.buildImageResourceUri(backdropURL, HTTPHelper.IMAGE_SIZE_XLARGE);
            mCallbacks.updateBackdrop(backdropUri);
        }

        if (posterURL != null) {
            Uri posterUri = HTTPHelper.buildImageResourceUri(posterURL, HTTPHelper.IMAGE_SIZE_SMALL);
            DisplayUtils.fitImageInto(mBinding.layoutMovieInfo.layoutPoster.imageViewPoster, posterUri);
        }

        mBinding.layoutMovieInfo.tvMovieTitle.setText(DisplayUtils.formatTitle(title, year));

        mBinding.layoutMovieInfo.tvMovieRuntime.setText(runningTime);

        if (!TextUtils.isEmpty(status)) {
            mBinding.layoutMovieInfo.tvMovieStatus.setText(status);
        } else {
            mBinding.layoutMovieInfo.tvMovieStatus.setVisibility(View.GONE);
        }

        DisplayUtils.addGenres(genres, mBinding.layoutContent.layoutGenres, mContext);

        mBinding.layoutMovieInfo.rbMovieRating.setRating((float) voteAverage);

        mBinding.layoutMovieInfo.tvMovieRating.setText(rating);

        mBinding.layoutMovieInfo.tvMovieRatingNum.setText(voteCount);

        mBinding.layoutContent.tvMovieTagline.setText(DisplayUtils.formatTagline(mContext, tagline));

        mBinding.layoutContent.tvBudget.setText(DisplayUtils.formatCurrency(budget));

        mBinding.layoutContent.tvRevenue.setText(DisplayUtils.formatCurrency(revenue));

        if (moviePlot != null && !moviePlot.isEmpty()) {
            mBinding.layoutContent.tvMoviePlot.setText(moviePlot);
        }

        mCallbacks.updateFavBtn(mFavored);
        showMovieDetails();
    }

    /**
     * Shows MovieDetailLayout, Hides ProgressBar and ErrorMessage
     */
    private void showMovieDetails() {
        mBinding.layoutMovieDetail.setVisibility(View.VISIBLE);
        mBinding.pbLoadingIndicator.setVisibility(View.INVISIBLE);
        mBinding.tvErrorMessageDisplay.setVisibility(View.INVISIBLE);
        mCallbacks.showFavBtn();
    }

    /**
     * Shows ErrorMessage, Hides ProgressBar and MovieDetailLayout
     */
    private void showErrorMessage() {
        mBinding.tvErrorMessageDisplay.setVisibility(View.VISIBLE);
        mBinding.pbLoadingIndicator.setVisibility(View.INVISIBLE);
        mBinding.layoutMovieDetail.setVisibility(View.INVISIBLE);
        mCallbacks.hideFavBtn();
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

    private void adjustPosterSize() {
        int[] screen = DisplayUtils.getScreenMetrics(getActivity());
        int min = Math.min(screen[0], screen[1]);
        int max = Math.max(screen[0], screen[1]);
        mBinding.layoutMovieInfo.layoutPoster.getRoot().setLayoutParams(new
                ConstraintLayout.LayoutParams((min / 3), (int) (max / 3.15)));
    }
}

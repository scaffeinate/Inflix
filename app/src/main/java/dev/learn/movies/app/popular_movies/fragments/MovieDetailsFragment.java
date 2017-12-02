package dev.learn.movies.app.popular_movies.fragments;

import android.content.ContentValues;
import android.content.Context;
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
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.Loader;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
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

import dev.learn.movies.app.popular_movies.R;
import dev.learn.movies.app.popular_movies.activities.AdditionalInfoActivity;
import dev.learn.movies.app.popular_movies.activities.DetailActivity;
import dev.learn.movies.app.popular_movies.activities.MovieDetailCallbacks;
import dev.learn.movies.app.popular_movies.adapters.FilmCastAdapter;
import dev.learn.movies.app.popular_movies.adapters.FilmStripAdapter;
import dev.learn.movies.app.popular_movies.adapters.OnItemClickHandler;
import dev.learn.movies.app.popular_movies.common.Genre;
import dev.learn.movies.app.popular_movies.common.Video;
import dev.learn.movies.app.popular_movies.common.VideosResult;
import dev.learn.movies.app.popular_movies.common.cast.Cast;
import dev.learn.movies.app.popular_movies.common.cast.CastsResult;
import dev.learn.movies.app.popular_movies.common.movies.Movie;
import dev.learn.movies.app.popular_movies.common.movies.MovieDetail;
import dev.learn.movies.app.popular_movies.common.movies.MoviesResult;
import dev.learn.movies.app.popular_movies.data.DataContract;
import dev.learn.movies.app.popular_movies.databinding.FragmentMovieDetailsBinding;
import dev.learn.movies.app.popular_movies.loaders.ContentLoader;
import dev.learn.movies.app.popular_movies.loaders.NetworkLoader;
import dev.learn.movies.app.popular_movies.util.DisplayUtils;
import dev.learn.movies.app.popular_movies.util.HTTPHelper;
import dev.learn.movies.app.popular_movies.util.VideoGridDialog;

import static dev.learn.movies.app.popular_movies.data.DataContract.MOVIES;
import static dev.learn.movies.app.popular_movies.data.DataContract.MediaEntry.COLUMN_BACKDROP_PATH;
import static dev.learn.movies.app.popular_movies.data.DataContract.MediaEntry.COLUMN_GENRES;
import static dev.learn.movies.app.popular_movies.data.DataContract.MediaEntry.COLUMN_IS_BOOKMARKED;
import static dev.learn.movies.app.popular_movies.data.DataContract.MediaEntry.COLUMN_IS_FAVORED;
import static dev.learn.movies.app.popular_movies.data.DataContract.MediaEntry.COLUMN_MEDIA_ID;
import static dev.learn.movies.app.popular_movies.data.DataContract.MediaEntry.COLUMN_OVERVIEW;
import static dev.learn.movies.app.popular_movies.data.DataContract.MediaEntry.COLUMN_POSTER_PATH;
import static dev.learn.movies.app.popular_movies.data.DataContract.MediaEntry.COLUMN_RELEASE_DATE;
import static dev.learn.movies.app.popular_movies.data.DataContract.MediaEntry.COLUMN_RUNTIME;
import static dev.learn.movies.app.popular_movies.data.DataContract.MediaEntry.COLUMN_STATUS;
import static dev.learn.movies.app.popular_movies.data.DataContract.MediaEntry.COLUMN_TAGLINE;
import static dev.learn.movies.app.popular_movies.data.DataContract.MediaEntry.COLUMN_TITLE;
import static dev.learn.movies.app.popular_movies.data.DataContract.MediaEntry.COLUMN_VOTE_AVG;
import static dev.learn.movies.app.popular_movies.data.DataContract.MediaEntry.COLUMN_VOTE_COUNT;
import static dev.learn.movies.app.popular_movies.loaders.ContentLoader.URI_EXTRA;
import static dev.learn.movies.app.popular_movies.util.AppConstants.ACTIVITY_DETAIL_LAZY_LOAD_DELAY_IN_MS;
import static dev.learn.movies.app.popular_movies.util.AppConstants.ADDITIONAL_INFO_ACTIVITY_FRAGMENT_TYPE_REVIEWS;
import static dev.learn.movies.app.popular_movies.util.AppConstants.LOCAL_MOVIE_DETAILS_LOADER_ID;
import static dev.learn.movies.app.popular_movies.util.AppConstants.MOVIE_CAST_LOADER_ID;
import static dev.learn.movies.app.popular_movies.util.AppConstants.MOVIE_DETAILS_LOADER_ID;
import static dev.learn.movies.app.popular_movies.util.AppConstants.MOVIE_ID;
import static dev.learn.movies.app.popular_movies.util.AppConstants.MOVIE_NAME;
import static dev.learn.movies.app.popular_movies.util.AppConstants.MOVIE_SIMILAR_LOADER_ID;
import static dev.learn.movies.app.popular_movies.util.AppConstants.MOVIE_TRAILERS_LOADER_ID;
import static dev.learn.movies.app.popular_movies.util.AppConstants.RESOURCE_ID;
import static dev.learn.movies.app.popular_movies.util.AppConstants.RESOURCE_TITLE;
import static dev.learn.movies.app.popular_movies.util.AppConstants.RESOURCE_TYPE;

/**
 * Created by sudharti on 11/12/17.
 */

public class MovieDetailsFragment extends Fragment implements DetailActivity.OnFavBtnClickListener,
        ContentLoader.ContentLoaderCallback, NetworkLoader.NetworkLoaderCallback,
        View.OnClickListener, OnItemClickHandler {

    private static final String MOVIE_DETAILS = "movie_details";
    private static final String MOVIE_SIMILAR = "movie_similar";
    private static final String MOVIE_CAST = "movie_cast";

    private Context mContext;
    private MovieDetailCallbacks mCallbacks;

    private final Gson gson = new Gson();
    private long mMovieId = 0L;
    private String mMovieName;

    private NetworkLoader mNetworkLoader;
    private ContentLoader mContentLoader;

    private RecyclerView.LayoutManager mSimilarLayoutManager;
    private RecyclerView.LayoutManager mFilmCastLayoutManager;

    private List<Movie> mSimilarList;
    private List<Cast> mCastList;

    private FilmStripAdapter mSimilarAdapter;
    private FilmCastAdapter mFilmCastAdapter;

    private MovieDetail mMovieDetail;

    private FragmentMovieDetailsBinding mBinding;

    private VideoGridDialog mVideoGridDialog;

    private MenuItem mBookmarkMenuItem;

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

        mSimilarAdapter = new FilmStripAdapter(this);
        mFilmCastAdapter = new FilmCastAdapter(this);

        mSimilarLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
        mFilmCastLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);

        mNetworkLoader = new NetworkLoader(mContext, this);
        mContentLoader = new ContentLoader(mContext, this);
        setHasOptionsMenu(true);

        mVideoGridDialog = VideoGridDialog.with(mContext);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_movie_details, container, false);
        View view = mBinding.getRoot();

        mBinding.layoutMovieInfo.layoutRating.setOnClickListener(this);

        mBinding.layoutMovieSimilar.rvSimilar.setLayoutManager(mSimilarLayoutManager);
        mBinding.layoutMovieSimilar.rvSimilar.setAdapter(mSimilarAdapter);
        mBinding.layoutMovieSimilar.rvSimilar.setNestedScrollingEnabled(false);

        mBinding.layoutCast.rvCast.setLayoutManager(mFilmCastLayoutManager);
        mBinding.layoutCast.rvCast.setAdapter(mFilmCastAdapter);
        mBinding.layoutCast.rvCast.setNestedScrollingEnabled(false);

        mBinding.layoutMovieSimilar.tvSimilarTitle.setText(getString(R.string.similar_movies));

        new Handler().post(new Runnable() {
            @Override
            public void run() {
                NestedScrollView nestedScrollView = getActivity().findViewById(R.id.layout_outlet);
                nestedScrollView.scrollTo(0, 0);
                AppBarLayout appBarLayout = getActivity().findViewById(R.id.app_bar_layout);
                appBarLayout.setExpanded(true);
            }
        });

        adjustPosterSize();

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
            mSimilarList = savedInstanceState.getParcelableArrayList(MOVIE_SIMILAR);
            mCastList = savedInstanceState.getParcelableArrayList(MOVIE_CAST);

            updateMovieDetailsUI();
            updateMovieCastUI();
            updateSimilarMoviesUI();
        }

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mCallbacks = (MovieDetailCallbacks) getActivity();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        mBookmarkMenuItem = menu.findItem(R.id.action_bookmark);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (!HTTPHelper.isNetworkEnabled(mContext)) {
            Toast.makeText(mContext, getResources().getString(R.string.no_network_connection_error_message), Toast.LENGTH_SHORT).show();
            return false;
        }

        switch (item.getItemId()) {
            case R.id.action_bookmark:
                Uri uri = DataContract.BOOKMARKS_CONTENT_URI
                        .buildUpon()
                        .appendPath(MOVIES)
                        .appendPath(String.valueOf(mMovieId))
                        .build();
                if (mMovieDetail.isBookmarked()) {
                    getActivity().getContentResolver().delete(uri, null, null);
                } else {
                    ContentValues cv = toContentValues(mMovieDetail);
                    if (cv != null) {
                        getActivity().getContentResolver().insert(uri, cv);
                    }
                }
                mMovieDetail.setBookmarked(!mMovieDetail.isBookmarked());
                updateBookmarkBtn(mMovieDetail.isBookmarked());
                showBookmarkToast();
                return true;
            case R.id.action_share:
                DisplayUtils.shareURL(getActivity(), mMovieName,
                        HTTPHelper.buildTMDBMovieURL(String.valueOf(mMovieId)));
                return true;
            case R.id.action_watch_trailer:
                mVideoGridDialog
                        .setTitle(getString(R.string.action_watch_trailer))
                        .setCancelable(true)
                        .setOnVideoSelectedListener(new VideoGridDialog.OnVideoSelectedListener() {
                            @Override
                            public void onVideoSelected(Video video) {
                                if (video != null && video.getKey() != null) {
                                    DisplayUtils.openYoutube(mContext, video.getKey());
                                }
                            }
                        })
                        .setOnShowListener(new DialogInterface.OnShowListener() {
                            @Override
                            public void onShow(DialogInterface dialog) {
                                loadMovieTrailersFromNetwork();
                            }
                        })
                        .build();
                return true;
            case R.id.action_imdb:
                DisplayUtils.openIMDB(mContext, mMovieDetail.getImdbId());
                return true;
            case R.id.action_user_reviews:
                goToReviews();
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
        outState.putParcelableArrayList(MOVIE_SIMILAR, (ArrayList<? extends Parcelable>) mSimilarList);
        outState.putParcelableArrayList(MOVIE_CAST, (ArrayList<? extends Parcelable>) mCastList);
    }

    /**
     * Implement onLoadFinished(Loader,Cursor) from ContentLoader.ContentLoaderCallback
     *
     * @param loader Loader instance
     * @param cursor Cursor
     */
    @Override
    public void onLoadFinished(Loader loader, Cursor cursor) {
        if (cursor == null || !cursor.moveToFirst()) {
            if (HTTPHelper.isNetworkEnabled(mContext)) {
                loadMovieDetailsFromNetwork();
            } else {
                DisplayUtils.setNoNetworkConnectionMessage(mContext, mBinding.tvErrorMessageDisplay);
                showErrorMessage();
            }
        } else {
            switch (loader.getId()) {
                case LOCAL_MOVIE_DETAILS_LOADER_ID:
                    mMovieDetail = fromCursor(cursor);
                    updateMovieDetailsUI();
                    break;
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
                mMovieDetail = (s == null) ? null : gson.fromJson(s, MovieDetail.class);
                updateMovieDetailsUI();
                break;
            case MOVIE_TRAILERS_LOADER_ID:
                VideosResult videosResult = (s == null) ? null : gson.fromJson(s, VideosResult.class);
                if (videosResult != null && videosResult.getVideos() != null && !videosResult.getVideos().isEmpty()) {
                    mVideoGridDialog
                            .setVideos(videosResult.getVideos())
                            .success();
                } else {
                    mVideoGridDialog.error();
                }
                break;
            case MOVIE_SIMILAR_LOADER_ID:
                MoviesResult similarMoviesResult = (s == null) ? null : gson.fromJson(s, MoviesResult.class);
                if (similarMoviesResult != null) {
                    mSimilarList = similarMoviesResult.getResults();
                }
                updateSimilarMoviesUI();
                break;
            case MOVIE_CAST_LOADER_ID:
                CastsResult castsResult = (s == null) ? null : gson.fromJson(s, CastsResult.class);
                if (castsResult != null) {
                    mCastList = castsResult.getCast();
                }
                updateMovieCastUI();
                break;
        }
    }

    @Override
    public void onFavBtnClicked(View v) {
        Uri uri = DataContract.FAVORITES_CONTENT_URI
                .buildUpon()
                .appendPath(MOVIES)
                .appendPath(String.valueOf(mMovieId))
                .build();
        if (mMovieDetail.isFavored()) {
            getActivity().getContentResolver().delete(uri, null, null);
        } else {
            ContentValues cv = toContentValues(mMovieDetail);
            if (cv != null) {
                getActivity().getContentResolver().insert(uri, cv);
            }
        }

        mMovieDetail.setFavored(!mMovieDetail.isFavored());
        mCallbacks.showFavToast(mMovieDetail.isFavored());
        mCallbacks.updateFavBtn(mMovieDetail.isFavored());
    }


    @Override
    public void onClick(View v) {
        goToReviews();
    }

    @Override
    public void onItemClicked(ViewGroup parent, View view, int position) {
        Movie movie = null;
        switch (parent.getId()) {
            case R.id.rv_similar:
                movie = mSimilarList.get(position);
                break;
        }

        if (movie != null) {
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.layout_outlet, MovieDetailsFragment.newInstance(movie.getId(), movie.getTitle()))
                    .commit();
        }
    }

    /**
     * Loads movie details from local ContentProvider
     */
    private void loadMovieDetailsFromDatabase() {
        Bundle args = new Bundle();
        Uri uri = DataContract.MEDIA_CONTENT_URI
                .buildUpon()
                .appendPath(String.valueOf(mMovieId))
                .build();
        args.putParcelable(URI_EXTRA, uri);
        getActivity().getSupportLoaderManager().restartLoader(LOCAL_MOVIE_DETAILS_LOADER_ID, args, mContentLoader);
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
                    loadSimilarMoviesFromNetwork();
                    loadMovieCastFromNetwork();
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

    private void loadSimilarMoviesFromNetwork() {
        URL url = HTTPHelper.buildSimilarMoviesURL(String.valueOf(mMovieId));
        Bundle args = new Bundle();
        args.putSerializable(NetworkLoader.URL_EXTRA, url);
        getActivity().getSupportLoaderManager().restartLoader(MOVIE_SIMILAR_LOADER_ID, args, mNetworkLoader);
    }

    private void loadMovieCastFromNetwork() {
        URL url = HTTPHelper.buildMovieCastURL(String.valueOf(mMovieId));
        Bundle args = new Bundle();
        args.putSerializable(NetworkLoader.URL_EXTRA, url);
        getActivity().getSupportLoaderManager().restartLoader(MOVIE_CAST_LOADER_ID, args, mNetworkLoader);
    }

    /**
     * Formats and sets the movie details into appropriate views
     */
    private void updateMovieDetailsUI() {
        if (mMovieDetail == null) {
            showErrorMessage();
            return;
        }

        int year = DisplayUtils.getYear(mMovieDetail.getReleaseDate());
        double voteAverage = mMovieDetail.getVoteAverage();
        String backdropURL = mMovieDetail.getBackdropPath();
        String posterURL = mMovieDetail.getPosterPath();
        String title = mMovieDetail.getTitle();
        String runningTime = mMovieDetail.getRuntime() + " min";
        String status = mMovieDetail.getStatus();
        String rating = String.valueOf(voteAverage);
        String voteCount = "(" + mMovieDetail.getVoteCount() + ")";
        String tagline = mMovieDetail.getTagline();
        String moviePlot = mMovieDetail.getOverview();
        long budget = mMovieDetail.getBudget();
        long revenue = mMovieDetail.getRevenue();
        List<Genre> genres = mMovieDetail.getGenres();

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

        mCallbacks.updateFavBtn(mMovieDetail.isFavored());
        showMovieDetails();
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                updateBookmarkBtn(mMovieDetail.isBookmarked());
            }
        });
    }

    private void updateSimilarMoviesUI() {
        if (mSimilarList != null && !mSimilarList.isEmpty()) {
            mSimilarAdapter.setFilmStripList(mSimilarList);
            showSimilar();
        } else {
            hideSimilar();
        }
    }

    private void updateMovieCastUI() {
        if (mCastList != null && !mCastList.isEmpty()) {
            mFilmCastAdapter.setFilmCastList(mCastList);
            showCast();
        } else {
            hideCast();
        }
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
     * Shows MovieDetailLayout, Hides ProgressBar and ErrorMessage
     */
    private void showSimilar() {
        mBinding.layoutMovieSimilar.rvSimilar.setVisibility(View.VISIBLE);
        mBinding.layoutMovieSimilar.pbSimilar.setVisibility(View.GONE);
    }

    /**
     * Shows ErrorMessage, Hides ProgressBar and MovieDetailLayout
     */
    private void hideSimilar() {
        mBinding.layoutMovieSimilar.getRoot().setVisibility(View.GONE);
    }

    /**
     * Shows MovieDetailLayout, Hides ProgressBar and ErrorMessage
     */
    private void showCast() {
        mBinding.layoutCast.rvCast.setVisibility(View.VISIBLE);
        mBinding.layoutCast.pbCast.setVisibility(View.GONE);
    }

    /**
     * Shows ErrorMessage, Hides ProgressBar and MovieDetailLayout
     */
    private void hideCast() {
        mBinding.layoutCast.getRoot().setVisibility(View.GONE);
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
            cv.put(COLUMN_MEDIA_ID, movieDetail.getId());
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
            cv.put(COLUMN_STATUS, movieDetail.getStatus());
            cv.put(COLUMN_IS_FAVORED, movieDetail.isFavored() ? 1 : 0);
            cv.put(COLUMN_IS_BOOKMARKED, movieDetail.isBookmarked() ? 1 : 0);
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
            movieDetail.setId(cursor.getLong(cursor.getColumnIndex(COLUMN_MEDIA_ID)));
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
            movieDetail.setStatus(cursor.getString(cursor.getColumnIndex(COLUMN_STATUS)));
            movieDetail.setFavored(cursor.getInt(cursor.getColumnIndex(COLUMN_IS_FAVORED)) == 1);
            movieDetail.setBookmarked(cursor.getInt(cursor.getColumnIndex(COLUMN_IS_BOOKMARKED)) == 1);
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

    private void goToReviews() {
        if (HTTPHelper.isNetworkEnabled(mContext)) {
            Intent additionalIntent = new Intent(mContext, AdditionalInfoActivity.class);

            Bundle extras = new Bundle();
            extras.putLong(RESOURCE_ID, mMovieId);
            extras.putString(RESOURCE_TITLE, mMovieName);
            extras.putString(RESOURCE_TYPE, ADDITIONAL_INFO_ACTIVITY_FRAGMENT_TYPE_REVIEWS);
            additionalIntent.putExtras(extras);

            startActivity(additionalIntent);
        } else {
            Toast.makeText(mContext, getResources().getString(R.string.no_network_connection_error_message), Toast.LENGTH_SHORT).show();
        }
    }

    private void updateBookmarkBtn(boolean isBookmarked) {
        if (mBookmarkMenuItem == null) return;
        if (isBookmarked) {
            mBookmarkMenuItem.setIcon(ContextCompat.getDrawable(mContext, R.drawable.ic_bookmark_white_24dp));
        } else {
            mBookmarkMenuItem.setIcon(ContextCompat.getDrawable(mContext, R.drawable.ic_bookmark_outline_white_24dp));
        }
    }

    private void showBookmarkToast() {
        if (mMovieDetail.isBookmarked()) {
            Toast.makeText(mContext, getString(R.string.added_to_bookmarks), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(mContext, getString(R.string.removed_from_bookmarks), Toast.LENGTH_SHORT).show();
        }
    }
}

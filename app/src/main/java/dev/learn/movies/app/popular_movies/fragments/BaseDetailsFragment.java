package dev.learn.movies.app.popular_movies.fragments;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import dev.learn.movies.app.popular_movies.R;
import dev.learn.movies.app.popular_movies.activities.DetailActivity;
import dev.learn.movies.app.popular_movies.activities.DetailActivityCallbacks;
import dev.learn.movies.app.popular_movies.adapters.FilmCastAdapter;
import dev.learn.movies.app.popular_movies.adapters.FilmStripAdapter;
import dev.learn.movies.app.popular_movies.adapters.OnItemClickHandler;
import dev.learn.movies.app.popular_movies.common.Genre;
import dev.learn.movies.app.popular_movies.common.Media;
import dev.learn.movies.app.popular_movies.common.MediaDetail;
import dev.learn.movies.app.popular_movies.common.Video;
import dev.learn.movies.app.popular_movies.common.cast.Cast;
import dev.learn.movies.app.popular_movies.common.movies.MovieDetail;
import dev.learn.movies.app.popular_movies.common.tv_show.TVShowDetail;
import dev.learn.movies.app.popular_movies.data.DataContract;
import dev.learn.movies.app.popular_movies.loaders.ContentLoader;
import dev.learn.movies.app.popular_movies.loaders.NetworkLoader;
import dev.learn.movies.app.popular_movies.util.DisplayUtils;
import dev.learn.movies.app.popular_movies.util.HTTPHelper;
import dev.learn.movies.app.popular_movies.util.LoadingContentUtil;
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
import static dev.learn.movies.app.popular_movies.data.DataContract.TV_SHOWS;
import static dev.learn.movies.app.popular_movies.loaders.ContentLoader.URI_EXTRA;
import static dev.learn.movies.app.popular_movies.util.AppConstants.ACTIVITY_DETAIL_LAZY_LOAD_DELAY_IN_MS;
import static dev.learn.movies.app.popular_movies.util.AppConstants.LOCAL_MOVIE_DETAILS_LOADER_ID;
import static dev.learn.movies.app.popular_movies.util.AppConstants.LOCAL_TV_SHOW_DETAILS_LOADER_ID;
import static dev.learn.movies.app.popular_movies.util.AppConstants.MOVIE_CAST_LOADER_ID;
import static dev.learn.movies.app.popular_movies.util.AppConstants.MOVIE_SIMILAR_LOADER_ID;
import static dev.learn.movies.app.popular_movies.util.AppConstants.MOVIE_TRAILERS_LOADER_ID;
import static dev.learn.movies.app.popular_movies.util.AppConstants.RESOURCE_ID;
import static dev.learn.movies.app.popular_movies.util.AppConstants.RESOURCE_TITLE;
import static dev.learn.movies.app.popular_movies.util.AppConstants.TV_SHOWS_CAST_LOADER_ID;
import static dev.learn.movies.app.popular_movies.util.AppConstants.TV_SHOWS_SIMILAR_LOADER_ID;

/**
 * Created by sudhar on 12/9/17.
 */

public abstract class BaseDetailsFragment extends Fragment implements
        DetailActivity.OnFavBtnClickListener,
        OnItemClickHandler,
        NetworkLoader.NetworkLoaderCallback,
        ContentLoader.ContentLoaderCallback {

    protected static final String DETAILS = "details";
    protected static final String SIMILAR = "similar";
    protected static final String CAST = "cast";

    protected Context mContext;
    protected DetailActivityCallbacks mCallbacks;

    protected final Gson gson = new Gson();
    protected long mResourceId = 0L;
    protected String mResourceTitle;

    protected NetworkLoader mNetworkLoader;
    protected ContentLoader mContentLoader;

    protected RecyclerView.LayoutManager mSimilarLayoutManager;
    protected RecyclerView.LayoutManager mFilmCastLayoutManager;

    protected MovieDetail mMovieDetail;
    protected TVShowDetail mTVShowDetail;
    protected List<? extends Media> mSimilarList;
    protected List<Cast> mCastList;

    protected FilmStripAdapter mSimilarAdapter;
    protected FilmCastAdapter mFilmCastAdapter;

    protected VideoGridDialog mVideoGridDialog;
    protected MenuItem mBookmarkMenuItem;

    protected LoadingContentUtil mContentLoadingUtil;
    protected LoadingContentUtil mSimilarLoadingUtil;
    protected LoadingContentUtil mCastLoadingUtil;

    protected boolean isMovieDetailFragment;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getContext();
        mFilmCastAdapter = new FilmCastAdapter(this);
        mFilmCastLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
        mNetworkLoader = new NetworkLoader(mContext, this);
        mContentLoader = new ContentLoader(mContext, this);
        mSimilarAdapter = new FilmStripAdapter(this);
        mSimilarLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);

        setHasOptionsMenu(true);
        mVideoGridDialog = VideoGridDialog.with(mContext);
        mContentLoadingUtil = LoadingContentUtil.with(mContext);
        mSimilarLoadingUtil = LoadingContentUtil.with(mContext);
        mCastLoadingUtil = LoadingContentUtil.with(mContext);

        isMovieDetailFragment = (this instanceof MovieDetailsFragment);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mCallbacks = (DetailActivityCallbacks) getActivity();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        mBookmarkMenuItem = menu.findItem(R.id.action_bookmark);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState == null) {
            mResourceId = getArguments().getLong(RESOURCE_ID, 0);
            mResourceTitle = getArguments().getString(RESOURCE_TITLE, "");
            final int localLoaderId = isMovieDetailFragment ? LOCAL_MOVIE_DETAILS_LOADER_ID : LOCAL_TV_SHOW_DETAILS_LOADER_ID;
            final int similarLoaderId = isMovieDetailFragment ? MOVIE_SIMILAR_LOADER_ID : TV_SHOWS_SIMILAR_LOADER_ID;
            final int castLoaderId = isMovieDetailFragment ? MOVIE_CAST_LOADER_ID : TV_SHOWS_CAST_LOADER_ID;

            if (mResourceId != 0) {
                Uri uri = DataContract.MEDIA_CONTENT_URI
                        .buildUpon()
                        .appendPath(String.valueOf(mResourceId))
                        .build();
                loadFromDatabase(uri, localLoaderId);
                lazyLoadAdditionalInfoFromNetwork(new Runnable() {
                    @Override
                    public void run() {
                        loadFromNetwork(HTTPHelper.buildSimilarMoviesURL(String.valueOf(mResourceId)), similarLoaderId);
                        loadFromNetwork(HTTPHelper.buildMovieCastURL(String.valueOf(mResourceId)), castLoaderId);
                    }
                });
            } else {
                mContentLoadingUtil.error();
            }
        } else {
            mResourceId = savedInstanceState.getLong(RESOURCE_ID);
            mResourceTitle = savedInstanceState.getString(RESOURCE_TITLE);
            mMovieDetail = savedInstanceState.getParcelable(DETAILS);
            mSimilarList = savedInstanceState.getParcelableArrayList(SIMILAR);
            mCastList = savedInstanceState.getParcelableArrayList(CAST);

            updateContent();
            updateCasts();
            updateSimilar();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(RESOURCE_ID, mResourceId);
        outState.putString(RESOURCE_TITLE, mResourceTitle);
        outState.putParcelable(DETAILS, mMovieDetail);
        outState.putParcelableArrayList(SIMILAR, (ArrayList<? extends Parcelable>) mSimilarList);
        outState.putParcelableArrayList(CAST, (ArrayList<? extends Parcelable>) mCastList);
    }

    @Override
    public void onFavBtnClicked(View v) {
        MediaDetail mediaDetail = isMovieDetailFragment ? mMovieDetail : mTVShowDetail;
        String type = isMovieDetailFragment ? MOVIES : TV_SHOWS;
        if (mediaDetail == null || type == null) return;

        Uri uri = DataContract.FAVORITES_CONTENT_URI
                .buildUpon()
                .appendPath(type)
                .appendPath(String.valueOf(mResourceId))
                .build();
        if (mMovieDetail.isFavored()) {
            getActivity().getContentResolver().delete(uri, null, null);
        } else {
            ContentValues cv = toContentValues(mediaDetail);
            if (cv != null) {
                getActivity().getContentResolver().insert(uri, cv);
            }
        }

        mediaDetail.setFavored(!mediaDetail.isFavored());
        mCallbacks.showFavToast(mediaDetail.isFavored());
        mCallbacks.updateFavBtn(mediaDetail.isFavored());
    }

    protected void adjustPosterSize(View poster) {
        int[] screen = DisplayUtils.getScreenMetrics(getActivity());
        int min = Math.min(screen[0], screen[1]);
        int max = Math.max(screen[0], screen[1]);
        poster.setLayoutParams(new ConstraintLayout.LayoutParams((min / 3), (int) (max / 3.15)));
    }

    protected void updateBookmarkBtn(boolean isBookmarked) {
        if (mBookmarkMenuItem == null) return;
        if (isBookmarked) {
            mBookmarkMenuItem.setIcon(ContextCompat.getDrawable(mContext, R.drawable.ic_bookmark_white_24dp));
        } else {
            mBookmarkMenuItem.setIcon(ContextCompat.getDrawable(mContext, R.drawable.ic_bookmark_outline_white_24dp));
        }
    }

    protected void showBookmarkToast(boolean isBookmarked) {
        if (isBookmarked) {
            Toast.makeText(mContext, getString(R.string.added_to_bookmarks), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(mContext, getString(R.string.removed_from_bookmarks), Toast.LENGTH_SHORT).show();
        }
    }

    protected void buildVideoGrid(final URL videosURL, final int loaderId) {
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
                        loadFromNetwork(videosURL, loaderId);
                    }
                })
                .build();
    }

    protected void loadFromNetwork(final URL url, final int loaderId) {
        Bundle args = new Bundle();
        args.putSerializable(NetworkLoader.URL_EXTRA, url);
        getActivity().getSupportLoaderManager().restartLoader(loaderId, args, mNetworkLoader);
    }

    protected void loadFromDatabase(final Uri uri, final int loaderId) {
        Bundle args = new Bundle();
        args.putParcelable(URI_EXTRA, uri);
        getActivity().getSupportLoaderManager().restartLoader(loaderId, args, mContentLoader);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (!HTTPHelper.isNetworkEnabled(mContext)) {
            Toast.makeText(mContext, getResources().getString(R.string.no_network_connection_error_message), Toast.LENGTH_SHORT).show();
            return false;
        }

        String type = (this instanceof MovieDetailsFragment) ? MOVIES : TV_SHOWS;

        switch (item.getItemId()) {
            case R.id.action_bookmark:
                Uri uri = DataContract.BOOKMARKS_CONTENT_URI
                        .buildUpon()
                        .appendPath(type)
                        .appendPath(String.valueOf(mResourceId))
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
                showBookmarkToast(mMovieDetail.isBookmarked());
                return true;
            case R.id.action_share:
                DisplayUtils.shareURL(getActivity(), mResourceTitle,
                        HTTPHelper.buildTMDBMovieURL(String.valueOf(mResourceId)));
                return true;
            case R.id.action_watch_trailer:
                buildVideoGrid(HTTPHelper.buildMovieTrailersURL(String.valueOf(mResourceId)), MOVIE_TRAILERS_LOADER_ID);
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    abstract protected void updateContent();

    protected void updateCasts() {
        if (mCastList != null && !mCastList.isEmpty()) {
            mFilmCastAdapter.setFilmCastList(mCastList);
            mCastLoadingUtil.success();
        } else {
            mCastLoadingUtil.error();
        }
    }

    protected void updateSimilar() {
        if (mSimilarList != null && !mSimilarList.isEmpty()) {
            mSimilarAdapter.setFilmStripList(mSimilarList);
            mSimilarLoadingUtil.success();
        } else {
            mSimilarLoadingUtil.error();
        }
    }

    protected void lazyLoadAdditionalInfoFromNetwork(Runnable runnable) {
        if (HTTPHelper.isNetworkEnabled(mContext)) {
            new Handler().postDelayed(runnable, ACTIVITY_DETAIL_LAZY_LOAD_DELAY_IN_MS);
        }
    }

    /**
     * Convert MovieDetail into ContentValues
     *
     * @param mediaDetail MediaDetail object
     * @return contentValues
     */
    protected ContentValues toContentValues(MediaDetail mediaDetail) {
        ContentValues cv = null;
        if (mediaDetail != null) {
            cv = new ContentValues();
            cv.put(COLUMN_MEDIA_ID, mediaDetail.getId());
            cv.put(COLUMN_OVERVIEW, mediaDetail.getOverview());
            cv.put(COLUMN_POSTER_PATH, mediaDetail.getPosterPath());
            cv.put(COLUMN_BACKDROP_PATH, mediaDetail.getBackdropPath());
            cv.put(COLUMN_VOTE_AVG, mediaDetail.getVoteAverage());
            cv.put(COLUMN_VOTE_COUNT, mediaDetail.getVoteCount());
            StringBuilder builder = new StringBuilder();
            List<Genre> genreList = mediaDetail.getGenres();
            if (genreList != null && !genreList.isEmpty()) {
                for (Genre genre : genreList) {
                    builder.append(genre.getName()).append(",");
                }
            }
            cv.put(COLUMN_GENRES, builder.toString());
            cv.put(COLUMN_STATUS, mediaDetail.getStatus());
            cv.put(COLUMN_IS_FAVORED, mediaDetail.isFavored() ? 1 : 0);
            cv.put(COLUMN_IS_BOOKMARKED, mediaDetail.isBookmarked() ? 1 : 0);

            if (mediaDetail instanceof MovieDetail) {
                MovieDetail movieDetail = (MovieDetail) mediaDetail;
                cv.put(COLUMN_TITLE, movieDetail.getTitle());
                cv.put(COLUMN_TAGLINE, movieDetail.getTagline());
                cv.put(COLUMN_RELEASE_DATE, movieDetail.getReleaseDate());
                cv.put(COLUMN_RUNTIME, movieDetail.getRuntime());
            } else if (mediaDetail instanceof TVShowDetail) {
                TVShowDetail tvShowDetail = (TVShowDetail) mediaDetail;
                //TODO: Update here
            }
        }
        return cv;
    }

    /**
     * Create a MovieDetail object from cursor
     *
     * @param cursor Cursor
     * @return movieDetail object
     */
    protected MediaDetail fromCursor(Cursor cursor) {
        MediaDetail mediaDetail = (this instanceof MovieDetailsFragment) ? new MovieDetail() : new TVShowDetail();
        if (cursor.moveToFirst()) {
            mediaDetail.setId(cursor.getLong(cursor.getColumnIndex(COLUMN_MEDIA_ID)));
            mediaDetail.setOverview(cursor.getString(cursor.getColumnIndex(COLUMN_OVERVIEW)));
            mediaDetail.setPosterPath(cursor.getString(cursor.getColumnIndex(COLUMN_POSTER_PATH)));
            mediaDetail.setBackdropPath(cursor.getString(cursor.getColumnIndex(COLUMN_BACKDROP_PATH)));
            mediaDetail.setVoteAverage(cursor.getDouble(cursor.getColumnIndex(COLUMN_VOTE_AVG)));
            mediaDetail.setVoteCount(cursor.getLong(cursor.getColumnIndex(COLUMN_VOTE_COUNT)));
            String genresStr = cursor.getString(cursor.getColumnIndex(COLUMN_GENRES));
            List<Genre> genreList = new ArrayList<>();
            String[] genresArr = genresStr.split(",");
            for (String genre : genresArr) {
                genreList.add(new Genre(0, genre));
            }
            mediaDetail.setGenres(genreList);
            mediaDetail.setStatus(cursor.getString(cursor.getColumnIndex(COLUMN_STATUS)));
            mediaDetail.setFavored(cursor.getInt(cursor.getColumnIndex(COLUMN_IS_FAVORED)) == 1);
            mediaDetail.setBookmarked(cursor.getInt(cursor.getColumnIndex(COLUMN_IS_BOOKMARKED)) == 1);

            if (this instanceof MovieDetailsFragment) {
                ((MovieDetail) mediaDetail).setTitle(cursor.getString(cursor.getColumnIndex(COLUMN_TITLE)));
                ((MovieDetail) mediaDetail).setTagline(cursor.getString(cursor.getColumnIndex(COLUMN_TAGLINE)));
                ((MovieDetail) mediaDetail).setReleaseDate(cursor.getString(cursor.getColumnIndex(COLUMN_RELEASE_DATE)));
                ((MovieDetail) mediaDetail).setRuntime(cursor.getLong(cursor.getColumnIndex(COLUMN_RUNTIME)));
            } else {

            }
        }
        return mediaDetail;
    }
}

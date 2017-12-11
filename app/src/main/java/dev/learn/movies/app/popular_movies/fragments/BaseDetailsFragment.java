package dev.learn.movies.app.popular_movies.fragments;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
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
import static dev.learn.movies.app.popular_movies.data.DataContract.TV_SHOWS;
import static dev.learn.movies.app.popular_movies.loaders.ContentLoader.URI_EXTRA;
import static dev.learn.movies.app.popular_movies.util.AppConstants.ACTIVITY_DETAIL_LAZY_LOAD_DELAY_IN_MS;
import static dev.learn.movies.app.popular_movies.util.AppConstants.MOVIE_TRAILERS_LOADER_ID;
import static dev.learn.movies.app.popular_movies.util.AppConstants.RESOURCE_ID;
import static dev.learn.movies.app.popular_movies.util.AppConstants.RESOURCE_TITLE;
import static dev.learn.movies.app.popular_movies.util.AppConstants.TV_SHOWS_TRAILERS_LOADER_ID;

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

    private NetworkLoader mNetworkLoader;
    private ContentLoader mContentLoader;

    private boolean isMovieDetailFragment;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getContext();

        mFilmCastAdapter = new FilmCastAdapter(this);
        mFilmCastLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);

        mSimilarAdapter = new FilmStripAdapter(this);
        mSimilarLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);

        mNetworkLoader = new NetworkLoader(mContext, this);
        mContentLoader = new ContentLoader(mContext, this);

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
    public boolean onOptionsItemSelected(MenuItem item) {
        if (!HTTPHelper.isNetworkEnabled(mContext)) {
            Toast.makeText(mContext, getResources().getString(R.string.no_network_connection_error_message), Toast.LENGTH_SHORT).show();
            return false;
        }

        switch (item.getItemId()) {
            case R.id.action_bookmark:
                String type = (this instanceof MovieDetailsFragment) ? MOVIES : TV_SHOWS;
                Uri uri = DataContract.BOOKMARKS_CONTENT_URI
                        .buildUpon()
                        .appendPath(type)
                        .appendPath(String.valueOf(mResourceId))
                        .build();
                MediaDetail mediaDetail = isMovieDetailFragment ? mMovieDetail : mTVShowDetail;
                if (mediaDetail == null || type == null) break;

                if (mediaDetail.isBookmarked()) {
                    getActivity().getContentResolver().delete(uri, null, null);
                } else {
                    ContentValues cv = isMovieDetailFragment ? MovieDetail.toContentValues(mMovieDetail) : TVShowDetail.toContentValues(mTVShowDetail);
                    if (cv != null) {
                        getActivity().getContentResolver().insert(uri, cv);
                    }
                }
                mediaDetail.setBookmarked(!mediaDetail.isBookmarked());
                updateBookmarkBtn(mediaDetail.isBookmarked());
                showBookmarkToast(mediaDetail.isBookmarked());
                return true;
            case R.id.action_share:
                if(HTTPHelper.isNetworkEnabled(mContext)) {
                    URL shareURL = isMovieDetailFragment ? HTTPHelper.buildTMDBMovieURL(String.valueOf(mResourceId)) :
                            HTTPHelper.buildTMDBTVShowURL(String.valueOf(mResourceId));
                    DisplayUtils.shareURL(getActivity(), mResourceTitle, shareURL);
                } else {

                }
                return true;
            case R.id.action_watch_trailer:
                URL videosURL = isMovieDetailFragment ? HTTPHelper.buildMovieTrailersURL(String.valueOf(mResourceId)) :
                        HTTPHelper.buildTVShowTrailersURL(String.valueOf(mResourceId));
                int loaderId = isMovieDetailFragment ? MOVIE_TRAILERS_LOADER_ID : TV_SHOWS_TRAILERS_LOADER_ID;
                buildVideoGrid(videosURL, loaderId);
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(RESOURCE_ID, mResourceId);
        outState.putString(RESOURCE_TITLE, mResourceTitle);
        if (isMovieDetailFragment) {
            outState.putParcelable(DETAILS, mMovieDetail);
        } else {
            outState.putParcelable(DETAILS, mTVShowDetail);
        }
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
        if (mediaDetail.isFavored()) {
            getActivity().getContentResolver().delete(uri, null, null);
        } else {
            ContentValues cv = isMovieDetailFragment ? MovieDetail.toContentValues(mMovieDetail) : TVShowDetail.toContentValues(mTVShowDetail);
            if (cv != null) {
                getActivity().getContentResolver().insert(uri, cv);
            }
        }

        mediaDetail.setFavored(!mediaDetail.isFavored());
        mCallbacks.showFavToast(mediaDetail.isFavored());
        mCallbacks.updateFavBtn(mediaDetail.isFavored());
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

    protected void lazyLoadAdditionalInfoFromNetwork(Runnable runnable) {
        if (HTTPHelper.isNetworkEnabled(mContext)) {
            new Handler().postDelayed(runnable, ACTIVITY_DETAIL_LAZY_LOAD_DELAY_IN_MS);
        } else {
            mCastLoadingUtil.error();
            mSimilarLoadingUtil.error();
        }
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

    protected void adjustPosterSize(View poster) {
        int[] screen = DisplayUtils.getScreenMetrics(getActivity());
        int min = Math.min(screen[0], screen[1]);
        int max = Math.max(screen[0], screen[1]);
        poster.setLayoutParams(new ConstraintLayout.LayoutParams((min / 3), (int) (max / 3.15)));
    }

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

    abstract protected void updateContent();
}

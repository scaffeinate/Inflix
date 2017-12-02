package dev.learn.movies.app.popular_movies.fragments;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import dev.learn.movies.app.popular_movies.R;
import dev.learn.movies.app.popular_movies.activities.DetailActivity;
import dev.learn.movies.app.popular_movies.adapters.FavoritesAdapter;
import dev.learn.movies.app.popular_movies.adapters.OnItemClickHandler;
import dev.learn.movies.app.popular_movies.data.DataContract;
import dev.learn.movies.app.popular_movies.databinding.FragmentMoviesBinding;
import dev.learn.movies.app.popular_movies.loaders.ContentLoader;

import static dev.learn.movies.app.popular_movies.data.DataContract.MediaEntry.COLUMN_MEDIA_ID;
import static dev.learn.movies.app.popular_movies.data.DataContract.MediaEntry.COLUMN_TITLE;
import static dev.learn.movies.app.popular_movies.util.AppConstants.BOOKMARKS;
import static dev.learn.movies.app.popular_movies.util.AppConstants.BOOKMARKS_LOADER_ID;
import static dev.learn.movies.app.popular_movies.util.AppConstants.DEFAULT_GRID_COUNT;
import static dev.learn.movies.app.popular_movies.util.AppConstants.DETAIL_ACTIVITY_FRAGMENT_TYPE_MOVIE;
import static dev.learn.movies.app.popular_movies.util.AppConstants.FAVORITES;
import static dev.learn.movies.app.popular_movies.util.AppConstants.FAVORITES_LOADER_ID;
import static dev.learn.movies.app.popular_movies.util.AppConstants.RESOURCE_ID;
import static dev.learn.movies.app.popular_movies.util.AppConstants.RESOURCE_TITLE;
import static dev.learn.movies.app.popular_movies.util.AppConstants.RESOURCE_TYPE;
import static dev.learn.movies.app.popular_movies.util.AppConstants.TABLET_GRID_COUNT;

/**
 * LocalMoviesFragment - Fetch and show favored movies from content provider
 */
public class LocalMoviesFragment extends Fragment implements ContentLoader.ContentLoaderCallback, OnItemClickHandler {

    private static final String TYPE = "type";
    private static final String SAVED_STATE = "saved_save";

    private Context mContext;
    private String mType = FAVORITES;

    private RecyclerView.LayoutManager mLayoutManager;
    private FavoritesAdapter mAdapter;
    private Cursor mCursor;
    private Parcelable mSavedState = null;

    private ContentLoader mContentLoader;
    private FragmentMoviesBinding mBinding;

    public static LocalMoviesFragment newInstance(String type) {
        LocalMoviesFragment localMoviesFragment = new LocalMoviesFragment();

        Bundle args = new Bundle();
        args.putString(TYPE, type);
        localMoviesFragment.setArguments(args);

        return localMoviesFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getContext();
        mContentLoader = new ContentLoader(mContext, this);
        boolean isTablet = getResources().getBoolean(R.bool.is_tablet);
        int mGridCount = isTablet ? TABLET_GRID_COUNT : DEFAULT_GRID_COUNT;
        mLayoutManager = new GridLayoutManager(mContext, mGridCount);

        if (savedInstanceState != null) {
            mSavedState = savedInstanceState.getParcelable(SAVED_STATE);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_movies, container, false);
        View view = mBinding.getRoot();

        if (getArguments() != null) {
            mType = getArguments().getString(TYPE, FAVORITES);
        }

        mBinding.recyclerViewMovies.setHasFixedSize(true);
        mBinding.recyclerViewMovies.setLayoutManager(mLayoutManager);

        mAdapter = new FavoritesAdapter(this);
        mBinding.recyclerViewMovies.setAdapter(mAdapter);

        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
        fetchContent();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mLayoutManager != null) {
            outState.putParcelable(SAVED_STATE, mLayoutManager.onSaveInstanceState());
        }
    }

    /**
     * Overrides onItemClicked(position) from MoviesAdapter.OnItemClickHandler
     *
     * @param position Position
     */
    @Override
    public void onItemClicked(ViewGroup parent, View view, int position) {
        if (position >= 0 && mCursor != null && position < mCursor.getCount()) {
            if (mCursor.moveToPosition(position)) {
                Intent detailActivityIntent = new Intent(mContext, DetailActivity.class);

                long resourceId = mCursor.getLong(mCursor.getColumnIndex(COLUMN_MEDIA_ID));
                String resourceTitle = mCursor.getString(mCursor.getColumnIndex(COLUMN_TITLE));

                detailActivityIntent.putExtra(RESOURCE_ID, resourceId);
                detailActivityIntent.putExtra(RESOURCE_TITLE, resourceTitle);
                detailActivityIntent.putExtra(RESOURCE_TYPE, DETAIL_ACTIVITY_FRAGMENT_TYPE_MOVIE);

                startActivity(detailActivityIntent);
            }
        }
    }

    /**
     * Implement onLoadFinished(Loader, Cursor) from NetworkLoader.NetworkLoaderCallback
     *
     * @param loader Loader instance
     * @param cursor Cursor
     */
    @Override
    public void onLoadFinished(Loader loader, Cursor cursor) {
        switch (loader.getId()) {
            case FAVORITES_LOADER_ID:
                if (cursor == null || cursor.getCount() == 0) {
                    showErrorMessage();
                } else {
                    mCursor = cursor;
                    mAdapter.swapCursor(mCursor);
                    showRecyclerView();
                    restoreState();
                }
                break;
            case BOOKMARKS_LOADER_ID:
                if (cursor == null || !cursor.moveToFirst()) {
                    showErrorMessage();
                } else {
                    mCursor = cursor;
                    mAdapter.swapCursor(mCursor);
                    showRecyclerView();
                    restoreState();
                }
                break;
        }
    }

    /**
     * Fetches local movies based on mType
     */
    private void fetchContent() {
        Bundle args = new Bundle();
        switch (mType) {
            case FAVORITES:
                args.putParcelable(ContentLoader.URI_EXTRA, DataContract.FAVORITES_CONTENT_URI);
                if (getActivity().getSupportLoaderManager() != null) {
                    getActivity().getSupportLoaderManager().restartLoader(FAVORITES_LOADER_ID, args, mContentLoader);
                }
                break;
            case BOOKMARKS:
                args.putParcelable(ContentLoader.URI_EXTRA, DataContract.BOOKMARKS_CONTENT_URI);
                if (getActivity().getSupportLoaderManager() != null) {
                    getActivity().getSupportLoaderManager().restartLoader(BOOKMARKS_LOADER_ID, args, mContentLoader);
                }
                break;
        }
    }

    /**
     * Restores the state of the RecyclerView LayoutManager
     * <p>
     * Reference: http://panavtec.me/retain-restore-recycler-view-scroll-position
     */
    private void restoreState() {
        if (mSavedState != null) {
            mLayoutManager.onRestoreInstanceState(mSavedState);
        }
    }

    /**
     * Shows RecyclerView, Hides ProgressBar and ErrorMessage
     */
    private void showRecyclerView() {
        mBinding.recyclerViewMovies.setVisibility(View.VISIBLE);
        mBinding.pbLoadingIndicator.setVisibility(View.INVISIBLE);
        mBinding.tvErrorMessageDisplay.setVisibility(View.INVISIBLE);
    }

    /**
     * Shows ErrorMessage, Hides ProgressBar and RecyclerView
     */
    private void showErrorMessage() {
        mBinding.tvErrorMessageDisplay.setVisibility(View.VISIBLE);
        mBinding.pbLoadingIndicator.setVisibility(View.INVISIBLE);
        mBinding.recyclerViewMovies.setVisibility(View.INVISIBLE);
    }
}

package dev.learn.movies.app.popular_movies.fragments;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import dev.learn.movies.app.popular_movies.DetailActivity;
import dev.learn.movies.app.popular_movies.R;
import dev.learn.movies.app.popular_movies.adapters.FavoritesAdapter;
import dev.learn.movies.app.popular_movies.adapters.OnItemClickHandler;
import dev.learn.movies.app.popular_movies.data.DataContract.FavoriteEntry;
import dev.learn.movies.app.popular_movies.databinding.FragmentMoviesBinding;
import dev.learn.movies.app.popular_movies.loaders.ContentLoader;

import static dev.learn.movies.app.popular_movies.data.DataContract.FavoriteEntry.COLUMN_MOVIE_ID;
import static dev.learn.movies.app.popular_movies.util.AppConstants.DEFAULT_GRID_COUNT;
import static dev.learn.movies.app.popular_movies.util.AppConstants.FAVORITES;
import static dev.learn.movies.app.popular_movies.util.AppConstants.FAVORITES_LOADER_ID;
import static dev.learn.movies.app.popular_movies.util.AppConstants.TABLET_GRID_COUNT;

/**
 * Created by sudharti on 11/5/17.
 */

public class LocalMoviesFragment extends Fragment implements ContentLoader.ContentLoaderCallback, OnItemClickHandler {

    private static final String TYPE = "type";

    private Context mContext;
    private String mType = FAVORITES;

    private RecyclerView.LayoutManager mLayoutManager;
    private FavoritesAdapter mAdapter;
    private Cursor mCursor;

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
        fetchFavorites();
    }

    @Override
    public void onClick(int position) {
        if (position >= 0 && mCursor != null && position < mCursor.getCount()) {
            /*
             * Starts DetailActivity with movieId and movieName passed in a bundle.
             */
            Intent detailActivityIntent = new Intent(mContext, DetailActivity.class);

            Bundle bundle = new Bundle();
            if (mCursor.moveToPosition(position)) {
                long movieId = mCursor.getLong(mCursor.getColumnIndex(COLUMN_MOVIE_ID));
                bundle.putLong(DetailActivity.MOVIE_ID, movieId);
                detailActivityIntent.putExtras(bundle);
            }

            startActivity(detailActivityIntent);
        }
    }

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
                }
                break;
        }
    }

    private void fetchFavorites() {
        switch (mType) {
            case FAVORITES:
                Bundle args = new Bundle();
                args.putParcelable(ContentLoader.URI_EXTRA, FavoriteEntry.CONTENT_URI);
                if (getActivity().getSupportLoaderManager() != null) {
                    getActivity().getSupportLoaderManager().restartLoader(FAVORITES_LOADER_ID, args, mContentLoader);
                }
                break;
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

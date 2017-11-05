package dev.learn.movies.app.popular_movies.fragments;

import android.content.Context;
import android.database.Cursor;
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
import android.widget.ProgressBar;
import android.widget.TextView;

import dev.learn.movies.app.popular_movies.R;
import dev.learn.movies.app.popular_movies.adapters.FavoritesAdapter;
import dev.learn.movies.app.popular_movies.adapters.OnItemClickHandler;
import dev.learn.movies.app.popular_movies.data.DataContract.FavoriteEntry;
import dev.learn.movies.app.popular_movies.network.ContentLoader;
import dev.learn.movies.app.popular_movies.network.ContentLoaderCallback;

import static dev.learn.movies.app.popular_movies.MainActivity.FAVORITES;

/**
 * Created by sudharti on 11/5/17.
 */

public class LocalMoviesFragment extends Fragment implements ContentLoaderCallback, OnItemClickHandler {

    private static final String TYPE = "type";

    private final static int GRID_COUNT = 2;
    private final static int FAVORITES_LOADER_ID = 300;

    private Context mContext;
    private String mType;

    private RecyclerView mRecyclerViewMovies;
    private ProgressBar mProgressBar;
    private TextView mErrorMessageDisplay;

    RecyclerView.LayoutManager mLayoutManager;
    private FavoritesAdapter mAdapter;
    private Cursor mCursor;

    private ContentLoader mContentLoader;

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
        mLayoutManager = new GridLayoutManager(mContext, GRID_COUNT);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_movies, container, false);

        mType = getArguments().getString(TYPE, FAVORITES);

        mRecyclerViewMovies = view.findViewById(R.id.recycler_view_movies);
        mProgressBar = view.findViewById(R.id.pb_loading_indicator);
        mErrorMessageDisplay = view.findViewById(R.id.tv_error_message_display);

        mRecyclerViewMovies.setHasFixedSize(true);
        mRecyclerViewMovies.setLayoutManager(mLayoutManager);

        mAdapter = new FavoritesAdapter(this);
        mRecyclerViewMovies.setAdapter(mAdapter);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        fetchFavorites();
    }

    @Override
    public void onClick(int position) {

    }

    @Override
    public void onContentLoadStarted() {
        showProgressBar();
    }

    @Override
    public void onContentLoadFinished(Loader loader, Cursor cursor) {
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
                getActivity().getSupportLoaderManager().initLoader(FAVORITES_LOADER_ID, args, mContentLoader);
                break;
        }
    }

    /**
     * Shows ProgressBar, Hides ErrorMessage and RecyclerView
     */
    private void showProgressBar() {
        mProgressBar.setVisibility(View.VISIBLE);
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
        mRecyclerViewMovies.setVisibility(View.INVISIBLE);
    }

    /**
     * Shows RecyclerView, Hides ProgressBar and ErrorMessage
     */
    private void showRecyclerView() {
        mRecyclerViewMovies.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.INVISIBLE);
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
    }

    /**
     * Shows ErrorMessage, Hides ProgressBar and RecyclerView
     */
    private void showErrorMessage() {
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.INVISIBLE);
        mRecyclerViewMovies.setVisibility(View.INVISIBLE);
    }
}

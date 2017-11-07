package dev.learn.movies.app.popular_movies.fragments;

import android.content.Context;
import android.content.Intent;
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
import android.widget.Toast;

import com.google.gson.Gson;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import dev.learn.movies.app.popular_movies.DetailActivity;
import dev.learn.movies.app.popular_movies.EndlessRecyclerViewScrollListener;
import dev.learn.movies.app.popular_movies.R;
import dev.learn.movies.app.popular_movies.adapters.MoviesAdapter;
import dev.learn.movies.app.popular_movies.adapters.OnItemClickHandler;
import dev.learn.movies.app.popular_movies.common.Movie;
import dev.learn.movies.app.popular_movies.common.MoviesResult;
import dev.learn.movies.app.popular_movies.databinding.FragmentMoviesBinding;
import dev.learn.movies.app.popular_movies.loaders.NetworkLoader;
import dev.learn.movies.app.popular_movies.util.DisplayUtils;
import dev.learn.movies.app.popular_movies.util.HTTPHelper;

import static dev.learn.movies.app.popular_movies.MainActivity.DISCOVER;
import static dev.learn.movies.app.popular_movies.MainActivity.MOST_POPULAR;
import static dev.learn.movies.app.popular_movies.MainActivity.TOP_RATED;

/**
 * Created by sudharti on 11/4/17.
 */

public class MoviesFragment extends Fragment implements NetworkLoader.NetworkLoaderCallback, OnItemClickHandler {

    private static final String TYPE = "type";

    private static final int START_PAGE = 1;
    private static final int MOVIES_LOADER_ID = 200;

    private String mType = DISCOVER;
    private Context mContext;
    private final Gson gson = new Gson();
    private int mGridCount = 2;

    private GridLayoutManager mLayoutManager;
    private MoviesAdapter mAdapter;
    private List<Movie> movieList;

    private EndlessRecyclerViewScrollListener mEndlessScollListener;
    private NetworkLoader mNetworkLoader;

    private FragmentMoviesBinding mBinding;

    public static MoviesFragment newInstance(String type) {
        MoviesFragment moviesFragment = new MoviesFragment();

        Bundle args = new Bundle();
        args.putString(TYPE, type);
        moviesFragment.setArguments(args);

        return moviesFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getContext();
        movieList = new ArrayList<>();
        mNetworkLoader = new NetworkLoader(mContext, this);
        boolean isTablet = getResources().getBoolean(R.bool.is_tablet);
        if (isTablet) {
            mGridCount = 4;
        }

        mLayoutManager = new GridLayoutManager(mContext, mGridCount);
        mLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return (position == mAdapter.getItemCount() - 1) ? mGridCount : 1;
            }
        });
        mEndlessScollListener = new EndlessRecyclerViewScrollListener(mLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                mAdapter.showLoading(true);
                fetchMovies(page);
            }
        };
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_movies, container, false);
        View view = mBinding.getRoot();

        if (getArguments() != null) {
            mType = getArguments().getString(TYPE, DISCOVER);
        }

        mBinding.recyclerViewMovies.setHasFixedSize(true);
        mBinding.recyclerViewMovies.setLayoutManager(mLayoutManager);

        mAdapter = new MoviesAdapter(this);
        mBinding.recyclerViewMovies.setAdapter(mAdapter);
        mBinding.recyclerViewMovies.addOnScrollListener(mEndlessScollListener);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        fetchMovies(START_PAGE);
    }

    /**
     * Overrides onClick(position) from MoviesAdapter.OnItemClickHandler
     *
     * @param position
     */
    @Override
    public void onClick(int position) {
        if (position >= 0 && position < this.movieList.size()) {
            /*
             * Starts DetailActivity with movieId and movieName passed in a bundle.
             */
            Intent detailActivityIntent = new Intent(mContext, DetailActivity.class);

            Bundle bundle = new Bundle();
            Movie movie = movieList.get(position);
            if (movie != null) {
                bundle.putLong(DetailActivity.MOVIE_ID, movie.getId());
                detailActivityIntent.putExtras(bundle);
            }

            startActivity(detailActivityIntent);
        }
    }

    @Override
    public void onLoadFinished(Loader loader, String s) {
        switch (loader.getId()) {
            case MOVIES_LOADER_ID:
                MoviesResult moviesResult = (s == null) ? null : gson.fromJson(s, MoviesResult.class);
                /*
                 * Shows recycler_view if moviesResult is not null and the movies list is non empty
                */
                if (moviesResult == null || moviesResult.getResults() == null || moviesResult.getResults().isEmpty()) {
                    if (movieList.isEmpty()) {
                        showErrorMessage();
                    } else {
                        mAdapter.showLoading(false);
                    }
                } else {
                    if (movieList.isEmpty()) {
                        showRecyclerView();
                    }
                    this.movieList.addAll(moviesResult.getResults());
                    mAdapter.setMovieList(movieList);
                }
                break;
        }
    }

    private void fetchMovies(int page) {
        if (HTTPHelper.isNetworkEnabled(mContext)) {
            URL url = null;
            switch (mType) {
                case DISCOVER:
                    url = HTTPHelper.buildDiscoverURL(page);
                    break;
                case MOST_POPULAR:
                    url = HTTPHelper.buildMostPopularURL(page);
                    break;
                case TOP_RATED:
                    url = HTTPHelper.builTopRatedURL(page);
                    break;
            }

            Bundle args = new Bundle();
            args.putSerializable(NetworkLoader.URL_EXTRA, url);
            if (getActivity().getSupportLoaderManager() != null) {
                getActivity().getSupportLoaderManager().restartLoader(MOVIES_LOADER_ID, args, mNetworkLoader);
            }

        } else {
            if (page == 1) {
                DisplayUtils.setNoNetworkConnectionMessage(mContext, mBinding.tvErrorMessageDisplay);
                showErrorMessage();
            } else {
                Toast.makeText(mContext, getResources().getString(R.string.no_network_connection_error_message), Toast.LENGTH_SHORT).show();
                mAdapter.showLoading(false);
                mAdapter.notifyDataSetChanged();
            }
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

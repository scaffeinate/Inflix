package dev.learn.movies.app.popular_movies.fragments;

import android.content.Context;
import android.content.Intent;
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
import android.widget.Toast;

import com.google.gson.Gson;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import dev.learn.movies.app.popular_movies.activities.DetailActivity;
import dev.learn.movies.app.popular_movies.util.EndlessRecyclerViewScrollListener;
import dev.learn.movies.app.popular_movies.R;
import dev.learn.movies.app.popular_movies.adapters.MoviesAdapter;
import dev.learn.movies.app.popular_movies.adapters.OnItemClickHandler;
import dev.learn.movies.app.popular_movies.common.movies.Movie;
import dev.learn.movies.app.popular_movies.common.movies.MoviesResult;
import dev.learn.movies.app.popular_movies.databinding.FragmentMoviesBinding;
import dev.learn.movies.app.popular_movies.loaders.NetworkLoader;
import dev.learn.movies.app.popular_movies.util.DisplayUtils;
import dev.learn.movies.app.popular_movies.util.HTTPHelper;

import static dev.learn.movies.app.popular_movies.activities.DetailActivity.RESOURCE_ID;
import static dev.learn.movies.app.popular_movies.fragments.MovieDetailsFragment.MOVIE_ID;
import static dev.learn.movies.app.popular_movies.util.AppConstants.DEFAULT_GRID_COUNT;
import static dev.learn.movies.app.popular_movies.util.AppConstants.DISCOVER;
import static dev.learn.movies.app.popular_movies.util.AppConstants.MOST_POPULAR;
import static dev.learn.movies.app.popular_movies.util.AppConstants.MOVIES_LOADER_ID;
import static dev.learn.movies.app.popular_movies.util.AppConstants.NOW_PLAYING;
import static dev.learn.movies.app.popular_movies.util.AppConstants.START_PAGE;
import static dev.learn.movies.app.popular_movies.util.AppConstants.TABLET_GRID_COUNT;
import static dev.learn.movies.app.popular_movies.util.AppConstants.TOP_RATED;
import static dev.learn.movies.app.popular_movies.util.AppConstants.UPCOMING;

/**
 * MoviesFragment - Fetch and show Movies Grid from API
 */
public class MoviesFragment extends Fragment implements NetworkLoader.NetworkLoaderCallback, OnItemClickHandler {

    private static final String TYPE = "type";
    private static final String PAGE = "page";
    private static final String RESPONSE = "response";

    private final Gson gson = new Gson();
    private String mType = DISCOVER;
    private Context mContext;
    private int mGridCount = DEFAULT_GRID_COUNT;

    private GridLayoutManager mLayoutManager;
    private MoviesAdapter mAdapter;
    private List<Movie> mMovieList;
    private int mPage = START_PAGE;

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
        mMovieList = new ArrayList<>();
        mNetworkLoader = new NetworkLoader(mContext, this);
        boolean isTablet = getResources().getBoolean(R.bool.is_tablet);
        mGridCount = isTablet ? TABLET_GRID_COUNT : DEFAULT_GRID_COUNT;

        mLayoutManager = new GridLayoutManager(mContext, mGridCount);
        mLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return (position == mAdapter.getItemCount() - 1) ? mGridCount : 1;
            }
        });

        if (savedInstanceState != null && savedInstanceState.containsKey(PAGE)) {
            mPage = savedInstanceState.getInt(PAGE, START_PAGE);
        }

        //onScrollListener to handle endless pagination
        mEndlessScollListener = new EndlessRecyclerViewScrollListener(mPage, mLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                mAdapter.showLoading(true);
                mPage = page;
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
        if (savedInstanceState != null && savedInstanceState.containsKey(RESPONSE)) {
            mMovieList = savedInstanceState.getParcelableArrayList(RESPONSE);
            mAdapter.setMovieList(mMovieList);
            showRecyclerView();
        } else {
            fetchMovies(START_PAGE);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (!mMovieList.isEmpty()) {
            outState.putInt(PAGE, mPage);
            outState.putParcelableArrayList(RESPONSE, (ArrayList<? extends Parcelable>) mMovieList);
        }
    }

    /**
     * Implement onClick(position) from MoviesAdapter.OnItemClickHandler
     *
     * @param position Position
     */
    @Override
    public void onClick(int position) {
        if (position >= 0 && position < this.mMovieList.size()) {
            // Starts DetailActivity with movieId passed in a bundle.
            Intent detailActivityIntent = new Intent(mContext, DetailActivity.class);

            Bundle bundle = new Bundle();
            Movie movie = mMovieList.get(position);
            if (movie != null) {
                bundle.putLong(RESOURCE_ID, movie.getId());
                detailActivityIntent.putExtras(bundle);
            }

            startActivity(detailActivityIntent);
        }
    }

    /**
     * Implement onLoadFinished(Loader loader, String s) from NetworkLoader.NetworkLoaderCallback
     *
     * @param loader Loader instance
     * @param s      responseString
     */
    @Override
    public void onLoadFinished(Loader loader, String s) {
        switch (loader.getId()) {
            case MOVIES_LOADER_ID:
                MoviesResult moviesResult = (s == null) ? null : gson.fromJson(s, MoviesResult.class);
                if (moviesResult == null || moviesResult.getResults() == null || moviesResult.getResults().isEmpty()) {
                    // If the first request failed then show error message hiding the content
                    // Otherwise stop loading further
                    if (mMovieList.isEmpty()) {
                        showErrorMessage();
                    } else {
                        mAdapter.showLoading(false);
                        mAdapter.notifyDataSetChanged();
                    }
                } else {
                    // For the first request change visibility of recyclerview
                    if (mMovieList.isEmpty()) {
                        showRecyclerView();
                    }
                    this.mMovieList.addAll(moviesResult.getResults());
                    mAdapter.setMovieList(mMovieList);
                }
                break;
        }
    }

    /**
     * Fetches Movies for the requested page
     *
     * @param page page number
     */
    private void fetchMovies(int page) {
        if (HTTPHelper.isNetworkEnabled(mContext)) {
            URL url = null;
            switch (mType) {
                case NOW_PLAYING:
                    url = HTTPHelper.buildNowPlayingURL(page);
                    break;
                case UPCOMING:
                    url = HTTPHelper.buildUpcomingURL(page);
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
            // If network is unavailable for the first request show the error textview
            // Otherview show Toast message
            if (page == START_PAGE) {
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

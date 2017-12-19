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

import dev.learn.movies.app.popular_movies.R;
import dev.learn.movies.app.popular_movies.activities.DetailActivity;
import dev.learn.movies.app.popular_movies.adapters.MediaAdapter;
import dev.learn.movies.app.popular_movies.adapters.OnItemClickHandler;
import dev.learn.movies.app.popular_movies.common.Media;
import dev.learn.movies.app.popular_movies.common.movies.MoviesResult;
import dev.learn.movies.app.popular_movies.databinding.FragmentMoviesBinding;
import dev.learn.movies.app.popular_movies.loaders.NetworkLoader;
import dev.learn.movies.app.popular_movies.utils.DisplayUtils;
import dev.learn.movies.app.popular_movies.utils.HTTPLoaderUtil;
import dev.learn.movies.app.popular_movies.utils.URIBuilderUtils;
import dev.learn.movies.app.popular_movies.views.EndlessRecyclerViewScrollListener;

import static dev.learn.movies.app.popular_movies.Inflix.DEFAULT_GRID_COUNT;
import static dev.learn.movies.app.popular_movies.Inflix.DISCOVER;
import static dev.learn.movies.app.popular_movies.Inflix.MOST_POPULAR;
import static dev.learn.movies.app.popular_movies.Inflix.MOVIES_LOADER_ID;
import static dev.learn.movies.app.popular_movies.Inflix.NOW_PLAYING;
import static dev.learn.movies.app.popular_movies.Inflix.RESOURCE_ID;
import static dev.learn.movies.app.popular_movies.Inflix.RESOURCE_TITLE;
import static dev.learn.movies.app.popular_movies.Inflix.RESOURCE_TYPE;
import static dev.learn.movies.app.popular_movies.Inflix.START_PAGE;
import static dev.learn.movies.app.popular_movies.Inflix.TABLET_GRID_COUNT;
import static dev.learn.movies.app.popular_movies.Inflix.TOP_RATED;
import static dev.learn.movies.app.popular_movies.Inflix.UPCOMING;
import static dev.learn.movies.app.popular_movies.data.DataContract.MOVIES;

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
    private MediaAdapter mAdapter;
    private List<Media> mMediaList;
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
        mMediaList = new ArrayList<>();
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

        if (savedInstanceState != null) {
            mPage = savedInstanceState.getInt(PAGE, START_PAGE);
        }

        //onScrollListener to handle endless pagination
        mEndlessScollListener = new EndlessRecyclerViewScrollListener(mPage, mLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                mAdapter.showLoading(true);
                mPage = page;
                fetchMovies();
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

        mAdapter = new MediaAdapter(this);
        mBinding.recyclerViewMovies.setAdapter(mAdapter);
        mBinding.recyclerViewMovies.addOnScrollListener(mEndlessScollListener);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null && savedInstanceState.containsKey(RESPONSE)) {
            mMediaList = savedInstanceState.getParcelableArrayList(RESPONSE);
            mAdapter.setMediaList(mMediaList);
            showRecyclerView();
        } else {
            fetchMovies();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (!mMediaList.isEmpty()) {
            outState.putInt(PAGE, mPage);
            outState.putParcelableArrayList(RESPONSE, (ArrayList<? extends Parcelable>) mMediaList);
        }
    }

    /**
     * Implement onClick(position) from MediaAdapter.OnItemClickHandler
     *
     * @param position Position
     */
    @Override
    public void onItemClicked(ViewGroup parent, View view, int position) {
        if (position >= 0 && position < this.mMediaList.size()) {
            // Starts DetailActivity with movieId passed in a bundle.
            Media media = mMediaList.get(position);

            Intent detailActivityIntent = new Intent(mContext, DetailActivity.class);
            detailActivityIntent.putExtra(RESOURCE_ID, media.getId());
            detailActivityIntent.putExtra(RESOURCE_TITLE, media.getTitle());
            detailActivityIntent.putExtra(RESOURCE_TYPE, MOVIES);

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
                    if (mPage == START_PAGE) {
                        showErrorMessage();
                    } else {
                        mAdapter.showLoading(false);
                        mAdapter.notifyDataSetChanged();
                    }
                } else {
                    // For the first request change visibility of recyclerview
                    if (mPage == START_PAGE) {
                        showRecyclerView();
                    }
                    this.mMediaList.addAll(moviesResult.getResults());
                    mAdapter.setMediaList(mMediaList);
                }
                break;
        }
    }

    /**
     * Fetches Movies for the requested page
     */
    private void fetchMovies() {
        HTTPLoaderUtil.with(mContext).tryCall(new HTTPLoaderUtil.HTTPBlock() {
            @Override
            public void run() {
                URL url = null;
                switch (mType) {
                    case NOW_PLAYING:
                        url = URIBuilderUtils.buildNowPlayingURL(mPage);
                        break;
                    case UPCOMING:
                        url = URIBuilderUtils.buildUpcomingURL(mPage);
                        break;
                    case MOST_POPULAR:
                        url = URIBuilderUtils.buildMostPopularURL(mPage);
                        break;
                    case TOP_RATED:
                        url = URIBuilderUtils.builTopRatedURL(mPage);
                        break;
                }

                Bundle args = new Bundle();
                args.putSerializable(NetworkLoader.URL_EXTRA, url);
                if (getActivity().getSupportLoaderManager() != null) {
                    getActivity().getSupportLoaderManager().restartLoader(MOVIES_LOADER_ID, args, mNetworkLoader);
                }
            }
        }).onNoNetwork(new HTTPLoaderUtil.HTTPBlock() {
            @Override
            public void run() {
                if (mPage == START_PAGE) {
                    DisplayUtils.setNoNetworkConnectionMessage(mContext, mBinding.tvErrorMessageDisplay);
                    showErrorMessage();
                } else {
                    Toast.makeText(mContext, getResources().getString(R.string.no_network_connection_error_message), Toast.LENGTH_SHORT).show();
                    mAdapter.showLoading(false);
                    mAdapter.notifyDataSetChanged();
                }
            }
        }).execute();
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

package dev.learn.movies.app.popular_movies.fragments;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Parcelable;
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
import dev.learn.movies.app.popular_movies.adapters.OnItemClickHandler;
import dev.learn.movies.app.popular_movies.adapters.TvShowsAdapter;
import dev.learn.movies.app.popular_movies.common.tv_show.TvShow;
import dev.learn.movies.app.popular_movies.common.tv_show.TvShowsResult;
import dev.learn.movies.app.popular_movies.databinding.FragmentMoviesBinding;
import dev.learn.movies.app.popular_movies.loaders.NetworkLoader;
import dev.learn.movies.app.popular_movies.util.DisplayUtils;
import dev.learn.movies.app.popular_movies.util.EndlessRecyclerViewScrollListener;
import dev.learn.movies.app.popular_movies.util.HTTPHelper;

import static dev.learn.movies.app.popular_movies.util.AppConstants.DEFAULT_GRID_COUNT;
import static dev.learn.movies.app.popular_movies.util.AppConstants.DETAIL_ACTIVITY_FRAGMENT_TYPE_TV_SHOW;
import static dev.learn.movies.app.popular_movies.util.AppConstants.DISCOVER;
import static dev.learn.movies.app.popular_movies.util.AppConstants.RESOURCE_ID;
import static dev.learn.movies.app.popular_movies.util.AppConstants.RESOURCE_TITLE;
import static dev.learn.movies.app.popular_movies.util.AppConstants.RESOURCE_TYPE;
import static dev.learn.movies.app.popular_movies.util.AppConstants.START_PAGE;
import static dev.learn.movies.app.popular_movies.util.AppConstants.TABLET_GRID_COUNT;
import static dev.learn.movies.app.popular_movies.util.AppConstants.TV_AIRING_TODAY;
import static dev.learn.movies.app.popular_movies.util.AppConstants.TV_ON_THE_AIR;
import static dev.learn.movies.app.popular_movies.util.AppConstants.TV_POPULAR;
import static dev.learn.movies.app.popular_movies.util.AppConstants.TV_SHOWS_LOADER_ID;
import static dev.learn.movies.app.popular_movies.util.AppConstants.TV_TOP_RATED;

/**
 * Created by sudharti on 11/12/17.
 */

public class TvShowsFragment extends Fragment implements NetworkLoader.NetworkLoaderCallback, OnItemClickHandler {

    private static final String TYPE = "type";
    private static final String PAGE = "page";
    private static final String RESPONSE = "response";

    private final Gson gson = new Gson();
    private String mType = DISCOVER;
    private Context mContext;
    private int mGridCount = DEFAULT_GRID_COUNT;

    private GridLayoutManager mLayoutManager;
    private TvShowsAdapter mAdapter;
    private List<TvShow> mTvShowsList;
    private int mPage = START_PAGE;

    private EndlessRecyclerViewScrollListener mEndlessScollListener;
    private NetworkLoader mNetworkLoader;

    private FragmentMoviesBinding mBinding;

    public static TvShowsFragment newInstance(String type) {
        TvShowsFragment tvShowsFragment = new TvShowsFragment();

        Bundle args = new Bundle();
        args.putString(TYPE, type);
        tvShowsFragment.setArguments(args);

        return tvShowsFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getContext();
        mTvShowsList = new ArrayList<>();
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
                fetchTVShows(page);
            }
        };
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_movies, container, false);
        View view = mBinding.getRoot();

        if (getArguments() != null) {
            mType = getArguments().getString(TYPE, DISCOVER);
        }

        mBinding.recyclerViewMovies.setHasFixedSize(true);
        mBinding.recyclerViewMovies.setLayoutManager(mLayoutManager);

        mAdapter = new TvShowsAdapter(this);
        mBinding.recyclerViewMovies.setAdapter(mAdapter);
        mBinding.recyclerViewMovies.addOnScrollListener(mEndlessScollListener);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null && savedInstanceState.containsKey(RESPONSE)) {
            mTvShowsList = savedInstanceState.getParcelableArrayList(RESPONSE);
            mAdapter.setTvShowList(mTvShowsList);
            showRecyclerView();
        } else {
            fetchTVShows(START_PAGE);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (!mTvShowsList.isEmpty()) {
            outState.putInt(PAGE, mPage);
            outState.putParcelableArrayList(RESPONSE, (ArrayList<? extends Parcelable>) mTvShowsList);
        }
    }

    @Override
    public void onItemClicked(ViewGroup parent, View view, int position) {
        if (position >= 0 && position < this.mTvShowsList.size()) {
            TvShow tvShow = mTvShowsList.get(position);

            Intent detailActivityIntent = new Intent(mContext, DetailActivity.class);
            detailActivityIntent.putExtra(RESOURCE_ID, tvShow.getId());
            detailActivityIntent.putExtra(RESOURCE_TITLE, tvShow.getName());
            detailActivityIntent.putExtra(RESOURCE_TYPE, DETAIL_ACTIVITY_FRAGMENT_TYPE_TV_SHOW);

            startActivity(detailActivityIntent);
        }
    }

    @Override
    public void onLoadFinished(Loader loader, String s) {
        switch (loader.getId()) {
            case TV_SHOWS_LOADER_ID:
                TvShowsResult tvShowsResult = (s == null) ? null : gson.fromJson(s, TvShowsResult.class);
                if (tvShowsResult == null || tvShowsResult.getResults() == null || tvShowsResult.getResults().isEmpty()) {
                    // If the first request failed then show error message hiding the content
                    // Otherwise stop loading further
                    if (mTvShowsList.isEmpty()) {
                        showErrorMessage();
                    } else {
                        mAdapter.showLoading(false);
                        mAdapter.notifyDataSetChanged();
                    }
                } else {
                    // For the first request change visibility of recyclerview
                    if (mTvShowsList.isEmpty()) {
                        showRecyclerView();
                    }
                    this.mTvShowsList.addAll(tvShowsResult.getResults());
                    mAdapter.setTvShowList(mTvShowsList);
                }
                break;
        }
    }

    private void fetchTVShows(int page) {
        if (HTTPHelper.isNetworkEnabled(mContext)) {
            URL url = null;
            switch (mType) {
                case TV_AIRING_TODAY:
                    url = HTTPHelper.buildTVAiringTodayURL(page);
                    break;
                case TV_ON_THE_AIR:
                    url = HTTPHelper.buildTVOnTheAirURL(page);
                    break;
                case TV_POPULAR:
                    url = HTTPHelper.builldTVPopularURL(page);
                    break;
                case TV_TOP_RATED:
                    url = HTTPHelper.buildTVTopRatedURL(page);
                    break;
            }

            Bundle args = new Bundle();
            args.putSerializable(NetworkLoader.URL_EXTRA, url);
            if (getActivity().getSupportLoaderManager() != null) {
                getActivity().getSupportLoaderManager().restartLoader(TV_SHOWS_LOADER_ID, args, mNetworkLoader);
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

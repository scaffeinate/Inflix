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
import dev.learn.movies.app.popular_movies.adapters.MediaAdapter;
import dev.learn.movies.app.popular_movies.adapters.OnItemClickHandler;
import dev.learn.movies.app.popular_movies.common.Media;
import dev.learn.movies.app.popular_movies.common.tv_show.TVShowsResult;
import dev.learn.movies.app.popular_movies.databinding.FragmentMoviesBinding;
import dev.learn.movies.app.popular_movies.loaders.NetworkLoader;
import dev.learn.movies.app.popular_movies.utils.ContentLoadingUtil;
import dev.learn.movies.app.popular_movies.utils.DisplayUtils;
import dev.learn.movies.app.popular_movies.utils.HTTPLoaderUtil;
import dev.learn.movies.app.popular_movies.utils.URIBuilderUtils;
import dev.learn.movies.app.popular_movies.views.EndlessRecyclerViewScrollListener;

import static dev.learn.movies.app.popular_movies.Inflix.DEFAULT_GRID_COUNT;
import static dev.learn.movies.app.popular_movies.Inflix.DISCOVER;
import static dev.learn.movies.app.popular_movies.Inflix.LANDSCAPE_TABLET_GRID_COUNT;
import static dev.learn.movies.app.popular_movies.Inflix.RESOURCE_ID;
import static dev.learn.movies.app.popular_movies.Inflix.RESOURCE_TITLE;
import static dev.learn.movies.app.popular_movies.Inflix.RESOURCE_TYPE;
import static dev.learn.movies.app.popular_movies.Inflix.START_PAGE;
import static dev.learn.movies.app.popular_movies.Inflix.TABLET_GRID_COUNT;
import static dev.learn.movies.app.popular_movies.Inflix.TV_AIRING_TODAY;
import static dev.learn.movies.app.popular_movies.Inflix.TV_ON_THE_AIR;
import static dev.learn.movies.app.popular_movies.Inflix.TV_POPULAR;
import static dev.learn.movies.app.popular_movies.Inflix.TV_SHOWS;
import static dev.learn.movies.app.popular_movies.Inflix.TV_SHOWS_LOADER_ID;
import static dev.learn.movies.app.popular_movies.Inflix.TV_TOP_RATED;

/**
 * Created by sudharti on 11/12/17.
 */

public class TVShowsFragment extends Fragment implements NetworkLoader.NetworkLoaderCallback, OnItemClickHandler {

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
    private ContentLoadingUtil mContentLoadingUtil;

    public static TVShowsFragment newInstance(String type) {
        TVShowsFragment TVShowsFragment = new TVShowsFragment();

        Bundle args = new Bundle();
        args.putString(TYPE, type);
        TVShowsFragment.setArguments(args);

        return TVShowsFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getContext();
        mMediaList = new ArrayList<>();
        mNetworkLoader = new NetworkLoader(mContext, this);
        boolean isTablet = getResources().getBoolean(R.bool.is_tablet);
        boolean isLand = getResources().getBoolean(R.bool.is_land);
        mGridCount = ((isTablet && isLand) ? LANDSCAPE_TABLET_GRID_COUNT : (isTablet ? TABLET_GRID_COUNT : DEFAULT_GRID_COUNT));

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
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_media_grid, container, false);
        mContentLoadingUtil = ContentLoadingUtil.with(mContext)
                .setContent(mBinding.recyclerViewMovies)
                .setProgress(mBinding.pbLoadingIndicator)
                .setError(mBinding.tvErrorMessageDisplay);
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
            mContentLoadingUtil.success();
        } else {
            fetchTVShows(START_PAGE);
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

    @Override
    public void onItemClicked(ViewGroup parent, View view, int position) {
        if (position >= 0 && position < this.mMediaList.size()) {
            Media media = mMediaList.get(position);

            Intent detailActivityIntent = new Intent(mContext, DetailActivity.class);
            detailActivityIntent.putExtra(RESOURCE_ID, media.getId());
            detailActivityIntent.putExtra(RESOURCE_TITLE, media.getName());
            detailActivityIntent.putExtra(RESOURCE_TYPE, TV_SHOWS);

            startActivity(detailActivityIntent);
        }
    }

    @Override
    public void onLoadFinished(Loader loader, String s) {
        switch (loader.getId()) {
            case TV_SHOWS_LOADER_ID:
                TVShowsResult tvShowsResult = (s == null) ? null : gson.fromJson(s, TVShowsResult.class);
                if (tvShowsResult == null || tvShowsResult.getResults() == null || tvShowsResult.getResults().isEmpty()) {
                    // If the first request failed then show error message hiding the content
                    // Otherwise stop loading further
                    if (mPage == START_PAGE) {
                        mContentLoadingUtil.error();
                    } else {
                        mAdapter.showLoading(false);
                        mAdapter.notifyDataSetChanged();
                    }
                } else {
                    // For the first request change visibility of recyclerview
                    if (mPage == START_PAGE) {
                        mContentLoadingUtil.success();
                    }
                    this.mMediaList.addAll(tvShowsResult.getResults());
                    mAdapter.setMediaList(mMediaList);
                }
                break;
        }
    }

    private void fetchTVShows(final int page) {
        HTTPLoaderUtil.with(mContext)
                .tryCall(new HTTPLoaderUtil.HTTPBlock() {
                    @Override
                    public void run() {
                        URL url = null;
                        switch (mType) {
                            case TV_AIRING_TODAY:
                                url = URIBuilderUtils.buildTVAiringTodayURL(page);
                                break;
                            case TV_ON_THE_AIR:
                                url = URIBuilderUtils.buildTVOnTheAirURL(page);
                                break;
                            case TV_POPULAR:
                                url = URIBuilderUtils.builldTVPopularURL(page);
                                break;
                            case TV_TOP_RATED:
                                url = URIBuilderUtils.buildTVTopRatedURL(page);
                                break;
                        }

                        Bundle args = new Bundle();
                        args.putSerializable(NetworkLoader.URL_EXTRA, url);
                        if (getActivity().getSupportLoaderManager() != null) {
                            getActivity().getSupportLoaderManager().restartLoader(TV_SHOWS_LOADER_ID, args, mNetworkLoader);
                        }
                    }
                })
                .onNoNetwork(new HTTPLoaderUtil.HTTPBlock() {
                    @Override
                    public void run() {
                        // If network is unavailable for the first request show the error textview
                        // Otherview show Toast message
                        if (page == START_PAGE) {
                            DisplayUtils.setNoNetworkConnectionMessage(mContext, mBinding.tvErrorMessageDisplay);
                            mContentLoadingUtil.error();
                        } else {
                            Toast.makeText(mContext, getResources().getString(R.string.no_network_connection_error_message), Toast.LENGTH_SHORT).show();
                            mAdapter.showLoading(false);
                            mAdapter.notifyDataSetChanged();
                        }
                    }
                }).execute();
    }
}

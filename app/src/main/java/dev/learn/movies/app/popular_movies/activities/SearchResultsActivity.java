package dev.learn.movies.app.popular_movies.activities;

import android.app.SearchManager;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import dev.learn.movies.app.popular_movies.R;
import dev.learn.movies.app.popular_movies.adapters.MediaAdapter;
import dev.learn.movies.app.popular_movies.adapters.OnItemClickHandler;
import dev.learn.movies.app.popular_movies.common.Media;
import dev.learn.movies.app.popular_movies.common.movies.MoviesResult;
import dev.learn.movies.app.popular_movies.common.tv_show.TVShowsResult;
import dev.learn.movies.app.popular_movies.databinding.ActivitySearchResultsBinding;
import dev.learn.movies.app.popular_movies.loaders.NetworkLoader;
import dev.learn.movies.app.popular_movies.utils.HTTPLoaderUtil;
import dev.learn.movies.app.popular_movies.utils.URIBuilderUtils;
import dev.learn.movies.app.popular_movies.views.EndlessRecyclerViewScrollListener;

import static dev.learn.movies.app.popular_movies.Inflix.DEFAULT_GRID_COUNT;
import static dev.learn.movies.app.popular_movies.Inflix.MOVIES;
import static dev.learn.movies.app.popular_movies.Inflix.MOVIES_SEARCH_LOADER_ID;
import static dev.learn.movies.app.popular_movies.Inflix.RESOURCE_ID;
import static dev.learn.movies.app.popular_movies.Inflix.RESOURCE_TITLE;
import static dev.learn.movies.app.popular_movies.Inflix.RESOURCE_TYPE;
import static dev.learn.movies.app.popular_movies.Inflix.START_PAGE;
import static dev.learn.movies.app.popular_movies.Inflix.TABLET_GRID_COUNT;
import static dev.learn.movies.app.popular_movies.Inflix.TV_SHOWS;
import static dev.learn.movies.app.popular_movies.Inflix.TV_SHOWS_SEARCH_LOADER_ID;

/**
 * Created by sudhar on 12/18/17.
 */

public class SearchResultsActivity extends AppCompatActivity implements NetworkLoader.NetworkLoaderCallback, OnItemClickHandler {

    private static final String PAGE = "page";
    private static final String RESPONSE = "response";

    private final Gson gson = new Gson();
    private int mGridCount = DEFAULT_GRID_COUNT;

    private GridLayoutManager mLayoutManager;
    private int mPage = START_PAGE;
    private int mLastKnownSize = 0;

    private EndlessRecyclerViewScrollListener mEndlessScollListener;
    private NetworkLoader mNetworkLoader;
    private List<Media> mMediaList;
    private MediaAdapter mAdapter;
    private String mQuery;

    private ActivitySearchResultsBinding mBinding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_search_results);
        mMediaList = new ArrayList<>();
        mNetworkLoader = new NetworkLoader(this, this);
        boolean isTablet = getResources().getBoolean(R.bool.is_tablet);
        mGridCount = isTablet ? TABLET_GRID_COUNT : DEFAULT_GRID_COUNT;

        mLayoutManager = new GridLayoutManager(this, mGridCount);
        mLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return (position == mAdapter.getItemCount() - 1) ? mGridCount : 1;
            }
        });

        mBinding.recyclerViewMovies.setHasFixedSize(true);
        mBinding.recyclerViewMovies.setLayoutManager(mLayoutManager);

        mAdapter = new MediaAdapter(this);
        mBinding.recyclerViewMovies.setAdapter(mAdapter);

        if (savedInstanceState != null) {
            mPage = savedInstanceState.getInt(PAGE, START_PAGE);
        }

        mBinding.toolbar.tvToolbarTitle.setText(getString(R.string.action_search));

        setSupportActionBar(mBinding.toolbar.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        if (savedInstanceState != null && savedInstanceState.containsKey(RESPONSE)) {
            mPage = savedInstanceState.getInt(PAGE, START_PAGE);
            mMediaList = savedInstanceState.getParcelableArrayList(RESPONSE);
            mAdapter.setMediaList(mMediaList);
            showRecyclerView();
        } else {
            handleIntent(getIntent());
        }

        //onScrollListener to handle endless pagination
        mEndlessScollListener = new EndlessRecyclerViewScrollListener(mPage, mLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                mAdapter.showLoading(true);
                mPage = page;
                searchMovies();
            }
        };

        mBinding.recyclerViewMovies.addOnScrollListener(mEndlessScollListener);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (!mMediaList.isEmpty()) {
            outState.putInt(PAGE, mPage);
            outState.putParcelableArrayList(RESPONSE, (ArrayList<? extends Parcelable>) mMediaList);
        }
    }

    @Override
    public void onLoadFinished(Loader loader, String s) {
        switch (loader.getId()) {
            case MOVIES_SEARCH_LOADER_ID:
                MoviesResult moviesResult = (s == null) ? null : gson.fromJson(s, MoviesResult.class);
                if (moviesResult != null && moviesResult.getResults() != null) {
                    List<Media> movieList = moviesResult.getResults();
                    for (Media media : movieList) {
                        media.setMediaType(MOVIES);
                        mMediaList.add(media);
                    }
                }
                searchTVShows();
                break;
            case TV_SHOWS_SEARCH_LOADER_ID:
                TVShowsResult tvShowsResult = (s == null) ? null : gson.fromJson(s, TVShowsResult.class);
                if (tvShowsResult != null && tvShowsResult.getResults() != null) {
                    List<Media> tvShowList = tvShowsResult.getResults();
                    for (Media media : tvShowList) {
                        media.setMediaType(TV_SHOWS);
                        mMediaList.add(media);
                    }
                }

                if (mMediaList.isEmpty()) {
                    showErrorMessage();
                } else if (mLastKnownSize == mMediaList.size()) {
                    mAdapter.showLoading(false);
                    mAdapter.notifyDataSetChanged();
                } else {
                    showRecyclerView();
                    mAdapter.setMediaList(mMediaList);
                }

                mLastKnownSize = mMediaList.size();
                break;
        }
    }


    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            mQuery = query;
            searchMovies();
        }
    }

    private void searchMovies() {
        HTTPLoaderUtil.with(this).tryCall(new HTTPLoaderUtil.HTTPBlock() {
            @Override
            public void run() {
                URL url = URIBuilderUtils.buildMovieSearchURL(mQuery, mPage);
                Bundle args = new Bundle();
                args.putSerializable(NetworkLoader.URL_EXTRA, url);
                if (getSupportLoaderManager() != null) {
                    getSupportLoaderManager().restartLoader(MOVIES_SEARCH_LOADER_ID, args, mNetworkLoader);
                }
            }
        }).execute();
    }

    private void searchTVShows() {
        HTTPLoaderUtil.with(this).tryCall(new HTTPLoaderUtil.HTTPBlock() {
            @Override
            public void run() {
                URL url = URIBuilderUtils.buildTVShowSearchURL(mQuery, mPage);
                Bundle args = new Bundle();
                args.putSerializable(NetworkLoader.URL_EXTRA, url);
                if (getSupportLoaderManager() != null) {
                    getSupportLoaderManager().restartLoader(TV_SHOWS_SEARCH_LOADER_ID, args, mNetworkLoader);
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

    @Override
    public void onItemClicked(ViewGroup parent, View view, int position) {
        if (position >= 0 && position < this.mMediaList.size()) {
            Media media = mMediaList.get(position);

            if (media != null) {
                Intent detailActivityIntent = new Intent(this, DetailActivity.class);
                detailActivityIntent.putExtra(RESOURCE_ID, media.getId());
                detailActivityIntent.putExtra(RESOURCE_TITLE, (media.getName() != null ? media.getName() : media.getTitle()));
                detailActivityIntent.putExtra(RESOURCE_TYPE, media.getMediaType());

                startActivity(detailActivityIntent);
            }
        }
    }
}

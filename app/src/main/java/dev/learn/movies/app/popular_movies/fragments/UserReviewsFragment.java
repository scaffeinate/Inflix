package dev.learn.movies.app.popular_movies.fragments;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.Loader;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
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
import dev.learn.movies.app.popular_movies.adapters.MovieReviewsAdapter;
import dev.learn.movies.app.popular_movies.common.movies.MovieReview;
import dev.learn.movies.app.popular_movies.common.movies.MovieReviewsResult;
import dev.learn.movies.app.popular_movies.databinding.FragmentUserReviewsBinding;
import dev.learn.movies.app.popular_movies.loaders.NetworkLoader;
import dev.learn.movies.app.popular_movies.utils.ContentLoadingUtil;
import dev.learn.movies.app.popular_movies.utils.DisplayUtils;
import dev.learn.movies.app.popular_movies.utils.HTTPLoaderUtil;
import dev.learn.movies.app.popular_movies.utils.URIBuilderUtils;
import dev.learn.movies.app.popular_movies.views.EndlessRecyclerViewScrollListener;

import static dev.learn.movies.app.popular_movies.Inflix.MOVIE_REVIEWS;
import static dev.learn.movies.app.popular_movies.Inflix.MOVIE_REVIEWS_LOADER_ID;
import static dev.learn.movies.app.popular_movies.Inflix.RESOURCE_ID;
import static dev.learn.movies.app.popular_movies.Inflix.START_PAGE;

/**
 * UserReviewsFragment - User Reviews Page
 */

public class UserReviewsFragment extends Fragment implements NetworkLoader.NetworkLoaderCallback {

    private final Gson gson = new Gson();
    private Context mContext;
    private long mResourceId;
    private NetworkLoader mNetworkLoader;

    private RecyclerView.LayoutManager mLayoutManager;
    private List<MovieReview> mReviewsList;
    private MovieReviewsAdapter mAdapter;

    private EndlessRecyclerViewScrollListener mEndlessScollListener;
    private int mPage = START_PAGE;

    private FragmentUserReviewsBinding mBinding;
    private ContentLoadingUtil mContentLoadingUtil;

    public static UserReviewsFragment newInstance(long resourceId) {
        UserReviewsFragment userReviewsFragment = new UserReviewsFragment();

        Bundle args = new Bundle();
        args.putLong(RESOURCE_ID, resourceId);
        userReviewsFragment.setArguments(args);

        return userReviewsFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getContext();

        mReviewsList = new ArrayList<>();
        mLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        mNetworkLoader = new NetworkLoader(mContext, this);
        mAdapter = new MovieReviewsAdapter();

        mEndlessScollListener = new EndlessRecyclerViewScrollListener(mPage, mLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                mPage = page;
                mAdapter.showLoading(true);
                fetchMovieReviews(mResourceId);
            }
        };
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_user_reviews, container, false);
        mContentLoadingUtil = ContentLoadingUtil.with(mContext)
                .setContent(mBinding.recyclerViewUserReviews)
                .setProgress(mBinding.progressBarLoading)
                .setError(mBinding.textViewErrorMessage);

        mBinding.recyclerViewUserReviews.setLayoutManager(mLayoutManager);
        mBinding.recyclerViewUserReviews.setHasFixedSize(true);
        mBinding.recyclerViewUserReviews.setAdapter(mAdapter);
        mBinding.recyclerViewUserReviews.addOnScrollListener(mEndlessScollListener);

        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL);
        mBinding.recyclerViewUserReviews.addItemDecoration(itemDecoration);

        if (savedInstanceState == null) {
            //noinspection ConstantConditions
            mResourceId = getArguments().getLong(RESOURCE_ID, 0);

            if (mResourceId != 0) {
                fetchMovieReviews(mResourceId);
            }
        } else {
            List<MovieReview> movieReviewList = savedInstanceState.getParcelableArrayList(MOVIE_REVIEWS);
            updateReviewsUI(movieReviewList);
        }

        return mBinding.getRoot();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(RESOURCE_ID, mResourceId);
        outState.putParcelableArrayList(MOVIE_REVIEWS, (ArrayList<? extends Parcelable>) mReviewsList);
    }

    @Override
    public void onLoadFinished(Loader loader, String s) {
        switch (loader.getId()) {
            case MOVIE_REVIEWS_LOADER_ID:
                MovieReviewsResult movieReviewsResult = (s == null) ? null : gson.fromJson(s, MovieReviewsResult.class);
                List<MovieReview> movieReviewList = (movieReviewsResult == null) ? null : movieReviewsResult.getResults();
                updateReviewsUI(movieReviewList);
                break;
        }
    }

    private void fetchMovieReviews(final long movieId) {
        HTTPLoaderUtil.with(mContext).tryCall(new HTTPLoaderUtil.HTTPBlock() {
            @Override
            public void run() {
                loadMovieReviewsFromNetwork(movieId);
            }
        }).onNoNetwork(new HTTPLoaderUtil.HTTPBlock() {
            @Override
            public void run() {
                if (mPage == START_PAGE) {
                    DisplayUtils.setNoNetworkConnectionMessage(mContext, mBinding.textViewErrorMessage);
                    mContentLoadingUtil.error();
                } else {
                    Toast.makeText(mContext, getResources().getString(R.string.no_network_connection_error_message), Toast.LENGTH_SHORT).show();
                    mAdapter.showLoading(false);
                    mAdapter.notifyDataSetChanged();
                }
            }
        }).execute();
    }

    /**
     * Loads movie reviews from Network
     */
    private void loadMovieReviewsFromNetwork(long movieId) {
        URL url = URIBuilderUtils.buildMovieReviewsURL(String.valueOf(movieId), mPage);
        Bundle args = new Bundle();
        args.putSerializable(NetworkLoader.URL_EXTRA, url);
        if (getActivity() != null && getActivity().getSupportLoaderManager() != null) {
            getActivity().getSupportLoaderManager().restartLoader(MOVIE_REVIEWS_LOADER_ID, args, mNetworkLoader);
        }
    }

    /**
     * Updates Reviews list
     */
    private void updateReviewsUI(List<MovieReview> movieReviewList) {
        if (movieReviewList == null || movieReviewList.isEmpty()) {
            if (mPage == START_PAGE) {
                mContentLoadingUtil.error();
            } else {
                mAdapter.showLoading(false);
                mAdapter.notifyDataSetChanged();
            }
        } else {
            if (mPage == START_PAGE) {
                mContentLoadingUtil.success();
            }

            mReviewsList.addAll(movieReviewList);
            mAdapter.setReviewList(mReviewsList);
        }
    }
}

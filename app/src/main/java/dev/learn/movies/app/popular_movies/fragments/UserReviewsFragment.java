package dev.learn.movies.app.popular_movies.fragments;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Parcelable;
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
import dev.learn.movies.app.popular_movies.util.DisplayUtils;
import dev.learn.movies.app.popular_movies.views.EndlessRecyclerViewScrollListener;
import dev.learn.movies.app.popular_movies.util.HTTPHelper;

import static dev.learn.movies.app.popular_movies.Inflix.MOVIE_ID;
import static dev.learn.movies.app.popular_movies.Inflix.MOVIE_REVIEWS;
import static dev.learn.movies.app.popular_movies.Inflix.MOVIE_REVIEWS_LOADER_ID;
import static dev.learn.movies.app.popular_movies.Inflix.START_PAGE;

/**
 * Created by sudhar on 11/14/17.
 */

public class UserReviewsFragment extends Fragment implements NetworkLoader.NetworkLoaderCallback {

    private Context mContext;

    private long mMovieId;

    private FragmentUserReviewsBinding mBinding;

    private final Gson gson = new Gson();
    private NetworkLoader mNetworkLoader;

    private RecyclerView.LayoutManager mLayoutManager;
    private List<MovieReview> mReviewsList;
    private MovieReviewsAdapter mAdapter;

    private EndlessRecyclerViewScrollListener mEndlessScollListener;
    private int mPage = START_PAGE;

    public static UserReviewsFragment newInstance(long movieId) {
        UserReviewsFragment userReviewsFragment = new UserReviewsFragment();

        Bundle args = new Bundle();
        args.putLong(MOVIE_ID, movieId);
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
                fetchMovieReviews(mMovieId);
            }
        };
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_user_reviews, container, false);

        mBinding.rvUserReviews.setLayoutManager(mLayoutManager);
        mBinding.rvUserReviews.setHasFixedSize(true);
        mBinding.rvUserReviews.setAdapter(mAdapter);
        mBinding.rvUserReviews.addOnScrollListener(mEndlessScollListener);

        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL);
        mBinding.rvUserReviews.addItemDecoration(itemDecoration);

        if (savedInstanceState == null) {
            mMovieId = getArguments().getLong(MOVIE_ID, 0);

            if (mMovieId != 0) {
                fetchMovieReviews(mMovieId);
            }
        } else {
            List<MovieReview> movieReviewList = savedInstanceState.getParcelableArrayList(MOVIE_REVIEWS);
            updateReviewsUI(movieReviewList);
        }

        return mBinding.getRoot();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(MOVIE_ID, mMovieId);
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

    private void fetchMovieReviews(long movieId) {
        if (HTTPHelper.isNetworkEnabled(mContext)) {
            loadMovieReviewsFromNetwork(movieId);
        } else {
            if (mPage == START_PAGE) {
                DisplayUtils.setNoNetworkConnectionMessage(mContext, mBinding.tvReviewsErrorMessageDisplay);
                showReviewsErrorMessage();
            } else {
                Toast.makeText(mContext, getResources().getString(R.string.no_network_connection_error_message), Toast.LENGTH_SHORT).show();
                mAdapter.showLoading(false);
                mAdapter.notifyDataSetChanged();
            }
        }
    }

    /**
     * Loads movie reviews from Network
     */
    private void loadMovieReviewsFromNetwork(long movieId) {
        URL url = HTTPHelper.buildMovieReviewsURL(String.valueOf(movieId), mPage);
        Bundle args = new Bundle();
        args.putSerializable(NetworkLoader.URL_EXTRA, url);
        getActivity().getSupportLoaderManager().restartLoader(MOVIE_REVIEWS_LOADER_ID, args, mNetworkLoader);
    }

    /**
     * Updates Reviews list
     */
    private void updateReviewsUI(List<MovieReview> movieReviewList) {
        if (movieReviewList == null || movieReviewList.isEmpty()) {
            if (mPage == START_PAGE) {
                showReviewsErrorMessage();
            } else {
                mAdapter.showLoading(false);
                mAdapter.notifyDataSetChanged();
            }
        } else {
            if (mPage == START_PAGE) {
                showReviews();
            }

            mReviewsList.addAll(movieReviewList);
            mAdapter.setReviewList(mReviewsList);
        }
    }

    /**
     * Shows MovieDetailLayout, Hides ProgressBar and ErrorMessage
     */
    private void showReviews() {
        mBinding.rvUserReviews.setVisibility(View.VISIBLE);
        mBinding.pbUserReviews.setVisibility(View.INVISIBLE);
        mBinding.tvReviewsErrorMessageDisplay.setVisibility(View.INVISIBLE);
    }

    /**
     * Shows ErrorMessage, Hides ProgressBar and MovieDetailLayout
     */
    private void showReviewsErrorMessage() {
        mBinding.tvReviewsErrorMessageDisplay.setVisibility(View.VISIBLE);
        mBinding.pbUserReviews.setVisibility(View.INVISIBLE);
        mBinding.rvUserReviews.setVisibility(View.INVISIBLE);
    }
}

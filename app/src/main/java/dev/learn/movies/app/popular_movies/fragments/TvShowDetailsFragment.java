package dev.learn.movies.app.popular_movies.fragments;

import android.content.Context;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import dev.learn.movies.app.popular_movies.R;
import dev.learn.movies.app.popular_movies.activities.MovieDetailCallbacks;
import dev.learn.movies.app.popular_movies.common.Video;
import dev.learn.movies.app.popular_movies.databinding.FragmentTvShowDetailsBinding;
import dev.learn.movies.app.popular_movies.loaders.ContentLoader;
import dev.learn.movies.app.popular_movies.loaders.NetworkLoader;

/**
 * Created by sudharti on 11/13/17.
 */

public class TvShowDetailsFragment extends Fragment implements NetworkLoader.NetworkLoaderCallback, ContentLoader.ContentLoaderCallback {

    public static final String TV_SHOW_ID = "movie_id";
    private static final String TV_SHOW_DETAILS = "movie_details";
    private static final String TV_SHOW_REVIEWS = "movie_reviews";
    private static final String TV_SHOW_TRAILERS = "movie_trailers";
    private static final String FAVORED = "favored";

    private Context mContext;
    private MovieDetailCallbacks mCallbacks;

    private final Gson gson = new Gson();
    private long mTvShowId = 0L;

    private NetworkLoader mNetworkLoader;
    private ContentLoader mContentLoader;

    private List<Video> mVideoList;

    private boolean mFavored = false;

    private FragmentTvShowDetailsBinding mBinding;

    public static MovieDetailsFragment newInstance(long tvShowId) {
        MovieDetailsFragment movieDetailsFragment = new MovieDetailsFragment();

        Bundle args = new Bundle();
        args.putLong(TV_SHOW_ID, tvShowId);
        movieDetailsFragment.setArguments(args);

        return movieDetailsFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getContext();
        mVideoList = new ArrayList<>();
        mNetworkLoader = new NetworkLoader(mContext, this);
        mContentLoader = new ContentLoader(mContext, this);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_tv_show_details, container, false);
        View view = mBinding.getRoot();
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mCallbacks = (MovieDetailCallbacks) getActivity();
    }

    @Override
    public void onLoadFinished(Loader loader, String s) {

    }

    @Override
    public void onLoadFinished(Loader loader, Cursor cursor) {

    }
}

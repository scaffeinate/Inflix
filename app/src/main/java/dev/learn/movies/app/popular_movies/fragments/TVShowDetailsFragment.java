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

import dev.learn.movies.app.popular_movies.R;
import dev.learn.movies.app.popular_movies.activities.MovieDetailCallbacks;
import dev.learn.movies.app.popular_movies.databinding.FragmentTvShowDetailsBinding;
import dev.learn.movies.app.popular_movies.loaders.ContentLoader;
import dev.learn.movies.app.popular_movies.loaders.NetworkLoader;

/**
 * Created by sudharti on 11/13/17.
 */

public class TVShowDetailsFragment extends Fragment implements NetworkLoader.NetworkLoaderCallback, ContentLoader.ContentLoaderCallback {

    public static final String TV_SHOW_ID = "tv_show_id";
    public static final String TV_SHOW_TITLE = "tv_show_title";
    private static final String TV_SHOW_DETAILS = "tv_show_details";

    private Context mContext;
    private MovieDetailCallbacks mCallbacks;

    private final Gson gson = new Gson();
    private long mTvShowId = 0L;

    private NetworkLoader mNetworkLoader;
    private ContentLoader mContentLoader;

    private FragmentTvShowDetailsBinding mBinding;

    public static MovieDetailsFragment newInstance(long tvShowId, String tvShowTitle) {
        MovieDetailsFragment movieDetailsFragment = new MovieDetailsFragment();

        Bundle args = new Bundle();
        args.putLong(TV_SHOW_ID, tvShowId);
        args.putString(TV_SHOW_TITLE, tvShowTitle);
        movieDetailsFragment.setArguments(args);

        return movieDetailsFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getContext();
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

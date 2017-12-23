package dev.learn.movies.app.popular_movies.fragments;

import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import java.net.URL;
import java.util.List;

import dev.learn.movies.app.popular_movies.R;
import dev.learn.movies.app.popular_movies.adapters.SeasonsAdapter;
import dev.learn.movies.app.popular_movies.common.Genre;
import dev.learn.movies.app.popular_movies.common.Media;
import dev.learn.movies.app.popular_movies.common.VideosResult;
import dev.learn.movies.app.popular_movies.common.cast.CastsResult;
import dev.learn.movies.app.popular_movies.common.tv_show.CreatedBy;
import dev.learn.movies.app.popular_movies.common.tv_show.Season;
import dev.learn.movies.app.popular_movies.common.tv_show.TVShowDetail;
import dev.learn.movies.app.popular_movies.common.tv_show.TVShowsResult;
import dev.learn.movies.app.popular_movies.data.DataContract;
import dev.learn.movies.app.popular_movies.databinding.FragmentTvShowDetailsBinding;
import dev.learn.movies.app.popular_movies.utils.ContentLoadingUtil;
import dev.learn.movies.app.popular_movies.utils.DisplayUtils;
import dev.learn.movies.app.popular_movies.utils.HTTPLoaderUtil;
import dev.learn.movies.app.popular_movies.utils.URIBuilderUtils;

import static dev.learn.movies.app.popular_movies.Inflix.LOCAL_TV_SHOW_DETAILS_LOADER_ID;
import static dev.learn.movies.app.popular_movies.Inflix.RESOURCE_ID;
import static dev.learn.movies.app.popular_movies.Inflix.RESOURCE_TITLE;
import static dev.learn.movies.app.popular_movies.Inflix.TV_SHOWS_CAST_LOADER_ID;
import static dev.learn.movies.app.popular_movies.Inflix.TV_SHOWS_DETAILS_LOADER_ID;
import static dev.learn.movies.app.popular_movies.Inflix.TV_SHOWS_SIMILAR_LOADER_ID;
import static dev.learn.movies.app.popular_movies.Inflix.TV_SHOWS_TRAILERS_LOADER_ID;

/**
 * Created by sudharti on 11/13/17.
 */

public class TVShowDetailsFragment extends BaseDetailsFragment {

    protected RecyclerView.LayoutManager mSeasonsLayoutManager;
    private FragmentTvShowDetailsBinding mBinding;
    private ContentLoadingUtil mSeasonLoadingUtil;
    private SeasonsAdapter mSeasonsAdapter;

    public static TVShowDetailsFragment newInstance(long tvShowId, String tvShowTitle) {
        TVShowDetailsFragment tvShowDetailsFragment = new TVShowDetailsFragment();

        Bundle args = new Bundle();
        args.putLong(RESOURCE_ID, tvShowId);
        args.putString(RESOURCE_TITLE, tvShowTitle);
        tvShowDetailsFragment.setArguments(args);

        return tvShowDetailsFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSeasonsAdapter = new SeasonsAdapter(getActivity(), this);
        mSeasonsLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_tv_show_details, container, false);
        View view = mBinding.getRoot();

        mBinding.layoutSimilar.recyclerViewSimilar.setLayoutManager(mSimilarLayoutManager);
        mBinding.layoutSimilar.recyclerViewSimilar.setAdapter(mSimilarAdapter);
        mBinding.layoutSimilar.recyclerViewSimilar.setNestedScrollingEnabled(false);

        mBinding.layoutCast.recyclerViewCast.setLayoutManager(mFilmCastLayoutManager);
        mBinding.layoutCast.recyclerViewCast.setAdapter(mFilmCastAdapter);
        mBinding.layoutCast.recyclerViewCast.setNestedScrollingEnabled(false);

        mBinding.layoutSimilar.textViewSimilarTitle.setText(getString(R.string.similar_tv_shows));

        mBinding.layoutSeasons.recyclerViewSeasons.setLayoutManager(mSeasonsLayoutManager);
        mBinding.layoutSeasons.recyclerViewSeasons.setAdapter(mSeasonsAdapter);
        mBinding.layoutSeasons.recyclerViewSeasons.setNestedScrollingEnabled(false);

        mSeasonLoadingUtil = ContentLoadingUtil.with(mContext);

        mSeasonLoadingUtil
                .setParent(mBinding.layoutSeasons.getRoot())
                .setContent(mBinding.layoutSeasons.recyclerViewSeasons)
                .setProgress(mBinding.layoutSeasons.progressBarSeasons)
                .hideParentOnError();

        mContentLoadingUtil.setContent(mBinding.layoutTvShowDetail)
                .setError(mBinding.textViewErrorMessageDisplay)
                .setProgress(mBinding.progressBarTvShowDetails);

        mSimilarLoadingUtil
                .setParent(mBinding.layoutSimilar.getRoot())
                .setContent(mBinding.layoutSimilar.recyclerViewSimilar)
                .setProgress(mBinding.layoutSimilar.progressBarSimilar)
                .hideParentOnError();

        mCastLoadingUtil.setParent(mBinding.layoutCast.getRoot())
                .setContent(mBinding.layoutCast.recyclerViewCast)
                .setProgress(mBinding.layoutCast.progressBarCast)
                .hideParentOnError();

        adjustPosterSize(mBinding.layoutTvShowInfo.layoutPoster.getRoot());
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState == null) {
            mResourceId = getArguments().getLong(RESOURCE_ID, 0);
            mResourceTitle = getArguments().getString(RESOURCE_TITLE, "");

            if (mResourceId != 0) {
                final URL similarURL = URIBuilderUtils.buildSimilarTVShowsURL(String.valueOf(mResourceId));
                final URL castsURL = URIBuilderUtils.buildTVShowCastURL(String.valueOf(mResourceId));

                Uri uri = DataContract.MEDIA_CONTENT_URI
                        .buildUpon()
                        .appendPath(String.valueOf(mResourceId))
                        .build();
                loadFromDatabase(uri, LOCAL_TV_SHOW_DETAILS_LOADER_ID);
                lazyLoadAdditionalInfoFromNetwork(new Runnable() {
                    @Override
                    public void run() {
                        if (similarURL != null) {
                            loadFromNetwork(similarURL, TV_SHOWS_SIMILAR_LOADER_ID);
                        }

                        if (castsURL != null) {
                            loadFromNetwork(castsURL, TV_SHOWS_CAST_LOADER_ID);
                        }
                    }
                });
            } else {
                mContentLoadingUtil.error();
                mCastLoadingUtil.error();
                mSimilarLoadingUtil.error();
            }
        } else {
            mResourceId = savedInstanceState.getLong(RESOURCE_ID);
            mResourceTitle = savedInstanceState.getString(RESOURCE_TITLE);
            mTVShowDetail = savedInstanceState.getParcelable(DETAILS);
            mSimilarList = savedInstanceState.getParcelableArrayList(SIMILAR);
            mCastList = savedInstanceState.getParcelableArrayList(CAST);

            updateContent();
            updateCasts();
            updateSimilar();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_tv_show_detail, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onItemClicked(ViewGroup parent, View view, int position) {
        switch (parent.getId()) {
            case R.id.recycler_view_similar:
                Media media = mSimilarList.get(position);
                if (media != null) {
                    getActivity().getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.layout_outlet, TVShowDetailsFragment.newInstance(media.getId(), media.getName()))
                            .commit();
                    mCallbacks.scrollToTop();
                }
                break;
            case R.id.recycler_view_seasons:
                //TODO: Handle this in future release
                break;
        }
    }

    @Override
    public void onLoadFinished(Loader loader, String s) {
        // Handle responses for the different loaderId calls
        switch (loader.getId()) {
            case TV_SHOWS_DETAILS_LOADER_ID:
                mTVShowDetail = (s == null) ? null : gson.fromJson(s, TVShowDetail.class);
                updateContent();
                break;
            case TV_SHOWS_TRAILERS_LOADER_ID:
                VideosResult videosResult = (s == null) ? null : gson.fromJson(s, VideosResult.class);
                if (videosResult != null && videosResult.getVideos() != null && !videosResult.getVideos().isEmpty()) {
                    mVideoGridDialog
                            .setVideos(videosResult.getVideos())
                            .success();
                } else {
                    mVideoGridDialog.error();
                }
                break;
            case TV_SHOWS_SIMILAR_LOADER_ID:
                TVShowsResult similarTVShowsRes = (s == null) ? null : gson.fromJson(s, TVShowsResult.class);
                if (similarTVShowsRes != null) {
                    mSimilarList = similarTVShowsRes.getResults();
                }
                updateSimilar();
                break;
            case TV_SHOWS_CAST_LOADER_ID:
                CastsResult castsResult = (s == null) ? null : gson.fromJson(s, CastsResult.class);
                if (castsResult != null) {
                    mCastList = castsResult.getCast();
                }
                updateCasts();
                break;
        }
    }

    @Override
    public void onLoadFinished(Loader loader, Cursor cursor) {
        if (cursor == null || !cursor.moveToFirst()) {
            HTTPLoaderUtil.with(mContext)
                    .tryCall(new HTTPLoaderUtil.HTTPBlock() {
                        @Override
                        public void run() {
                            loadFromNetwork(URIBuilderUtils.buildTVShowDetailsURL(String.valueOf(mResourceId)),
                                    TV_SHOWS_DETAILS_LOADER_ID);
                        }
                    })
                    .onNoNetwork(new HTTPLoaderUtil.HTTPBlock() {
                        @Override
                        public void run() {
                            DisplayUtils.setNoNetworkConnectionMessage(mContext, mBinding.textViewErrorMessageDisplay);
                            mContentLoadingUtil.error();
                            mCallbacks.hideFavBtn();
                        }
                    }).execute();
        } else {
            switch (loader.getId()) {
                case LOCAL_TV_SHOW_DETAILS_LOADER_ID:
                    mTVShowDetail = TVShowDetail.fromCursor(cursor);
                    updateContent();
                    break;
            }
        }
    }

    @Override
    protected void updateContent() {
        if (mTVShowDetail == null) {
            mContentLoadingUtil.error();
            mCallbacks.hideFavBtn();
            return;
        }

        double voteAverage = mTVShowDetail.getVoteAverage();
        String backdropURL = mTVShowDetail.getBackdropPath();
        String posterURL = mTVShowDetail.getPosterPath();
        String title = mTVShowDetail.getName();
        String episodeRunningTime = "0 min";
        if (mTVShowDetail.getEpisodeRunTime() != null && !mTVShowDetail.getEpisodeRunTime().isEmpty()) {
            episodeRunningTime = mTVShowDetail.getEpisodeRunTime().get(0) + " min";
        }
        String status = mTVShowDetail.getStatus();
        String rating = String.valueOf(voteAverage);
        String voteCount = "(" + mTVShowDetail.getVoteCount() + ")";
        String plot = mTVShowDetail.getOverview();
        String numEpisodes = mTVShowDetail.getNumberOfEpisodes() + " Episodes";
        String numSeasons = (mTVShowDetail.getNumberOfSeasons() + 1) + " Seasons";
        String firstAired = mTVShowDetail.getFirstAirDate();
        String lastAired = mTVShowDetail.getLastAirDate();
        List<Genre> genres = mTVShowDetail.getGenres();
        List<CreatedBy> createdByList = mTVShowDetail.getCreatedBy();
        List<Season> seasonList = mTVShowDetail.getSeasons();

        if (backdropURL != null) {
            Uri backdropUri = URIBuilderUtils.buildImageResourceUri(backdropURL, URIBuilderUtils.IMAGE_SIZE_XLARGE);
            mCallbacks.updateBackdrop(backdropUri);
        }

        if (posterURL != null) {
            Uri posterUri = URIBuilderUtils.buildImageResourceUri(posterURL, URIBuilderUtils.IMAGE_SIZE_SMALL);
            DisplayUtils.fitImageInto(mBinding.layoutTvShowInfo.layoutPoster.imageViewPoster, posterUri);
        }

        mBinding.layoutTvShowInfo.textViewTvShowTitle.setText(title);

        mBinding.layoutTvShowInfo.textViewTvShowRuntime.setText(episodeRunningTime);

        mBinding.layoutTvShowInfo.textViewTvShowNumSeasons.setText(numSeasons);

        mBinding.layoutTvShowInfo.textViewTvShowNumEpisodes.setText(numEpisodes);

        if (!TextUtils.isEmpty(status)) {
            mBinding.layoutTvShowInfo.textViewTvShowStatus.setText(status);
        } else {
            mBinding.layoutTvShowInfo.textViewTvShowStatus.setVisibility(View.GONE);
        }

        if (genres != null && !genres.isEmpty()) {
            DisplayUtils.addGenres(genres, mBinding.layoutTvShowContent.layoutGenres, mContext);
        } else {
            mBinding.layoutTvShowContent.textViewMovieGenresTitle.setVisibility(View.GONE);
            mBinding.layoutTvShowContent.layoutGenres.setVisibility(View.GONE);
        }

        if (createdByList != null && !createdByList.isEmpty()) {
            DisplayUtils.addCreatedBy(createdByList, mBinding.layoutTvShowContent.layoutCreatedBy, mContext);
        } else {
            mBinding.layoutTvShowContent.textViewCreatedByTitle.setVisibility(View.GONE);
            mBinding.layoutTvShowContent.layoutCreatedBy.setVisibility(View.GONE);
        }

        mBinding.layoutTvShowInfo.layoutRating.ratingBarRating.setRating((float) voteAverage);

        mBinding.layoutTvShowInfo.layoutRating.textViewRating.setText(rating);

        mBinding.layoutTvShowInfo.layoutRating.textViewRatingNum.setText(voteCount);

        if (!TextUtils.isEmpty(plot)) {
            mBinding.layoutTvShowContent.textViewTvShowPlot.setText(plot);
        }

        if (!TextUtils.isEmpty(firstAired)) {
            mBinding.layoutTvShowContent.textViewFirstAirDate.setText(DisplayUtils.formatDate(firstAired));
        } else {
            mBinding.layoutTvShowContent.textViewFirstAirDate.setText("N/A");
        }

        if (!TextUtils.isEmpty(lastAired)) {
            mBinding.layoutTvShowContent.textViewLastAirDate.setText(DisplayUtils.formatDate(lastAired));
        } else {
            mBinding.layoutTvShowContent.textViewLastAirDate.setText("N/A");
        }

        if (seasonList != null && !seasonList.isEmpty()) {
            mSeasonsAdapter.setSeasonList(seasonList);
            mSeasonLoadingUtil.success();
        } else {
            mSeasonLoadingUtil.error();
        }

        mCallbacks.updateFavBtn(mTVShowDetail.isFavored());
        mContentLoadingUtil.success();
        mCallbacks.showFavBtn();
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                updateBookmarkBtn(mTVShowDetail.isBookmarked());
            }
        });
    }
}

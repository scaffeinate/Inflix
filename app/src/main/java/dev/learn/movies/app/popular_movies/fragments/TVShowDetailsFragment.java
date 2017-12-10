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
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import dev.learn.movies.app.popular_movies.R;
import dev.learn.movies.app.popular_movies.adapters.SeasonsAdapter;
import dev.learn.movies.app.popular_movies.common.Genre;
import dev.learn.movies.app.popular_movies.common.VideosResult;
import dev.learn.movies.app.popular_movies.common.cast.CastsResult;
import dev.learn.movies.app.popular_movies.common.tv_show.CreatedBy;
import dev.learn.movies.app.popular_movies.common.tv_show.Season;
import dev.learn.movies.app.popular_movies.common.tv_show.TVShow;
import dev.learn.movies.app.popular_movies.common.tv_show.TVShowDetail;
import dev.learn.movies.app.popular_movies.common.tv_show.TVShowsResult;
import dev.learn.movies.app.popular_movies.databinding.FragmentTvShowDetailsBinding;
import dev.learn.movies.app.popular_movies.util.DisplayUtils;
import dev.learn.movies.app.popular_movies.util.HTTPHelper;
import dev.learn.movies.app.popular_movies.util.LoadingContentUtil;

import static dev.learn.movies.app.popular_movies.util.AppConstants.LOCAL_TV_SHOW_DETAILS_LOADER_ID;
import static dev.learn.movies.app.popular_movies.util.AppConstants.RESOURCE_ID;
import static dev.learn.movies.app.popular_movies.util.AppConstants.RESOURCE_TITLE;
import static dev.learn.movies.app.popular_movies.util.AppConstants.TV_SHOWS_CAST_LOADER_ID;
import static dev.learn.movies.app.popular_movies.util.AppConstants.TV_SHOWS_DETAILS_LOADER_ID;
import static dev.learn.movies.app.popular_movies.util.AppConstants.TV_SHOWS_SIMILAR_LOADER_ID;
import static dev.learn.movies.app.popular_movies.util.AppConstants.TV_SHOWS_TRAILERS_LOADER_ID;

/**
 * Created by sudharti on 11/13/17.
 */

public class TVShowDetailsFragment extends BaseDetailsFragment {

    private FragmentTvShowDetailsBinding mBinding;

    protected RecyclerView.LayoutManager mSeasonsLayoutManager;
    private LoadingContentUtil mSeasonLoadingUtil;
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
        mSeasonsAdapter = new SeasonsAdapter(this);
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

        mSeasonLoadingUtil = LoadingContentUtil.with(mContext);

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
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                mCallbacks.scrollToTop();
            }
        });

        adjustPosterSize(mBinding.layoutTvShowInfo.layoutPoster.getRoot());
        return view;
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
        String numSeasons = mTVShowDetail.getNumberOfSeasons() + " Seasons";
        String firstAired = mTVShowDetail.getFirstAirDate();
        String lastAired = mTVShowDetail.getLastAirDate();
        List<Genre> genres = mTVShowDetail.getGenres();
        List<CreatedBy> createdByList = mTVShowDetail.getCreatedBy();
        List<Season> seasonList = mTVShowDetail.getSeasons();

        if (backdropURL != null) {
            Uri backdropUri = HTTPHelper.buildImageResourceUri(backdropURL, HTTPHelper.IMAGE_SIZE_XLARGE);
            mCallbacks.updateBackdrop(backdropUri);
        }

        if (posterURL != null) {
            Uri posterUri = HTTPHelper.buildImageResourceUri(posterURL, HTTPHelper.IMAGE_SIZE_SMALL);
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

        mBinding.layoutTvShowContent.textViewFirstAirDate.setText(DisplayUtils.formatDate(firstAired));

        mBinding.layoutTvShowContent.textViewLastAirDate.setText(DisplayUtils.formatDate(lastAired));

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
            if (HTTPHelper.isNetworkEnabled(mContext)) {
                loadFromNetwork(HTTPHelper.buildTVShowDetailsURL(String.valueOf(mResourceId)), TV_SHOWS_DETAILS_LOADER_ID);
            } else {
                DisplayUtils.setNoNetworkConnectionMessage(mContext, mBinding.textViewErrorMessageDisplay);
                mContentLoadingUtil.error();
                mCallbacks.hideFavBtn();
            }
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
    public void onItemClicked(ViewGroup parent, View view, int position) {
        switch (parent.getId()) {
            case R.id.recycler_view_similar:
                TVShow tvShow = (TVShow) mSimilarList.get(position);
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.layout_outlet, TVShowDetailsFragment.newInstance(tvShow.getId(), tvShow.getName()))
                        .commit();
                break;
            case R.id.recycler_view_seasons:
                break;
        }
    }
}

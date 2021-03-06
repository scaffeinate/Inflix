package dev.learn.movies.app.popular_movies.fragments;

import android.content.Intent;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.net.URL;
import java.util.List;

import dev.learn.movies.app.popular_movies.R;
import dev.learn.movies.app.popular_movies.activities.AdditionalInfoActivity;
import dev.learn.movies.app.popular_movies.common.Genre;
import dev.learn.movies.app.popular_movies.common.Media;
import dev.learn.movies.app.popular_movies.common.VideosResult;
import dev.learn.movies.app.popular_movies.common.cast.CastsResult;
import dev.learn.movies.app.popular_movies.common.movies.MovieDetail;
import dev.learn.movies.app.popular_movies.common.movies.MoviesResult;
import dev.learn.movies.app.popular_movies.data.DataContract;
import dev.learn.movies.app.popular_movies.databinding.FragmentMovieDetailsBinding;
import dev.learn.movies.app.popular_movies.utils.DisplayUtils;
import dev.learn.movies.app.popular_movies.utils.HTTPLoaderUtil;
import dev.learn.movies.app.popular_movies.utils.URIBuilderUtils;

import static dev.learn.movies.app.popular_movies.Inflix.ADDITIONAL_INFO_ACTIVITY_FRAGMENT_TYPE_REVIEWS;
import static dev.learn.movies.app.popular_movies.Inflix.LOCAL_MOVIE_DETAILS_LOADER_ID;
import static dev.learn.movies.app.popular_movies.Inflix.MOVIE_CAST_LOADER_ID;
import static dev.learn.movies.app.popular_movies.Inflix.MOVIE_DETAILS_LOADER_ID;
import static dev.learn.movies.app.popular_movies.Inflix.MOVIE_SIMILAR_LOADER_ID;
import static dev.learn.movies.app.popular_movies.Inflix.MOVIE_TRAILERS_LOADER_ID;
import static dev.learn.movies.app.popular_movies.Inflix.RESOURCE_ID;
import static dev.learn.movies.app.popular_movies.Inflix.RESOURCE_TITLE;
import static dev.learn.movies.app.popular_movies.Inflix.RESOURCE_TYPE;

/**
 * MovieDetailsFragment - Movie Details Fragment
 */

public class MovieDetailsFragment extends BaseDetailsFragment implements View.OnClickListener {

    private FragmentMovieDetailsBinding mBinding;

    public static MovieDetailsFragment newInstance(long movieId, String movieName) {
        MovieDetailsFragment movieDetailsFragment = new MovieDetailsFragment();

        Bundle args = new Bundle();
        args.putLong(RESOURCE_ID, movieId);
        args.putString(RESOURCE_TITLE, movieName);
        movieDetailsFragment.setArguments(args);

        return movieDetailsFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_movie_details, container, false);
        View view = mBinding.getRoot();

        mBinding.layoutInfo.layoutRating.getRoot().setOnClickListener(this);

        mBinding.layoutSimilar.recyclerViewSimilar.setLayoutManager(mSimilarLayoutManager);
        mBinding.layoutSimilar.recyclerViewSimilar.setAdapter(mSimilarAdapter);
        mBinding.layoutSimilar.recyclerViewSimilar.setNestedScrollingEnabled(false);

        mBinding.layoutCast.recyclerViewCast.setLayoutManager(mFilmCastLayoutManager);
        mBinding.layoutCast.recyclerViewCast.setAdapter(mFilmCastAdapter);
        mBinding.layoutCast.recyclerViewCast.setNestedScrollingEnabled(false);

        mBinding.layoutSimilar.textViewSimilarTitle.setText(getString(R.string.similar_movies));

        mContentLoadingUtil.setContent(mBinding.layoutMovieDetail)
                .setError(mBinding.textViewErrorMessage)
                .setProgress(mBinding.progressBar);

        mSimilarLoadingUtil
                .setParent(mBinding.layoutSimilar.getRoot())
                .setContent(mBinding.layoutSimilar.recyclerViewSimilar)
                .setProgress(mBinding.layoutSimilar.progressBarSimilar)
                .hideParentOnError();

        mCastLoadingUtil.setParent(mBinding.layoutCast.getRoot())
                .setContent(mBinding.layoutCast.recyclerViewCast)
                .setProgress(mBinding.layoutCast.progressBarCast)
                .hideParentOnError();

        adjustPosterSize(mBinding.layoutInfo.layoutPoster.getRoot());
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState == null) {
            //noinspection ConstantConditions
            mResourceId = getArguments().getLong(RESOURCE_ID, 0);
            mResourceTitle = getArguments().getString(RESOURCE_TITLE, "");

            if (mResourceId != 0) {
                final URL similarURL = URIBuilderUtils.buildSimilarMoviesURL(String.valueOf(mResourceId));
                final URL castsURL = URIBuilderUtils.buildMovieCastURL(String.valueOf(mResourceId));
                Uri uri = DataContract.MEDIA_CONTENT_URI
                        .buildUpon()
                        .appendPath(String.valueOf(mResourceId))
                        .build();
                loadFromDatabase(uri, LOCAL_MOVIE_DETAILS_LOADER_ID);
                lazyLoadAdditionalInfoFromNetwork(new Runnable() {
                    @Override
                    public void run() {
                        if (similarURL != null) {
                            loadFromNetwork(similarURL, MOVIE_SIMILAR_LOADER_ID);
                        }

                        if (castsURL != null) {
                            loadFromNetwork(castsURL, MOVIE_CAST_LOADER_ID);
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
            mMovieDetail = savedInstanceState.getParcelable(DETAILS);
            mSimilarList = savedInstanceState.getParcelableArrayList(SIMILAR);
            mCastList = savedInstanceState.getParcelableArrayList(CAST);

            updateContent();
            updateCasts();
            updateSimilar();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_movie_detail, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_user_reviews:
                HTTPLoaderUtil.with(mContext)
                        .tryCall(new HTTPLoaderUtil.HTTPBlock() {
                            @Override
                            public void run() {
                                goToReviews();
                            }
                        })
                        .execute();
                return true;
            case R.id.action_imdb:
                HTTPLoaderUtil.with(mContext)
                        .tryCall(new HTTPLoaderUtil.HTTPBlock() {
                            @Override
                            public void run() {
                                DisplayUtils.openIMDB(mContext, mMovieDetail.getImdbId());
                            }
                        })
                        .execute();
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layout_rating:
                goToReviews();
                break;
        }
    }

    @Override
    public void onItemClicked(ViewGroup parent, View view, int position) {
        switch (parent.getId()) {
            case R.id.recycler_view_similar:
                Media media = mSimilarList.get(position);
                if (media != null && getActivity() != null && getActivity().getSupportFragmentManager() != null) {
                    getActivity().getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.layout_outlet, MovieDetailsFragment.newInstance(media.getId(), media.getTitle()))
                            .commit();
                    mCallbacks.scrollToTop();
                }
                break;
        }
    }

    @Override
    public void onLoadFinished(Loader loader, String s) {
        // Handle responses for the different loaderId calls
        switch (loader.getId()) {
            case MOVIE_DETAILS_LOADER_ID:
                mMovieDetail = (s == null) ? null : gson.fromJson(s, MovieDetail.class);
                updateContent();
                break;
            case MOVIE_TRAILERS_LOADER_ID:
                VideosResult videosResult = (s == null) ? null : gson.fromJson(s, VideosResult.class);
                if (videosResult != null && videosResult.getVideos() != null && !videosResult.getVideos().isEmpty()) {
                    mVideoGridDialog
                            .setVideos(videosResult.getVideos())
                            .success();
                } else {
                    mVideoGridDialog.error();
                }
                break;
            case MOVIE_SIMILAR_LOADER_ID:
                MoviesResult similarMoviesResult = (s == null) ? null : gson.fromJson(s, MoviesResult.class);
                if (similarMoviesResult != null) {
                    mSimilarList = similarMoviesResult.getResults();
                }
                updateSimilar();
                break;
            case MOVIE_CAST_LOADER_ID:
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
            HTTPLoaderUtil.with(mContext).tryCall(new HTTPLoaderUtil.HTTPBlock() {
                @Override
                public void run() {
                    loadFromNetwork(URIBuilderUtils.buildMovieDetailsURL(String.valueOf(mResourceId)), MOVIE_DETAILS_LOADER_ID);
                }
            }).onNoNetwork(new HTTPLoaderUtil.HTTPBlock() {
                @Override
                public void run() {
                    DisplayUtils.setNoNetworkConnectionMessage(mContext, mBinding.textViewErrorMessage);
                    mContentLoadingUtil.error();
                    mCallbacks.hideFavBtn();
                }
            }).execute();

        } else {
            switch (loader.getId()) {
                case LOCAL_MOVIE_DETAILS_LOADER_ID:
                    mMovieDetail = MovieDetail.fromCursor(cursor);
                    updateContent();
                    break;
            }
        }
    }

    @Override
    protected void updateContent() {
        if (mMovieDetail == null) {
            mContentLoadingUtil.error();
            mCallbacks.hideFavBtn();
            return;
        }

        int year = DisplayUtils.getYear(mMovieDetail.getReleaseDate());
        double voteAverage = mMovieDetail.getVoteAverage();
        String backdropURL = mMovieDetail.getBackdropPath();
        String posterURL = mMovieDetail.getPosterPath();
        String title = mMovieDetail.getTitle();
        String runningTime = mMovieDetail.getRuntime() + " " + getString(R.string.min);
        String status = mMovieDetail.getStatus();
        String rating = String.valueOf(voteAverage);
        String voteCount = "(" + mMovieDetail.getVoteCount() + ")";
        String tagline = mMovieDetail.getTagline();
        String moviePlot = mMovieDetail.getOverview();
        long budget = mMovieDetail.getBudget();
        long revenue = mMovieDetail.getRevenue();
        List<Genre> genres = mMovieDetail.getGenres();

        if (backdropURL != null) {
            Uri backdropUri = URIBuilderUtils.buildImageResourceUri(backdropURL, URIBuilderUtils.IMAGE_SIZE_XLARGE);
            mCallbacks.updateBackdrop(backdropUri);
        }

        if (posterURL != null) {
            Uri posterUri = URIBuilderUtils.buildImageResourceUri(posterURL, URIBuilderUtils.IMAGE_SIZE_SMALL);
            DisplayUtils.fitImageInto(mBinding.layoutInfo.layoutPoster.imageViewPoster, posterUri);
        }

        mBinding.layoutInfo.textViewMovieTitle.setText(DisplayUtils.formatTitle(title, year));

        mBinding.layoutInfo.textViewMovieRuntime.setText(runningTime);

        if (!TextUtils.isEmpty(status)) {
            mBinding.layoutInfo.textViewMovieStatus.setText(status);
        } else {
            mBinding.layoutInfo.textViewMovieStatus.setVisibility(View.GONE);
        }

        if (DisplayUtils.addGenres(genres, mBinding.layoutContent.layoutGenres, mContext) == 0) {
            mBinding.layoutContent.textViewMovieGenresTitle.setVisibility(View.GONE);
            mBinding.layoutContent.layoutGenres.setVisibility(View.GONE);
        }

        mBinding.layoutInfo.layoutRating.ratingBar.setRating((float) voteAverage);

        mBinding.layoutInfo.layoutRating.textViewRating.setText(rating);

        mBinding.layoutInfo.layoutRating.textViewRatingNum.setText(voteCount);

        mBinding.layoutContent.textViewMovieTagline.setText(DisplayUtils.formatTagline(mContext, tagline));

        mBinding.layoutContent.textViewBudget.setText(DisplayUtils.formatCurrency(budget));

        mBinding.layoutContent.textViewRevenue.setText(DisplayUtils.formatCurrency(revenue));

        if (!TextUtils.isEmpty(moviePlot)) {
            mBinding.layoutContent.textViewMoviePlot.setText(moviePlot);
        } else {
            mBinding.layoutContent.textViewMoviePlot.setText(getString(R.string.no_plot_available));
        }

        mCallbacks.updateFavBtn(mMovieDetail.isFavored());
        mContentLoadingUtil.success();
        mCallbacks.showFavBtn();
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                updateBookmarkBtn(mMovieDetail.isBookmarked());
            }
        });
    }

    private void goToReviews() {
        HTTPLoaderUtil.with(mContext).tryCall(new HTTPLoaderUtil.HTTPBlock() {
            @Override
            public void run() {
                Intent additionalIntent = new Intent(mContext, AdditionalInfoActivity.class);

                Bundle extras = new Bundle();
                extras.putLong(RESOURCE_ID, mResourceId);
                extras.putString(RESOURCE_TITLE, mResourceTitle);
                extras.putString(RESOURCE_TYPE, ADDITIONAL_INFO_ACTIVITY_FRAGMENT_TYPE_REVIEWS);
                additionalIntent.putExtras(extras);

                startActivity(additionalIntent);
            }
        }).execute();
    }
}

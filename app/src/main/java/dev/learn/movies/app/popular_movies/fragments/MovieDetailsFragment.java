package dev.learn.movies.app.popular_movies.fragments;

import android.content.Intent;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.net.URL;
import java.util.List;

import dev.learn.movies.app.popular_movies.R;
import dev.learn.movies.app.popular_movies.activities.AdditionalInfoActivity;
import dev.learn.movies.app.popular_movies.common.Genre;
import dev.learn.movies.app.popular_movies.common.VideosResult;
import dev.learn.movies.app.popular_movies.common.cast.CastsResult;
import dev.learn.movies.app.popular_movies.common.movies.Movie;
import dev.learn.movies.app.popular_movies.common.movies.MovieDetail;
import dev.learn.movies.app.popular_movies.common.movies.MoviesResult;
import dev.learn.movies.app.popular_movies.data.DataContract;
import dev.learn.movies.app.popular_movies.databinding.FragmentMovieDetailsBinding;
import dev.learn.movies.app.popular_movies.util.DisplayUtils;
import dev.learn.movies.app.popular_movies.util.HTTPHelper;

import static dev.learn.movies.app.popular_movies.util.AppConstants.ADDITIONAL_INFO_ACTIVITY_FRAGMENT_TYPE_REVIEWS;
import static dev.learn.movies.app.popular_movies.util.AppConstants.LOCAL_MOVIE_DETAILS_LOADER_ID;
import static dev.learn.movies.app.popular_movies.util.AppConstants.MOVIE_CAST_LOADER_ID;
import static dev.learn.movies.app.popular_movies.util.AppConstants.MOVIE_DETAILS_LOADER_ID;
import static dev.learn.movies.app.popular_movies.util.AppConstants.MOVIE_SIMILAR_LOADER_ID;
import static dev.learn.movies.app.popular_movies.util.AppConstants.MOVIE_TRAILERS_LOADER_ID;
import static dev.learn.movies.app.popular_movies.util.AppConstants.RESOURCE_ID;
import static dev.learn.movies.app.popular_movies.util.AppConstants.RESOURCE_TITLE;
import static dev.learn.movies.app.popular_movies.util.AppConstants.RESOURCE_TYPE;

/**
 * Created by sudharti on 11/12/17.
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
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_movie_details, container, false);
        View view = mBinding.getRoot();

        mBinding.layoutMovieInfo.layoutRating.getRoot().setOnClickListener(this);

        mBinding.layoutMovieSimilar.recyclerViewSimilar.setLayoutManager(mSimilarLayoutManager);
        mBinding.layoutMovieSimilar.recyclerViewSimilar.setAdapter(mSimilarAdapter);
        mBinding.layoutMovieSimilar.recyclerViewSimilar.setNestedScrollingEnabled(false);

        mBinding.layoutCast.recyclerViewCast.setLayoutManager(mFilmCastLayoutManager);
        mBinding.layoutCast.recyclerViewCast.setAdapter(mFilmCastAdapter);
        mBinding.layoutCast.recyclerViewCast.setNestedScrollingEnabled(false);

        mBinding.layoutMovieSimilar.textViewSimilarTitle.setText(getString(R.string.similar_movies));

        mContentLoadingUtil.setContent(mBinding.layoutMovieDetail)
                .setError(mBinding.textViewErrorMessageDisplay)
                .setProgress(mBinding.progressBarMovieDetails);

        mSimilarLoadingUtil
                .setParent(mBinding.layoutMovieSimilar.getRoot())
                .setContent(mBinding.layoutMovieSimilar.recyclerViewSimilar)
                .setProgress(mBinding.layoutMovieSimilar.progressBarSimilar)
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

        adjustPosterSize(mBinding.layoutMovieInfo.layoutPoster.getRoot());
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState == null) {
            mResourceId = getArguments().getLong(RESOURCE_ID, 0);
            mResourceTitle = getArguments().getString(RESOURCE_TITLE, "");

            if (mResourceId != 0) {
                final URL similarURL = HTTPHelper.buildSimilarMoviesURL(String.valueOf(mResourceId));
                final URL castsURL = HTTPHelper.buildMovieCastURL(String.valueOf(mResourceId));
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
    public boolean onOptionsItemSelected(MenuItem item) {
        if (!HTTPHelper.isNetworkEnabled(mContext)) {
            Toast.makeText(mContext, getResources().getString(R.string.no_network_connection_error_message), Toast.LENGTH_SHORT).show();
            return false;
        }

        switch (item.getItemId()) {
            case R.id.action_user_reviews:
                goToReviews();
                return true;
            case R.id.action_imdb:
                DisplayUtils.openIMDB(mContext, mMovieDetail.getImdbId());
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
        Movie movie = null;
        switch (parent.getId()) {
            case R.id.recycler_view_similar:
                movie = (Movie) mSimilarList.get(position);
                break;
        }

        if (movie != null) {
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.layout_outlet, MovieDetailsFragment.newInstance(movie.getId(), movie.getTitle()))
                    .commit();
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
            if (HTTPHelper.isNetworkEnabled(mContext)) {
                loadFromNetwork(HTTPHelper.buildMovieDetailsURL(String.valueOf(mResourceId)), MOVIE_DETAILS_LOADER_ID);
            } else {
                DisplayUtils.setNoNetworkConnectionMessage(mContext, mBinding.textViewErrorMessageDisplay);
                mContentLoadingUtil.error();
                mCallbacks.hideFavBtn();
            }
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
        String runningTime = mMovieDetail.getRuntime() + " min";
        String status = mMovieDetail.getStatus();
        String rating = String.valueOf(voteAverage);
        String voteCount = "(" + mMovieDetail.getVoteCount() + ")";
        String tagline = mMovieDetail.getTagline();
        String moviePlot = mMovieDetail.getOverview();
        long budget = mMovieDetail.getBudget();
        long revenue = mMovieDetail.getRevenue();
        List<Genre> genres = mMovieDetail.getGenres();

        if (backdropURL != null) {
            Uri backdropUri = HTTPHelper.buildImageResourceUri(backdropURL, HTTPHelper.IMAGE_SIZE_XLARGE);
            mCallbacks.updateBackdrop(backdropUri);
        }

        if (posterURL != null) {
            Uri posterUri = HTTPHelper.buildImageResourceUri(posterURL, HTTPHelper.IMAGE_SIZE_SMALL);
            DisplayUtils.fitImageInto(mBinding.layoutMovieInfo.layoutPoster.imageViewPoster, posterUri);
        }

        mBinding.layoutMovieInfo.textViewMovieTitle.setText(DisplayUtils.formatTitle(title, year));

        mBinding.layoutMovieInfo.textViewMovieRuntime.setText(runningTime);

        if (!TextUtils.isEmpty(status)) {
            mBinding.layoutMovieInfo.textViewMovieStatus.setText(status);
        } else {
            mBinding.layoutMovieInfo.textViewMovieStatus.setVisibility(View.GONE);
        }

        if (genres != null && !genres.isEmpty()) {
            DisplayUtils.addGenres(genres, mBinding.layoutContent.layoutGenres, mContext);
        } else {
            mBinding.layoutContent.textViewMovieGenresTitle.setVisibility(View.GONE);
            mBinding.layoutContent.layoutGenres.setVisibility(View.GONE);
        }

        mBinding.layoutMovieInfo.layoutRating.ratingBarRating.setRating((float) voteAverage);

        mBinding.layoutMovieInfo.layoutRating.textViewRating.setText(rating);

        mBinding.layoutMovieInfo.layoutRating.textViewRatingNum.setText(voteCount);

        mBinding.layoutContent.textViewMovieTagline.setText(DisplayUtils.formatTagline(mContext, tagline));

        mBinding.layoutContent.textViewBudget.setText(DisplayUtils.formatCurrency(budget));

        mBinding.layoutContent.textViewRevenue.setText(DisplayUtils.formatCurrency(revenue));

        if (moviePlot != null && !moviePlot.isEmpty()) {
            mBinding.layoutContent.textViewMoviePlot.setText(moviePlot);
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
        if (HTTPHelper.isNetworkEnabled(mContext)) {
            Intent additionalIntent = new Intent(mContext, AdditionalInfoActivity.class);

            Bundle extras = new Bundle();
            extras.putLong(RESOURCE_ID, mResourceId);
            extras.putString(RESOURCE_TITLE, mResourceTitle);
            extras.putString(RESOURCE_TYPE, ADDITIONAL_INFO_ACTIVITY_FRAGMENT_TYPE_REVIEWS);
            additionalIntent.putExtras(extras);

            startActivity(additionalIntent);
        } else {
            Toast.makeText(mContext, getResources().getString(R.string.no_network_connection_error_message), Toast.LENGTH_SHORT).show();
        }
    }
}

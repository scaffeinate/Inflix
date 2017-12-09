package dev.learn.movies.app.popular_movies.fragments;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.Loader;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import dev.learn.movies.app.popular_movies.R;
import dev.learn.movies.app.popular_movies.activities.AdditionalInfoActivity;
import dev.learn.movies.app.popular_movies.activities.DetailActivity;
import dev.learn.movies.app.popular_movies.activities.DetailActivityCallbacks;
import dev.learn.movies.app.popular_movies.adapters.FilmCastAdapter;
import dev.learn.movies.app.popular_movies.adapters.FilmStripAdapter;
import dev.learn.movies.app.popular_movies.adapters.OnItemClickHandler;
import dev.learn.movies.app.popular_movies.common.Genre;
import dev.learn.movies.app.popular_movies.common.Media;
import dev.learn.movies.app.popular_movies.common.Video;
import dev.learn.movies.app.popular_movies.common.VideosResult;
import dev.learn.movies.app.popular_movies.common.cast.Cast;
import dev.learn.movies.app.popular_movies.common.cast.CastsResult;
import dev.learn.movies.app.popular_movies.common.movies.Movie;
import dev.learn.movies.app.popular_movies.common.movies.MovieDetail;
import dev.learn.movies.app.popular_movies.common.movies.MoviesResult;
import dev.learn.movies.app.popular_movies.data.DataContract;
import dev.learn.movies.app.popular_movies.databinding.FragmentMovieDetailsBinding;
import dev.learn.movies.app.popular_movies.loaders.ContentLoader;
import dev.learn.movies.app.popular_movies.loaders.NetworkLoader;
import dev.learn.movies.app.popular_movies.util.DisplayUtils;
import dev.learn.movies.app.popular_movies.util.HTTPHelper;
import dev.learn.movies.app.popular_movies.util.VideoGridDialog;

import static dev.learn.movies.app.popular_movies.data.DataContract.MOVIES;
import static dev.learn.movies.app.popular_movies.data.DataContract.MediaEntry.COLUMN_BACKDROP_PATH;
import static dev.learn.movies.app.popular_movies.data.DataContract.MediaEntry.COLUMN_GENRES;
import static dev.learn.movies.app.popular_movies.data.DataContract.MediaEntry.COLUMN_IS_BOOKMARKED;
import static dev.learn.movies.app.popular_movies.data.DataContract.MediaEntry.COLUMN_IS_FAVORED;
import static dev.learn.movies.app.popular_movies.data.DataContract.MediaEntry.COLUMN_MEDIA_ID;
import static dev.learn.movies.app.popular_movies.data.DataContract.MediaEntry.COLUMN_OVERVIEW;
import static dev.learn.movies.app.popular_movies.data.DataContract.MediaEntry.COLUMN_POSTER_PATH;
import static dev.learn.movies.app.popular_movies.data.DataContract.MediaEntry.COLUMN_RELEASE_DATE;
import static dev.learn.movies.app.popular_movies.data.DataContract.MediaEntry.COLUMN_RUNTIME;
import static dev.learn.movies.app.popular_movies.data.DataContract.MediaEntry.COLUMN_STATUS;
import static dev.learn.movies.app.popular_movies.data.DataContract.MediaEntry.COLUMN_TAGLINE;
import static dev.learn.movies.app.popular_movies.data.DataContract.MediaEntry.COLUMN_TITLE;
import static dev.learn.movies.app.popular_movies.data.DataContract.MediaEntry.COLUMN_VOTE_AVG;
import static dev.learn.movies.app.popular_movies.data.DataContract.MediaEntry.COLUMN_VOTE_COUNT;
import static dev.learn.movies.app.popular_movies.loaders.ContentLoader.URI_EXTRA;
import static dev.learn.movies.app.popular_movies.util.AppConstants.ACTIVITY_DETAIL_LAZY_LOAD_DELAY_IN_MS;
import static dev.learn.movies.app.popular_movies.util.AppConstants.ADDITIONAL_INFO_ACTIVITY_FRAGMENT_TYPE_REVIEWS;
import static dev.learn.movies.app.popular_movies.util.AppConstants.LOCAL_MOVIE_DETAILS_LOADER_ID;
import static dev.learn.movies.app.popular_movies.util.AppConstants.MOVIE_CAST_LOADER_ID;
import static dev.learn.movies.app.popular_movies.util.AppConstants.MOVIE_DETAILS_LOADER_ID;
import static dev.learn.movies.app.popular_movies.util.AppConstants.MOVIE_ID;
import static dev.learn.movies.app.popular_movies.util.AppConstants.MOVIE_NAME;
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
                    mMovieDetail = (MovieDetail) fromCursor(cursor);
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

        DisplayUtils.addGenres(genres, mBinding.layoutContent.layoutGenres, mContext);

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

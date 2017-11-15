package dev.learn.movies.app.popular_movies.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import dev.learn.movies.app.popular_movies.R;
import dev.learn.movies.app.popular_movies.common.movies.MovieReview;
import dev.learn.movies.app.popular_movies.databinding.ItemUserReviewBinding;

/**
 * MovieReviewsAdapter - RecyclerView Adapter for Movie reviews
 */
public class MovieReviewsAdapter extends LoadMoreAdapter {

    private List<MovieReview> mMovieReviewList;

    public MovieReviewsAdapter() {
        this.mMovieReviewList = new ArrayList<>();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        if (viewType == VIEWTYPE_CONTENT) {
            View view = layoutInflater.inflate(R.layout.item_user_review, parent, false);
            return new ReviewsHolder(view);
        } else {
            View view = layoutInflater.inflate(R.layout.item_footer_loader, parent, false);
            return new LoaderViewHolder(view);
        }
    }

    @Override
    public int getItemCount() {
        return mMovieReviewList.size() + 1;
    }

    /**
     * Sets the movieReviewList
     *
     * @param movieReviewList movieReviewList
     */
    public void setReviewList(List<MovieReview> movieReviewList) {
        this.mMovieReviewList = movieReviewList;
        notifyDataSetChanged();
    }

    /**
     * ViewHolder class to show review content
     */
    class ReviewsHolder extends ContentViewHolder {
        private final ItemUserReviewBinding mBinding;

        public ReviewsHolder(View view) {
            super(view);
            this.mBinding = ItemUserReviewBinding.bind(view);
        }

        @Override
        public void bind(int position) {
            MovieReview movieReview = mMovieReviewList.get(position);
            if (movieReview != null) {
                if (movieReview.getAuthor() != null) {
                    mBinding.setUsername(movieReview.getAuthor());
                }

                if (movieReview.getContent() != null) {
                    mBinding.setReview(movieReview.getContent());
                }
            }
            mBinding.executePendingBindings();
        }
    }
}

package dev.learn.movies.app.popular_movies.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import dev.learn.movies.app.popular_movies.R;
import dev.learn.movies.app.popular_movies.common.movies.Review;
import dev.learn.movies.app.popular_movies.databinding.ItemUserReviewBinding;

/**
 * MovieReviewsAdapter - RecyclerView Adapter for Movie reviews
 */
public class MovieReviewsAdapter extends RecyclerView.Adapter<MovieReviewsAdapter.ViewHolder> {

    private List<Review> mReviewList;

    public MovieReviewsAdapter() {
        this.mReviewList = new ArrayList<>();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_user_review, null);
        return new ViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return mReviewList.size();
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(position);
    }

    /**
     * Sets the reviewList
     *
     * @param reviewList reviewList
     */
    public void setReviewList(List<Review> reviewList) {
        this.mReviewList = reviewList;
        notifyDataSetChanged();
    }

    /**
     * ViewHolder class to show review content
     */
    class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemUserReviewBinding mBinding;

        public ViewHolder(View view) {
            super(view);
            this.mBinding = ItemUserReviewBinding.bind(view);
        }

        private void bind(int position) {
            Review review = mReviewList.get(position);
            if (review != null) {
                if (review.getAuthor() != null) {
                    mBinding.setUsername(review.getAuthor());
                }

                if (review.getContent() != null) {
                    mBinding.setReview(review.getContent());
                }
            }
            mBinding.executePendingBindings();
        }
    }
}

package dev.learn.movies.app.popular_movies;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import dev.learn.movies.app.popular_movies.common.Review;

/**
 * Created by sudharti on 11/4/17.
 */

public class MovieReviewsAdapter extends RecyclerView.Adapter<MovieReviewsAdapter.ViewHolder> {

    List<Review> mReviewList;

    public MovieReviewsAdapter() {
        this.mReviewList = new ArrayList<>();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_review_card, null);
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

    public void setReviewList(List<Review> reviewList) {
        this.mReviewList = reviewList;
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        final TextView mUserNameTextView;
        final TextView mReviewTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            mUserNameTextView = itemView.findViewById(R.id.tv_user_name);
            mReviewTextView = itemView.findViewById(R.id.tv_user_review);
        }

        private void bind(int position) {
            Review review = mReviewList.get(position);
            if (review != null) {
                if (review.getAuthor() != null) {
                    mUserNameTextView.setText(review.getAuthor());
                }

                if (review.getContent() != null) {
                    mReviewTextView.setText(review.getContent());
                }
            }
        }
    }
}

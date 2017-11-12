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
public class MovieReviewsAdapter extends RecyclerView.Adapter<MovieReviewsAdapter.ViewHolder> {

    private List<MovieReview> mMovieReviewList;

    public MovieReviewsAdapter() {
        this.mMovieReviewList = new ArrayList<>();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_user_review, null);
        return new ViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return mMovieReviewList.size();
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(position);
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
    class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemUserReviewBinding mBinding;

        public ViewHolder(View view) {
            super(view);
            this.mBinding = ItemUserReviewBinding.bind(view);
        }

        private void bind(int position) {
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

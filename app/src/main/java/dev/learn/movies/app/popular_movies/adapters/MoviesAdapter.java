package dev.learn.movies.app.popular_movies.adapters;

import android.net.Uri;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import dev.learn.movies.app.popular_movies.R;
import dev.learn.movies.app.popular_movies.common.movies.Movie;
import dev.learn.movies.app.popular_movies.util.DisplayUtils;
import dev.learn.movies.app.popular_movies.util.HTTPHelper;

/**
 * MoviesAdapter - RecyclerView Adapter for Movies
 */
public class MoviesAdapter extends LoadMoreAdapter {

    private List<Movie> mMovieList;
    private final OnItemClickHandler mHandler;

    public MoviesAdapter(OnItemClickHandler handler) {
        mMovieList = new ArrayList<>();
        mHandler = handler;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        if (viewType == VIEWTYPE_CONTENT) {
            View view = layoutInflater.inflate(R.layout.item_poster, parent, false);

            int height = parent.getMeasuredHeight();
            MyViewHolder viewHolder = new MyViewHolder(view);
            viewHolder.adjustPosterHeight(height);
            return viewHolder;
        } else {
            View view = layoutInflater.inflate(R.layout.item_footer_loader, parent, false);
            return new LoaderViewHolder(view);
        }
    }

    @Override
    public int getItemCount() {
        return mMovieList.size() + 1;
    }

    public void setMovieList(List<Movie> movieList) {
        this.mMovieList = movieList;
        notifyDataSetChanged();
    }

    private class MyViewHolder extends LoadMoreAdapter.ContentViewHolder implements View.OnClickListener {

        private final ImageView mPosterImageView;

        public MyViewHolder(View itemView) {
            super(itemView);
            mPosterImageView = itemView.findViewById(R.id.image_view_poster);
            mPosterImageView.setOnClickListener(this);
        }

        @Override
        public void bind(int position) {
            Movie movie = mMovieList.get(position);
            String posterURL;
            if (movie != null && (posterURL = movie.getPosterPath()) != null) {
                Uri posterUri = HTTPHelper.buildImageResourceUri(posterURL, HTTPHelper.IMAGE_SIZE_MEDIUM);
                DisplayUtils.fitImageInto(mPosterImageView, posterUri);
            }
        }

        @Override
        public void onClick(View v) {
            mHandler.onClick(getAdapterPosition());
        }

        public void adjustPosterHeight(int height) {
            mPosterImageView.setMinimumHeight(height / 2);
        }
    }
}

package dev.learn.movies.app.popular_movies.adapters;

import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import dev.learn.movies.app.popular_movies.R;
import dev.learn.movies.app.popular_movies.common.Movie;
import dev.learn.movies.app.popular_movies.util.HTTPHelper;
import dev.learn.movies.app.popular_movies.util.DisplayUtils;

/**
 * MoviesAdapter - RecyclerView Adapter for Movies
 */

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.ViewHolder> {

    private final OnItemClickHandler mHandler;
    private List<Movie> movieList;

    public MoviesAdapter(OnItemClickHandler handler) {
        this.mHandler = handler;
        this.movieList = new ArrayList<>();
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_poster, parent, false);

        /* Set the minimum height of the view to scale for all screens.
         *
         * Reference: https://stackoverflow.com/questions/35221566/how-to-set-the-height-of-an-item-row-in-gridlayoutmanager
         */
        int height = parent.getMeasuredHeight() / 2;
        ViewHolder viewHolder = new ViewHolder(view);
        viewHolder.mPosterImageView.setMinimumHeight(height);
        return viewHolder;
    }

    @Override
    public int getItemCount() {
        return movieList.size();
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(position);
    }

    public void setMovieList(List<Movie> movieList) {
        this.movieList = movieList;
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final ImageView mPosterImageView;

        public ViewHolder(View itemView) {
            super(itemView);
            mPosterImageView = itemView.findViewById(R.id.image_view_poster);
            mPosterImageView.setOnClickListener(this);
        }

        /**
         * Populates the layout with movie details
         *
         * @param position Adapter position to populate
         */
        private void bind(int position) {
            Movie movie = movieList.get(position);
            String posterURL;
            if (movie != null && (posterURL = movie.getPosterPath()) != null) {
                Uri posterUri = HTTPHelper.buildImageResourceUri(posterURL, HTTPHelper.IMAGE_SIZE_MEDIUM);
                DisplayUtils.fitImageInto(mPosterImageView, posterUri, null);
            }
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            mHandler.onClick(position);
        }
    }
}
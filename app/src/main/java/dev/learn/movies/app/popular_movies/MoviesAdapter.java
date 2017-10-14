package dev.learn.movies.app.popular_movies;

import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Callback;

import java.util.ArrayList;
import java.util.List;

import dev.learn.movies.app.popular_movies.common.Movie;
import dev.learn.movies.app.popular_movies.network.HTTPHelper;
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_poster, parent, false);

        /* Set the minimum height of the view to scale for all screens.
         *
         * Reference: https://stackoverflow.com/questions/35221566/how-to-set-the-height-of-an-item-row-in-gridlayoutmanager
         */
        view.setMinimumHeight(parent.getMeasuredHeight() / 2);
        return new ViewHolder(view);
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

    interface OnItemClickHandler {
        void onClick(int position);
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final ImageView mPosterImageView;
        private final TextView mMovieNameTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            mPosterImageView = itemView.findViewById(R.id.image_view_poster);
            mMovieNameTextView = itemView.findViewById(R.id.tv_movie_name);
            mPosterImageView.setOnClickListener(this);
        }

        /**
         * Populates the layout with movie details
         *
         * @param position Adapter position to populate
         */
        private void bind(int position) {
            Movie movie = movieList.get(position);
            if (movie != null) {
                mMovieNameTextView.setText(movie.getTitle());
                String posterURL = movie.getPosterPath();
                if (posterURL != null) {
                    Uri posterUri = HTTPHelper.buildImageResourceUri(posterURL, HTTPHelper.IMAGE_SIZE_MEDIUM);
                    DisplayUtils.fitImageInto(mPosterImageView, posterUri, new Callback() {
                        @Override
                        public void onSuccess() {
                            mMovieNameTextView.setVisibility(View.INVISIBLE);
                        }

                        @Override
                        public void onError() {
                            mMovieNameTextView.setVisibility(View.VISIBLE);
                        }
                    });
                }
            }
        }

        @Override
        public void onClick(View view) {
            mHandler.onClick(getAdapterPosition());
        }
    }
}

package dev.learn.movies.app.popularmovies_udacity;

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

import dev.learn.movies.app.popularmovies_udacity.common.Movie;
import dev.learn.movies.app.popularmovies_udacity.network.HTTPHelper;
import dev.learn.movies.app.popularmovies_udacity.util.DisplayUtils;

/**
 * Created by sudharti on 10/10/17.
 */

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.ViewHolder> {

    private List<Movie> movieList;
    private final OnItemClickHandler mHandler;

    public MoviesAdapter(OnItemClickHandler handler) {
        this.mHandler = handler;
        this.movieList = new ArrayList<>();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_grid, parent, false);
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

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final ImageView mPosterImageView;
        private final TextView mMovieNameTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            mPosterImageView = itemView.findViewById(R.id.imageview_poster);
            mMovieNameTextView = itemView.findViewById(R.id.tv_movie_name);
            mPosterImageView.setOnClickListener(this);
        }

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

    interface OnItemClickHandler {
        void onClick(int position);
    }
}

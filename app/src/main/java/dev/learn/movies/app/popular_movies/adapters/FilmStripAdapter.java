package dev.learn.movies.app.popular_movies.adapters;

import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import dev.learn.movies.app.popular_movies.R;
import dev.learn.movies.app.popular_movies.common.Media;
import dev.learn.movies.app.popular_movies.common.movies.Movie;
import dev.learn.movies.app.popular_movies.common.tv_show.TVShow;
import dev.learn.movies.app.popular_movies.util.DisplayUtils;
import dev.learn.movies.app.popular_movies.util.HTTPHelper;


/**
 * Created by sudhar on 11/15/17.
 */
public class FilmStripAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<? extends Media> mFilmStripList;
    private final OnItemClickHandler mHandler;

    public FilmStripAdapter(OnItemClickHandler handler) {
        mFilmStripList = new ArrayList<>();
        mHandler = handler;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());

        View view = layoutInflater.inflate(R.layout.item_film_strip, parent, false);

        int width = parent.getMeasuredWidth();
        FilmStripHolder viewHolder = new FilmStripHolder(parent, view);
        viewHolder.adjustPosterHeight(width);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((FilmStripHolder) holder).bind(position);
    }

    @Override
    public int getItemCount() {
        return mFilmStripList.size();
    }

    public void setFilmStripList(List<? extends Media> filmStripList) {
        this.mFilmStripList = filmStripList;
        notifyDataSetChanged();
    }

    private class FilmStripHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final ViewGroup mParent;
        private final ImageView mPosterImageView;

        public FilmStripHolder(ViewGroup parent, View itemView) {
            super(itemView);
            mPosterImageView = itemView.findViewById(R.id.image_view_poster);
            mPosterImageView.setOnClickListener(this);
            mParent = parent;
        }

        public void bind(int position) {
            Media media = mFilmStripList.get(position);
            if (media instanceof Movie) {
                Movie movie = (Movie) mFilmStripList.get(position); //Or TVShow
                String posterURL;
                if (movie != null && (posterURL = movie.getPosterPath()) != null) {
                    Uri posterUri = HTTPHelper.buildImageResourceUri(posterURL, HTTPHelper.IMAGE_SIZE_MEDIUM);
                    DisplayUtils.fitImageInto(mPosterImageView, posterUri);
                }
            } else if (media instanceof TVShow) {
                TVShow tvShow = (TVShow) mFilmStripList.get(position); //Or TVShow
                String posterURL;
                if (tvShow != null && (posterURL = tvShow.getPosterPath()) != null) {
                    Uri posterUri = HTTPHelper.buildImageResourceUri(posterURL, HTTPHelper.IMAGE_SIZE_MEDIUM);
                    DisplayUtils.fitImageInto(mPosterImageView, posterUri);
                }
            }
        }

        @Override
        public void onClick(View view) {
            mHandler.onItemClicked(mParent, view, getAdapterPosition());
        }

        public void adjustPosterHeight(int width) {
            mPosterImageView.setLayoutParams(new FrameLayout.LayoutParams((int) (width / 2.5), (width / 2)));
        }
    }
}

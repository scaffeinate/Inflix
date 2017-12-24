package dev.learn.movies.app.popular_movies.adapters;

import android.app.Activity;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import dev.learn.movies.app.popular_movies.R;
import dev.learn.movies.app.popular_movies.common.Media;
import dev.learn.movies.app.popular_movies.utils.DisplayUtils;
import dev.learn.movies.app.popular_movies.utils.URIBuilderUtils;


/**
 * FilmStripAdapter - RecyclerView Adapter for showing film strip
 */
public class FilmStripAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final OnItemClickHandler mHandler;
    private final int minScreenSize, maxScreenSize;
    private List<? extends Media> mFilmStripList;

    public FilmStripAdapter(Activity activity, OnItemClickHandler handler) {
        mFilmStripList = new ArrayList<>();
        mHandler = handler;
        int[] screen = DisplayUtils.getScreenMetrics(activity);
        minScreenSize = Math.min(screen[0], screen[1]);
        maxScreenSize = Math.max(screen[0], screen[1]);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_film_strip, parent, false);
        return new FilmStripHolder(parent, view);
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
            adjusPosterSize();
        }

        public void bind(int position) {
            Media media = mFilmStripList.get(position);
            String posterURL;
            if (media != null && (posterURL = media.getPosterPath()) != null) {
                Uri posterUri = URIBuilderUtils.buildImageResourceUri(posterURL, URIBuilderUtils.IMAGE_SIZE_MEDIUM);
                DisplayUtils.fitImageInto(mPosterImageView, posterUri);
            }
        }

        @Override
        public void onClick(View view) {
            mHandler.onItemClicked(mParent, view, getAdapterPosition());
        }

        private void adjusPosterSize() {
            mPosterImageView.setLayoutParams(new FrameLayout.LayoutParams((minScreenSize / 3),
                    (int) (maxScreenSize / 3.15)));
        }
    }
}

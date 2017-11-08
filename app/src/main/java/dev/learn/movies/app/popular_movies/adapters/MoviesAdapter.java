package dev.learn.movies.app.popular_movies.adapters;

import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import dev.learn.movies.app.popular_movies.R;
import dev.learn.movies.app.popular_movies.common.Movie;
import dev.learn.movies.app.popular_movies.util.DisplayUtils;
import dev.learn.movies.app.popular_movies.util.HTTPHelper;

/**
 * MoviesAdapter - RecyclerView Adapter for Movies
 */
public class MoviesAdapter extends RecyclerView.Adapter<ViewHolder> {

    private static final int VIEWTYPE_LOADING = 1;
    private static final int VIEWTYPE_CONTENT = 2;

    private final OnItemClickHandler mHandler;
    private List<Movie> movieList;
    private boolean mShowLoading = true;

    public MoviesAdapter(OnItemClickHandler handler) {
        this.mHandler = handler;
        this.movieList = new ArrayList<>();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        if (viewType == VIEWTYPE_CONTENT) {
            View view = layoutInflater.inflate(R.layout.item_poster, parent, false);

            // Set the minimum height of the view to scale for all screens.
            // Reference: https://stackoverflow.com/questions/35221566/how-to-set-the-height-of-an-item-row-in-gridlayoutmanager
            int height = parent.getMeasuredHeight() / 2;
            ContentViewHolder viewHolder = new ContentViewHolder(view);
            viewHolder.mPosterImageView.setMinimumHeight(height);
            return viewHolder;
        } else {
            View view = layoutInflater.inflate(R.layout.item_footer_loader, parent, false);
            return new LoaderViewHolder(view);
        }
    }

    @Override
    public int getItemCount() {
        // Add size() + 1 for the footer loader
        return movieList.size() + 1;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (holder instanceof LoaderViewHolder) {
            ((LoaderViewHolder) holder).bind();
        } else if (holder instanceof ContentViewHolder) {
            ((ContentViewHolder) holder).bind(position);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == getItemCount() - 1) {
            return VIEWTYPE_LOADING;
        }

        return VIEWTYPE_CONTENT;
    }

    /**
     * Sets the movieList and calls notifyDataSetChanged()
     *
     * @param movieList movieList
     */
    public void setMovieList(List<Movie> movieList) {
        this.movieList = movieList;
        notifyDataSetChanged();
    }

    /**
     * Toggles progressBar footer view
     *
     * @param showLoading showLoading
     */
    public void showLoading(boolean showLoading) {
        this.mShowLoading = showLoading;
    }

    /**
     * ViewHolder class to show item content
     */
    class ContentViewHolder extends ViewHolder implements View.OnClickListener {

        private final ImageView mPosterImageView;

        public ContentViewHolder(View itemView) {
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
                DisplayUtils.fitImageInto(mPosterImageView, posterUri);
            }
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            mHandler.onClick(position);
        }
    }

    /**
     * ViewHolder class to show loading progress footer
     *
     * Reference: http://www.jayrambhia.com/blog/footer-loader
     */
    class LoaderViewHolder extends ViewHolder {
        public LoaderViewHolder(View itemView) {
            super(itemView);
        }

        private void bind() {
            if (mShowLoading) {
                itemView.setVisibility(View.VISIBLE);
            } else {
                itemView.setVisibility(View.GONE);
            }
        }
    }
}

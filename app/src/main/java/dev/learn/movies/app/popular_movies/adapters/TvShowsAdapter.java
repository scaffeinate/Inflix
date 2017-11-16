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
import dev.learn.movies.app.popular_movies.common.tv_show.TVShow;
import dev.learn.movies.app.popular_movies.util.DisplayUtils;
import dev.learn.movies.app.popular_movies.util.HTTPHelper;

/**
 * Created by sudharti on 11/12/17.
 */

public class TvShowsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int VIEWTYPE_LOADING = 1;
    private static final int VIEWTYPE_CONTENT = 2;

    private final OnItemClickHandler mHandler;
    private List<TVShow> mTvShowList;
    private boolean mShowLoading = true;

    public TvShowsAdapter(OnItemClickHandler handler) {
        this.mHandler = handler;
        this.mTvShowList = new ArrayList<>();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        if (viewType == VIEWTYPE_CONTENT) {
            View view = layoutInflater.inflate(R.layout.item_poster, parent, false);

            // Set the minimum height of the view to scale for all screens.
            // Reference: https://stackoverflow.com/questions/35221566/how-to-set-the-height-of-an-item-row-in-gridlayoutmanager
            int height = parent.getMeasuredHeight() / 2;
            TvShowsAdapter.ContentViewHolder viewHolder = new TvShowsAdapter.ContentViewHolder(view);
            viewHolder.mPosterImageView.setMinimumHeight(height);
            return viewHolder;
        } else {
            View view = layoutInflater.inflate(R.layout.item_footer_loader, parent, false);
            return new TvShowsAdapter.LoaderViewHolder(view);
        }
    }

    @Override
    public int getItemCount() {
        // Add size() + 1 for the footer loader
        return mTvShowList.size() + 1;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof TvShowsAdapter.LoaderViewHolder) {
            ((TvShowsAdapter.LoaderViewHolder) holder).bind();
        } else if (holder instanceof TvShowsAdapter.ContentViewHolder) {
            ((TvShowsAdapter.ContentViewHolder) holder).bind(position);
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
     * Sets the tvShowList and calls notifyDataSetChanged()
     *
     * @param tvShowList Tv Shows
     */
    public void setTvShowList(List<TVShow> tvShowList) {
        this.mTvShowList = tvShowList;
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
    class ContentViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final ImageView mPosterImageView;

        public ContentViewHolder(View itemView) {
            super(itemView);
            mPosterImageView = itemView.findViewById(R.id.image_view_poster);
            mPosterImageView.setOnClickListener(this);
        }

        /**
         * Populates the layout with tvShow details
         *
         * @param position Adapter position to populate
         */
        private void bind(int position) {
            TVShow tvShow = mTvShowList.get(position);
            String posterURL;
            if (tvShow != null && (posterURL = tvShow.getPosterPath()) != null) {
                Uri posterUri = HTTPHelper.buildImageResourceUri(posterURL, HTTPHelper.IMAGE_SIZE_MEDIUM);
                DisplayUtils.fitImageInto(mPosterImageView, posterUri);
            }
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            mHandler.onItemClicked(position);
        }
    }

    /**
     * ViewHolder class to show loading progress footer
     * <p>
     * Reference: http://www.jayrambhia.com/blog/footer-loader
     */
    class LoaderViewHolder extends RecyclerView.ViewHolder {
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

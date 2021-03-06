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
import dev.learn.movies.app.popular_movies.common.Media;
import dev.learn.movies.app.popular_movies.utils.DisplayUtils;
import dev.learn.movies.app.popular_movies.utils.URIBuilderUtils;

/**
 * MediaAdapter - RecyclerView Adapter for Movies
 */
public class MediaAdapter extends LoadMoreAdapter {

    private final OnItemClickHandler mHandler;
    private List<Media> mMediaList;

    public MediaAdapter(OnItemClickHandler handler) {
        mMediaList = new ArrayList<>();
        mHandler = handler;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        if (viewType == VIEWTYPE_CONTENT) {
            View view = layoutInflater.inflate(R.layout.item_poster, parent, false);

            int height = parent.getMeasuredHeight();
            MyViewHolder viewHolder = new MyViewHolder(parent, view);
            viewHolder.adjustPosterHeight(height);
            return viewHolder;
        } else {
            View view = layoutInflater.inflate(R.layout.item_footer_loader, parent, false);
            return new LoaderViewHolder(view);
        }
    }

    @Override
    public int getItemCount() {
        return mMediaList.size() + 1;
    }

    public void setMediaList(List<Media> mediaList) {
        this.mMediaList = mediaList;
        notifyDataSetChanged();
    }

    private class MyViewHolder extends LoadMoreAdapter.ContentViewHolder implements View.OnClickListener {

        private final ViewGroup mParent;
        private final ImageView mPosterImageView;

        public MyViewHolder(ViewGroup parent, View itemView) {
            super(itemView);
            mPosterImageView = itemView.findViewById(R.id.image_view_poster);
            mPosterImageView.setOnClickListener(this);
            mParent = parent;
        }

        @Override
        public void bind(int position) {
            Media movie = mMediaList.get(position);
            String posterURL;
            if (movie != null && (posterURL = movie.getPosterPath()) != null) {
                Uri posterUri = URIBuilderUtils.buildImageResourceUri(posterURL, URIBuilderUtils.IMAGE_SIZE_MEDIUM);
                DisplayUtils.fitImageInto(mPosterImageView, posterUri);
            }
        }

        @Override
        public void onClick(View view) {
            mHandler.onItemClicked(mParent, view, getAdapterPosition());
        }

        public void adjustPosterHeight(int height) {
            mPosterImageView.setMinimumHeight(height / 2);
        }
    }
}

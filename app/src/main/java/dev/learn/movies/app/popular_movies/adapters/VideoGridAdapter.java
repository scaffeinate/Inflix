package dev.learn.movies.app.popular_movies.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import dev.learn.movies.app.popular_movies.R;
import dev.learn.movies.app.popular_movies.common.Video;
import dev.learn.movies.app.popular_movies.util.DisplayUtils;
import dev.learn.movies.app.popular_movies.util.HTTPHelper;

/**
 * Created by sudharti on 11/27/17.
 */

public class VideoGridAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Video> mVideoList;
    private OnItemClickHandler mOnItemClickHandler;

    public VideoGridAdapter(OnItemClickHandler onItemClickHandler) {
        mVideoList = new ArrayList<>();
        mOnItemClickHandler = onItemClickHandler;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_video_grid, parent, false);
        return new ViewHolder(parent, view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ViewHolder viewHolder = (ViewHolder) holder;
        viewHolder.bind(position);
    }

    @Override
    public int getItemCount() {
        return mVideoList.size();
    }

    public void setVideoList(List<Video> videoList) {
        mVideoList = videoList;
        notifyDataSetChanged();
    }

    private class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        final ViewGroup mParent;
        final ImageView mVideoThumbImageView;

        public ViewHolder(ViewGroup parent, View itemView) {
            super(itemView);
            this.mParent = parent;
            mVideoThumbImageView = itemView.findViewById(R.id.image_view_video_thumb);
            itemView.setOnClickListener(this);
        }

        private void bind(int position) {
            Video video = mVideoList.get(position);
            if (video != null && video.getKey() != null) {
                DisplayUtils.fitImageInto(mVideoThumbImageView, HTTPHelper.buildYouTubeThumbURI(video.getKey()));
            }
        }

        @Override
        public void onClick(View v) {
            if (mOnItemClickHandler != null) {
                mOnItemClickHandler.onItemClicked(mParent, v, getAdapterPosition());
            }
        }
    }
}

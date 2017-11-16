package dev.learn.movies.app.popular_movies.adapters;

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
import dev.learn.movies.app.popular_movies.common.movies.Movie;
import dev.learn.movies.app.popular_movies.util.DisplayUtils;
import dev.learn.movies.app.popular_movies.util.HTTPHelper;


/**
 * Created by sudhar on 11/15/17.
 */
public class RecommendationsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<Movie> mRecommendationList;
    private final OnItemClickHandler mHandler;

    public RecommendationsAdapter(OnItemClickHandler handler) {
        mRecommendationList = new ArrayList<>();
        mHandler = handler;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());

        View view = layoutInflater.inflate(R.layout.item_recommendation, parent, false);

        int width = parent.getMeasuredWidth();
        RecommendationsHolder viewHolder = new RecommendationsHolder(view);
        viewHolder.adjustPosterHeight(width);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((RecommendationsHolder) holder).bind(position);
    }

    @Override
    public int getItemCount() {
        return mRecommendationList.size();
    }

    public void setRecommendationList(List<Movie> recommendationList) {
        this.mRecommendationList = recommendationList;
        notifyDataSetChanged();
    }

    private class RecommendationsHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final ImageView mPosterImageView;

        public RecommendationsHolder(View itemView) {
            super(itemView);
            mPosterImageView = itemView.findViewById(R.id.image_view_poster);
            mPosterImageView.setOnClickListener(this);
        }

        public void bind(int position) {
            Movie movie = mRecommendationList.get(position);
            String posterURL;
            if (movie != null && (posterURL = movie.getPosterPath()) != null) {
                Uri posterUri = HTTPHelper.buildImageResourceUri(posterURL, HTTPHelper.IMAGE_SIZE_MEDIUM);
                DisplayUtils.fitImageInto(mPosterImageView, posterUri);
            }
        }

        @Override
        public void onClick(View v) {
            mHandler.onItemClicked(getAdapterPosition());
        }

        public void adjustPosterHeight(int width) {
            //mPosterImageView.setLayoutParams(new FrameLayout.LayoutParams(width / 2, (int) (width / 1.5)));
        }
    }
}

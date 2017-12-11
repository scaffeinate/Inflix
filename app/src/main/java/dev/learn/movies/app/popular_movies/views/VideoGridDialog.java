package dev.learn.movies.app.popular_movies.views;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import dev.learn.movies.app.popular_movies.R;
import dev.learn.movies.app.popular_movies.adapters.OnItemClickHandler;
import dev.learn.movies.app.popular_movies.adapters.VideoGridAdapter;
import dev.learn.movies.app.popular_movies.common.Video;
import dev.learn.movies.app.popular_movies.databinding.DialogVideoGridBinding;

import static dev.learn.movies.app.popular_movies.util.AppConstants.DEFAULT_GRID_COUNT;

/**
 * Created by sudharti on 11/28/17.
 */

public class VideoGridDialog implements OnItemClickHandler {

    private Context mContext;
    private AlertDialog.Builder mBuilder;
    private AlertDialog mDialog;
    private String mTitle = "Videos";
    private boolean mCancelable = true;
    private RecyclerView.LayoutManager mLayoutManager;
    private VideoGridAdapter mAdapter;
    private List<Video> mVideoList;
    private OnVideoSelectedListener mOnVideoSelectedListener;
    private DialogInterface.OnShowListener mOnShowListener;
    private DialogVideoGridBinding mBinding;

    private VideoGridDialog(Context context) {
        mContext = context;
        mBuilder = new AlertDialog.Builder(context);
        mLayoutManager = new GridLayoutManager(context, DEFAULT_GRID_COUNT);
        mBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.dialog_video_grid, null, false);
        mBuilder.setView(mBinding.getRoot());
        mAdapter = new VideoGridAdapter(this);
        mBinding.rvVideoGrid.setLayoutManager(mLayoutManager);
        mBinding.rvVideoGrid.setAdapter(mAdapter);
    }

    public static VideoGridDialog with(Context context) {
        return new VideoGridDialog(context);
    }

    public VideoGridDialog setTitle(String title) {
        mTitle = title;
        return this;
    }

    public VideoGridDialog setCancelable(boolean cancelable) {
        mCancelable = cancelable;
        return this;
    }

    public VideoGridDialog setOnVideoSelectedListener(OnVideoSelectedListener onVideoSelectedListener) {
        mOnVideoSelectedListener = onVideoSelectedListener;
        return this;
    }

    public VideoGridDialog setOnShowListener(DialogInterface.OnShowListener onShowListener) {
        mOnShowListener = onShowListener;
        return this;
    }

    public VideoGridDialog setVideos(List<Video> videoList) {
        mVideoList = videoList;
        mAdapter.setVideoList(videoList);
        return this;
    }

    public void success() {
        showVideos();
    }

    public void error() {
        showErrorMessage();
    }

    private void showVideos() {
        mBinding.rvVideoGrid.setVisibility(View.VISIBLE);
        mBinding.pbVideoGrid.setVisibility(View.GONE);
        mBinding.tvVideoGridErrorMessage.setVisibility(View.GONE);
    }

    private void showErrorMessage() {
        mBinding.tvVideoGridErrorMessage.setVisibility(View.VISIBLE);
        mBinding.rvVideoGrid.setVisibility(View.GONE);
        mBinding.pbVideoGrid.setVisibility(View.GONE);
    }

    public void build() {
        if (mDialog == null) {
            mDialog = mBuilder
                    .setTitle(mTitle)
                    .setCancelable(mCancelable)
                    .create();
            mDialog.setOnShowListener(mOnShowListener);
        }
        mDialog.show();
    }

    @Override
    public void onItemClicked(ViewGroup parent, View view, int position) {
        if (mVideoList != null && position >= 0 && position < mVideoList.size()) {
            mOnVideoSelectedListener.onVideoSelected(mVideoList.get(position));
        }
    }

    public interface OnVideoSelectedListener {
        void onVideoSelected(Video video);
    }
}

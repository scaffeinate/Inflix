package dev.learn.movies.app.popular_movies.util;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
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

import static dev.learn.movies.app.popular_movies.util.AppConstants.DEFAULT_GRID_COUNT;

/**
 * DialogBuilderHelper - Contains helpers to build and show a dialog
 */
public final class DialogBuilderHelper {

    public static void buildVideoDialog(Context context,
                                        String title, List<Video> videoList, final Dialog.OnClickListener onClickListener) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_video_grid, null);
        final AlertDialog dialog = builder.setView(view)
                .setTitle(title)
                .setCancelable(true)
                .create();

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(context, DEFAULT_GRID_COUNT);
        ((RecyclerView) view).setLayoutManager(layoutManager);

        VideoGridAdapter adapter = new VideoGridAdapter(new OnItemClickHandler() {
            @Override
            public void onItemClicked(ViewGroup parent, View view, int position) {
                onClickListener.onClick(dialog, position);
            }
        });
        ((RecyclerView) view).setAdapter(adapter);
        adapter.setVideoList(videoList);
        dialog.show();
    }
}

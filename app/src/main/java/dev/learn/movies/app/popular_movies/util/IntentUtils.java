package dev.learn.movies.app.popular_movies.util;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.ShareCompat;
import android.widget.Toast;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import dev.learn.movies.app.popular_movies.R;
import dev.learn.movies.app.popular_movies.common.Video;

/**
 * Created by sudharti on 11/12/17.
 */

public final class IntentUtils {
    /**
     * Build and call Share intent for a video
     *
     * @param videoList Video List
     */
    public static void shareVideos(final Activity activity, final List<Video> videoList) {
        if (videoList != null && !videoList.isEmpty()) {
            List<String> values = new ArrayList<>();
            for (Video video : videoList) {
                values.add(video.getName());
            }
            DialogBuilderHelper.build(activity, "Share Trailer", values, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Video video = videoList.get(which);
                    shareVideo(activity, video);
                }
            });
        }
    }

    /**
     * Build and call Youtube intent
     * Reference: https://stackoverflow.com/questions/574195/android-youtube-app-play-video-intent
     *
     * @param videoList Video List
     */
    public static void watchVideos(final Context context, final List<Video> videoList) {
        if (videoList != null && !videoList.isEmpty()) {
            List<String> values = new ArrayList<>();
            for (Video video : videoList) {
                values.add(video.getName());
            }
            DialogBuilderHelper.build(context, "Share Trailer", values, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Video video = videoList.get(which);
                    watchVideo(context, video);
                }
            });
        }
    }

    /**
     * Build and call Web browser intent to open IMDB Link
     *
     * @param imdbId IMDB Title ID
     */
    public static void openIMDBLink(Context context, String imdbId) {
        if (imdbId != null) {
            URL url = HTTPHelper.buildIMDBURL(imdbId);
            if (url != null) {
                Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url.toString()));
                context.startActivity(webIntent);
            }
        } else {
            Toast.makeText(context, context.getResources().getString(R.string.imdb_link_not_available), Toast.LENGTH_SHORT).show();
        }
    }

    private static void shareVideo(Activity activity, Video video) {
        if (video != null && video.getKey() != null) {
            String mimeType = "text/plain";
            String title = (video.getName() == null) ? activity.getResources().getString(R.string.trailer_1) : video.getName();
            URL url = HTTPHelper.buildYouTubeURL(video.getKey());
            if (url != null) {
                ShareCompat.IntentBuilder
                        .from(activity)
                        .setType(mimeType)
                        .setChooserTitle("Share " + title)
                        .setText(url.toString())
                        .startChooser();
            }
        }
    }

    private static void watchVideo(Context context, Video video) {
        if (video != null && video.getKey() != null) {
            String key = video.getKey();
            try {
                Intent youtubeAppIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd:youtube:" + key));
                context.startActivity(youtubeAppIntent);
            } catch (ActivityNotFoundException e) {
                URL url = HTTPHelper.buildYouTubeURL(key);
                if (url != null) {
                    Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url.toString()));
                    context.startActivity(webIntent);
                }
            }
        }
    }
}

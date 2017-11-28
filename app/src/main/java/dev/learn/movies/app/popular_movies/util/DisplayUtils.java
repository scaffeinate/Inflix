package dev.learn.movies.app.popular_movies.util;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.ShareCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.net.URL;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import dev.learn.movies.app.popular_movies.R;
import dev.learn.movies.app.popular_movies.common.Genre;
import dev.learn.movies.app.popular_movies.common.Video;

/**
 * DisplayUtils - Contains helper methods for common formatting and view changes
 */

public final class DisplayUtils {

    private final static String TAG = DisplayUtils.class.getSimpleName();

    /**
     * Sets the imageView with image at uri using Picasso
     *
     * @param imageView ImageView to populate
     * @param uri       URI of the image
     */
    public static void fitImageInto(ImageView imageView, Uri uri) {
        if (imageView != null && uri != null) {
            Log.i(TAG, "Loading Image from: " + uri);
            Picasso.with(imageView.getContext()).load(uri)
                    .fit().centerCrop()
                    .into(imageView);
        }
    }

    /**
     * Gets the year from dateStr, If not valid returns -1
     *
     * @param dateStr Date String in yyyy-mm-dd format
     * @return year if valid or -1
     */
    public static int getYear(String dateStr) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd", Locale.getDefault());
        try {
            Date date = dateFormat.parse(dateStr);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            return calendar.get(Calendar.YEAR);
        } catch (ParseException e) {
            Log.e(TAG, e.getMessage());
        }

        return -1;
    }

    /**
     * Formats title of a movie
     *
     * @param title Movie title
     * @param year  Movie release year
     * @return formatted title String
     */
    public static String formatTitle(String title, int year) {
        StringBuilder builder = new StringBuilder();
        if (title != null && !title.isEmpty()) {
            builder.append(title);
        }

        if (year != -1) {
            builder.append(" (").append(year).append(")");
        }
        return builder.toString();
    }

    /**
     * Creates TextViews for each genre and adds to the parent layout
     *
     * @param genres List of Genres
     */
    public static void addGenres(List<Genre> genres, ViewGroup parent, Context context) {
        if (genres != null && !genres.isEmpty()) {
            for (int i = 0; i < genres.size(); i++) {
                TextView tagView = (TextView) LayoutInflater.from(context).inflate(R.layout.layout_tag, parent, false);
                tagView.setText(genres.get(i).getName());
                parent.addView(tagView);
            }
        }
    }

    /**
     * Formats the movie tagline
     *
     * @param tagline Movie Tagline
     * @return formatted tagline inside quotes
     */
    public static String formatTagline(Context context, String tagline) {
        if (tagline == null || tagline.isEmpty()) {
            tagline = context.getString(R.string.no_tagline_error_message);
        }
        return "“" + tagline + "”";
    }

    /**
     * Checks if there is a valid networj=k connection
     *
     * @param context       Context
     * @param errorTextView View to which the setText is called
     */
    public static void setNoNetworkConnectionMessage(Context context, TextView errorTextView) {
        String errorMessaage = context.getResources().getString(R.string.no_network_connection_error_message);
        errorTextView.setText(errorMessaage);
    }

    public static int[] getScreenMetrics(Activity activity) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int screenHeight = displayMetrics.heightPixels;
        int screenWidth = displayMetrics.widthPixels;
        return new int[]{screenWidth, screenHeight};
    }

    public static String formatCurrency(long amount) {
        if (amount == 0) {
            return "N/A";
        }

        NumberFormat numberFormat = NumberFormat.getCurrencyInstance(Locale.getDefault());
        numberFormat.setMinimumFractionDigits(0);
        String result = numberFormat.format(amount);
        return result;
    }

    /**
     * Build and call Youtube intent
     * Reference: https://stackoverflow.com/questions/574195/android-youtube-app-play-video-intent
     *
     * @param videoList Video List
     */
    public static void buildTrailersDialog(final Context context, final List<Video> videoList) {
        if (videoList != null && !videoList.isEmpty()) {
            DialogBuilderHelper.buildVideoDialog(context, "Watch Trailer", videoList, new DialogInterface.OnClickListener() {
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

    public static void shareURL(Activity activity, String title, URL url) {
        if (url != null) {
            String mimeType = "text/plain";
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

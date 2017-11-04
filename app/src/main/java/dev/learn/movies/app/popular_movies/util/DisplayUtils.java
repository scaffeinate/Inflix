package dev.learn.movies.app.popular_movies.util;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import dev.learn.movies.app.popular_movies.R;
import dev.learn.movies.app.popular_movies.common.Genre;

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
     * @param callback  Picasso callback override
     */
    public static void fitImageInto(ImageView imageView, Uri uri, Callback callback) {
        Picasso.with(imageView.getContext()).load(uri)
                .fit().centerCrop()
                .into(imageView, callback);
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
            builder.append("(").append(year).append(")");
        }
        return builder.toString();
    }

    /**
     * Formats genres using a pipe separator
     *
     * @param genres List of Genres
     * @return Genres separated by a pipe separator
     */
    public static String formatGenres(List<Genre> genres) {
        StringBuilder builder = new StringBuilder();
        if (genres != null && !genres.isEmpty()) {
            for (int i = 0; i < genres.size(); i++) {
                builder.append(genres.get(i).getName()).append((i < genres.size() - 1) ? " | " : "");
            }
        }
        return builder.toString();
    }

    /**
     * Formats the movie tagline
     *
     * @param tagline Movie Tagline
     * @return formatted tagline inside quotes
     */
    public static String formatTagline(String tagline) {
        if (tagline == null || tagline.isEmpty()) {
            tagline = "No Synopsis available.";
        }
        return "\"" + tagline + "\"";
    }

    /**
     * Checks if there is a valid networj=k connection
     *
     * @param context       Context
     * @param errorTextView View to which the setText is called
     */
    public static void setNoNetworkConnectionMessage(Context context, TextView errorTextView) {
        String errorMessaage = context.getResources().getString(R.string.no_network_connection);
        errorTextView.setText(errorMessaage);
    }
}

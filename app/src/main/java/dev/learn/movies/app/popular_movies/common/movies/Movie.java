package dev.learn.movies.app.popular_movies.common.movies;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import dev.learn.movies.app.popular_movies.common.Media;

@SuppressWarnings("ALL")
public class Movie extends Media implements Parcelable {

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };
    @SerializedName("title")
    @Expose
    private String title;

    protected Movie(Parcel in) {
        id = in.readLong();
        title = in.readString();
        posterPath = in.readString();
    }

    public String getTitle() {
        return title;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(title);
        dest.writeString(posterPath);
    }
}


package dev.learn.movies.app.popularmovies_udacity.common;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Movie {


    @SerializedName("id")
    @Expose
    private long id;

    @SerializedName("title")
    @Expose
    private String title;

    @SerializedName("poster_path")
    @Expose
    private String posterPath;

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getPosterPath() {
        return posterPath;
    }

}
